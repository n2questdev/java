CREATE OR REPLACE FORCE VIEW prism_contracts_payments_v AS
    SELECT DISTINCT
        e.segment1 vendor_number,
        e.vendor_id,
        f.vendor_site_id,
        REPLACE(DECODE(e.organization_type_lookup_code, 'INDIVIDUAL', e.individual_1099,
                                                'FOREIGN INDIVIDUAL', e.individual_1099,
                                                                     e.num_1099), '-', '') AS vendor_tax_id,
        e.vendor_name AS vendor_name,
        TO_CHAR(trunc(d.creation_date) ) AS po_date,
        d.segment1 AS po_num,
        a.invoice_id AS contract_number,
        g.item_description AS contract_name,
        g.unit_price AS unit_price,
        ( nvl(c.quantity_ordered,0) - nvl(c.quantity_cancelled,0) ) * nvl(g.unit_price,0) AS po_line_amount,
        DECODE(d.approved_flag, 'Y', 'Approved',
                                     'Not Approved') AS po_approved,
        a.invoice_type_lookup_code AS invoice_type,
        a.invoice_amount AS invoice_amount,
        TO_CHAR(trunc(a.invoice_date) ) AS invoice_date,
        a.invoice_num AS invoice_number,
        a.amount_paid,
        h.check_id,
        i.check_number,
        h.invoice_payment_id,
        TO_CHAR(trunc(i.check_date) ) AS invoice_payment_date
    FROM
        po.po_headers_all d,
        po.po_lines_all g,
        po.po_distributions_all c,
        ap.ap_suppliers e,
        ap.ap_supplier_sites_all f,
        ap.ap_invoices_all a,
        ap.ap_invoice_distributions_all b,
        ap.ap_invoice_payments_all h,
        ap.ap_checks_all i
    WHERE
        d.po_header_id = g.po_header_id (+)
        AND   g.po_line_id = c.po_line_id
        AND   d.vendor_id = e.vendor_id (+)
        AND   e.vendor_id = f.vendor_id (+)
        AND   a.vendor_id = d.vendor_id
        AND   a.invoice_id = b.invoice_id
        AND   b.po_distribution_id = c.po_distribution_id (+)
        AND   a.invoice_id = h.invoice_id
        AND   h.check_id = i.check_id
--        AND   e.vendor_id = 1134657 -- uncomment this
        ;
