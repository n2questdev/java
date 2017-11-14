DROP TABLE prism_contracts;

CREATE TABLE prism_contracts (
    prism_job_id      NUMBER,
    vendor_number     VARCHAR2(30 BYTE),
    vendor_id         NUMBER,
    vendor_site_id    NUMBER,
    vendor_tax_id     VARCHAR2(30 BYTE),

-- Profile
    contract_number   NUMBER,
    contract_name     VARCHAR2(2000)
--Award_Amount varchar2(15),
--Contract_Term	varchar2(50),
--Award_Date	varchar2(10),
--Start_Date	varchar2(10),
--End_Date	varchar2(10),
--Industry	varchar2(100),
--Department	varchar2(40),
--Division	varchar2(50),
--Buyer	varchar2(40),
--Status	varchar2(50),
--Specialist	varchar2(40),
--Overall_Goal	varchar2(9),
--SOW	varchar2(100),
--Address1	varchar2(100),
--Address2	varchar2(20),
--City	varchar2(100),
--State	varchar2(2),
--Zip	varchar2(10),
--County	varchar2(100),
--Residence	varchar2(100),
---- Fund source
--Fund_Source	varchar2(50),
--Source_Amount	varchar2(15),
----Goal
--Goal_Type	varchar2(20),
--Goal_Percentage	varchar2(12),
--Goal_Waiver	varchar2(100),
--Goal_Requested_Waiver	varchar2(1000),
--Goal_Granted_Waiver	varchar2(1000),
--Goal_Waiver_Reason	varchar2(1000),
---- Prime,Sub
--Prime_Taxid	varchar2(20),
--Sub_Taxid	varchar2(20),
--Sub_Start_Date	varchar2(10),
--Sub_End_Date	varchar2(10),
--Sub_Status	varchar2(20),
--Sub_Original_Amount	varchar2(30),
--Sub_Commitment_Amount varchar2(30),	
--Sub_Percentage varchar2(20),	
--Sub_SOW	varchar2(100),
--Sub_Involvement	varchar2(100),
--Sub_Involvement_Percentage varchar2(100),
--Sub_Retainage	varchar2(100),
--Subbing_to_Taxid1	varchar2(20),
--Subbing_to_Taxid2	varchar2(20),
--Sub_Role	varchar2(20),
--
---- PO
--PO_Number	varchar2(20),
--PO_Issue_Date	varchar2(10),
--PO_Amount	varchar2(18),
--POSOW	varchar2(100),
--Change_Order_Number	varchar2(20),
--Change_Order_Date	varchar2(10),
--Change_Order_Amount	varchar2(18),
--Change_Order_SOW	varchar2(1000),
--Compliance_Required	varchar2(1000),
---- Projects
--Project_Number	varchar2(150),
--Project_Name	varchar2(500),
--Project_Type	varchar2(255),
--Projects_Status	varchar2(50),
---- Master Agreement
--MA_Number	varchar2(255),
--MA_Name	varchar2(255),
--MA_Award_Amount	varchar2(20),
--MA_Overall_Goal	varchar2(9),
--MA_Type	varchar2(100),
--MA_Start_Date	varchar2(10),
--MA_End_Date	varchar2(10)
--constraint pk_prism_contracts primary key (Contract_Number)
);