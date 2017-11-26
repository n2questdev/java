DROP PACKAGE PRISM_PKG;
CREATE OR REPLACE PACKAGE prism_pkg
AS
  PROCEDURE process_prism_xmls(
      p_file_type IN VARCHAR2,
      ocresult OUT VARCHAR2,
      ocerrorcategory OUT VARCHAR2,
      onerrorcode OUT NUMBER,
      ocerrorarea OUT VARCHAR2,
      ocothermessage OUT VARCHAR2 );
  PROCEDURE process_vendors_xml(
      p_job_id       IN NUMBER,
      p_max_job_date IN DATE,
      ocresult OUT VARCHAR2,
      ocerrorcategory OUT VARCHAR2,
      onerrorcode OUT NUMBER,
      ocerrorarea OUT VARCHAR2,
      ocothermessage OUT VARCHAR2 );
--  PROCEDURE process_contracts_xml(
--      p_job_id       IN NUMBER,
--      p_max_job_date IN DATE,
--      ocresult OUT VARCHAR2,
--      ocerrorcategory OUT VARCHAR2,
--      onerrorcode OUT NUMBER,
--      ocerrorarea OUT VARCHAR2,
--      ocothermessage OUT VARCHAR2 );
--  PROCEDURE process_compliance_xml(
--      p_job_id       IN NUMBER,
--      p_max_job_date IN DATE,
--      ocresult OUT VARCHAR2,
--      ocerrorcategory OUT VARCHAR2,
--      onerrorcode OUT NUMBER,
--      ocerrorarea OUT VARCHAR2,
--      ocothermessage OUT VARCHAR2 );
END prism_pkg;
/
CREATE OR REPLACE PACKAGE BODY prism_pkg
AS
  PROCEDURE save_prism_job_hist(
      p_dml           IN VARCHAR2,
      p_job_id        IN prism_job_hist.job_id%TYPE,
      p_seq_num       IN prism_job_hist.seq_num%TYPE,
      p_job_date      IN prism_job_hist.job_date%TYPE,
      p_job_name      IN prism_job_hist.job_name%TYPE,
      p_start_time    IN prism_job_hist.start_time%TYPE,
      p_end_time      IN prism_job_hist.end_time%TYPE,
      p_status        IN prism_job_hist.status%TYPE,
      p_status_detail IN prism_job_hist.status_detail%TYPE )
  IS
    PRAGMA autonomous_transaction;
  BEGIN
    IF p_dml = 'I' THEN
      INSERT
      INTO prism_job_hist
        (
          job_id,
          seq_num,
          job_date,
          job_name,
          start_time,
          status
        )
        VALUES
        (
          p_job_id,
          p_seq_num,
          p_job_date,
          p_job_name,
          p_start_time,
          p_status
        );
    ELSIF p_dml = 'U' THEN
      UPDATE prism_job_hist
      SET end_time    = p_end_time,
        status        = p_status,
        status_detail = p_status_detail
      WHERE job_id    = p_job_id
      AND seq_num     = p_seq_num;
    END IF;
    COMMIT;
  END save_prism_job_hist;
  PROCEDURE process_prism_xmls(
      p_file_type IN VARCHAR2,
      ocresult OUT VARCHAR2,
      ocerrorcategory OUT VARCHAR2,
      onerrorcode OUT NUMBER,
      ocerrorarea OUT VARCHAR2,
      ocothermessage OUT VARCHAR2 )
  IS
    v_job_id   NUMBER;
    v_job_date DATE;
  BEGIN
    BEGIN
      SELECT MAX(job_date) INTO v_job_date FROM prism_job_hist;
    EXCEPTION
    WHEN OTHERS THEN
      v_job_date := TRUNC(SYSDATE);
    END;
    DELETE prism_job_hist;
    DELETE prism_vendors; -- Change accordingly
    DELETE prism_contracts;
    DELETE prism_compliance;
    COMMIT;
    IF p_file_type = 'ALL' THEN
      v_job_id    := prism_job_id_seq.nextval;
      process_vendors_xml(v_job_id,v_job_date,ocresult,ocerrorcategory,onerrorcode,ocerrorarea,ocothermessage);
--      process_contracts_xml(v_job_id,v_job_date,ocresult,ocerrorcategory,onerrorcode,ocerrorarea,ocothermessage);
--      process_compliance_xml(v_job_id,v_job_date,ocresult,ocerrorcategory,onerrorcode,ocerrorarea,ocothermessage);
      COMMIT;
    ELSIF p_file_type = 'VENDORS' THEN
      v_job_id       := prism_job_id_seq.nextval;
      process_vendors_xml(v_job_id,v_job_date,ocresult,ocerrorcategory,onerrorcode,ocerrorarea,ocothermessage);
      COMMIT;
--    ELSIF p_file_type = 'CONTRACTS' THEN
--      v_job_id       := prism_job_id_seq.nextval;
--      process_contracts_xml(v_job_id,v_job_date,ocresult,ocerrorcategory,onerrorcode,ocerrorarea,ocothermessage);
--      COMMIT;
--    ELSIF p_file_type = 'COMPLIANCE' THEN
--      v_job_id       := prism_job_id_seq.nextval;
--      process_compliance_xml(v_job_id,v_job_date,ocresult,ocerrorcategory,onerrorcode,ocerrorarea,ocothermessage);
--      COMMIT;
    END IF;
  END process_prism_xmls;
  PROCEDURE process_vendors_xml(
      p_job_id       IN NUMBER,
      p_max_job_date IN DATE,
      ocresult OUT VARCHAR2,
      ocerrorcategory OUT VARCHAR2,
      onerrorcode OUT NUMBER,
      ocerrorarea OUT VARCHAR2,
      ocothermessage OUT VARCHAR2 )
  IS
    -- Standard Variables
    cunitname       VARCHAR2(255) := 'vendors';
    cversion        VARCHAR2(255) := '1.0';
    nmaxlinesize    NUMBER        := 32767;
    v_contact_count NUMBER        := 0;
    --exception variables
    eexception    EXCEPTION;
    eskiploop     EXCEPTION;
    einvalidinput EXCEPTION;
    clobBuffer    CLOB;
    cbuffer       VARCHAR2(32767);
--	  bigfatxml 	  CLOB;
    -- Unit specific Variables
    v_vendor_number prism_vendors_v.vendor_number%TYPE;
    v_vendor_id prism_vendors_v.vendor_id%TYPE;
    v_vendor_tax_id prism_vendors_v.vendor_tax_id%TYPE;
    v_vendor_name prism_vendors_v.vendor_name%TYPE;
    v_party_id prism_vendors_v.party_id%TYPE;
    v_certificate_number prism_vendors_v.certificate_number%TYPE;
    v_certificate_jurisdiction prism_vendors_v.certificate_jurisdiction%TYPE;
    v_certificate_type prism_vendors_v.certificate_type%TYPE;
    v_certificate_issued_date prism_vendors_v.certificate_issued_date%TYPE;
    v_recertification_date prism_vendors_v.recertification_date%TYPE;
    v_certificate_expiration_date prism_vendors_v.certificate_expiration_date%TYPE;
    v_date_established prism_vendors_v.date_established%TYPE;
    v_url prism_vendors_v.url%TYPE;
    v_contact_person_1_name prism_vendor_contacts_v.party_name%TYPE;
    v_contact_person_1_phone prism_vendor_contacts_v.primary_phone_number%TYPE;
    v_contact_person_1_email prism_vendor_contacts_v.email_address%TYPE;
    v_contact_person_2_name prism_vendor_contacts_v.party_name%TYPE;
    v_contact_person_2_phone prism_vendor_contacts_v.primary_phone_number%TYPE;
    v_contact_person_2_email prism_vendor_contacts_v.email_address%TYPE;
--    v_vendor_site_id prism_vendors_v.vendor_site_id%TYPE;
    v_phone prism_vendors_v.phone%TYPE;
    v_fax prism_vendors_v.fax%TYPE;
