-- SmartWMS incremental upgrade for databases created before 2026-09-13.
-- Run once only. Fresh installations already include this column in schema.sql.
ALTER TABLE sys_role
    ADD COLUMN sort_order INT DEFAULT 0 COMMENT '排序' AFTER description;
