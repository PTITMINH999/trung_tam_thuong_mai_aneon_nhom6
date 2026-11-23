
-- Người phụ trách code: Bảo
-- Chức năng: tạo khóa tra cứu số điện thoại và chỉ mục hỗ trợ tìm khách check-in.

ALTER TABLE khach_mua_sam
    ADD COLUMN phone_lookup VARCHAR(30);

UPDATE khach_mua_sam
SET phone_lookup = REPLACE(
    REPLACE(
        REPLACE(
            REPLACE(
                REPLACE(
                    REPLACE(phone, ' ', ''),
                '-', ''),
            '.', ''),
        '(', ''),
    ')', ''),
    '+', '');

ALTER TABLE khach_mua_sam
    ADD INDEX ix_khach_mua_sam_phone_lookup (phone_lookup),
    ADD INDEX ix_khach_mua_sam_name_lookup (full_name, archived_at, id);
