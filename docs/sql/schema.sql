
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_bill_shipment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` int NOT NULL COMMENT '12',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `supplier_id` bigint NOT NULL COMMENT 'id',
  `supplier_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `date` date NOT NULL COMMENT '',
  `ship_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `ship_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `amount` decimal(10,2) NOT NULL COMMENT '',
  `ship_amount` decimal(5,2) NOT NULL COMMENT '',
  `package_amount` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '',
  `goods_amount` decimal(10,2) NOT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int NOT NULL COMMENT '01)',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='-';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_bill_shop_accounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` int NOT NULL COMMENT '12',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `trade_time` datetime NOT NULL COMMENT '',
  `date` date NOT NULL COMMENT '',
  `amount` decimal(10,2) NOT NULL COMMENT '',
  `usage_scenario` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int NOT NULL COMMENT '01)',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_bill_shop_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL,
  `shop_type` int NOT NULL,
  `order_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `type` int NOT NULL COMMENT '12',
  `amount` float NOT NULL COMMENT '',
  `biz_time` datetime NOT NULL COMMENT '',
  `biz_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '',
  `unit_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '',
  `category_id` bigint DEFAULT '0' COMMENT 'ID',
  `bar_code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '12',
  `length` float NOT NULL DEFAULT '0' COMMENT '//',
  `height` float NOT NULL DEFAULT '0' COMMENT '/',
  `width` float NOT NULL DEFAULT '0' COMMENT '/()',
  `width1` float NOT NULL DEFAULT '0' COMMENT '',
  `width2` float NOT NULL DEFAULT '0' COMMENT '',
  `width3` float NOT NULL DEFAULT '0' COMMENT '',
  `weight` float NOT NULL DEFAULT '0' COMMENT '',
  `disable` tinyint(1) DEFAULT '0' COMMENT '1   0',
  `period` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '',
  `pur_price` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `whole_price` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `retail_price` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `unit_cost` decimal(8,2) DEFAULT NULL COMMENT '',
  `supplier_id` bigint DEFAULT '0' COMMENT 'id',
  `brand_id` bigint DEFAULT '0' COMMENT 'id',
  `attr1` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '1',
  `attr2` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '2',
  `attr3` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '3',
  `attr4` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '4',
  `attr5` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '5',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'url',
  `low_qty` int DEFAULT '0' COMMENT '',
  `high_qty` int DEFAULT '0' COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `number` (`number`) USING BTREE,
  KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_brand` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '',
  `status` int NOT NULL COMMENT '',
  `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `top_id` bigint DEFAULT NULL COMMENT 'id',
  `parent_id` bigint DEFAULT NULL COMMENT 'id',
  `path` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '',
  `sort` int DEFAULT '0' COMMENT '',
  `image` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0  1',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_category_attribute` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `category_id` bigint NOT NULL,
  `type` int NOT NULL DEFAULT '0' COMMENT '01',
  `title` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '''''',
  `code` char(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'colorsizestyle',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_category_attribute_value` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `category_attribute_id` bigint DEFAULT NULL COMMENT 'id',
  `value` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'SKU',
  `order_num` int DEFAULT '0',
  `is_delete` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=427 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `goods_id` bigint NOT NULL COMMENT 'id',
  `goods_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_id` bigint NOT NULL COMMENT 'id',
  `sku_code` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '',
  `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'SKU',
  `quantity` bigint NOT NULL DEFAULT '0' COMMENT '',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0  1',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `locked_qty` int NOT NULL DEFAULT '0' COMMENT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `specIdIndex` (`sku_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_inventory_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `inventory_id` bigint NOT NULL COMMENT 'id',
  `batch_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `origin_qty` bigint NOT NULL COMMENT '',
  `current_qty` bigint NOT NULL DEFAULT '0' COMMENT '',
  `pur_price` double DEFAULT '0' COMMENT '',
  `pur_id` bigint NOT NULL COMMENT 'id',
  `pur_item_id` bigint NOT NULL COMMENT 'itemId',
  `remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_id` bigint NOT NULL COMMENT 'id',
  `goods_id` bigint NOT NULL COMMENT 'id',
  `sku_code` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `warehouse_id` bigint NOT NULL COMMENT 'id',
  `position_id` bigint NOT NULL COMMENT 'id',
  `position_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_inventory_operation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `goods_id` bigint NOT NULL COMMENT 'id',
  `goods_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_id` bigint NOT NULL COMMENT 'id',
  `sku_code` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '',
  `batch_id` bigint NOT NULL COMMENT 'id',
  `batch_num` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `type` int NOT NULL COMMENT '123',
  `inventory_id` bigint NOT NULL COMMENT 'idid',
  `quantity` int NOT NULL DEFAULT '0' COMMENT '',
  `locked_quantity` int NOT NULL DEFAULT '0' COMMENT 'statusquantity',
  `price` double DEFAULT '0' COMMENT 'type=1type=2',
  `biz_type` int NOT NULL COMMENT '101102201202203211212999',
  `biz_id` bigint NOT NULL COMMENT 'id',
  `biz_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `biz_item_id` bigint NOT NULL COMMENT 'itemId',
  `warehouse_id` bigint NOT NULL COMMENT 'id',
  `position_id` bigint NOT NULL COMMENT 'id',
  `position_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int NOT NULL COMMENT '01',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `goods_id` bigint NOT NULL COMMENT 'id',
  `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `goods_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `spec_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '',
  `spec_num` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '',
  `color_id` int DEFAULT '0' COMMENT 'id',
  `color_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `color_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `color_image` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `size_id` int DEFAULT '0' COMMENT 'id',
  `size_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `size_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `style_id` int DEFAULT '0' COMMENT 'id',
  `style_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `style_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `bar_code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `pur_price` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `whole_price` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `retail_price` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `unit_cost` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '',
  `status` tinyint(1) DEFAULT '1' COMMENT '',
  `low_qty` int DEFAULT '0' COMMENT '',
  `high_qty` int DEFAULT '0' COMMENT '',
  `disable` tinyint(1) DEFAULT '0' COMMENT '0   1',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `id` (`id`) USING BTREE,
  KEY `number` (`spec_num`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_goods_sku_attr` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `goods_id` bigint NOT NULL,
  `type` char(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `k` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `kid` bigint DEFAULT NULL,
  `vid` bigint DEFAULT NULL,
  `v` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `img` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_import_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` int DEFAULT NULL COMMENT '平台 1淘宝天猫 2京东 3拼多多 4抖店 5微信小店 9其他',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `mapping_json` varchar(2000) NOT NULL COMMENT '映射JSON：系统字段→Excel列索引',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单导入表头映射模板';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_logistics_company` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `platform_id` bigint DEFAULT NULL COMMENT 'id',
  `shop_id` bigint DEFAULT NULL COMMENT 'ID',
  `logistics_id` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int DEFAULT NULL COMMENT '10',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=426 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `shop_type` int NOT NULL COMMENT '',
  `shop_id` bigint NOT NULL COMMENT 'ID',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `buyer_memo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `seller_memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `refund_status` int NOT NULL COMMENT ' 1234  ',
  `order_status` int NOT NULL COMMENT '123',
  `goods_amount` double DEFAULT NULL COMMENT '',
  `order_amount` double NOT NULL COMMENT '',
  `receiver_name_mask` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_address` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `town` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_time` datetime DEFAULT NULL COMMENT '',
  `shipper` int NOT NULL DEFAULT '0' COMMENT ' 0 12',
  `ship_status` int NOT NULL DEFAULT '0' COMMENT ' 0  1  2',
  `ship_type` int DEFAULT NULL COMMENT '12',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `has_push_erp` int DEFAULT '0' COMMENT 'ERP',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `pay_amount` double DEFAULT NULL COMMENT '',
  `express_fee` decimal(12,2) DEFAULT NULL COMMENT '快递费用',
  `platform_fee` decimal(12,2) DEFAULT NULL COMMENT '平台扣费（订单自带，回退参考；优先用对账单）',
  `waybill_code` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `waybill_status` int NOT NULL DEFAULT '0' COMMENT '01',
  `waybill_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_sn_index` (`order_num`) USING BTREE,
  KEY `shopid_index` (`shop_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_order_after_sale` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` int NOT NULL COMMENT '10208099',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `after_sale_order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sub_order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'spuid',
  `sku_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skuid',
  `count` int DEFAULT NULL COMMENT '',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `img` varchar(555) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_info` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `sku_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `erp_goods_id` bigint DEFAULT NULL COMMENT 'ERPid',
  `erp_sku_id` bigint DEFAULT NULL COMMENT 'ERPskuId',
  `return_info` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'json',
  `return_waybill_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `return_company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_town` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_waybill_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int DEFAULT NULL COMMENT ':0123',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `shipping_status` int DEFAULT '0' COMMENT ' 0: 1:',
  `user_shipping_status` int DEFAULT '0' COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `order_id` bigint NOT NULL COMMENT 'IDo_order',
  `order_num` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sub_order_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `sku_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'skuId',
  `erp_goods_id` bigint NOT NULL DEFAULT '0' COMMENT 'erpid',
  `erp_sku_id` bigint NOT NULL DEFAULT '0' COMMENT 'erpid',
  `goods_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_img` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_num` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_spec` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_num` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_price` double NOT NULL COMMENT '',
  `unit_cost` double DEFAULT NULL COMMENT '商品成本单价快照（发货时记录）',
  `item_amount` double DEFAULT NULL COMMENT '',
  `quantity` int NOT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `refund_count` int DEFAULT '0' COMMENT '',
  `refund_status` int DEFAULT NULL COMMENT ' 1234  ',
  `has_push_erp` int DEFAULT '0' COMMENT 'ERP',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `ship_status` int NOT NULL DEFAULT '0' COMMENT ' 0  1  2',
  `shipper` int NOT NULL DEFAULT '0' COMMENT ' 0 12',
  `ship_type` int DEFAULT NULL COMMENT '12',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `goodId_index` (`erp_goods_id`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_order_ship_list` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `shipper` int NOT NULL COMMENT ' 0  1 ',
  `ship_supplier_id` bigint NOT NULL DEFAULT '0' COMMENT 'ID0',
  `ship_supplier` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_id` bigint DEFAULT NULL COMMENT 'erpid',
  `order_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `town` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `buyer_memo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `seller_memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_logistics_company` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_logistics_company_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'code',
  `ship_logistics_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_status` int NOT NULL COMMENT '123',
  `status` int NOT NULL COMMENT '0123\r\n013',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='-';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_order_ship_list_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `list_id` bigint NOT NULL COMMENT 'id',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `shipper` int NOT NULL DEFAULT '0' COMMENT ' 0  1 ',
  `ship_supplier_id` bigint NOT NULL DEFAULT '0' COMMENT 'ID0',
  `ship_supplier` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_id` bigint DEFAULT NULL COMMENT 'erpid',
  `order_item_id` bigint DEFAULT NULL COMMENT 'erpitemid',
  `order_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `original_sku_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skuid',
  `goods_id` bigint DEFAULT '0' COMMENT 'erpid',
  `sku_id` bigint DEFAULT '0' COMMENT 'erpid',
  `goods_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_img` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_num` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_num` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `quantity` int NOT NULL COMMENT '',
  `status` int NOT NULL COMMENT '0123',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='-';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_order_ship_waybill` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tentant_id` bigint NOT NULL COMMENT 'id',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `order_num` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `erp_order_id` bigint NOT NULL COMMENT 'ID',
  `waybill_order_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id()',
  `waybill_code` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `logistics_code` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `print_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '',
  `sign` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '',
  `status` int NOT NULL COMMENT '123',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `template_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'url',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='-';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_platform_bill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint NOT NULL COMMENT '店铺ID',
  `shop_type` int DEFAULT NULL COMMENT '店铺类型',
  `bill_month` varchar(7) NOT NULL COMMENT '账期月份 yyyy-MM',
  `order_num` varchar(64) DEFAULT NULL COMMENT '订单号',
  `biz_type` varchar(50) DEFAULT NULL COMMENT '业务类型 佣金/推广扣费/运费/退款/活动补贴/其他',
  `income_type` tinyint NOT NULL COMMENT '收支 1收入 2支出',
  `amount` decimal(12,2) NOT NULL COMMENT '金额（正数）',
  `biz_time` datetime DEFAULT NULL COMMENT '发生时间',
  `match_status` tinyint NOT NULL DEFAULT '0' COMMENT '匹配状态 0未匹配 1已匹配',
  `order_id` varchar(32) DEFAULT NULL COMMENT '匹配的系统订单ID',
  `batch_no` varchar(64) DEFAULT NULL COMMENT '上传批次号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '上传人',
  `create_time` datetime DEFAULT NULL COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_month` (`bill_month`),
  KEY `idx_order_num` (`order_num`),
  KEY `idx_batch` (`batch_no`),
  KEY `idx_match` (`match_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='平台对账单明细';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_promotion_fee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint NOT NULL COMMENT '店铺ID',
  `goods_id` bigint DEFAULT NULL COMMENT '链接/商品ID（空=店铺级统一分摊）',
  `goods_name` varchar(200) DEFAULT NULL COMMENT '链接名称（冗余）',
  `plan_name` varchar(100) DEFAULT NULL COMMENT '推广计划名（直通车/千川/多多搜索/其他）',
  `fee_month` varchar(7) NOT NULL COMMENT '费用月份 yyyy-MM',
  `amount` decimal(12,2) NOT NULL COMMENT '金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_month` (`fee_month`),
  KEY `idx_shop` (`shop_id`),
  KEY `idx_goods` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='推广费';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_purchase_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `supplier_id` bigint NOT NULL COMMENT 'id',
  `order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '',
  `order_date` date NOT NULL COMMENT '',
  `order_time` bigint NOT NULL COMMENT '',
  `order_amount` decimal(10,2) NOT NULL COMMENT '',
  `ship_amount` decimal(6,2) NOT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT ' 0110110223',
  `audit_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `audit_time` bigint DEFAULT '0' COMMENT '',
  `supplier_confirm_time` datetime DEFAULT NULL COMMENT '',
  `supplier_delivery_time` datetime DEFAULT NULL COMMENT '',
  `received_time` datetime DEFAULT NULL COMMENT '',
  `stock_in_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=474 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_purchase_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `supplier_id` bigint DEFAULT NULL COMMENT 'id',
  `order_id` bigint DEFAULT '0' COMMENT 'id',
  `order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `trans_type` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '150501 150502',
  `amount` double DEFAULT '0' COMMENT '',
  `order_date` date DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `goods_id` bigint DEFAULT '0' COMMENT 'ID',
  `goods_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `spec_id` bigint DEFAULT '0' COMMENT 'id',
  `spec_num` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `color_value` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `color_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `size_value` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `style_value` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `price` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `dis_amount` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `dis_rate` decimal(8,2) DEFAULT '0.00' COMMENT '',
  `quantity` bigint NOT NULL DEFAULT '0' COMMENT '()',
  `inQty` bigint NOT NULL DEFAULT '0' COMMENT '',
  `locationId` int DEFAULT NULL COMMENT 'id',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '1 0',
  `status` int DEFAULT '0' COMMENT 'billStatus0123',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `type` (`trans_type`) USING BTREE,
  KEY `billdate` (`order_date`) USING BTREE,
  KEY `invId` (`goods_id`) USING BTREE,
  KEY `transType` (`trans_type`) USING BTREE,
  KEY `iid` (`order_id`) USING BTREE,
  KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1969 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_purchase_order_payable` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `supplier_id` bigint NOT NULL COMMENT 'id',
  `supplier_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `amount` decimal(10,2) NOT NULL COMMENT '',
  `date` date NOT NULL COMMENT '',
  `invoice_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `purchase_order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `purchase_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int NOT NULL COMMENT '01)',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_purchase_order_ship` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `supplier_id` bigint DEFAULT NULL COMMENT 'id',
  `order_id` bigint DEFAULT NULL COMMENT 'id',
  `ship_company` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `freight` decimal(6,0) DEFAULT NULL COMMENT '',
  `ship_time` datetime DEFAULT NULL COMMENT '',
  `receipt_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `status` int DEFAULT NULL COMMENT '012',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `back_count` int DEFAULT NULL COMMENT '',
  `stock_in_time` datetime DEFAULT NULL COMMENT '',
  `stock_in_count` int DEFAULT NULL COMMENT '',
  `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `order_date` date DEFAULT NULL COMMENT '',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_spec_unit` int DEFAULT NULL COMMENT '',
  `order_goods_unit` int DEFAULT NULL COMMENT '',
  `order_spec_unit_total` int DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=472 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `refund_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '',
  `refund_type` int DEFAULT NULL COMMENT '(10- 20- 30- 40- 50- 60- 70- 90- 80- 100- 11-)',
  `shop_id` int DEFAULT NULL COMMENT 'id',
  `shop_type` int DEFAULT NULL COMMENT '',
  `refund_fee` float NOT NULL COMMENT '',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '',
  `order_item_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `sku_id` bigint DEFAULT NULL COMMENT 'skuId',
  `erp_goods_id` bigint DEFAULT NULL COMMENT 'erpid',
  `erp_sku_id` bigint DEFAULT NULL COMMENT 'erp sku id',
  `sku_num` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `goods_name` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_sku` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `goods_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `quantity` bigint DEFAULT NULL COMMENT '',
  `return_logistics_company` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `return_logistics_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receive_time` datetime DEFAULT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `contactPerson` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `status` int NOT NULL COMMENT '100011000210005140001001110010',
  `create_time` datetime NOT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shipment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `order_id` bigint DEFAULT NULL COMMENT 'o_orderid',
  `order_num` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_time` datetime DEFAULT NULL COMMENT '',
  `shipper` int NOT NULL DEFAULT '0' COMMENT ' 0  1 ',
  `supplier_id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
  `supplier` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_type` int DEFAULT NULL COMMENT '123',
  `ship_company` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_company_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'code',
  `ship_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_fee` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '',
  `ship_time` datetime DEFAULT NULL COMMENT '',
  `ship_operator` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `ship_status` int DEFAULT NULL COMMENT '0 12',
  `package_weight` float DEFAULT NULL COMMENT '',
  `package_length` float DEFAULT NULL COMMENT '',
  `package_width` float DEFAULT NULL COMMENT '',
  `package_height` float DEFAULT NULL COMMENT '',
  `packsge_operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `packsge_time` datetime DEFAULT NULL COMMENT '',
  `packages` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'JSON',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'json',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `order_id_index` (`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shipment_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `shipment_id` bigint NOT NULL COMMENT 'id',
  `shipper` int NOT NULL COMMENT ' 0  1 ',
  `supplier_id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
  `supplier` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `shop_type` int NOT NULL COMMENT '',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `order_id` bigint NOT NULL COMMENT ' id',
  `order_num` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `order_time` datetime DEFAULT NULL COMMENT '',
  `order_item_id` bigint NOT NULL COMMENT 'itemIDo_order_item',
  `erp_goods_id` bigint NOT NULL DEFAULT '0' COMMENT 'erpid',
  `erp_sku_id` bigint NOT NULL DEFAULT '0' COMMENT 'erpid',
  `goods_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_img` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_num` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_spec` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_num` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `quantity` int NOT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `stock_status` int NOT NULL COMMENT ' 0  1  2 ',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `goodId_index` (`erp_goods_id`) USING BTREE,
  KEY `order_id` (`order_item_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '',
  `type` int NOT NULL COMMENT 'Id',
  `url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'url',
  `sort` int NOT NULL DEFAULT '9' COMMENT '',
  `status` int DEFAULT '0' COMMENT '12',
  `modify_on` bigint NOT NULL COMMENT '',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `seller_id` bigint NOT NULL DEFAULT '0' COMMENT 'id',
  `app_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'Appkey',
  `app_sercet` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'Appsercet',
  `access_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'sessionKeyaccess_token',
  `expires_in` bigint DEFAULT NULL COMMENT '',
  `expires_at` bigint DEFAULT NULL COMMENT '',
  `access_token_begin` bigint DEFAULT NULL COMMENT 'access_token',
  `refresh_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'token',
  `refresh_token_timeout` bigint DEFAULT NULL COMMENT 'token',
  `api_request_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'url',
  `api_callback_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'url',
  `api_status` int NOT NULL DEFAULT '0' COMMENT 'api01',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='-';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_fixed_fee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint NOT NULL COMMENT '店铺ID',
  `fee_month` varchar(7) NOT NULL COMMENT '费用月份 yyyy-MM',
  `fee_type` varchar(20) NOT NULL COMMENT '费用项 人工/房租/软件/包装/其他',
  `amount` decimal(12,2) NOT NULL COMMENT '金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_month` (`fee_month`),
  KEY `idx_shop` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='店铺固定费用';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint DEFAULT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `out_product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `head_imgs` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `head_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `desc_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '',
  `attrs` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int DEFAULT NULL COMMENT '',
  `edit_status` int DEFAULT NULL COMMENT '',
  `min_price` int DEFAULT NULL COMMENT ' SKU ',
  `market_price` int DEFAULT NULL COMMENT '',
  `spu_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `product_type` int DEFAULT NULL COMMENT '1: 2: 3: : data_type=2',
  `create_time` int DEFAULT NULL COMMENT '',
  `edit_time` int DEFAULT NULL COMMENT '',
  `erp_goods_id` bigint NOT NULL DEFAULT '0' COMMENT 'id',
  `quantity` int NOT NULL DEFAULT '0' COMMENT '',
  `create_on` datetime DEFAULT NULL COMMENT '',
  `update_on` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_goods_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_goods_id` bigint NOT NULL COMMENT 'id',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `product_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `out_product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skuID',
  `out_sku_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skuID',
  `thumb_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `sale_price` int DEFAULT NULL COMMENT '',
  `stock_num` int DEFAULT NULL COMMENT 'sku',
  `sku_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `status` int DEFAULT NULL COMMENT 'sku',
  `create_time` int DEFAULT NULL COMMENT '',
  `sku_attrs` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku_attrs',
  `sku_deliver_info` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku_deliver_info',
  `erp_goods_id` bigint NOT NULL DEFAULT '0' COMMENT 'erpid',
  `erp_goods_sku_id` bigint NOT NULL DEFAULT '0' COMMENT 'erpskuid',
  `quantity` int NOT NULL DEFAULT '0' COMMENT '',
  `create_on` datetime DEFAULT NULL COMMENT '',
  `update_on` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_type` int NOT NULL COMMENT '3pdd5wei',
  `event_type` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `event_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `seller_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `status` int NOT NULL DEFAULT '0' COMMENT '01',
  `create_time` int NOT NULL COMMENT '',
  `update_time` int DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `order_model` int NOT NULL DEFAULT '0' COMMENT '01',
  `order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `openid` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` int DEFAULT NULL COMMENT '',
  `update_time` int DEFAULT NULL COMMENT '',
  `unionid` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` int DEFAULT NULL COMMENT '10	20	21	30	100	200	250	',
  `aftersale_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'json',
  `pay_info` varchar(5500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'json',
  `product_price` int DEFAULT NULL COMMENT '',
  `order_price` int DEFAULT NULL COMMENT 'order_price=original_order_price-discounted_price-deduction_price-change_down_price',
  `freight` int DEFAULT NULL COMMENT '',
  `discounted_price` int DEFAULT NULL COMMENT '',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `postal_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `province_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `city_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `county_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `detail_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `tel_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '\r\n',
  `house_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `virtual_order_tel_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '(deliver_method=1)',
  `tel_number_ext_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `use_tel_number` int DEFAULT NULL COMMENT '01',
  `hash_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `delivery_product_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'JSON',
  `ship_done_time` int DEFAULT NULL COMMENT '',
  `ewaybill_order_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '\r\n',
  `settle_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'json',
  `confirm_status` int DEFAULT '0' COMMENT '01',
  `confirm_time` datetime DEFAULT NULL COMMENT '',
  `erp_send_status` int DEFAULT NULL COMMENT 'erp',
  `erp_send_time` datetime DEFAULT NULL COMMENT 'ERP',
  `create_on` datetime DEFAULT NULL COMMENT '',
  `update_on` datetime DEFAULT NULL COMMENT '',
  `seller_discount` int NOT NULL DEFAULT '0' COMMENT '',
  `platform_discount` int NOT NULL DEFAULT '0' COMMENT '',
  `pay_amount` int NOT NULL DEFAULT '0' COMMENT '',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `buyer_memo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `seller_memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_address` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `refund_status` int NOT NULL DEFAULT '1' COMMENT ' 1234  ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint DEFAULT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `shop_order_id` bigint DEFAULT NULL COMMENT 'id',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'spuid',
  `sku_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skuid\r\n',
  `thumb_img` varchar(550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'sku',
  `sku_cnt` int DEFAULT NULL COMMENT 'sku',
  `sale_price` int DEFAULT NULL COMMENT '',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `on_aftersale_sku_cnt` int DEFAULT NULL COMMENT '/ sku ',
  `finish_aftersale_sku_cnt` int DEFAULT NULL COMMENT '/ sku ',
  `sku_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `market_price` int DEFAULT NULL COMMENT '',
  `sku_attrs` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skuJSON',
  `real_price` int DEFAULT NULL COMMENT 'skuestimate_pricechange_price',
  `out_product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'spuid',
  `out_sku_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skuid',
  `is_discounted` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'false',
  `estimate_price` int DEFAULT NULL COMMENT 'skuis_discountedtrue',
  `is_change_price` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'false',
  `change_price` int DEFAULT NULL COMMENT 'skuis_change_pricetrue',
  `out_warehouse_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `sku_deliver_info` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JSON',
  `extra_service` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JSON',
  `use_deduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '\r\n',
  `deduction_price` int DEFAULT NULL COMMENT '',
  `order_product_coupon_info_list` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT ' order.order_detail.coupon_info',
  `erp_goods_id` bigint NOT NULL DEFAULT '0' COMMENT 'erpid',
  `erp_sku_id` bigint NOT NULL DEFAULT '0' COMMENT 'erpid',
  `order_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_time` int DEFAULT NULL COMMENT '',
  `create_on` datetime DEFAULT NULL COMMENT '',
  `update_on` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_platform` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `app_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `redirect_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'uri',
  `server_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_pull_lasttime` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint DEFAULT NULL COMMENT 'id',
  `pull_type` enum('ORDER','REFUND') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'ORDER:REFUND:',
  `lasttime` datetime DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_pull_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT 'id',
  `pull_type` enum('ORDER','REFUND','GOODS') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'ORDERGOODSREFUND',
  `pull_way` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `pull_params` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `pull_result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `pull_time` datetime DEFAULT NULL COMMENT '',
  `duration` bigint DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `after_sale_order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'AfterSaleStatus',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `unionid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_id` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `order_amount` float DEFAULT NULL COMMENT '',
  `product_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'spuid',
  `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'skuid',
  `count` int DEFAULT NULL COMMENT '',
  `fast_refund` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'RefundInfo',
  `refund_reason` int DEFAULT NULL COMMENT ',  RefundReason',
  `refund_amount` int DEFAULT NULL COMMENT '',
  `return_waybill_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `return_delivery_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `return_delivery_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `merchant_upload_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'JSON',
  `create_time` int DEFAULT NULL COMMENT '',
  `update_time` int DEFAULT NULL COMMENT '',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `reason_text` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'REFUND:RETURN:',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'detail json',
  `complaint_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `refund_resp` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `confirm_status` int DEFAULT '0' COMMENT '10',
  `confirm_time` datetime DEFAULT NULL COMMENT '',
  `user_shipping_status` int DEFAULT NULL COMMENT '0- 1- 2-',
  `after_sales_status` int DEFAULT NULL COMMENT '',
  `after_sales_type` int DEFAULT NULL COMMENT '',
  `dispute_refund_status` int DEFAULT NULL COMMENT '1 0',
  `create_on` datetime DEFAULT NULL COMMENT '',
  `update_on` datetime DEFAULT NULL COMMENT '',
  `shipping_status` int DEFAULT '0' COMMENT ' 0: 1:',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_task` (
  `id` int NOT NULL,
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '',
  `task_type` int NOT NULL COMMENT '1',
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cron` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` int NOT NULL COMMENT '10',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_task_logs` (
  `id` bigint NOT NULL COMMENT 'ID',
  `task_id` int DEFAULT NULL COMMENT 'ID',
  `result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `start_time` datetime DEFAULT NULL COMMENT '',
  `end_time` datetime DEFAULT NULL COMMENT '',
  `status` int DEFAULT NULL COMMENT '12',
  `create_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_waybill_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT 'id',
  `shop_type` int NOT NULL COMMENT '359',
  `seller_shop_id` bigint DEFAULT NULL COMMENT 'idshop_id',
  `delivery_id` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `company_type` int DEFAULT NULL COMMENT '1 2',
  `site_code` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `site_name` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `acct_id` bigint DEFAULT NULL COMMENT 'idacct_id',
  `acct_type` int DEFAULT NULL COMMENT '0 1',
  `status` int DEFAULT NULL COMMENT '',
  `available` int DEFAULT NULL COMMENT '',
  `allocated` int DEFAULT NULL COMMENT '',
  `cancel` int DEFAULT NULL COMMENT '',
  `recycled` int DEFAULT NULL COMMENT '',
  `monthly_card` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'company_type ',
  `site_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'JSON',
  `sender_province` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sender_city` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sender_county` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sender_street` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sender_address` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `mobile` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `phone` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `is_show` int DEFAULT NULL COMMENT '10',
  `supplier_ids` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `template_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'url',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_shop_waybill_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_type` int NOT NULL COMMENT '359',
  `wp_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'code',
  `template_id` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `waybill_type` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `template_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'url',
  `desc1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `width` int DEFAULT NULL,
  `height` int DEFAULT NULL,
  `custom_config` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `is_customize` int NOT NULL DEFAULT '0' COMMENT '01',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_stock_goods_bad_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `goodsId` int NOT NULL COMMENT 'id',
  `specId` int NOT NULL COMMENT 'id',
  `specNumber` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '',
  `sourceId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'IDtype==1 order_send_returnid',
  `type` int NOT NULL COMMENT '13',
  `locationId` int NOT NULL DEFAULT '0' COMMENT '',
  `quantity` int NOT NULL DEFAULT '0' COMMENT '',
  `lossAmount` double NOT NULL DEFAULT '0' COMMENT '',
  `isDelete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0  1',
  `result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `resultTime` datetime DEFAULT NULL COMMENT '',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '01',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `modifyTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_stock_goods_bad_stock_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stockId` bigint NOT NULL COMMENT 'id',
  `specId` int NOT NULL COMMENT 'id',
  `locationId` int NOT NULL COMMENT '',
  `type` int NOT NULL COMMENT '12',
  `quantity` bigint NOT NULL DEFAULT '0' COMMENT '',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_stock_in` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_in_num` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '',
  `stock_in_type` int NOT NULL COMMENT '12',
  `source_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `source_id` bigint DEFAULT NULL COMMENT 'id',
  `source_goods_unit` int DEFAULT NULL COMMENT '',
  `source_spec_unit_total` int DEFAULT NULL COMMENT '',
  `source_spec_unit` int DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `stock_in_operator_id` bigint DEFAULT NULL COMMENT 'id',
  `stock_in_operator` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `stock_in_time` datetime DEFAULT NULL COMMENT '',
  `status` int NOT NULL DEFAULT '0' COMMENT '012',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_stock_in_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock_in_id` bigint NOT NULL COMMENT 'id',
  `stock_in_type` int DEFAULT NULL COMMENT '12',
  `source_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `source_id` bigint DEFAULT NULL COMMENT 'id',
  `source_item_id` bigint NOT NULL COMMENT 'itemId',
  `goods_id` bigint NOT NULL COMMENT 'id',
  `goods_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_id` bigint NOT NULL COMMENT 'id',
  `sku_code` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_name` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `quantity` int NOT NULL COMMENT '',
  `pur_price` decimal(10,2) NOT NULL COMMENT '',
  `in_quantity` int NOT NULL DEFAULT '0' COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `status` int DEFAULT '0' COMMENT '02',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `warehouse_id` bigint DEFAULT NULL COMMENT 'id',
  `position_id` bigint DEFAULT NULL COMMENT 'id',
  `position_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `specIndex` (`sku_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_stock_in_item_position` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `in_id` bigint NOT NULL COMMENT 'ID',
  `item_id` bigint NOT NULL DEFAULT '0' COMMENT 'ItemID',
  `goods_inventory_id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
  `goods_inventory_batch_id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
  `quantity` int NOT NULL DEFAULT '0' COMMENT '',
  `operator_id` bigint NOT NULL DEFAULT '0' COMMENT 'userid',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime NOT NULL COMMENT '',
  `warehouse_id` bigint NOT NULL COMMENT 'id',
  `position_id` bigint NOT NULL COMMENT 'id',
  `position_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `goods_stock_info_item_id_index` (`goods_inventory_batch_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_stock_out` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `out_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `source_num` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `source_id` bigint DEFAULT NULL COMMENT 'Id',
  `type` int NOT NULL DEFAULT '1' COMMENT '1234',
  `goods_unit` int NOT NULL COMMENT '',
  `spec_unit` int NOT NULL COMMENT '',
  `spec_unit_total` int NOT NULL COMMENT '',
  `out_total` int DEFAULT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int NOT NULL COMMENT '012',
  `print_status` int NOT NULL COMMENT '10',
  `print_time` datetime DEFAULT NULL COMMENT '',
  `out_time` datetime DEFAULT NULL COMMENT '',
  `complete_time` datetime DEFAULT NULL COMMENT '',
  `operator_id` bigint DEFAULT '0' COMMENT 'userid',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
  `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_stock_out_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` int NOT NULL COMMENT '1234',
  `entry_id` bigint NOT NULL COMMENT 'id',
  `source_order_id` bigint DEFAULT NULL COMMENT 'id',
  `source_order_item_id` bigint DEFAULT NULL COMMENT 'itemIditemIdorder_itemidinvoice_infoid',
  `source_order_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `original_quantity` bigint NOT NULL COMMENT '',
  `out_quantity` bigint NOT NULL DEFAULT '0' COMMENT '',
  `complete_time` datetime DEFAULT NULL COMMENT '',
  `picked_time` datetime DEFAULT NULL COMMENT '',
  `status` int NOT NULL DEFAULT '0' COMMENT '012',
  `goods_id` bigint NOT NULL COMMENT 'id',
  `goods_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `goods_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_id` bigint NOT NULL COMMENT 'id',
  `sku_code` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `sku_name` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `pur_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_stock_out_item_position` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `out_id` bigint NOT NULL COMMENT 'ID',
  `item_id` bigint NOT NULL DEFAULT '0' COMMENT 'ItemID',
  `goods_inventory_id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
  `goods_inventory_batch_id` bigint NOT NULL DEFAULT '0' COMMENT 'ID',
  `quantity` int NOT NULL DEFAULT '0' COMMENT '',
  `operator_id` bigint DEFAULT '0' COMMENT 'userid',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `out_time` datetime DEFAULT NULL COMMENT '',
  `warehouse_id` bigint NOT NULL COMMENT 'id',
  `position_id` bigint NOT NULL COMMENT 'id',
  `position_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `goods_stock_info_item_id_index` (`goods_inventory_batch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_supplier` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `number` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `tax_rate` double DEFAULT '0' COMMENT '',
  `amount` double DEFAULT '0' COMMENT '',
  `period_money` double DEFAULT '0' COMMENT '',
  `dif_money` double DEFAULT '0' COMMENT '',
  `begin_date` date DEFAULT NULL COMMENT '',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `place` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `contact_man` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `contact` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `county` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `pinYin` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '',
  `disable` tinyint(1) DEFAULT '0' COMMENT '1   0',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0 1',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_warehouse` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `district` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `street` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `address` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int NOT NULL DEFAULT '0' COMMENT '0  1',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_warehouse_position` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `warehouse_id` bigint NOT NULL COMMENT 'id',
  `number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '/',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '/',
  `parent_id` int NOT NULL COMMENT 'id',
  `depth` int DEFAULT '1' COMMENT '123',
  `parent_id1` int NOT NULL COMMENT 'id',
  `parent_id2` int NOT NULL COMMENT 'id',
  `address` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `is_delete` int NOT NULL DEFAULT '0' COMMENT '0  1',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erp_work_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint DEFAULT NULL COMMENT '店铺ID',
  `order_num` varchar(64) DEFAULT NULL COMMENT '订单号（平台单号，可后补）',
  `goods_id` bigint DEFAULT NULL COMMENT '商品ID（补发扣库存用）',
  `type` tinyint NOT NULL COMMENT '类型 1补款 2返现 3补发 4仅退款 5退货退款 6其他',
  `amount` decimal(12,2) NOT NULL COMMENT '金额',
  `voucher` varchar(500) DEFAULT NULL COMMENT '凭证图路径',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0待处理 1已结算',
  `settle_time` datetime DEFAULT NULL COMMENT '结算时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_status` (`status`),
  KEY `idx_tenant_order` (`order_num`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单（售后支出）';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fms_payable_ship_fee` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `logistics_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `logistics_company_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `logistics_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `order_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `shop_id` int DEFAULT NULL COMMENT 'id',
  `amount` decimal(10,2) NOT NULL COMMENT '',
  `date` date NOT NULL COMMENT '',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` int NOT NULL COMMENT '01)',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '',
  `length` float DEFAULT '0' COMMENT '',
  `width` float DEFAULT '0' COMMENT '',
  `height` float DEFAULT '0' COMMENT '',
  `weight` float DEFAULT NULL COMMENT '',
  `receiver_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `town` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='--';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_contract` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contact_id` bigint DEFAULT NULL COMMENT 'id',
  `bill_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `contractNo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `billDate` date DEFAULT NULL COMMENT '',
  `userId` smallint DEFAULT '0' COMMENT 'id',
  `userName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `transType` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT ':BUY BUYR SALE SALER OTHER',
  `transTypeName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `totalAmount` decimal(10,2) DEFAULT NULL COMMENT '',
  `disRate` double DEFAULT '0' COMMENT '',
  `disAmount` double DEFAULT '0' COMMENT '',
  `amount` double DEFAULT '0' COMMENT '',
  `totalDiscount` double DEFAULT '0' COMMENT '',
  `totalQuantity` bigint DEFAULT '0' COMMENT '',
  `qualifiedQuantity` bigint DEFAULT '0' COMMENT '',
  `inQuantity` bigint DEFAULT '0' COMMENT '()',
  `rpAmount` double DEFAULT '0' COMMENT '',
  `arrears` double DEFAULT '0' COMMENT '',
  `freight` double DEFAULT '0' COMMENT '',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `billType` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'PO OI PUR BAL',
  `billStatus` tinyint(1) DEFAULT '0' COMMENT ' 0123 11',
  `isDelete` tinyint(1) DEFAULT '0' COMMENT '1  0',
  `checkName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `checked` tinyint(1) DEFAULT '0' COMMENT '01',
  `createTime` bigint DEFAULT NULL COMMENT '',
  `modifyTime` bigint DEFAULT NULL COMMENT '',
  `salesId` smallint DEFAULT NULL,
  `customerFree` double DEFAULT '0' COMMENT '',
  `hxStateCode` tinyint DEFAULT '0' COMMENT ' 0  1  2',
  `hxAmount` double DEFAULT '0' COMMENT '',
  `payment` double DEFAULT '0' COMMENT '',
  `srcOrderNo` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `srcOrderId` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id',
  `logisticsNo` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `logisticsCompany` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `logisticsCompanyCode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `logisticsNumber` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `locationId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'id,',
  `inLocationId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'ID,',
  `outLocationId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'ID,',
  `serialno` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `checkoutName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `checkoutTime` bigint DEFAULT '0' COMMENT '',
  `checkoutStatus` int DEFAULT '0' COMMENT '0   1',
  `qualifiedStatus` int DEFAULT '0' COMMENT '00,10',
  `stockInName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `stockInTime` bigint DEFAULT '0' COMMENT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `parent_id` bigint DEFAULT '0' COMMENT 'id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `order_num` int DEFAULT '0' COMMENT '',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 1',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 2',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '',
  `dict_sort` int DEFAULT '0' COMMENT '',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'N' COMMENT 'Y N',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 1',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 1',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE KEY `dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'IP',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 1',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `login_time` datetime DEFAULT NULL COMMENT '',
  PRIMARY KEY (`info_id`) USING BTREE,
  KEY `idx_sys_logininfor_s` (`status`) USING BTREE,
  KEY `idx_sys_logininfor_lt` (`login_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `parent_id` bigint DEFAULT '0' COMMENT 'ID',
  `order_num` int DEFAULT '0' COMMENT '',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `is_frame` int DEFAULT '1' COMMENT '0 1',
  `is_cache` int DEFAULT '0' COMMENT '0 1',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'M C F',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 1',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 1',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `update_time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2099 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `business_type` int DEFAULT '0' COMMENT '0 1 2 3',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `operator_type` int DEFAULT '0' COMMENT '0 1 2',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `status` int DEFAULT '0' COMMENT '0 1',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `oper_time` datetime DEFAULT NULL COMMENT '',
  `cost_time` bigint DEFAULT '0' COMMENT '',
  PRIMARY KEY (`oper_id`) USING BTREE,
  KEY `idx_sys_oper_log_bt` (`business_type`) USING BTREE,
  KEY `idx_sys_oper_log_s` (`status`) USING BTREE,
  KEY `idx_sys_oper_log_ot` (`oper_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=174 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `role_sort` int NOT NULL COMMENT '',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '1 2 3 4',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '0 1',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 2',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT 'ID',
  `menu_id` bigint NOT NULL COMMENT 'ID',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='角色与部门关联';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `dept_id` bigint DEFAULT NULL COMMENT 'ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '00' COMMENT '00',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 1 2',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 1',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '0 2',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'IP',
  `login_date` datetime DEFAULT NULL COMMENT '',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `create_time` datetime DEFAULT NULL COMMENT '',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '',
  `update_time` datetime DEFAULT NULL COMMENT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=167 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT 'ID',
  `role_id` bigint NOT NULL COMMENT 'ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

