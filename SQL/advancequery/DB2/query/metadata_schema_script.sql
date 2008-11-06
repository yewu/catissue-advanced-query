drop table CATEGORIAL_CLASS;
drop table CATEGORIAL_ATTRIBUTE;
drop table CATEGORY;

create table CATEGORIAL_CLASS (
   ID DECIMAL(31) not null ,
   DE_ENTITY_ID DECIMAL(31),
   PATH_FROM_PARENT_ID DECIMAL(31),
   PARENT_CATEGORIAL_CLASS_ID DECIMAL(31),
   primary key (ID)
);

create table CATEGORIAL_ATTRIBUTE (
   ID DECIMAL(31) not null,
   CATEGORIAL_CLASS_ID DECIMAL(31),
   DE_CATEGORY_ATTRIBUTE_ID DECIMAL(31),
   DE_SOURCE_CLASS_ATTRIBUTE_ID DECIMAL(31),
   primary key (ID)
);

/*for DB2  ROOT_CATEGORIAL_CLASS_ID is set to not null it may cause problem*/
create table CATEGORY (
   ID DECIMAL(31) not null ,
   DE_ENTITY_ID DECIMAL(31),
   PARENT_CATEGORY_ID DECIMAL(31),
   ROOT_CATEGORIAL_CLASS_ID DECIMAL(31) not null unique,
   primary key (ID)
);

alter table CATEGORIAL_CLASS add constraint FK9651EF32D8D56A33 foreign key (PARENT_CATEGORIAL_CLASS_ID) references CATEGORIAL_CLASS (ID);
alter table CATEGORIAL_ATTRIBUTE add constraint FK31F77B56E8CBD948 foreign key (CATEGORIAL_CLASS_ID) references CATEGORIAL_CLASS (ID);
alter table CATEGORY add constraint FK31A8ACFE2D0F63E7 foreign key (PARENT_CATEGORY_ID) references CATEGORY (ID);
alter table CATEGORY add constraint FK31A8ACFE211D9A6B foreign key (ROOT_CATEGORIAL_CLASS_ID) references CATEGORIAL_CLASS (ID);