CREATE OR REPLACE FORCE VIEW prism_vendor_contacts_v AS
    SELECT DISTINCT
        hzr.object_id party_id,
        hp.person_first_name,
        hp.person_last_name,
        hp.party_name,
        hcpe.email_address,
        ltrim(rtrim(hcpp.phone_area_code
        || '-'
        || hcpp.phone_number
        || '-'
        || hcpp.phone_extension) ) AS primary_phone_number,
-- TODO: this is not resolving. Check with Rajeev
--        fnd_message.get_string('POS', DECODE(sign(trunc(nvl(hzr.end_date,SYSDATE + 1) ) - trunc(SYSDATE) ), 1, 'POS_SP_STATUS_CURRENT', 'POS_SP_STATUS_INACTIVE') ) AS status_display,
        'TODO_ACTIVE' AS status_display,
        hcpp.raw_phone_number
    FROM
        AR.hz_parties hp,
        APPLSYS.fnd_user fu,
        AR.hz_relationships hzr,
        AR.hz_party_usg_assignments hpua,
        AR.hz_contact_points hcpp,
        AR.hz_contact_points hcpe
    WHERE
        hp.party_id = hzr.subject_id
        AND   hzr.relationship_type = 'CONTACT'
        AND   hzr.relationship_code = 'CONTACT_OF'
        AND   hzr.subject_type = 'PERSON'
        AND   hzr.object_type = 'ORGANIZATION'
        AND   hzr.status IN ( 'A', 'I' )
        AND   fu.person_party_id (+) = hp.party_id
        AND   hp.party_id NOT IN (
            SELECT
                contact_party_id
            FROM
                POS.pos_contact_requests pcr,
                POS.pos_supplier_mappings psm
            WHERE
                pcr.request_status = 'PENDING'
                AND   psm.mapping_id = pcr.mapping_id
                AND   psm.party_id = hzr.object_id
                AND   contact_party_id IS NOT NULL
        )
        AND   hpua.party_id = hp.party_id
        AND   hpua.status_flag IN ( 'A', 'I' )
        AND   hpua.party_usage_code = 'SUPPLIER_CONTACT'
        AND   hcpp.owner_table_name (+) = 'HZ_PARTIES'
        AND   hcpp.owner_table_id (+) = hzr.party_id
        AND   hcpp.phone_line_type (+) = 'GEN'
        AND   hcpp.contact_point_type (+) = 'PHONE'
        AND   hcpp.primary_flag (+) = 'Y'
        AND   hcpe.owner_table_name (+) = 'HZ_PARTIES'
        AND   hcpe.owner_table_id (+) = hzr.party_id
        AND   hcpe.contact_point_type (+) = 'EMAIL'
        AND   hcpe.primary_flag (+) = 'Y'
        AND   (
            hcpe.status IS NULL
            OR    hcpe.status IN ( 'A', 'I' )
        )
        AND   (
            hcpp.status IS NULL
            OR    hcpp.status IN ( 'A', 'I' )
        )
    UNION
    SELECT
        hzr.object_id AS party_id,
        hp.person_first_name,
        hp.person_last_name,
        hp.party_name,
        hcpe.email_address,
        ltrim(rtrim(hcpp.phone_area_code
        || '-'
        || hcpp.phone_number
        || '-'
        || hcpp.phone_extension) ) AS primary_phone_number,
-- TODO: this is not resolving. Work with Rajeev
--        fnd_message.get_string('POS', DECODE(sign(trunc(nvl(hzr.end_date,SYSDATE + 1) ) - trunc(SYSDATE) ), 1, 'POS_SP_STATUS_CHANGED_PENDING', 'POS_SP_STATUS_INACTIVE') ) AS status_display,
        'TODO_ACTIVE' AS status_display,
        hcpp.raw_phone_number
    FROM
        AR.hz_parties hp,
        APPLSYS.fnd_user fu,
        AR.hz_relationships hzr,
        POS.pos_contact_requests pcr,
        POS.pos_supplier_mappings psm,
        AR.hz_party_usg_assignments hpua,
        AR.hz_contact_points hcpp,
        AR.hz_contact_points hcpe
    WHERE
        hp.party_id = hzr.subject_id
        AND   hzr.relationship_type = 'CONTACT'
        AND   hzr.relationship_code = 'CONTACT_OF'
        AND   hzr.subject_type = 'PERSON'
        AND   hzr.object_type = 'ORGANIZATION'
        AND   hzr.status = 'A'
        AND   fu.person_party_id (+) = hp.party_id
        AND   hp.party_id = pcr.contact_party_id
        AND   pcr.request_status = 'PENDING'
        AND   psm.mapping_id = pcr.mapping_id
        AND   psm.party_id = hzr.object_id
        AND   pcr.request_type IN (
            'UPDATE',
            'DELETE'
        )
        AND   hpua.party_id = hp.party_id
        AND   hpua.status_flag = 'A'
        AND   hpua.party_usage_code = 'SUPPLIER_CONTACT'
        AND   hcpp.owner_table_name (+) = 'HZ_PARTIES'
        AND   hcpp.owner_table_id (+) = hzr.party_id
        AND   hcpp.phone_line_type (+) = 'GEN'
        AND   hcpp.contact_point_type (+) = 'PHONE'
        AND   hcpp.primary_flag (+) = 'Y'
        AND   hcpe.owner_table_name (+) = 'HZ_PARTIES'
        AND   hcpe.owner_table_id (+) = hzr.party_id
        AND   hcpe.contact_point_type (+) = 'EMAIL'
        AND   hcpe.status (+) = 'A'
        AND   hcpp.status (+) = 'A'
    UNION
    SELECT
        psm.party_id AS party_id,
        first_name AS person_first_name,
        last_name AS person_last_name,
        first_name
        || ' '
        || last_name AS party_name,
        pcr.email_address,
        ltrim(rtrim(phone_area_code
        || '-'
        || phone_number
        || '-'
        || phone_extension) ) AS primary_phone_number,
-- TODO: this is not resolving. Work with Rajeev
--        fnd_message.get_string('POS', 'POS_SP_STATUS_NEW') AS status_display,
        'TODO_ACTIVE' AS status_display,
        pcr.phone_area_code
        || '-'
        || pcr.phone_number raw_phone_number
    FROM
        POS.pos_contact_requests pcr,
        APPLSYS.fnd_user fu,
        POS.pos_supplier_mappings psm
    WHERE
        fu.person_party_id (+) = pcr.contact_party_id
        AND   psm.mapping_id = pcr.mapping_id
        AND   pcr.request_status = 'PENDING'
        AND   pcr.request_type = 'ADD';