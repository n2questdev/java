SELECT table_name
FROM dba_tab_columns
WHERE lower(column_name)='vendor_tax_id';

SELECT table_name,owner FROM all_tables WHERE table_name='FND_MESSAGE';

SELECT * FROM USER_ERRORS;

--Following function is not working, it seems some setup is required for this to work...
SELECT fnd_message.get_string('POS', DECODE(SIGN(TRUNC(NVL(hzr.end_date,SYSDATE + 1) ) - TRUNC(SYSDATE) ), 1, 'POS_SP_STATUS_CURRENT', 'POS_SP_STATUS_INACTIVE' ) ) AS status_display
FROM DUAL;

SET serveroutput ON;


DECLARE
  ocresult        VARCHAR2(500);
  ocerrorcategory VARCHAR2(500);
  onerrorcode     NUMBER;
  ocerrorarea     VARCHAR2(500);
  ocothermessage  VARCHAR(1000);
BEGIN
  PRISM_PKG.process_prism_xmls('VENDORS', ocresult, ocerrorcategory, onerrorcode, ocerrorarea, ocothermessage);
  dbms_output.put_line(ocresult || OCERRORCATEGORY || ONERRORCODE || OCERRORAREA || OCOTHERMESSAGE);
END;


DECLARE
  fhandle UTL_FILE.FILE_TYPE;
  line VARCHAR2(2000);
BEGIN
  dbms_output.enable(buffer_size => NULL);
  fhandle := utl_file.fopen('PRISM', 'PRISM Vendors' || '_' || TO_CHAR(SYSDATE,'YYMMDD') || '.xml', 'R');
  LOOP
    UTL_FILE.GET_LINE(fhandle, line);
    IF line IS NULL THEN
      dbms_output.put_line(''); --Got empty line
    ELSE
      dbms_output.put_line(line); --Non empty line
    END IF;
  END LOOP;
EXCEPTION
WHEN no_data_found THEN
  dbms_output.put_line('No more data to read');
END;
/

SELECT * FROM prism_vendors;
INSERT
INTO prism_vendors
  (
    prism_job_id,
    vendor_number,
    vendor_id,
    vendor_site_id,
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
    addresstype,
    address1,
    address2,
    district,
    county,
    city,
    state,
    zip,
    country,
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

SELECT 28,
  v.*
FROM
  (SELECT vendor_number,
    vendor_id,
    vendor_site_id,
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
    addresstype,
    address1,
    address2,
    district,
    county,
    city,
    state,
    zip,
    country,
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

SELECT party_name,
  email_address,
  NVL(primary_phone_number,raw_phone_number) AS phone_number
FROM prism_vendor_contacts_v
WHERE --party_id     = cur_vendors.party_id
  status_display = 'Current'

SELECT REPLACE('12-121212', '-', '') FROM dual;


select WOMEN_OWNED_FLAG from ap.ap_suppliers;

select distinct MINORITY_OWNED_TYPE from ar.hz_organization_profiles;

select * from all_directories;