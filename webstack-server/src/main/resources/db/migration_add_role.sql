-- ============================================================
-- 权限体系改造：为 sys_user 增加 role 列
-- 执行前请备份数据库
-- ============================================================

-- 1. 新增角色列，默认普通用户
ALTER TABLE sys_user
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN / USER' AFTER phone;

-- 2. 将历史已存在的用户提升为管理员（按需调整 WHERE 条件，避免把所有人都设为管理员）
--    例如仅把初始管理员账号设为 ADMIN：
UPDATE sys_user SET role = 'ADMIN' WHERE username = 'admin';

-- 如确认现有库中均为管理员账号，可执行：
-- UPDATE sys_user SET role = 'ADMIN';
