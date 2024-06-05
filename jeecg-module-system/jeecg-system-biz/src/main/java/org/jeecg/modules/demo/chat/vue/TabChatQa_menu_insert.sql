-- 注意：该页面对应的前台目录为views/chat文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2024050702433200590', NULL, '问答回复', '/chat/tabChatQaList', 'chat/TabChatQaList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 1, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2024-05-07 14:43:59', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024050702433200591', '2024050702433200590', '添加问答回复', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_chat_qa:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-05-07 14:43:59', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024050702433200592', '2024050702433200590', '编辑问答回复', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_chat_qa:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-05-07 14:43:59', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024050702433200593', '2024050702433200590', '删除问答回复', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_chat_qa:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-05-07 14:43:59', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024050702433200594', '2024050702433200590', '批量删除问答回复', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_chat_qa:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-05-07 14:43:59', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024050702433200595', '2024050702433200590', '导出excel_问答回复', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_chat_qa:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-05-07 14:43:59', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024050702433200596', '2024050702433200590', '导入excel_问答回复', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.demo:tab_chat_qa:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-05-07 14:43:59', NULL, NULL, 0, 0, '1', 0);