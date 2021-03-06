--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'COMMONS_GRAPH') DROP TABLE COMMONS_GRAPH;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'COMMONS_GRAPH_EDGE') DROP TABLE COMMONS_GRAPH_EDGE;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'COMMONS_GRAPH_TO_EDGES') DROP TABLE COMMONS_GRAPH_TO_EDGES;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'COMMONS_GRAPH_TO_VERTICES') DROP TABLE COMMONS_GRAPH_TO_VERTICES;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY') DROP TABLE QUERY;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_ARITHMETIC_OPERAND') DROP TABLE QUERY_ARITHMETIC_OPERAND;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_BASEEXPR_TO_CONNECTORS') DROP TABLE QUERY_BASEEXPR_TO_CONNECTORS;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_BASE_EXPRESSION') DROP TABLE QUERY_BASE_EXPRESSION;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_BASE_EXPR_OPND') DROP TABLE QUERY_BASE_EXPR_OPND;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_CONDITION') DROP TABLE QUERY_CONDITION;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_CONDITION_VALUES') DROP TABLE QUERY_CONDITION_VALUES;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_CONNECTOR') DROP TABLE QUERY_CONNECTOR;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_CONSTRAINTS') DROP TABLE QUERY_CONSTRAINTS;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_CONSTRAINT_TO_EXPR') DROP TABLE QUERY_CONSTRAINT_TO_EXPR;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_CUSTOM_FORMULA') DROP TABLE QUERY_CUSTOM_FORMULA;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_EXPRESSION') DROP TABLE QUERY_EXPRESSION;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_FORMULA_RHS') DROP TABLE QUERY_FORMULA_RHS;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_INTER_MODEL_ASSOCIATION') DROP TABLE QUERY_INTER_MODEL_ASSOCIATION;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_INTRA_MODEL_ASSOCIATION') DROP TABLE QUERY_INTRA_MODEL_ASSOCIATION;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_JOIN_GRAPH') DROP TABLE QUERY_JOIN_GRAPH;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_MODEL_ASSOCIATION') DROP TABLE QUERY_MODEL_ASSOCIATION;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_OPERAND') DROP TABLE QUERY_OPERAND;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_OUTPUT_ATTRIBUTE') DROP TABLE QUERY_OUTPUT_ATTRIBUTE;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_OUTPUT_TERM') DROP TABLE QUERY_OUTPUT_TERM;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_PARAMETER') DROP TABLE QUERY_PARAMETER;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_PARAMETERIZED_QUERY') DROP TABLE QUERY_PARAMETERIZED_QUERY;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_QUERY_ENTITY') DROP TABLE QUERY_QUERY_ENTITY;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_RULE_COND') DROP TABLE QUERY_RULE_COND;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_SUBEXPR_OPERAND') DROP TABLE QUERY_SUBEXPR_OPERAND;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_TO_OUTPUT_TERMS') DROP TABLE QUERY_TO_OUTPUT_TERMS;
--IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'QUERY_TO_PARAMETERS') DROP TABLE QUERY_TO_PARAMETERS;


create table COMMONS_GRAPH (IDENTIFIER bigint not null identity);
alter table COMMONS_GRAPH add constraint PK_COMMONS_GRAPH_ID primary key (IDENTIFIER);

create table COMMONS_GRAPH_EDGE (IDENTIFIER bigint not null identity, SOURCE_VERTEX_CLASS varchar(255), SOURCE_VERTEX_ID bigint, TARGET_VERTEX_CLASS varchar(255), TARGET_VERTEX_ID bigint, EDGE_CLASS varchar(255), EDGE_ID bigint);
alter table COMMONS_GRAPH_EDGE add constraint PK_COMMONS_GRAPH_EDGE_ID primary key (IDENTIFIER);

create table COMMONS_GRAPH_TO_EDGES (GRAPH_ID bigint not null, EDGE_ID bigint not null unique);
alter table COMMONS_GRAPH_TO_EDGES add constraint PK_COMMONS_GPH_ID_TO_EDGES_ID primary key (GRAPH_ID, EDGE_ID);

