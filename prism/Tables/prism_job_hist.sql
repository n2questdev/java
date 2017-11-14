DROP TABLE prism_job_hist;
DROP INDEX idx_prism_job_date;

CREATE TABLE prism_job_hist (
    job_id          NUMBER,
    seq_num         NUMBER,
    job_date        DATE,
    job_name        VARCHAR2(50),
    start_time      TIMESTAMP(6),
    end_time        TIMESTAMP(6),
    status          VARCHAR2(10),
    status_detail   VARCHAR2(2000),
    CONSTRAINT pk_prism_job_hist PRIMARY KEY ( job_id,
    seq_num )
);

CREATE INDEX idx_prism_job_date ON
    prism_job_hist ( job_date );