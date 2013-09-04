package edu.wustl.query.bizlogic;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.dto.QueryExportDTO;
import edu.wustl.query.querysuite.QueryDataExportTask;
import edu.wustl.query.util.global.AQConstants;

public class QueryDataExportService {
	
	private static QueryDataExportService instance = null;
	
	private static ExecutorService executorService = 
			Executors.newFixedThreadPool(Integer.parseInt(XMLPropertyHandler.getValue("export.threadPool.size")));
	
	private static ScheduledExecutorService schedulerExecutor = Executors.newSingleThreadScheduledExecutor(); 
	
	private static final Logger LOGGER = Logger.getCommonLogger(QueryDataExportService.class);
	
	//
	// Key   : userCsmId_queryId 
	// Value : QueryDataExportTask
	//
	private static Map<String, QueryDataExportTask> exportTaskMap = new HashMap<String, QueryDataExportTask>();
	
	private static String appPath = CommonServiceLocator.getInstance().getPropDirPath();
	
	private static String path = appPath + System.getProperty("file.separator")+"reports";
	
	public static void initialize(){
		if(instance == null){
			instance = getInstance();
		}
		instance.scheduleExpiredExportDataFilesDeletion();
	}
		
	public static QueryDataExportService getInstance() {
		instance = new QueryDataExportService();
		String appPath = CommonServiceLocator.getInstance().getPropDirPath();
		String path = appPath + System.getProperty("file.separator")+"reports";
		File file = new File(path);
		file.mkdir();
		return instance;
	}
	
	private QueryDataExportService(){}
	
	public void exportAllData(final QueryExportDTO queryExportDTO)
	{
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				Long queryId = queryExportDTO.getQueryDetails().getQuery().getId();
				QueryDataExportTask queryDataExportTask = new QueryDataExportTask(queryExportDTO);
				exportTaskMap.put(queryExportDTO.getCsmUserId()+"_"+queryId, queryDataExportTask);
				try {
					queryDataExportTask.run();
				} finally {
					exportTaskMap.remove(queryExportDTO.getCsmUserId()+"_"+queryId);
				}		 
			}
		});
	}
	
	public void exportClientSideData(final QueryExportDTO queryExportDTO, HttpServletResponse response, List<List<String>> dataList)
	{
		List<List<String>> exportList = queryExportDTO.getExportList();
		exportList.add(queryExportDTO.getColumnList());
		exportList.addAll(dataList);
		QueryDataExportTask queryDataExportTask = new QueryDataExportTask(queryExportDTO);
		try {
			queryDataExportTask.exportAndSend(exportList, true, false);	
		} catch (IOException e) {
			LOGGER.error("Error while exporting client side data"+ e.getMessage());
		}
	}
	
	public boolean isAlreadyInProgress(QueryExportDTO queryExportDTO) 
	{
		Long queryId = queryExportDTO.getQueryDetails().getQuery().getId();
		QueryDataExportTask queryDataExportTask = exportTaskMap.get(queryExportDTO.getCsmUserId()+"_"+queryId);
		if(queryDataExportTask != null){
			return true;
		}	
		return false;		
	}
	
	public void scheduleExpiredExportDataFilesDeletion() {
		final Runnable cleanUpTask = new Runnable() {
			public void run() {
				deleteExpiredExportDataFiles();
			}
		};
				
		schedulerExecutor.scheduleWithFixedDelay(cleanUpTask, getStartTimeForSchedular(), 
				AQConstants.MILLISECONDS_IN_DAY, TimeUnit.MILLISECONDS);
	}
	 
	private Long getStartTimeForSchedular() {
		
		Calendar startTime = Calendar.getInstance();
		startTime.add (Calendar.DAY_OF_MONTH, 1);
		startTime.set (Calendar.HOUR_OF_DAY, 0);
		startTime.set (Calendar.MINUTE, 0);
		startTime.set (Calendar.SECOND, 0);
		startTime.set (Calendar.MILLISECOND, 0);
		
		return (startTime.getTimeInMillis() - System.currentTimeMillis());
	}
	
	private void deleteExpiredExportDataFiles() {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		Long retainTimeInDay = Long.parseLong(XMLPropertyHandler.getValue("queryExportDataFileRetainDays"));
		//retainTimeInMilis represents the time to retain file in milliseconds 
		Long retainTimeInMilis = retainTimeInDay * AQConstants.MILLISECONDS_IN_DAY;
			
		for (File file : listOfFiles) {
		    if (file.isFile() && (System.currentTimeMillis() - file.lastModified()) > retainTimeInMilis) {
		    	   file.delete();        
		    }
		}
	 }
}