create table COMMONS_GRAPH_TO_VERTICES (GRAPH_ID bigint not null, VERTEX_CLASS varchar(255), VERTEX_ID bigint);

create table QUERY (IDENTIFIER bigint not null identity, CONSTRAINTS_ID bigint);
alter table QUERY add constraint PK_QUERY_ID primary key (IDENTIFIER);

create table QUERY_ARITHMETIC_OPERAND (IDENTIFIER bigint not null, LITERAL varchar(255), TERM_TYPE varchar(255), DATE_LITERAL smalldatetime, TIME_INTERVAL varchar(255), DE_ATTRIBUTE_ID bigint, EXPRESSION_ID bigint);
alter table QUERY_ARITHMETIC_OPERAND add constraint PK_QRY_ARITHMETIC_OPERAND_ID primary key (IDENTIFIER);

create table QUERY_BASEEXPR_TO_CONNECTORS (BASE_EXPRESSION_ID bigint not null, CONNECTOR_ID bigint not null, POSITION integer not null);
alter table QUERY_BASEEXPR_TO_CONNECTORS add constraint PK_QRY_BASEEXP_ID_POS_TO_CONN primary key (BASE_EXPRESSION_ID, POSITION);

create table QUERY_BASE_EXPRESSION (IDENTIFIER bigint not null identity, EXPR_TYPE varchar(255) not null);
alter table QUERY_BASE_EXPRESSION add constraint PK_QUERY_BASE_EXPRESSION_ID primary key (IDENTIFIER);

create table QUERY_BASE_EXPR_OPND (BASE_EXPRESSION_ID bigint not null, OPERAND_ID bigint not null, POSITION integer not null);
alter table QUERY_BASE_EXPR_OPND add constraint PK_QRY_BASE_EXPR_ID_POS_OPND primary key (BASE_EXPRESSION_ID, POSITION);

create table QUERY_CONDITION (IDENTIFIER bigint not null identity, ATTRIBUTE_ID bigint not null, RELATIONAL_OPERATOR varchar(255));
alter table QUERY_CONDITION add constraint PK_QUERY_CONDITION_ID primary key (IDENTIFIER);

create table QUERY_CONDITION_VALUES (CONDITION_ID bigint not null, VALUE varchar(255), POSITION integer not null);
alter table QUERY_CONDITION_VALUES add constraint PK_QRY_COND_VALS_COND_ID_POS primary key (CONDITION_ID, POSITION);

create table QUERY_CONNECTOR (IDENTIFIER bigint not null identity, OPERATOR varchar(255), NESTING_NUMBER integer);
alter table QUERY_CONNECTOR add constraint PK_QUERY_CONNECTOR_ID primary key (IDENTIFIER);

create table QUERY_CONSTRAINTS (IDENTIFIER bigint not null identity, QUERY_JOIN_GRAPH_ID bigint);
alter table QUERY_CONSTRAINTS add constraint PK_QUERY_CONSTRAINTS_ID primary key (IDENTIFIER);

create table QUERY_CONSTRAINT_TO_EXPR (CONSTRAINT_ID bigint not null, EXPRESSION_ID bigint not null unique);
alter table QUERY_CONSTRAINT_TO_EXPR add constraint PK_QRY_CONST_ID_TO_EXPR_ID primary key (CONSTRAINT_ID, EXPRESSION_ID);

create table QUERY_CUSTOM_FORMULA (IDENTIFIER bigint not null, OPERATOR varchar(255), LHS_TERM_ID bigint);
alter table QUERY_CUSTOM_FORMULA add constraint PK_QUERY_CUSTOM_FORMULA_ID primary key (IDENTIFIER);

create table QUERY_EXPRESSION (IDENTIFIER bigint not null, IS_IN_VIEW BIT, IS_VISIBLE BIT, UI_EXPR_ID integer, QUERY_ENTITY_ID bigint);
alter table QUERY_EXPRESSION add constraint PK_QUERY_EXPRESSION_ID primary key (IDENTIFIER);

