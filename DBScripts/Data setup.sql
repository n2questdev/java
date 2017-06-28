sqlplus /nolog;
CONNECT wpb_LMS_SYNCUSER/welcome1;
-- DataSync app sweeps this table on a defined interval and processes any record that has SYNC_STATUS as 'NEW'. So this Employee table should get a new record with SYNC_STATUS as 'NEW' whenever a change occurred to an employee in Apps table.

drop table wpb_lms_employee;

drop table wpb_lms_DataSync_Job;

drop sequence wpb_lms_DataSync_Job_ID_SEQ;
drop sequence wpb_lms_employee_ID_SEQ;


CREATE TABLE wpb_lms_Employee
  (
	ID				  NUMBER,
    FIRST_NAME_MI     VARCHAR2(100 CHAR),
    LAST_NAME         VARCHAR2(100 CHAR),
    EMPLOYEE_ID       NUMBER primary key,
    USERNAME          VARCHAR2(100 CHAR),
    EMAIL             VARCHAR2(200 CHAR),
    TEMP_PASSWORD     VARCHAR2(100 CHAR),
    DEPT              VARCHAR2(100 CHAR),
    DIVISION          VARCHAR2(100 CHAR),
    JOB_TITLE         VARCHAR2(150 CHAR),
    MANAGEMENT        VARCHAR2(100 CHAR),
    EMPLOYEE_GROUP    VARCHAR2(100 CHAR),
    EMPLOYEE_CATEGORY VARCHAR2(100 CHAR),
    TS_USER_ID        VARCHAR2(100 CHAR),
    EFFECTIVE_HIRE    DATE,
    SUPERVISOR        VARCHAR2(200 CHAR),
    SUPERVISOR_RESP   VARCHAR2(100 CHAR),
	STATUS			  VARCHAR2(20 CHAR),
    SYNC_STATUS       VARCHAR2(20 CHAR) CHECK( SYNC_STATUS IN ('NEW','SYNC_SUCCESS','SYNC_FAILURE')),
    SYNC_REASON       VARCHAR2(2000 CHAR),
    SYNC_TIMESTAMP    DATE,
    DataSync_Job_ID   NUMBER
  );
CREATE TABLE wpb_lms_DataSync_Job
  (
    DataSync_Job_ID NUMBER primary key,
    rundate         DATE,
    total           NUMBER,
    failed          NUMBER
  );
CREATE SEQUENCE wpb_lms_DataSync_Job_ID_SEQ START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE wpb_lms_Employee_ID_SEQ START WITH 100 INCREMENT BY 1;

  INSERT
  INTO Employee VALUES
    (
	  wpb_lms_Employee_ID_SEQ.nextval,
      'Katelin Nicole',
      'Fry',
      7923,
      'WPB7923',
      'KFry@wpb.org',
      'Welcome1',
      'City Attorney Department',
      'City Attorney Admin',
      'Legal Assistant',
      'None',
      'RGUNCL',
      'FR',
      NULL,
      '11-APR-16',
      'Rothenburg Kimberly L',
      'NO',
      'NEW',
      NULL,
      sysdate,
      NULL
    );
  INSERT INTO wpb_lms_DataSync_Job VALUES
    (DataSync_Job_ID_SEQ.nextval, sysdate, 10,0
    );
);