--    v_addresstype prism_vendors_v.addresstype%TYPE;
--    v_address1 prism_vendors_v.address1%TYPE;
--    v_address2 prism_vendors_v.address2%TYPE;
--    v_district prism_vendors_v.district%TYPE;
--    v_county prism_vendors_v.county%TYPE;
--    v_city prism_vendors_v.city%TYPE;
--    v_state prism_vendors_v.state%TYPE;
--    v_zip prism_vendors_v.zip%TYPE;
--    v_country prism_vendors_v.country%TYPE;
    v_cpfname prism_vendors_v.cpfname%TYPE;
    v_cplname prism_vendors_v.cplname%TYPE;
    v_race prism_vendors_v.race%TYPE;
    v_gender prism_vendors_v.gender%TYPE;
    v_structure prism_vendors_v.structure%TYPE;
    v_product_service_desc prism_vendors_v.product_service_desc%TYPE;
    v_industry prism_vendors_v.industry%TYPE;
    v_service_product prism_vendors_v.service_product%TYPE;
    v_total_employees prism_vendors_v.total_employees%TYPE;
    v_minority_employees prism_vendors_v.minority_employees%TYPE;
    v_insurance_number prism_vendors_v.insurance_number%TYPE;
    v_insurance_exp_date prism_vendors_v.insurance_exp_date%TYPE;
    v_insurance_company prism_vendors_v.insurance_company%TYPE;
    v_bus_doing_name prism_vendors_v.bus_doing_name%TYPE;
    v_market_area prism_vendors_v.market_area%TYPE;
    v_unit prism_vendors_v.unit%TYPE;
    v_major_customers prism_vendors_v.major_customers%TYPE;
    v_primary_bus_activity prism_vendors_v.primary_bus_activity%TYPE;
    v_duns_number prism_vendors_v.duns_number%TYPE;
    v_cage_number prism_vendors_v.cage_number%TYPE;
    v_annual_sales_year prism_vendors_v.annual_sales_year%TYPE;
    v_total_revenue prism_vendors_v.total_revenue%TYPE;
    ------ Add rest of fields here ------
    fxmlfile utl_file.file_type;
    --xml specific
    xdoc dbms_xmldom.domdocument;
    xrootnode dbms_xmldom.domnode;
    xcontractorsnode dbms_xmldom.domnode;
    xprocessnode dbms_xmldom.domnode;
    xcontractornode dbms_xmldom.domnode;
    xcontractoraddrnode dbms_xmldom.domnode;
    xindustriesnode dbms_xmldom.domnode;
    xcontrindustriesnode dbms_xmldom.domnode;
    xcertificationnode dbms_xmldom.domnode;
    xannualsalesnode dbms_xmldom.domnode;
    xcustomnode dbms_xmldom.domnode;
    xcurrnode dbms_xmldom.domnode;
    cxmlfilename VARCHAR2(1000);
    dsysdate     DATE := SYSDATE;
    FUNCTION appendchild(
        node      IN dbms_xmldom.domnode,
        tag_name  IN VARCHAR2,
        ement_val IN VARCHAR2 )
      RETURN dbms_xmldom.domnode
    IS
      xcurrnode dbms_xmldom.domnode;
      xcurrtextnode dbms_xmldom.domnode;
      xelement dbms_xmldom.domelement;
      xtext dbms_xmldom.domtext;
    BEGIN
      xelement      := dbms_xmldom.createelement(xdoc,tag_name);
      xcurrnode     := dbms_xmldom.appendchild(node,dbms_xmldom.makenode(xelement) );
      xtext         := dbms_xmldom.createtextnode(xdoc,ement_val);
      xcurrtextnode := dbms_xmldom.appendchild(xcurrnode,dbms_xmldom.makenode(xtext) );
      RETURN xcurrtextnode;
    END appendchild;
    
  PROCEDURE printvendordetails
  IS
    isPrimaryVendorSite VARCHAR2(10) := 'true';
  BEGIN
    xdoc                 := dbms_xmldom.newdomdocument;
    xrootnode            := dbms_xmldom.makenode(xdoc);
--    xcontractorsnode     := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Contractors') ) );
--    xprocessnode         := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Process') ) );
--    xcurrnode            := appendchild(xprocessnode,'ImportType', 'Contractor');
--    xcurrnode            := appendchild(xprocessnode,'Jurisdiction', 'City of West Palm Beach');
--    xcurrnode            := appendchild(xprocessnode,'UserName', 'rpotineni');
--    xcurrnode            := appendchild(xprocessnode,'OnErrorNotify', 'rpotineni@wpb.org');
--    xcurrnode            := appendchild(xprocessnode,'Time', TO_CHAR(SYSDATE,'MM-DD-YYYY HH24:MI') );
--    xcurrnode            := appendchild(xprocessnode,'Encrypted', 'No');
    xcontractornode      := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Contractor') ) );
    xcurrnode            := appendchild(xcontractornode,'Action','Update');
    xcurrnode            := appendchild(xcontractornode,'TaxID',v_vendor_tax_id);
    xcurrnode            := appendchild(xcontractornode,'Name',v_vendor_name);
    xcurrnode            := appendchild(xcontractornode,'DOINGBUSINESSAS',v_bus_doing_name);
    xcurrnode            := appendchild(xcontractornode,'STRUCTURE',v_structure);
    xcurrnode            := appendchild(xcontractornode,'MarketArea',v_market_area);
    xcurrnode            := appendchild(xcontractornode,'Unit',v_unit);
    xcurrnode            := appendchild(xcontractornode,'MajorCustomers',v_major_customers);
    xcurrnode            := appendchild(xcontractornode,'DateEstablished',v_date_established);
    xcurrnode            := appendchild(xcontractornode,'PrimaryBusinessActivity',v_primary_bus_activity);
    xcurrnode            := appendchild(xcontractornode,'PRODUCTSERVICE',v_product_service_desc);
    xcurrnode            := appendchild(xcontractornode,'Race',v_race);
    xcurrnode            := appendchild(xcontractornode,'Gender',v_gender);
    xcurrnode            := appendchild(xcontractornode,'Phone',v_phone);
    xcurrnode            := appendchild(xcontractornode,'Fax',v_fax);
    xcurrnode            := appendchild(xcontractornode,'URL',v_url);
    xcurrnode            := appendchild(xcontractornode,'InsuranceCompany',v_insurance_company);
    xcurrnode            := appendchild(xcontractornode,'InsuranceNumber',v_insurance_number);
    xcurrnode            := appendchild(xcontractornode,'InsuranceExpDate',v_insurance_exp_date);
    xcurrnode            := appendchild(xcontractornode,'CP1Name',v_contact_person_1_name);
    xcurrnode            := appendchild(xcontractornode,'CP1Title',NULL);
    xcurrnode            := appendchild(xcontractornode,'CP1Phone',v_contact_person_1_phone);
    xcurrnode            := appendchild(xcontractornode,'CP1CellPhone',v_contact_person_1_phone);
    xcurrnode            := appendchild(xcontractornode,'CP1Email',v_contact_person_1_email);
    xcurrnode            := appendchild(xcontractornode,'CP2Name',v_contact_person_2_name);
    xcurrnode            := appendchild(xcontractornode,'CP2Title',NULL);
    xcurrnode            := appendchild(xcontractornode,'CP2Phone',v_contact_person_2_phone);
    xcurrnode            := appendchild(xcontractornode,'CP2CellPhone',v_contact_person_2_phone);
    xcurrnode            := appendchild(xcontractornode,'CP2Email',v_contact_person_2_email);
    xcurrnode            := appendchild(xcontractornode,'VIN',v_vendor_number);
    xcurrnode            := appendchild(xcontractornode,'DUNS',v_duns_number);
    xcurrnode            := appendchild(xcontractornode,'CAGE',v_cage_number);
    xcurrnode            := appendchild(xcontractornode,'TOTALEMPLOYEES',v_total_employees);
    xcurrnode            := appendchild(xcontractornode,'MINORITYEMPLOYEES',v_minority_employees);
    