create table QUERY_FORMULA_RHS (CUSTOM_FORMULA_ID bigint not null, RHS_TERM_ID bigint not null, POSITION integer not null);
alter table QUERY_FORMULA_RHS add constraint PK_QRY_FORMU_ID_POS_RHS_CUST primary key (CUSTOM_FORMULA_ID, POSITION);

create table QUERY_INTER_MODEL_ASSOCIATION (IDENTIFIER bigint not null, SOURCE_SERVICE_URL text not null, TARGET_SERVICE_URL text not null, SOURCE_ATTRIBUTE_ID bigint not null, TARGET_ATTRIBUTE_ID bigint not null);
alter table QUERY_INTER_MODEL_ASSOCIATION add constraint PK_QUERY_INTER_MODEL_ASSO_ID primary key (IDENTIFIER);

create table QUERY_INTRA_MODEL_ASSOCIATION (IDENTIFIER bigint not null, DE_ASSOCIATION_ID bigint not null);
alter table QUERY_INTRA_MODEL_ASSOCIATION add constraint PK_QUERY_INTRA_MODEL_ASSO_ID primary key (IDENTIFIER);

create table QUERY_JOIN_GRAPH (IDENTIFIER bigint not null identity, COMMONS_GRAPH_ID bigint);
alter table QUERY_JOIN_GRAPH add constraint PK_QUERY_JOIN_GRAPH_ID primary key (IDENTIFIER);

create table QUERY_MODEL_ASSOCIATION (IDENTIFIER bigint not null identity);
alter table QUERY_MODEL_ASSOCIATION add constraint PK_QUERY_MODEL_ASSOCIATION_ID primary key (IDENTIFIER);

create table QUERY_OPERAND (IDENTIFIER bigint not null identity, OPND_TYPE varchar(255) not null);
alter table QUERY_OPERAND add constraint PK_QUERY_OPERAND_ID primary key (IDENTIFIER);

create table QUERY_OUTPUT_ATTRIBUTE (IDENTIFIER bigint not null identity, EXPRESSION_ID bigint, ATTRIBUTE_ID bigint not null, PARAMETERIZED_QUERY_ID bigint, POSITION integer);
alter table QUERY_OUTPUT_ATTRIBUTE add constraint PK_QUERY_OUTPUT_ATTRIBUTE_ID primary key (IDENTIFIER);

create table QUERY_OUTPUT_TERM (IDENTIFIER bigint not null identity, NAME varchar(255), TIME_INTERVAL varchar(255), TERM_ID bigint);
alter table QUERY_OUTPUT_TERM add constraint PK_QUERY_OUTPUT_TERM_ID primary key (IDENTIFIER);

create table QUERY_PARAMETER (IDENTIFIER bigint not null identity, NAME varchar(255), OBJECT_CLASS varchar(255), OBJECT_ID bigint);
alter table QUERY_PARAMETER add constraint PK_QUERY_PARAMETER_ID primary key (IDENTIFIER);

create table QUERY_PARAMETERIZED_QUERY (IDENTIFIER bigint not null, QUERY_NAME varchar(255), DESCRIPTION text);
alter table QUERY_PARAMETERIZED_QUERY add constraint PK_QRY_PARAMETERIZED_QRY_ID primary key (IDENTIFIER);

create table QUERY_QUERY_ENTITY (IDENTIFIER bigint not null identity, ENTITY_ID bigint not null);
alter table QUERY_QUERY_ENTITY add constraint PK_QUERY_QUERY_ENTITY_ID primary key (IDENTIFIER);

create table QUERY_RULE_COND (RULE_ID bigint not null, CONDITION_ID bigint not null, POSITION integer not null);
alter table QUERY_RULE_COND add constraint PK_QRY_RULE_COND_RULE_ID_POS primary key (RULE_ID, POSITION);

create table QUERY_SUBEXPR_OPERAND (IDENTIFIER bigint not null, EXPRESSION_ID bigint);
alter table QUERY_SUBEXPR_OPERAND add constraint PK_QUERY_SUBEXPR_OPERAND_ID primary key (IDENTIFIER);

