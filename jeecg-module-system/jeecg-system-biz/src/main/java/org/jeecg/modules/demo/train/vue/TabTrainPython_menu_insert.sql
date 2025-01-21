-- 注意：该页面对应的前台目录为views/train文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2025011402218430040', NULL, '训练脚本模板', '/train/tabTrainPythonList', 'train/TabTrainPythonList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 1, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2025-01-14 14:21:04', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025011402218440041', '2025011402218430040', '添加训练脚本模板', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_train_python:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-14 14:21:04', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025011402218440042', '2025011402218430040', '编辑训练脚本模板', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_train_python:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-14 14:21:04', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025011402218440043', '2025011402218430040', '删除训练脚本模板', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_train_python:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-14 14:21:04', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025011402218440044', '2025011402218430040', '批量删除训练脚本模板', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_train_python:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-14 14:21:04', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025011402218440045', '2025011402218430040', '导出excel_训练脚本模板', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_train_python:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-14 14:21:04', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025011402218440046', '2025011402218430040', '导入excel_训练脚本模板', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_train_python:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-14 14:21:04', NULL, NULL, 0, 0, '1', 0);