--### Repeat this per each vendor site id	
    FOR curr_vendor_site IN
    (
      SELECT DISTINCT 
		replace(substr(TRIM(replace(replace(replace(replace(pvs.area_code, ' ', ''), '-',''), '(',''), ')','')) || TRIM(replace(replace(replace(replace(pvs.phone, ' ',''), '-',''), '(',''), ')','')), 1, 3) || '-' 
		|| substr(TRIM(replace(replace(replace(replace(pvs.area_code, ' ', ''), '-',''), '(',''), ')','')) || TRIM(replace(replace(replace(replace(pvs.phone, ' ',''), '-',''), '(',''), ')','')), 4, 3) || '-' 
		|| substr(TRIM(replace(replace(replace(replace(pvs.area_code, ' ', ''), '-',''), '(',''), ')','')) || TRIM(replace(replace(replace(replace(pvs.phone, ' ',''), '-',''), '(',''), ')','')), 7, 4), '--','') AS phone,
        pvs.vendor_site_id              AS vendor_site_id, 
        pvs.address_line1               AS address1,
        pvs.address_line2               AS address2,
        pvs.province                    AS district,
        pvs.county                      AS county,
        pvs.city                        AS city,
        pvs.state                       AS state,
        pvs.zip                         AS zip,
        pvs.country                     AS country
      FROM AP.ap_suppliers pv
      INNER JOIN ap.ap_supplier_sites_all pvs
      ON ( pvs.vendor_id = pv.vendor_id ) where pv.VENDOR_ID = v_vendor_id 
    )
    LOOP
      xcontractoraddrnode  := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'ContractorAddress') ) );
      xcurrnode            := appendchild(xcontractoraddrnode,'Action','Update');
      xcurrnode            := appendchild(xcontractoraddrnode,'TaxID',v_vendor_tax_id);
      IF isPrimaryVendorSite = 'true' THEN
        xcurrnode            := appendchild(xcontractoraddrnode,'AddressType','PRIMARY');
        isPrimaryVendorSite := 'false';
      ELSE
        xcurrnode            := appendchild(xcontractoraddrnode,'AddressType','OTHER');
      END IF;
      
      xcurrnode            := appendchild(xcontractoraddrnode,'Address1',curr_vendor_site.address1);
      xcurrnode            := appendchild(xcontractoraddrnode,'Address2',curr_vendor_site.address2);
      xcurrnode            := appendchild(xcontractoraddrnode,'District',curr_vendor_site.district);
      xcurrnode            := appendchild(xcontractoraddrnode,'County',curr_vendor_site.county);
      xcurrnode            := appendchild(xcontractoraddrnode,'City',curr_vendor_site.city);
      xcurrnode            := appendchild(xcontractoraddrnode,'State',curr_vendor_site.state);
      xcurrnode            := appendchild(xcontractoraddrnode,'Zip',curr_vendor_site.zip);
      xcurrnode            := appendchild(xcontractoraddrnode,'Country',curr_vendor_site.country);
  
      xcurrnode            := appendchild(xcontractoraddrnode,'CPFName',NULL);
      xcurrnode            := appendchild(xcontractoraddrnode,'CPLName',NULL);
      xcurrnode            := appendchild(xcontractoraddrnode,'CPEmail',NULL);
      xcurrnode            := appendchild(xcontractoraddrnode,'CPPhone',curr_vendor_site.phone);
      xcurrnode            := appendchild(xcontractoraddrnode,'SITEID',curr_vendor_site.vendor_site_id);
    END LOOP;
--### Repeat this per each vendor site id	
    
-- ### Skipping following sections of the XML because Oracle Apps don't have data corresponding to below.
    IF v_industry != NULL 
      OR v_service_product != NULL THEN
      xindustriesnode      := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Industries') ) );
      xcontrindustriesnode := dbms_xmldom.appendchild(xindustriesnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'ContractorIndustry') ) );
      xcurrnode            := appendchild(xcontrindustriesnode,'Action','Update');
      xcurrnode            := appendchild(xcontrindustriesnode,'TaxID',v_vendor_tax_id);
      xcurrnode            := appendchild(xcontrindustriesnode,'Industry',v_industry);
      xcurrnode            := appendchild(xcontrindustriesnode,'Service_Product',v_service_product);
      xcurrnode            := appendchild(xcontrindustriesnode,'Codeset',NULL);
    END IF;
  
    IF v_certificate_number != NULL 
      OR v_certificate_type != NULL 
      OR v_certificate_issued_date != NULL 
      OR v_recertification_date != NULL 
      OR v_certificate_expiration_date != NULL 
      OR v_certificate_jurisdiction != NULL THEN
      xcertificationnode   := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Certification') ) );
      xcurrnode            := appendchild(xcertificationnode,'Action','Update');
      xcurrnode            := appendchild(xcertificationnode,'TaxID',v_vendor_tax_id);
      xcurrnode            := appendchild(xcertificationnode,'CertificateNumber',v_certificate_number);
      xcurrnode            := appendchild(xcertificationnode,'CertificateType',v_certificate_type);
      xcurrnode            := appendchild(xcertificationnode,'CertificateIssuedDate',v_certificate_issued_date);
      xcurrnode            := appendchild(xcertificationnode,'RecertificationDate',v_recertification_date);
      xcurrnode            := appendchild(xcertificationnode,'CertificateExpDate',v_certificate_expiration_date);
      xcurrnode            := appendchild(xcertificationnode,'CERTIFICATEJURI',v_certificate_jurisdiction);
    END IF;
  
    IF v_annual_sales_year != NULL 
      OR v_total_revenue != NULL THEN
      xannualsalesnode     := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'AnnualSales') ) );
      xcurrnode            := appendchild(xannualsalesnode,'TaxID',v_vendor_tax_id);
      xcurrnode            := appendchild(xannualsalesnode,'Action','Update');
      xcurrnode            := appendchild(xannualsalesnode,'Year',v_annual_sales_year);
      xcurrnode            := appendchild(xannualsalesnode,'TotalRevenue',v_total_revenue);
    END IF;

-- ### Skipping following sections of the XML because Oracle Apps don't have data corresponding to below.   
--    xcustomnode          := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Custom') ) );
--    xcurrnode            := appendchild(xcustomnode,'Action','Update');
--    xcurrnode            := appendchild(xcustomnode,'ContractNumber',v_vendor_tax_id);
--    xcurrnode            := appendchild(xcustomnode,'Custom1',NULL);
--    xcurrnode            := appendchild(xcustomnode,'Custom2',NULL);
--    xcurrnode            := appendchild(xcustomnode,'Custom3',NULL);
    ------ Add rest of fields here ------

    dbms_xmldom.writetobuffer(xdoc, cbuffer); --Option 1 try writing to char buffer and then to a file
    utl_file.put_line(fxmlfile, cbuffer);

--    dbms_xmldom.writetoClob(xdoc, clobBuffer); --Option 2 try writing to clob and serialize the clob to file
--    DBMS_XSLPROCESSOR.clob2file(clobBuffer, '/wpb/findev/prism/', cxmlfilename, 0);

--    dbms_xmldom.writeToFile(xdoc, '/wpb/findev/prism/' || cxmlfilename); --Option 3 try writing directly into file

    dbms_xmldom.freedocument(xdoc); -- free up the resources held by xdoc
--      EXCEPTION
--       WHEN OTHERS
--       THEN
--          ocothermessage  := ocothermessage + 'Fail_vendor:' || v_vendor_id || ', '; 

  END printvendordetails;
  
