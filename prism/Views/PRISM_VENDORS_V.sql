CREATE OR REPLACE FORCE VIEW prism_vendors_v AS
    SELECT 
        *
    FROM
        (
            SELECT DISTINCT -- Vendor Data(Primary Data)
                pv.segment1 AS vendor_number,
                pv.vendor_id AS vendor_id,
--                pvs.vendor_site_id AS vendor_site_id,
                DECODE(pv.organization_type_lookup_code, 'INDIVIDUAL', pv.individual_1099,
                                                        'FOREIGN INDIVIDUAL', pv.individual_1099, 
                                                                             pv.num_1099) AS vendor_tax_id,
                substr(pv.vendor_name, 1, 100) AS vendor_name, 
      -- Certification(Secondary Data)
                (
                    CASE
                        WHEN pv.attribute2 IS NOT NULL THEN pv.segment1
                        ELSE NULL
                    END
                ) AS certificate_number,
                pv.attribute8 AS certificate_jurisdiction,
                pv.attribute3 AS certificate_type,
                TO_CHAR(TO_DATE(pv.attribute6,'yyyy/mm/dd hh24:mi:ss'),'mm-dd-yyyy') AS certificate_issued_date,
                TO_CHAR(TO_DATE(pv.attribute7,'yyyy/mm/dd hh24:mi:ss') - 60,'mm-dd-yyyy') AS recertification_date,
                TO_CHAR(TO_DATE(pv.attribute7,'yyyy/mm/dd hh24:mi:ss'),'mm-dd-yyyy') AS certificate_expiration_date,
                pv.attribute2 AS is_certified,
                pv.attribute1 AS is_sb_vendor,
                pv.attribute5 AS certificate_business_location,
                nvl(org.year_established,hp.year_established) AS date_established,
                hp.url,
--                TRIM(pvs.area_code)
--                || '-'
--                || TRIM(pvs.phone) AS phone,
--                TRIM(pvs.fax_area_code)
--                || '-'
--                || TRIM(pvs.fax) AS fax,
--### Site details
--                'Primary' AS addresstype,
--                pvs.address_line1 AS address1,
--                pvs.address_line2 AS address2,
--                pvs.province AS district,
--                pvs.county,
--                pvs.city,
--                pvs.state,
--                pvs.zip,
--                pvs.country,
                NULL AS cpfname,
                NULL AS cplname,
                NULL AS race,
                NULL AS gender,
                pv.organization_type_lookup_code AS structure,
                NULL AS product_service_desc,
                NULL AS industry,
                NULL AS service_product,
                nvl(org.employees_total, hp.employees_total) AS total_employees,
                NULL AS minority_employees,
                pv.ni_number AS insurance_number,
                NULL AS insurance_exp_date,
                NULL AS insurance_company,
                hp.party_name AS bus_doing_name,
                NULL AS market_area,
                NULL AS unit,
                NULL AS major_customers,
                NULL AS primary_bus_activity,
                nvl(hp.duns_number_c, pvs.duns_number) AS duns_number,
                pvs.cage_code AS cage_number,
                NULL AS annual_sales_year,
                org.curr_fy_potential_revenue total_revenue,
      -- other fileds will go below
                hp.party_id AS party_id,
                pv.start_date_active AS start_date_active,
                pv.end_date_active AS end_date_active,
                org.minority_owned_ind AS minority_owned_ind,
                org.minority_owned_type AS minority_owned_type,
                org.woman_owned_ind AS woman_owned_ind
            FROM
                AP.ap_suppliers pv
                INNER JOIN ap.ap_supplier_sites_all pvs ON ( pvs.vendor_id = pv.vendor_id )
                LEFT OUTER JOIN AR.hz_parties hp ON ( pv.party_id = hp.party_id )
                LEFT OUTER JOIN AR.hz_organization_profiles org ON ( hp.party_id = org.party_id
                                                                  AND org.effective_end_date IS NULL )
            WHERE
                (
                    end_date_active >= trunc(SYSDATE)
                    OR end_date_active IS NULL
                )
        )
    WHERE
        vendor_tax_id IS NOT NULL
--        AND   vendor_id = 1134657
    ORDER BY
        vendor_number,
        vendor_id;--,
        --vendor_site_id;