create table QUERY_TO_OUTPUT_TERMS (QUERY_ID bigint not null, OUTPUT_TERM_ID bigint not null unique, POSITION integer not null);
alter table QUERY_TO_OUTPUT_TERMS add constraint PK_QRY_TO_OP_TERMS_QRY_ID_POS primary key (QUERY_ID, POSITION);

create table QUERY_TO_PARAMETERS (QUERY_ID bigint not null, PARAMETER_ID bigint not null unique, POSITION integer not null);
alter table QUERY_TO_PARAMETERS add constraint PK_QRY_TO_PARAMS_QRY_ID_POS primary key (QUERY_ID, POSITION);


alter table COMMONS_GRAPH_TO_EDGES add constraint FK_COMM_GPH_TO_EDGES_GPH_ID foreign key (GRAPH_ID) references COMMONS_GRAPH;
alter table COMMONS_GRAPH_TO_EDGES add constraint FK_COMM_GPH_TO_EDGES_EDGE_ID foreign key (EDGE_ID) references COMMONS_GRAPH_EDGE;
alter table COMMONS_GRAPH_TO_VERTICES add constraint FK_COMM_GPH_TO_VRTICES_GPH_ID foreign key (GRAPH_ID) references COMMONS_GRAPH;
alter table QUERY add constraint FK_QUERY_CONSTRAINTS_ID foreign key (CONSTRAINTS_ID) references QUERY_CONSTRAINTS;
alter table QUERY_ARITHMETIC_OPERAND add constraint FK_QRY_ARITH_OPEND_IDFK_IER1 foreign key (IDENTIFIER) references QUERY_OPERAND;
alter table QUERY_ARITHMETIC_OPERAND add constraint FK_QRY_ARITH_OPEND_IDFK_IER2 foreign key (IDENTIFIER) references QUERY_OPERAND;
alter table QUERY_ARITHMETIC_OPERAND add constraint FK_QRY_ARITH_OPEND_IDFK_IER3 foreign key (IDENTIFIER) references QUERY_OPERAND;
alter table QUERY_ARITHMETIC_OPERAND add constraint FK_QRY_ARITH_OPEND_IDFK_IER4 foreign key (IDENTIFIER) references QUERY_OPERAND;
alter table QUERY_ARITHMETIC_OPERAND add constraint FK_QRY_ARITH_OPEND_IDFK_IER5 foreign key (IDENTIFIER) references QUERY_OPERAND;
alter table QUERY_ARITHMETIC_OPERAND add constraint FK_QUERY_ARITH_OPEND_EXP_ID foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_BASEEXPR_TO_CONNECTORS add constraint FK_QRY_BASEEXP_TO_CONS_CON_ID foreign key (CONNECTOR_ID) references QUERY_CONNECTOR;
alter table QUERY_BASEEXPR_TO_CONNECTORS add constraint FK_QRY_BASEEXP_ID_TO_CONS foreign key (BASE_EXPRESSION_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_BASE_EXPR_OPND add constraint FK_QRY_BASE_EXP_OPND_OPEND_ID foreign key (OPERAND_ID) references QUERY_OPERAND;
alter table QUERY_BASE_EXPR_OPND add constraint FK_QRY_BSE_EXP_OPND_BSE_EXPId foreign key (BASE_EXPRESSION_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_CONDITION_VALUES add constraint FK_QUERY_COND_VALUES_COND_ID foreign key (CONDITION_ID) references QUERY_CONDITION;
alter table QUERY_CONSTRAINTS add constraint FK_QRY_CONST_QRY_JOIN_GPH_ID foreign key (QUERY_JOIN_GRAPH_ID) references QUERY_JOIN_GRAPH;
alter table QUERY_CONSTRAINT_TO_EXPR add constraint FK_QRY_CONST_TO_EXP_CONST_ID foreign key (CONSTRAINT_ID) references QUERY_CONSTRAINTS;
alter table QUERY_CONSTRAINT_TO_EXPR add constraint FK_QRY_CONST_TO_EXPR_EXP_ID foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_CUSTOM_FORMULA add constraint FK_QUERY_CUSTOM_FORMULA_LHS_TERM_ID foreign key (LHS_TERM_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_CUSTOM_FORMULA add constraint FK_QUERY_CUSTOM_FORMULA_ID foreign key (IDENTIFIER) references QUERY_OPERAND;
alter table QUERY_EXPRESSION add constraint FK_QUERY_EXPRESSION_ID foreign key (IDENTIFIER) references QUERY_BASE_EXPRESSION;
alter table QUERY_EXPRESSION add constraint FK_QUERY_EXP_QRY_ENTITY_ID foreign key (QUERY_ENTITY_ID) references QUERY_QUERY_ENTITY;
alter table QUERY_FORMULA_RHS add constraint FK_QUERY_FORMULA_RHS_TERM_ID foreign key (RHS_TERM_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_FORMULA_RHS add constraint FK_QRY_FRM_RHS_CUST_FRM_ID foreign key (CUSTOM_FORMULA_ID) references QUERY_OPERAND;
alter table QUERY_INTER_MODEL_ASSOCIATION add constraint FK_QRY_INTER_MDL_ASSO_ID1 foreign key (IDENTIFIER) references QUERY_MODEL_ASSOCIATION;
alter table QUERY_INTRA_MODEL_ASSOCIATION add constraint FK_QRY_INTER_MDL_ASSO_ID2 foreign key (IDENTIFIER) references QUERY_MODEL_ASSOCIATION;
alter table QUERY_JOIN_GRAPH add constraint FK_QRY_JOIN_GPH_COMMON_GPH_ID foreign key (COMMONS_GRAPH_ID) references COMMONS_GRAPH;
alter table QUERY_OUTPUT_ATTRIBUTE add constraint FK_QRY_OP_ATB_PARAM_QRY_ID foreign key (PARAMETERIZED_QUERY_ID) references QUERY_PARAMETERIZED_QUERY;
alter table QUERY_OUTPUT_ATTRIBUTE add constraint FK_QRY_OUTPUT_ATB_EXP_ID foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_OUTPUT_TERM add constraint FK_QUERY_OUTPUT_TERM_TERM_ID foreign key (TERM_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_PARAMETERIZED_QUERY add constraint FK_QRY_PARAMETERIZED_QRY_ID foreign key (IDENTIFIER) references QUERY;
alter table QUERY_RULE_COND add constraint FK_QRY_RULE_COND_CONDITION_ID foreign key (CONDITION_ID) references QUERY_CONDITION;
alter table QUERY_RULE_COND add constraint FK_QUERY_RULE_COND_RULE_ID foreign key (RULE_ID) references QUERY_OPERAND;
alter table QUERY_SUBEXPR_OPERAND add constraint FK_QRY_SUBEXPR_OPERAND_ID foreign key (IDENTIFIER) references QUERY_OPERAND;
alter table QUERY_SUBEXPR_OPERAND add constraint FK_QRY_SUBEXPR_OPERAND_EXP_ID foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION;
alter table QUERY_TO_OUTPUT_TERMS add constraint FK_QRY_TO_OP_TRMS_OP_TRM_ID foreign key (OUTPUT_TERM_ID) references QUERY_OUTPUT_TERM;
alter table QUERY_TO_OUTPUT_TERMS add constraint FK_QRY_TO_OUTPUT_TERMS_QRY_ID foreign key (QUERY_ID) references QUERY;
alter table QUERY_TO_PARAMETERS add constraint FK_QUERY_TO_PARAMS_PARAM_ID foreign key (PARAMETER_ID) references QUERY_PARAMETER;
alter table QUERY_TO_PARAMETERS add constraint FK_QRY_TO_PARAMETERS_QRY_ID foreign key (QUERY_ID) references QUERY_PARAMETERIZED_QUERY;
