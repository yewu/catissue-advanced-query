
package edu.wustl.query.testQuerySuite;

/**
 *
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.wustl.query.bizlogic.CreateQueryObjectTestCase;
//import edu.wustl.query.generator.SqlGeneratorGenericTestCase;
import edu.wustl.query.htmlprovider.HtmlProviderTestCase;

/**
 * @author prafull_kadam
 * Test Suite for testing all Query Interface related classes.
 */
public class TestAll
{
	public static void main(String[] args)
	{
		junit.swingui.TestRunner.run(TestAll.class);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test suite for Query Interface Classes");
		//suite.addTestSuite(SqlGeneratorGenericTestCase.class);
		//suite.addTestSuite(MySqlQueryGenerator.class);
		suite.addTestSuite(CreateQueryObjectTestCase.class);
		suite.addTestSuite(HtmlProviderTestCase.class);
		return suite;
	}
}
