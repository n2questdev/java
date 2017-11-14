DROP sequence prism_job_id_seq;

CREATE SEQUENCE prism_job_id_seq MINVALUE 1 MAXVALUE 9999999999 INCREMENT BY 1 START WITH 1 NOCACHE ORDER NOCYCLE;

-- select prism_job_id_seq.nextval from dual;