BEGIN
  save_prism_job_hist('I',p_job_id,1,TRUNC(SYSDATE),'VENDORS',systimestamp,NULL,'PEND',NULL);
  INSERT
  INTO prism_vendors
    (
      prism_job_id,
      vendor_number,
      vendor_id,
--###      vendor_site_id,
      vendor_tax_id,
      vendor_name,
      party_id,
      certificate_number,
      certificate_jurisdiction,
      certificate_type,
      certificate_issued_date,
      recertification_date,
      certificate_expiration_date,
      date_established,
      url,
      phone,
      fax,
      race,
      gender,
      structure,
      product_service_desc,
      industry,
      service_product,
      total_employees,
      minority_employees,
      insurance_number,
      insurance_exp_date,
      insurance_company,
      bus_doing_name,
      market_area,
      unit,
      major_customers,
      primary_bus_activity,
      duns_number,
      cage_number,
      annual_sales_year,
      total_revenue
    )
  SELECT p_job_id,
    v.*
  FROM
    (SELECT vendor_number,
      vendor_id,
--###      vendor_site_id, --NULL,
      vendor_tax_id,
      vendor_name,
      party_id,
      certificate_number,
      certificate_jurisdiction,
      certificate_type,
      certificate_issued_date,
      recertification_date,
      certificate_expiration_date,
      date_established,
      url,
      phone,
      fax,
      race,
      gender,
      structure,
      product_service_desc,
      industry,
      service_product,
      total_employees,
      minority_employees,
      insurance_number,
      insurance_exp_date,
      insurance_company,
      bus_doing_name,
      market_area,
      unit,
      major_customers,
      primary_bus_activity,
      duns_number,
      cage_number,
      annual_sales_year,
      total_revenue
    FROM prism_vendors_v
      --      minus
      --    select vendor_number,vendor_id,vendor_site_id,vendor_tax_id,vendor_name,party_id,certificate_number,certificate_jurisdiction,certificate_type,certificate_issued_date,recertification_date,certificate_expiration_date,date_established,url,phone,
      --           fax,addresstype,address1,address2,district,county,city,state,zip,country,race,gender,structure,product_service_desc,industry,service_product,total_employees,minority_employees,insurance_number,insurance_exp_date,insurance_company,bus_doing_name,
      --           market_area,unit,major_customers,primary_bus_activity,duns_number,cage_number,annual_sales_year,total_revenue from prism_vendors
      --    where prism_job_id in ( select job_id from prism_job_hist where job_date = p_max_job_date )
    ) v;
  --file name
  cxmlfilename := 'PRISM Vendors' || '_' || TO_CHAR(SYSDATE,'YYMMDD') || '.xml';
  fxmlfile     := utl_file.fopen('PRISM',cxmlfilename,'W',nmaxlinesize);
  -- print PI in the XML first
--  --dbms_lob.append(bigfatxml, '<?xml version="1.0" encoding="utf-8"?>');
  utl_file.put_line(fxmlfile,'<?xml version="1.0" encoding="UTF-8"?>');
    utl_file.put_line(fxmlfile,'<Contractors>');
      utl_file.put_line(fxmlfile,'  <Process>');
        utl_file.put_line(fxmlfile,'    <ImportType>Contractor></ImportType>');
        utl_file.put_line(fxmlfile,'    <Jurisdiction>City of West Palm Beach</Jurisdiction>');
        utl_file.put_line(fxmlfile,'    <UserName>rpotineni</UserName>');
        utl_file.put_line(fxmlfile,'    <OnErrorNotify>rpotineni@wpb.org</OnErrorNotify>');
        utl_file.put_line(fxmlfile,'    <Time>' || TO_CHAR(SYSDATE,'MM-DD-YYYY HH24:MI') || '</Time>' );
        utl_file.put_line(fxmlfile,'    <Encrypted>No</Encrypted>');
      utl_file.put_line(fxmlfile,'  </Process>'); 
  -- Iterate through all vendors
  FOR cur_vendors IN
  ( SELECT * FROM prism_vendors WHERE prism_job_id = p_job_id
  )
  LOOP
    v_vendor_number               := cur_vendors.vendor_number;
    v_vendor_id                   := cur_vendors.vendor_id;
--###    v_vendor_site_id              := cur_vendors.vendor_site_id;
    v_vendor_tax_id               := cur_vendors.vendor_tax_id;
    v_vendor_name                 := cur_vendors.vendor_name;
    v_party_id                    := cur_vendors.party_id;
    v_certificate_number          := cur_vendors.certificate_number;
    v_certificate_jurisdiction    := cur_vendors.certificate_jurisdiction;
    v_certificate_type            := cur_vendors.certificate_type;
    v_certificate_issued_date     := cur_vendors.certificate_issued_date;
    v_recertification_date        := cur_vendors.recertification_date;
    v_certificate_expiration_date := cur_vendors.certificate_expiration_date;
    v_date_established            := cur_vendors.date_established;
    v_url                         := cur_vendors.url;
    v_phone                       := cur_vendors.phone;
    v_fax                         := cur_vendors.fax;
    v_race                        := cur_vendors.race;
    v_gender                      := cur_vendors.gender;
    v_structure                   := cur_vendors.structure;
    v_product_service_desc        := cur_vendors.product_service_desc;
    v_industry                    := cur_vendors.industry;
    v_service_product             := cur_vendors.service_product;
    v_total_employees             := cur_vendors.total_employees;
    v_minority_employees          := cur_vendors.minority_employees;
    v_insurance_number            := cur_vendors.insurance_number;
    v_insurance_exp_date          := cur_vendors.insurance_exp_date;
    v_insurance_company           := cur_vendors.insurance_company;
    v_bus_doing_name              := cur_vendors.bus_doing_name;
    v_market_area                 := cur_vendors.market_area;
    v_unit                        := cur_vendors.unit;
    v_major_customers             := cur_vendors.major_customers;
    v_primary_bus_activity        := cur_vendors.primary_bus_activity;
    v_duns_number                 := cur_vendors.duns_number;
    v_cage_number                 := cur_vendors.cage_number;
    v_annual_sales_year           := cur_vendors.annual_sales_year;
    v_total_revenue               := cur_vendors.total_revenue;
    ------ Add rest of fields here ------
    FOR cur_ven_contacts IN
    (SELECT party_name,
      email_address,
      NVL(primary_phone_number,raw_phone_number) AS phone_number
    FROM prism_vendor_contacts_v
    WHERE party_id     = cur_vendors.party_id
    AND status_display = 'Current'
    )
    LOOP
      IF v_contact_count          = 0 THEN
        v_contact_person_1_name  := cur_ven_contacts.party_name;
        v_contact_person_1_phone := cur_ven_contacts.phone_number;
        v_contact_person_1_email := cur_ven_contacts.email_address;
        UPDATE prism_vendors
        SET contact_person_1_name = v_contact_person_1_name,
          contact_person_1_phone  = v_contact_person_1_phone,
          contact_person_1_email  = v_contact_person_1_email
        WHERE prism_job_id        = p_job_id
        AND vendor_number         = v_vendor_number
        AND vendor_id             = v_vendor_id;
--        AND vendor_site_id        = v_vendor_site_id;
      END IF;
      IF v_contact_count          = 1 THEN
        v_contact_person_2_name  := cur_ven_contacts.party_name;
        v_contact_person_2_phone := cur_ven_contacts.phone_number;
        v_contact_person_2_email := cur_ven_contacts.email_address;
        UPDATE prism_vendors
        SET contact_person_2_name = v_contact_person_2_name,
          contact_person_2_phone  = v_contact_person_2_phone,
          contact_person_2_email  = v_contact_person_2_email
        WHERE prism_job_id        = p_job_id
        AND vendor_number         = v_vendor_number
        AND vendor_id             = v_vendor_id;
--        AND vendor_site_id        = v_vendor_site_id;
      END IF;
      v_contact_count := v_contact_count + 1;
      EXIT
    WHEN v_contact_count = 2;
    END LOOP;
    
    -- call inner procedure to print this vendor into XML
    printvendordetails;
    v_contact_count := 0;
  END LOOP;
-- Close XML File
utl_file.put_line(fxmlfile,'</Contractors>');
utl_file.fflush(fxmlfile);

