
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (100,0,'0','启航小店',0,'','','','0','0','admin','2023-08-07 19:31:37','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1,'用户性别','sys_user_sex','0','admin','2023-08-07 19:31:38','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2023-08-07 19:31:38','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2023-08-07 19:31:38','',NULL,'系统开关列表'),(6,'系统是否','sys_yes_no','0','admin','2023-08-07 19:31:38','',NULL,'系统是否列表'),(9,'操作类型','sys_oper_type','0','admin','2023-08-07 19:31:38','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2023-08-07 19:31:38','',NULL,'登录状态列表');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2023-08-07 19:31:38','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2023-08-07 19:31:38','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2023-08-07 19:31:38','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2023-08-07 19:31:38','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2023-08-07 19:31:38','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2023-08-07 19:31:38','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2023-08-07 19:31:38','',NULL,'停用状态'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2023-08-07 19:31:38','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2023-08-07 19:31:38','',NULL,'系统默认否'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2023-08-07 19:31:38','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2023-08-07 19:31:38','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2023-08-07 19:31:38','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2023-08-07 19:31:38','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2023-08-07 19:31:38','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2023-08-07 19:31:38','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2023-08-07 19:31:38','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2023-08-07 19:31:38','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2023-08-07 19:31:38','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2023-08-07 19:31:38','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2023-08-07 19:31:38','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2023-08-07 19:31:38','',NULL,'停用状态');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1,'系统管理',0,99,'system','','',1,0,'M','0','0','','system','admin','2023-12-27 15:00:27','admin','2024-04-20 22:21:25','系统管理目录'),(3,'订单管理',0,2,'order','','',1,0,'M','0','0','','shopping','admin','2023-12-29 16:53:03','admin','2024-04-10 11:02:32',''),(7,'店铺设置',0,3,'shop','','',1,0,'M','0','0','','theme','admin','2023-12-29 17:07:23','admin','2025-05-19 06:55:10',''),(102,'菜单管理',1,3,'menu','system/menu/index','',1,0,'C','0','0','system:menu:list','tree-table','admin','2023-12-27 15:00:27','','','菜单管理菜单'),(105,'字典管理',1,4,'dict','system/dict/index','',1,0,'C','0','0','system:dict:list','dict','admin','2023-12-27 15:00:27','','','字典管理菜单'),(106,'操作日志',1,5,'operlog','system/operlog/index','',1,0,'C','0','0','system:operlog:list','form','admin',NULL,'',NULL,'全局操作日志菜单'),(910,'工单',0,1,'workOrder','','',1,0,'M','0','0','','message','admin',NULL,'',NULL,'租户侧：售后支出记账'),(911,'工单管理',910,1,'index','workOrder/index','',1,0,'C','0','0','workOrder:workOrder:list','form','admin',NULL,'',NULL,'工单列表/速记/结算/导入'),(912,'工单新增',911,1,'','','',1,0,'F','0','0','workOrder:workOrder:add','#','admin',NULL,'',NULL,'速记/完整新建/导入'),(913,'工单修改',911,2,'','','',1,0,'F','0','0','workOrder:workOrder:edit','#','admin',NULL,'',NULL,''),(914,'工单删除',911,3,'','','',1,0,'F','0','0','workOrder:workOrder:remove','#','admin',NULL,'',NULL,''),(915,'工单结算',911,4,'','','',1,0,'F','0','0','workOrder:workOrder:settle','#','admin',NULL,'',NULL,'批量结算/撤销结算'),(920,'费用中心',0,5,'fee','','',1,0,'M','0','0','','money','admin',NULL,'',NULL,'租户侧：推广费/固定费用/对账单'),(921,'推广费',920,1,'promotion','fee/promotion','',1,0,'C','0','0','fee:promotion:list','money','admin',NULL,'',NULL,'按链接/店铺、月份录入'),(922,'推广费新增',921,1,'','','',1,0,'F','0','0','fee:promotion:add','#','admin',NULL,'',NULL,''),(923,'推广费修改',921,2,'','','',1,0,'F','0','0','fee:promotion:edit','#','admin',NULL,'',NULL,''),(924,'推广费删除',921,3,'','','',1,0,'F','0','0','fee:promotion:remove','#','admin',NULL,'',NULL,''),(925,'推广费导入',921,4,'','','',1,0,'F','0','0','fee:promotion:import','#','admin',NULL,'',NULL,''),(926,'固定费用',920,2,'fixed','fee/fixed','',1,0,'C','0','0','fee:fixed:list','money','admin',NULL,'',NULL,'人工/房租/软件/包装'),(927,'固定费用新增',926,1,'','','',1,0,'F','0','0','fee:fixed:add','#','admin',NULL,'',NULL,''),(928,'固定费用修改',926,2,'','','',1,0,'F','0','0','fee:fixed:edit','#','admin',NULL,'',NULL,''),(929,'固定费用删除',926,3,'','','',1,0,'F','0','0','fee:fixed:remove','#','admin',NULL,'',NULL,''),(930,'固定费用导入',926,4,'','','',1,0,'F','0','0','fee:fixed:import','#','admin',NULL,'',NULL,''),(931,'对账单',920,3,'bill','fee/bill','',1,0,'C','0','0','fee:bill:list','money','admin',NULL,'',NULL,'平台扣费/退款数据来源'),(932,'账单补录',931,1,'','','',1,0,'F','0','0','fee:bill:add','#','admin',NULL,'',NULL,'手动补录/上传导入'),(933,'账单匹配',931,2,'','','',1,0,'F','0','0','fee:bill:match','#','admin',NULL,'',NULL,'按订单号自动匹配'),(934,'账单删除',931,3,'','','',1,0,'F','0','0','fee:bill:remove','#','admin',NULL,'',NULL,''),(940,'利润分析',0,6,'profit','','',1,0,'M','0','0','','chart','admin',NULL,'',NULL,'租户侧：三层级利润报表'),(941,'订单利润',940,1,'order','profit/order','',1,0,'C','0','0','profit:report:list','chart','admin',NULL,'',NULL,'一行一单，找亏损单'),(942,'链接利润',940,2,'link','profit/link','',1,0,'C','0','0','profit:report:list','chart','admin',NULL,'',NULL,'一行一款，扣推广费后'),(943,'店铺利润',940,3,'shop','profit/shop','',1,0,'C','0','0','profit:report:list','chart','admin',NULL,'',NULL,'一行一店，净利润'),(944,'利润导出',940,4,'','','',1,0,'F','0','0','profit:report:export','#','admin',NULL,'',NULL,'导出 Excel'),(951,'订单导入',3,1,'import','order/import','',1,0,'C','0','0','order:import:list','download','admin',NULL,'',NULL,'Excel 表头映射导入，主入口'),(952,'订单导入提交',951,1,'','','',1,0,'F','0','0','order:import:commit','#','admin',NULL,'',NULL,'预览/提交/模板'),(954,'手动建单',3,3,'create','order/create','',1,0,'C','0','0','order:manual:create','edit','admin',NULL,'',NULL,'零星订单、补录历史单'),(960,'商品成本',0,4,'goodsCost','','',1,0,'M','0','0','','excel','admin',NULL,'',NULL,'租户侧：SKU 成本价档案'),(961,'商品档案',960,1,'profile','goodsCost/profile','',1,0,'C','0','0','goodsCost:sku:list','list','admin',NULL,'',NULL,'成本价行内编辑/Excel 导入/未填成本筛选'),(962,'成本价修改',961,1,'','','',1,0,'F','0','0','goodsCost:sku:edit','#','admin',NULL,'',NULL,'行内编辑/接口更新'),(963,'成本导入',961,2,'','','',1,0,'F','0','0','goodsCost:sku:import','#','admin',NULL,'',NULL,'Excel 批量导入'),(1012,'菜单查询',102,1,'','','',1,0,'F','0','0','system:menu:query','#','admin','2023-12-27 15:00:27','','',''),(1013,'菜单新增',102,2,'','','',1,0,'F','0','0','system:menu:add','#','admin','2023-12-27 15:00:27','','',''),(1014,'菜单修改',102,3,'','','',1,0,'F','0','0','system:menu:edit','#','admin','2023-12-27 15:00:27','','',''),(1015,'菜单删除',102,4,'','','',1,0,'F','0','0','system:menu:remove','#','admin','2023-12-27 15:00:27','','',''),(1025,'字典查询',105,1,'#','','',1,0,'F','0','0','system:dict:query','#','admin','2023-12-27 15:00:27','','',''),(1026,'字典新增',105,2,'#','','',1,0,'F','0','0','system:dict:add','#','admin','2023-12-27 15:00:27','','',''),(1027,'字典修改',105,3,'#','','',1,0,'F','0','0','system:dict:edit','#','admin','2023-12-27 15:00:27','','',''),(1028,'字典删除',105,4,'#','','',1,0,'F','0','0','system:dict:remove','#','admin','2023-12-27 15:00:27','','',''),(1029,'字典导出',105,5,'#','','',1,0,'F','0','0','system:dict:export','#','admin','2023-12-27 15:00:27','','',''),(1606,'日志删除',106,1,'','','',1,0,'F','0','0','system:operlog:remove','#','admin',NULL,'',NULL,'操作日志删除/清空按钮'),(2008,'商品分类',960,2,'category','goods/category/index','',1,0,'C','0','0','goods:category','tree-table','admin','2023-12-29 13:32:41','admin','2024-04-21 14:41:32',''),(2033,'订单库',3,2,'list','order/index','',1,0,'C','0','0','shop:order:list','list','admin','2023-12-31 20:05:05','admin','2025-04-19 10:45:33',''),(2072,'店铺管理',7,2,'list','shop/index',NULL,1,0,'C','0','0','shop:shop:list','clipboard','admin','2024-01-28 11:29:21','admin','2024-04-10 11:00:21',''),(2073,'店铺平台',7,3,'platform','shop/platform/index',NULL,1,0,'C','0','0','shop:shop:list','clipboard','admin',NULL,'',NULL,''),(2074,'店铺查询',2072,1,'','','',1,0,'F','0','0','shop:shop:query','#','admin',NULL,'',NULL,''),(2075,'店铺新增',2072,2,'','','',1,0,'F','0','0','shop:shop:add','#','admin',NULL,'',NULL,''),(2076,'店铺修改',2072,3,'','','',1,0,'F','0','0','shop:shop:edit','#','admin',NULL,'',NULL,''),(2077,'店铺删除',2072,4,'','','',1,0,'F','0','0','shop:shop:remove','#','admin',NULL,'',NULL,''),(2078,'订单查询',2033,1,'','','',1,0,'F','0','0','shop:order:list','#','admin',NULL,'',NULL,''),(2079,'分类查询',2008,1,'','','',1,0,'F','0','0','goods:category:query','#','admin',NULL,'',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2023-08-07 19:31:37','',NULL,'超级管理员');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1,1),(1,3),(1,7),(1,102),(1,105),(1,106),(1,910),(1,911),(1,912),(1,913),(1,914),(1,915),(1,920),(1,921),(1,922),(1,923),(1,924),(1,925),(1,926),(1,927),(1,928),(1,929),(1,930),(1,931),(1,932),(1,933),(1,934),(1,940),(1,941),(1,942),(1,943),(1,944),(1,951),(1,952),(1,954),(1,960),(1,961),(1,962),(1,963),(1,1012),(1,1013),(1,1014),(1,1015),(1,1025),(1,1026),(1,1027),(1,1028),(1,1029),(1,1606),(1,2008),(1,2033),(1,2072),(1,2073),(1,2074),(1,2075),(1,2076),(1,2077),(1,2078),(1,2079);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1,100,'admin','管理员','00','','','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','',NULL,'admin','2023-08-07 19:31:37','',NULL,'开源版默认账号');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1,1);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `erp_shop_platform` WRITE;
/*!40000 ALTER TABLE `erp_shop_platform` DISABLE KEYS */;
INSERT INTO `erp_shop_platform` (`id`, `name`, `code`, `app_key`, `app_secret`, `redirect_uri`, `server_url`) VALUES (3,'拼多多','PDD',NULL,NULL,NULL,NULL),(4,'抖店','DOU',NULL,NULL,NULL,NULL),(5,'微信小店','WEI','',NULL,NULL,'https://api.weixin.qq.com'),(9,'其他渠道','QT',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `erp_shop_platform` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

