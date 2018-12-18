drop table DRIVERS if exists;

drop sequence DRV_SEQ if exists;

CREATE TABLE DRIVERS (DRV_ID bigint not null,DGD_GRADE_CODE varchar(10) not null,DRIVER_FORENAME varchar(30) not null,DRIVER_MIDDLENAME varchar(80),DRIVER_SURNAME varchar(40) not null,EA_ACCOUNT_CODE varchar(25) not null,EA_ACCOUNT_TYPE varchar(1) not null,EA_C_ID bigint not null,NAT_INS_NO varchar(25) not null,TITLE varchar(10) not null, VERSIONTS timestamp, primary key (DRV_ID));

create sequence DRV_SEQ;

drop table VSESSION if exists;

create table VSESSION (AUDSID bigint not null,USERNAME varchar(30) not null,primary key (AUDSID));