utl_file.fclose(fxmlfile);
save_prism_job_hist('U',p_job_id,1,TRUNC(SYSDATE),'VENDORS',NULL,systimestamp,'END',NULL);
EXCEPTION
WHEN OTHERS THEN
  ocresult        := 'ERROR';
  ocerrorcategory := 'OTHER';
  onerrorcode     := SQLCODE;
  ocothermessage  := SUBSTR(ocresult || ':Oracle:' || sqlerrm || dbms_utility.format_error_backtrace(),1,2000);
  IF utl_file.is_open(fxmlfile) THEN
    utl_file.fclose(fxmlfile);
  END IF;
  save_prism_job_hist('U',p_job_id,1,TRUNC(SYSDATE),'VENDORS',NULL,systimestamp,'END',ocothermessage);
  ROLLBACK;
END process_vendors_xml;
--
--
--PROCEDURE process_contracts_xml(
--    p_job_id       IN NUMBER,
--    p_max_job_date IN DATE,
--    ocresult OUT VARCHAR2,
--    ocerrorcategory OUT VARCHAR2,
--    onerrorcode OUT NUMBER,
--    ocerrorarea OUT VARCHAR2,
--    ocothermessage OUT VARCHAR2 )
--IS
--  -- Standard Variables
--  cunitname    VARCHAR2(255) := 'contracts';
--  cversion     VARCHAR2(255) := '1.0';
--  nmaxlinesize NUMBER        := 32767;
--  --exception variables
--  eexception    EXCEPTION;
--  eskiploop     EXCEPTION;
--  einvalidinput EXCEPTION;
--  cbuffer       VARCHAR2(32767);
--  bigfatxml 	CLOB;
--
--  -- Unit specific Variables
--  v_vendor_number prism_vendors_v.vendor_number%TYPE;
--  v_vendor_id prism_vendors_v.vendor_id%TYPE;
--  v_vendor_site_id prism_vendors_v.vendor_site_id%TYPE;
--  v_vendor_tax_id prism_vendors_v.vendor_tax_id%TYPE;
--  v_vendor_name prism_vendors_v.vendor_name%TYPE;
--  v_contract_number prism_contracts_payments_v.contract_number%TYPE;
--  v_contract_name prism_contracts_payments_v.contract_name%TYPE;
--  ------ Add rest of fields here ------
--  fxmlfile utl_file.file_type;
--  --xml specific
--  xdoc dbms_xmldom.domdocument;
--  xrootnode dbms_xmldom.domnode;
--  xcontractsnode dbms_xmldom.domnode;
--  xprocessnode dbms_xmldom.domnode;
--  xcontractnode dbms_xmldom.domnode;
--  xprojectnode dbms_xmldom.domnode;
--  xmanode dbms_xmldom.domnode;
--  xprofilenode dbms_xmldom.domnode;
--  xprimesnode dbms_xmldom.domnode;
--  xprimenode dbms_xmldom.domnode;
--  xsubsnode dbms_xmldom.domnode;
--  xsubnode dbms_xmldom.domnode;
--  xgoalsnode dbms_xmldom.domnode;
--  xgoalnode dbms_xmldom.domnode;
--  xfundsourcesnode dbms_xmldom.domnode;
--  xfundsourcenode dbms_xmldom.domnode;
--  xposnode dbms_xmldom.domnode;
--  xponode dbms_xmldom.domnode;
--  xcosnode dbms_xmldom.domnode;
--  xconode dbms_xmldom.domnode;
--  xworkflowsnode dbms_xmldom.domnode;
--  xworkflownode dbms_xmldom.domnode;
--  xcomplaintsnode dbms_xmldom.domnode;
--  xcomplaintnode dbms_xmldom.domnode;
--  xcustomnode dbms_xmldom.domnode;
--  xcurrnode dbms_xmldom.domnode;
--  cxmlfilename VARCHAR2(1000);
--  dsysdate     DATE := SYSDATE;
--  FUNCTION appendchild(
--      node      IN dbms_xmldom.domnode,
--      tag_name  IN VARCHAR2,
--      ement_val IN VARCHAR2 )
--    RETURN dbms_xmldom.domnode
--  IS
--    xcurrnode dbms_xmldom.domnode;
--    xcurrtextnode dbms_xmldom.domnode;
--    xelement dbms_xmldom.domelement;
--    xtext dbms_xmldom.domtext;
--  BEGIN
--    xelement      := dbms_xmldom.createelement(xdoc,tag_name);
--    xcurrnode     := dbms_xmldom.appendchild(node,dbms_xmldom.makenode(xelement) );
--    xtext         := dbms_xmldom.createtextnode(xdoc,ement_val);
--    xcurrtextnode := dbms_xmldom.appendchild(xcurrnode,dbms_xmldom.makenode(xtext) );
--    RETURN xcurrtextnode;
--  END appendchild;
--  PROCEDURE printcontractsdetails
--  IS
--  BEGIN
--    xdoc             := dbms_xmldom.newdomdocument;
--    xrootnode        := dbms_xmldom.makenode(xdoc);
--    xcontractsnode   := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Contracts') ) );
--    xprocessnode     := dbms_xmldom.appendchild(xcontractsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Process') ) );
--    xcurrnode        := appendchild(xprocessnode,'ImportType','Contracts');
--    xcurrnode        := appendchild(xprocessnode,'Jurisdiction','City of West Palm Beach');
--    xcurrnode        := appendchild(xprocessnode,'UserName','rpotineni');
--    xcurrnode        := appendchild(xprocessnode,'OnErrorNotify','rpotineni@wpb.org');
--    xcurrnode        := appendchild(xprocessnode,'Time',TO_CHAR(SYSDATE,'MM-DD-YYYY HH24:MI') );
--    xcurrnode        := appendchild(xprocessnode,'Encrypted','no');
--    xcontractnode    := dbms_xmldom.appendchild(xcontractsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Contract') ) );
--    xprojectnode     := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Project') ) );
--    xcurrnode        := appendchild(xprojectnode,'ProjectNumber',NULL);
--    xcurrnode        := appendchild(xprojectnode,'Action','Update');
--    xcurrnode        := appendchild(xprojectnode,'ProjectName',NULL);
--    xcurrnode        := appendchild(xprojectnode,'ProjectType',NULL);
--    xcurrnode        := appendchild(xprojectnode,'Status',NULL);
--    xmanode          := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'MasterAgreements') ) );
--    xcurrnode        := appendchild(xmanode,'MANumber',NULL);
--    xcurrnode        := appendchild(xmanode,'Action','Update');
--    xcurrnode        := appendchild(xmanode,'MAName',NULL);
--    xcurrnode        := appendchild(xmanode,'AwardAmount',NULL);
--    xcurrnode        := appendchild(xmanode,'OverGoal',NULL);
--    xcurrnode        := appendchild(xmanode,'MasterAgreementType',NULL);
--    xcurrnode        := appendchild(xmanode,'StartDate',NULL);
--    xcurrnode        := appendchild(xmanode,'EndDate',NULL);
--    xprofilenode     := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Profile') ) );
--    xcurrnode        := appendchild(xprofilenode,'Action','Update');
--    xcurrnode        := appendchild(xprofilenode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xprofilenode,'Name',v_contract_name);
--    xcurrnode        := appendchild(xprofilenode,'AwardAmount',NULL);
--    xcurrnode        := appendchild(xprofilenode,'ContractTerm',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Department',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Division',NULL);
--    xcurrnode        := appendchild(xprofilenode,'AwardDate',NULL);
--    xcurrnode        := appendchild(xprofilenode,'EndDate',NULL);
--    xcurrnode        := appendchild(xprofilenode,'StartDate',NULL);
--    xcurrnode        := appendchild(xprofilenode,'OverallGoal',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Buyer',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Industry',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Status',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Specialist',NULL);
--    xcurrnode        := appendchild(xprofilenode,'SOW',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Address1',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Address2',NULL);
--    xcurrnode        := appendchild(xprofilenode,'City',NULL);
--    xcurrnode        := appendchild(xprofilenode,'State',NULL);
--    xcurrnode        := appendchild(xprofilenode,'ZIP',NULL);
--    xcurrnode        := appendchild(xprofilenode,'County',NULL);
--    xcurrnode        := appendchild(xprofilenode,'Residence',NULL);
--    xcurrnode        := appendchild(xprofilenode,'ProjectNumber',NULL);
--    xcurrnode        := appendchild(xprofilenode,'MANumber',NULL);
--    xprimesnode      := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Primes') ) );
--    xprimenode       := dbms_xmldom.appendchild(xprimesnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Prime') ) );
--    xcurrnode        := appendchild(xprimenode,'Action','Update');
--    xcurrnode        := appendchild(xprimenode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xprimenode,'PrimeTaxid',v_vendor_tax_id);
--    xcurrnode        := appendchild(xprimenode,'Role',NULL);
--    xsubsnode        := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Subs') ) );
--    xsubnode         := dbms_xmldom.appendchild(xsubsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Sub') ) );
--    xcurrnode        := appendchild(xsubnode,'Action','Update');
--    xcurrnode        := appendchild(xsubnode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xsubnode,'PrimeTaxid',v_vendor_tax_id);
--    xcurrnode        := appendchild(xsubnode,'SubTaxid',NULL);
--    xcurrnode        := appendchild(xsubnode,'Role',NULL);
--    xcurrnode        := appendchild(xsubnode,'StartDate',NULL);
--    xcurrnode        := appendchild(xsubnode,'EndDate',NULL);
--    xcurrnode        := appendchild(xsubnode,'Status',NULL);
--    xcurrnode        := appendchild(xsubnode,'OriginalAmount',NULL);
--    xcurrnode        := appendchild(xsubnode,'CommitmentAmount',NULL);
--    xcurrnode        := appendchild(xsubnode,'Percentage',NULL);
--    xcurrnode        := appendchild(xsubnode,'SubSOW',NULL);
--    xcurrnode        := appendchild(xsubnode,'Involvement',NULL);
--    xcurrnode        := appendchild(xsubnode,'InvolvementPercent',NULL);
--    xcurrnode        := appendchild(xsubnode,'Retainage',NULL);
--    xcurrnode        := appendchild(xsubnode,'SubbingToTAXID1',NULL);
--    xcurrnode        := appendchild(xsubnode,'SubbingToTAXID2',NULL);
--    xgoalsnode       := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Goals') ) );
--    xgoalnode        := dbms_xmldom.appendchild(xgoalsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Goal') ) );
--    xcurrnode        := appendchild(xgoalnode,'Action','Update');
--    xcurrnode        := appendchild(xgoalnode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xgoalnode,'GoalType',NULL);
--    xcurrnode        := appendchild(xgoalnode,'Percentage',NULL);
--    xcurrnode        := appendchild(xgoalnode,'Waiver',NULL);
--    xcurrnode        := appendchild(xgoalnode,'RequestedWaiver',NULL);
--    xcurrnode        := appendchild(xgoalnode,'GrantedWaiver',NULL);
--    xcurrnode        := appendchild(xgoalnode,'WaiverReason',NULL);
--    xfundsourcesnode := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'FundSources') ) );
--    xfundsourcenode  := dbms_xmldom.appendchild(xfundsourcesnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'FundSource') ) );
--    xcurrnode        := appendchild(xfundsourcenode,'Action','Update');
--    xcurrnode        := appendchild(xfundsourcenode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xfundsourcenode,'Source',NULL);
--    xcurrnode        := appendchild(xfundsourcenode,'SourceAmount',NULL);
--    xposnode         := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'POs') ) );
--    xponode          := dbms_xmldom.appendchild(xposnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'PO') ) );
--    xcurrnode        := appendchild(xponode,'Action','Update');
--    xcurrnode        := appendchild(xponode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xponode,'PrimeTaxid',v_vendor_tax_id);
--    xcurrnode        := appendchild(xponode,'PONumber',NULL);
--    xcurrnode        := appendchild(xponode,'IssueDate',NULL);
--    xcurrnode        := appendchild(xponode,'POAmount',NULL);
--    xcurrnode        := appendchild(xponode,'POSOW',NULL);
--    xcosnode         := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'COs') ) );
--    xconode          := dbms_xmldom.appendchild(xcosnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'CO') ) );
--    xcurrnode        := appendchild(xconode,'Action','Update');
--    xcurrnode        := appendchild(xconode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xconode,'PrimeTaxid',v_vendor_tax_id);
--    xcurrnode        := appendchild(xconode,'ChangeOrderNumber',NULL);
--    xcurrnode        := appendchild(xconode,'ChangeOrderDate',NULL);
--    xcurrnode        := appendchild(xconode,'ChangeOrderAmount',NULL);
--    xcurrnode        := appendchild(xconode,'ChangeOrderSOW',NULL);
--    xcurrnode        := appendchild(xconode,'ComplianceRequired',NULL);
--    xworkflowsnode   := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Workflows') ) );
--    xworkflownode    := dbms_xmldom.appendchild(xworkflowsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Workflow') ) );
--    xcurrnode        := appendchild(xworkflownode,'Action','Update');
--    xcurrnode        := appendchild(xworkflownode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xworkflownode,'status',NULL);
--    xcurrnode        := appendchild(xworkflownode,'StatusChangedDate',NULL);
--    xcurrnode        := appendchild(xworkflownode,'AssignedTo',NULL);
--    xcurrnode        := appendchild(xworkflownode,'Comments',NULL);
--    xcomplaintsnode  := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Complaints') ) );
--    xcomplaintnode   := dbms_xmldom.appendchild(xcomplaintsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Complaint') ) );
--    xcurrnode        := appendchild(xcomplaintnode,'Action','Update');
--    xcurrnode        := appendchild(xcomplaintnode,'ContractNumber',v_contract_number);
--    xcurrnode        := appendchild(xcomplaintnode,'ReferenceNumber',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'TaxID',v_vendor_tax_id);
--    xcurrnode        := appendchild(xcomplaintnode,'AssignedTo',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'AssignmentDate',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'DateReceived',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'DateSubmitted',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'DateCompleted',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'Complaint',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'Complainant',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'Determination',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'ActionTaken',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'Phone',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'Address1',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'Address2',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'City',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'State',NULL);
--    xcurrnode        := appendchild(xcomplaintnode,'ZIP',NULL);
--    xcustomnode      := dbms_xmldom.appendchild(xcontractnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Custom') ) );
--    xcurrnode        := appendchild(xcustomnode,'Action','Update');
--    xcurrnode        := appendchild(xcustomnode,'ContractNumber',NULL);
--    xcurrnode        := appendchild(xcustomnode,'Custom1',NULL);
--    xcurrnode        := appendchild(xcustomnode,'Custom2',NULL);
--    xcurrnode        := appendchild(xcustomnode,'Custom3',NULL);
--    ------ Add rest of fields here ------
--    dbms_xmldom.writetobuffer(xdoc,cbuffer);
--    --dbms_lob.append(bigfatxml, cbuffer);
--    utl_file.put_line(fxmlfile,cbuffer);
--    dbms_xmldom.freedocument(xdoc);
--  END printcontractsdetails;
--BEGIN
--  save_prism_job_hist('I',p_job_id,2,TRUNC(SYSDATE),'CONTRACTS',systimestamp,NULL,'PEND',NULL);
--  INSERT
--  INTO prism_contracts
--    (
--      prism_job_id,
--      vendor_number,
--      vendor_id,
--      vendor_site_id,
--      vendor_tax_id,
--      contract_number,
--      contract_name
--    )
--  SELECT p_job_id,
--    vendor_number,
--    vendor_id,
--    vendor_site_id,
--    vendor_tax_id,
--    contract_number,
--    contract_name
--  FROM prism_contracts_payments_v
--  WHERE vendor_tax_id IS NOT NULL;
--  --file name
--  cxmlfilename := 'PRISM Contracts' || '_' || TO_CHAR(SYSDATE,'YYMMDD') || '.xml';
--  
--  -- Open the file in prism db directory which is mapped to /tmp/prism for now...
--  fxmlfile     := utl_file.fopen('PRISM',cxmlfilename,'W',nmaxlinesize);
--  --print parent tags
--  --dbms_lob.append(bigfatxml, cbuffer);
--  utl_file.put_line(fxmlfile,'<?xml version="1.0" encoding="utf-8"?>');
--  --UTL_FILE.PUT_LINE(fXMLFile, '<Vendor>');
--  FOR cur_contracts IN
--  ( SELECT * FROM prism_contracts
--  )
--  LOOP
--    v_vendor_tax_id   := cur_contracts.vendor_tax_id;
--    v_contract_number := cur_contracts.contract_number;
--    v_contract_name   := cur_contracts.contract_name;
--    ------ Add rest of fields here ------
--    printcontractsdetails;
--  END LOOP;
--  --UTL_FILE.PUT_LINE(fXMLFile, '</Vendor>');
--  -- Close XML File
--  utl_file.fclose(fxmlfile);
--  save_prism_job_hist('U',p_job_id,2,TRUNC(SYSDATE),'CONTRACTS',NULL,systimestamp,'END',NULL);
--EXCEPTION
--WHEN OTHERS THEN
--  ocresult        := 'ERROR';
--  ocerrorcategory := 'OTHER';
--  onerrorcode     := SQLCODE;
--  ocothermessage  := SUBSTR(ocresult || ':Oracle:' || sqlerrm || dbms_utility.format_error_backtrace(),1,2000);
--  IF utl_file.is_open(fxmlfile) THEN
--    utl_file.fclose(fxmlfile);
--  END IF;
--  save_prism_job_hist('U',p_job_id,2,TRUNC(SYSDATE),'CONTRACTS',NULL,systimestamp,'END',ocothermessage);
--  ROLLBACK;
--END process_contracts_xml;
--PROCEDURE process_compliance_xml(
--    p_job_id       IN NUMBER,
--    p_max_job_date IN DATE,
--    ocresult OUT VARCHAR2,
--    ocerrorcategory OUT VARCHAR2,
--    onerrorcode OUT NUMBER,
--    ocerrorarea OUT VARCHAR2,
--    ocothermessage OUT VARCHAR2 )
--IS
--  -- Standard Variables
--  cunitname    VARCHAR2(255) := 'compliance';
--  cversion     VARCHAR2(255) := '1.0';
--  nmaxlinesize NUMBER        := 32767;
--  --exception variables
--  eexception    EXCEPTION;
--  eskiploop     EXCEPTION;
--  einvalidinput EXCEPTION;
--  cbuffer       VARCHAR2(32767);
--  bigfatxml 	CLOB;
--
--  -- Unit specific Variables
--  v_vendor_tax_id prism_contracts_payments_v.vendor_tax_id%TYPE;
--  v_contract_number prism_contracts_payments_v.contract_number%TYPE;
--  v_payment_amount prism_contracts_payments_v.invoice_amount%TYPE;
--  v_payment_date prism_contracts_payments_v.invoice_payment_date%TYPE;
--  v_issue_date prism_contracts_payments_v.invoice_date%TYPE;
--  v_purchase_order_number prism_contracts_payments_v.po_num%TYPE;
--  ------ Add rest of fields here ------
--  fxmlfile utl_file.file_type;
--  --xml specific
--  xdoc dbms_xmldom.domdocument;
--  xrootnode dbms_xmldom.domnode;
--  xcompliancenode dbms_xmldom.domnode;
--  xprimepaymentsnode dbms_xmldom.domnode;
--  xprocessnode dbms_xmldom.domnode;
--  xpaymentsindustriesnode dbms_xmldom.domnode;
--  xindustriesnode dbms_xmldom.domnode;
--  xcustomfields dbms_xmldom.domnode;
--  xsubpaymentsnode dbms_xmldom.domnode;
--  xpaymentsnode dbms_xmldom.domnode;
--  xpaymentnode dbms_xmldom.domnode;
--  xcustomnode dbms_xmldom.domnode;
--  xcurrnode dbms_xmldom.domnode;
--  cxmlfilename VARCHAR2(1000);
--  dsysdate     DATE := SYSDATE;
--  
--  -- Utility function to append XML child elements
--  FUNCTION appendchild(
--      node      IN dbms_xmldom.domnode,
--      tag_name  IN VARCHAR2,
--      ement_val IN VARCHAR2 )
--    RETURN dbms_xmldom.domnode
--  IS
--    xcurrnode dbms_xmldom.domnode;
--    xcurrtextnode dbms_xmldom.domnode;
--    xelement dbms_xmldom.domelement;
--    xtext dbms_xmldom.domtext;
--  BEGIN
--    xelement      := dbms_xmldom.createelement(xdoc,tag_name);
--    xcurrnode     := dbms_xmldom.appendchild(node,dbms_xmldom.makenode(xelement) );
--    xtext         := dbms_xmldom.createtextnode(xdoc,ement_val);
--    xcurrtextnode := dbms_xmldom.appendchild(xcurrnode,dbms_xmldom.makenode(xtext) );
--    RETURN xcurrtextnode;
--  END appendchild;
--  
--  PROCEDURE printprimepayments
--  IS
--  BEGIN
--    xdoc                    := dbms_xmldom.newdomdocument;
--    xrootnode               := dbms_xmldom.makenode(xdoc);
--    xprimepaymentsnode      := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'PrimePayments') ) );
--    xprocessnode            := dbms_xmldom.appendchild(xprimepaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Process') ) );
--    xcurrnode               := appendchild(xprocessnode,'ImportType','PrimePayments');
--    xcurrnode               := appendchild(xprocessnode,'Jurisdiction','City of West Palm Beach');
--    xcurrnode               := appendchild(xprocessnode,'UserName','rpotineni');
--    xcurrnode               := appendchild(xprocessnode,'OnErrorNotify','rpotineni@wpb.org');
--    xcurrnode               := appendchild(xprocessnode,'Time',TO_CHAR(SYSDATE,'MM-DD-YYYY HH24:MI') );
--    xcurrnode               := appendchild(xprocessnode,'Encrypted','no');
--    xpaymentsnode           := dbms_xmldom.appendchild(xprimepaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Payments') ) );
--    xpaymentnode            := dbms_xmldom.appendchild(xpaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Payment') ) );
--    xcurrnode               := appendchild(xpaymentnode,'Action','Update');
--    xcurrnode               := appendchild(xpaymentnode,'PurchaseOrderNumber',v_purchase_order_number);
--    xcurrnode               := appendchild(xpaymentnode,'PrimeTaxID',v_vendor_tax_id);
--    xcurrnode               := appendchild(xpaymentnode,'ContractNumber',v_contract_number);
--    xcurrnode               := appendchild(xpaymentnode,'PaymentAmount',v_payment_amount);
--    xcurrnode               := appendchild(xpaymentnode,'PaymentDate',v_payment_date);
--    xcurrnode               := appendchild(xpaymentnode,'IssueDate',v_issue_date);
--    xcurrnode               := appendchild(xpaymentnode,'Reference',NULL);
--    xcurrnode               := appendchild(xpaymentnode,'Exempt',NULL);
--    xpaymentsindustriesnode := dbms_xmldom.appendchild(xprimepaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'PaymentsIndustries') ) );
--    -- Not setting industries data intentionally as per Prism team comments
--    xindustriesnode := dbms_xmldom.appendchild(xpaymentsindustriesnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Industries') ) );
--    xcurrnode       := appendchild(xindustriesnode,'Reference',NULL);
--    xcurrnode       := appendchild(xindustriesnode,'Action',NULL);
--    xcurrnode       := appendchild(xindustriesnode,'TaxID',NULL);
--    xcurrnode       := appendchild(xindustriesnode,'Industry',NULL);
--    xcurrnode       := appendchild(xindustriesnode,'Service_Product',NULL);
--    xcurrnode       := appendchild(xindustriesnode,'CodeSet',NULL);
--    xcustomfields   := dbms_xmldom.appendchild(xprimepaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'CustomFields') ) );
--    xcustomnode     := dbms_xmldom.appendchild(xcustomfields,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Custom') ) );
--    xcurrnode       := appendchild(xcustomnode,'Reference',NULL);
--    xcurrnode       := appendchild(xcustomnode,'Action',NULL);
--    xcurrnode       := appendchild(xcustomnode,'Custom1',NULL);
--    xcurrnode       := appendchild(xcustomnode,'Custom2',NULL);
--    xcurrnode       := appendchild(xcustomnode,'Custom3',NULL);
--    ------ Add rest of fields here ------
--    dbms_xmldom.writetobuffer(xdoc,cbuffer);
--    --dbms_lob.append(bigfatxml, cbuffer);
--    utl_file.put_line(fxmlfile,cbuffer);
--    dbms_xmldom.freedocument(xdoc);
--  END printprimepayments;
--  
--  PROCEDURE printsubpayments
--  IS
--  BEGIN
--    xdoc             := dbms_xmldom.newdomdocument;
--    xrootnode        := dbms_xmldom.makenode(xdoc);
--    xsubpaymentsnode := dbms_xmldom.appendchild(xrootnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'SubPayments') ) );
--    xprocessnode     := dbms_xmldom.appendchild(xsubpaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Process') ) );
--    --xCurrNode    := AppendChild(xProcessNode,'ImportType','PrimePayments');
--    xcurrnode     := appendchild(xprocessnode,'Jurisdiction','City of West Palm Beach');
--    xcurrnode     := appendchild(xprocessnode,'UserName','rpotineni');
--    xcurrnode     := appendchild(xprocessnode,'OnErrorNotify','rpotineni@wpb.org');
--    xcurrnode     := appendchild(xprocessnode,'Time',TO_CHAR(SYSDATE,'MM-DD-YYYY HH24:MI') );
--    xcurrnode     := appendchild(xprocessnode,'Encrypted','no');
--    xpaymentsnode := dbms_xmldom.appendchild(xsubpaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Payments') ) );
--    xpaymentnode  := dbms_xmldom.appendchild(xpaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Payment') ) );
--    xcurrnode     := appendchild(xpaymentnode,'Action','Update');
--    xcurrnode     := appendchild(xpaymentnode,'PrimeTaxID',v_vendor_tax_id);
--    xcurrnode     := appendchild(xpaymentnode,'SubTaxID',NULL);
--    xcurrnode     := appendchild(xpaymentnode,'ContractNumber',NULL);
--    xcurrnode     := appendchild(xpaymentnode,'SubRole',NULL);
--    xcurrnode     := appendchild(xpaymentnode,'AmountPaid',NULL);
--    xcurrnode     := appendchild(xpaymentnode,'DatePaid',NULL);
--    xcurrnode     := appendchild(xpaymentnode,'IssueDate',NULL);
--    xcurrnode     := appendchild(xpaymentnode,'PONumber',NULL);
--    xcurrnode     := appendchild(xpaymentnode,'InvoiceNumber',NULL);
--    xcurrnode     := appendchild(xpaymentnode,'Reference',NULL);
--    xcustomnode   := dbms_xmldom.appendchild(xpaymentsnode,dbms_xmldom.makenode(dbms_xmldom.createelement(xdoc,'Custom') ) );
--    xcurrnode     := appendchild(xcustomnode,'Reference',NULL);
--    xcurrnode     := appendchild(xcustomnode,'Action',NULL);
--    xcurrnode     := appendchild(xcustomnode,'Custom1',NULL);
--    xcurrnode     := appendchild(xcustomnode,'Custom2',NULL);
--    xcurrnode     := appendchild(xcustomnode,'Custom3',NULL);
--    ------ Add rest of fields here ------
--    dbms_xmldom.writetobuffer(xdoc,cbuffer);    
--    --dbms_lob.append(bigfatxml, cbuffer);
--    utl_file.put_line(fxmlfile,cbuffer);
--    dbms_xmldom.freedocument(xdoc);
--  END printsubpayments;
--  
--BEGIN
--  save_prism_job_hist('I',p_job_id,3,TRUNC(SYSDATE),'COMPLIANCE',systimestamp,NULL,'PEND',NULL);
--  INSERT
--  INTO prism_compliance
--    (
--      prism_job_id,
--      vendor_number,
--      vendor_id,
--      vendor_site_id,
--      vendor_tax_id,
--      contract_number,
--      payment_amount,
--      payment_date,
--      issue_date,
--      purchase_order_number
--    )
--  SELECT p_job_id,
--    vendor_number,
--    vendor_id,
--    vendor_site_id,
--    vendor_tax_id,
--    contract_number,
--    invoice_amount,
--    invoice_payment_date,
--    invoice_date,
--    po_num
--  FROM prism_contracts_payments_v
--  WHERE vendor_tax_id IS NOT NULL;
--  -- Prime Payments
--  --file name
--  cxmlfilename := 'PRISM Prime Payments' || '_' || TO_CHAR(SYSDATE,'YYMMDD') || '.xml';
--  fxmlfile     := utl_file.fopen('PRISM',cxmlfilename,'W',nmaxlinesize);
--  --print parent tags
--  --dbms_lob.append(bigfatxml, '<?xml version="1.0" encoding="utf-8"?>');
--  utl_file.put_line(fxmlfile,'<?xml version="1.0" encoding="utf-8"?>');
--  FOR cur_compliance IN
--  ( SELECT * FROM prism_compliance WHERE prism_job_id = p_job_id
--  )
--  LOOP
--    v_vendor_tax_id         := cur_compliance.vendor_tax_id;
--    v_contract_number       := cur_compliance.contract_number;
--    v_payment_amount        := cur_compliance.payment_amount;
--    v_payment_date          := cur_compliance.payment_date;
--    v_issue_date            := cur_compliance.issue_date;
--    v_purchase_order_number := cur_compliance.purchase_order_number;
--    ------ Add rest of fields here ------
--    printprimepayments;
--  END LOOP;
--  -- Close XML File
--  insert into prism_files values ('PRISM Sub Payments' || '_' || TO_CHAR(SYSDATE,'YYMMDD') || TO_CHAR(SYSDATE,'HH24MISS') || '.xml', bigfatxml);
--
--  -- Sub Payments
--  cxmlfilename := 'PRISM Sub Payments' || '_' || TO_CHAR(SYSDATE,'YYMMDD') || '.xml';
--  fxmlfile     := utl_file.fopen('PRISM',cxmlfilename,'W',nmaxlinesize);
--  --print parent tags
--  --dbms_lob.append(bigfatxml, cbuffer);  
--  utl_file.put_line(fxmlfile,'<?xml version="1.0" encoding="utf-8"?>');
--  printsubpayments;
--  -- Close XML File
--  utl_file.fclose(fxmlfile);
--  save_prism_job_hist('U',p_job_id,3,TRUNC(SYSDATE),'COMPLIANCE',NULL,systimestamp,'END',NULL);
--EXCEPTION
--WHEN OTHERS THEN
--  ocresult        := 'ERROR';
--  ocerrorcategory := 'OTHER';
--  onerrorcode     := SQLCODE;
--  ocothermessage  := SUBSTR(ocresult || ':Oracle:' || sqlerrm || dbms_utility.format_error_backtrace(),1,2000);
--  IF utl_file.is_open(fxmlfile) THEN
--    utl_file.fclose(fxmlfile);
--  END IF;
--  save_prism_job_hist('U',p_job_id,3,TRUNC(SYSDATE),'COMPLIANCE',NULL,systimestamp,'END',ocothermessage);
--  ROLLBACK;
--END process_compliance_xml;
END prism_pkg;