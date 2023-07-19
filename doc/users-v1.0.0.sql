/*
 Navicat Premium Data Transfer

 Source Server         : project bbn
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : users

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 19/07/2023 07:50:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_country_codes
-- ----------------------------
DROP TABLE IF EXISTS `t_country_codes`;
CREATE TABLE `t_country_codes` (
  `KEY_COUNTRY_CODE` varchar(3) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `str_country_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`KEY_COUNTRY_CODE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of t_country_codes
-- ----------------------------
BEGIN;
INSERT INTO `t_country_codes` VALUES ('237', 'CAMEROON');
COMMIT;

-- ----------------------------
-- Table structure for t_paramters
-- ----------------------------
DROP TABLE IF EXISTS `t_paramters`;
CREATE TABLE `t_paramters` (
  `str_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `str_value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `str_description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `b_status` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`str_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of t_paramters
-- ----------------------------
BEGIN;
INSERT INTO `t_paramters` VALUES ('JWT_ENCRYPTION_KEY', '2021@Ad', 'key used to sign jwt tokens.', 1);
INSERT INTO `t_paramters` VALUES ('JWT_TOKEN_EXPIRATION_TIME', '1800000', 'time for bearer token to expire in milliseconds', 1);
COMMIT;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `lg_role_id` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `str_role_description` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `b_active` tinyint DEFAULT '0',
  `dt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`lg_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of t_role
-- ----------------------------
BEGIN;
INSERT INTO `t_role` VALUES ('CUSTOMER', 'customers of the system who pay for services', 1, '2023-06-10 14:25:15');
COMMIT;

-- ----------------------------
-- Table structure for t_role_user
-- ----------------------------
DROP TABLE IF EXISTS `t_role_user`;
CREATE TABLE `t_role_user` (
  `lg_role_user_id` bigint NOT NULL AUTO_INCREMENT,
  `lg_role_id` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_id` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dt_created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`lg_role_user_id`),
  KEY `lg_role_id` (`lg_role_id`),
  KEY `t_role_user_t_users_user_id_fk` (`user_id`),
  CONSTRAINT `t_role_user_ibfk_1` FOREIGN KEY (`lg_role_id`) REFERENCES `t_role` (`lg_role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_role_user_t_users_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `t_users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of t_role_user
-- ----------------------------
BEGIN;
INSERT INTO `t_role_user` VALUES (1, 'CUSTOMER', 'b9ef7273-640f-4749-a30b-ca8812d782e4', NULL);
INSERT INTO `t_role_user` VALUES (2, 'CUSTOMER', '6ecd851f-3b2f-4b7d-b431-10826213b604', NULL);
INSERT INTO `t_role_user` VALUES (3, 'CUSTOMER', '06b1e8bc-ef91-4c2e-bd80-fc12c0a33ef2', NULL);
INSERT INTO `t_role_user` VALUES (4, 'CUSTOMER', '933459c8-ecbd-48e5-8213-bd963b4bfb4e', NULL);
INSERT INTO `t_role_user` VALUES (5, 'CUSTOMER', 'dae77ad9-90d8-489f-a61f-858164556c52', NULL);
INSERT INTO `t_role_user` VALUES (6, 'CUSTOMER', '54a6038b-3e1e-4cbd-a81f-91b30c6a92b7', NULL);
INSERT INTO `t_role_user` VALUES (7, 'CUSTOMER', 'b92816a5-bb59-49bf-8a9d-36d829e625b1', NULL);
INSERT INTO `t_role_user` VALUES (8, 'CUSTOMER', '1dfd2b52-3a9c-4506-837d-7c2cf01de2d5', NULL);
INSERT INTO `t_role_user` VALUES (9, 'CUSTOMER', '11d00958-97ea-478d-a259-dc86572fedcd', NULL);
INSERT INTO `t_role_user` VALUES (10, 'CUSTOMER', '7efefc76-4561-4b03-8811-f6a7df171b03', NULL);
COMMIT;

-- ----------------------------
-- Table structure for t_token
-- ----------------------------
DROP TABLE IF EXISTS `t_token`;
CREATE TABLE `t_token` (
  `lg_token_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `str_refresh` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `status` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `dt_created` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`lg_token_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of t_token
-- ----------------------------
BEGIN;
INSERT INTO `t_token` VALUES ('1f727130-2f31-4d04-b63c-5c3c8de0ab0c', '8089fbaa-5f14-4144-ad71-8c947f91ecb0', 'active', '2022-05-02 22:14:59');
COMMIT;

-- ----------------------------
-- Table structure for t_users
-- ----------------------------
DROP TABLE IF EXISTS `t_users`;
CREATE TABLE `t_users` (
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 NOT NULL,
  `status` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `dt_created` datetime DEFAULT NULL,
  `dt_updated` datetime DEFAULT NULL,
  `email` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `str_login_provider` varchar(15) COLLATE utf8mb4_general_ci DEFAULT 'local',
  PRIMARY KEY (`user_id`) USING BTREE,
  KEY `user status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of t_users
-- ----------------------------
BEGIN;
INSERT INTO `t_users` VALUES ('06b1e8bc-ef91-4c2e-bd80-fc12c0a33ef2', 'John Elizabeth', 'Ernest', NULL, '$2a$10$i2zfsIITIj07.v.eQz.s.OuaPyoj0ses7F1DFqBKJkdEOAsIZEFPC', 'active', '2023-07-12 09:56:04', NULL, 'Ernest.john@gmail.com', 'local');
INSERT INTO `t_users` VALUES ('11d00958-97ea-478d-a259-dc86572fedcd', 'jame web', 'web1993', '+237651251913', '$2a$10$OvJdzF71S58ZkoHYWTOxR.dg9gtGwE1fELoDnxo93ajKpN.rFInE6', 'active', '2023-07-13 07:47:16', NULL, 'web@gamil.com', 'local');
INSERT INTO `t_users` VALUES ('1dfd2b52-3a9c-4506-837d-7c2cf01de2d5', 'Oswald Ebu Ndorrh', 'Eburian22', '+237651201810', '$2a$10$LWT1sI1JwLcCjrzgEjtbIOZKtLnKtHDDhQNHmctr0DgJwaVMy.dly', 'active', '2023-07-13 07:38:36', NULL, 'oswald@gmail.com', 'local');
INSERT INTO `t_users` VALUES ('1f727130-2f31-4d04-b63c-5c3c8de0ab0c', 'Ngai', 'Ngai_e', '237650931636', '$2a$10$5LwOwU6JnmG7Tfcsjem4hOLGcQfpCEFHMfcaxEK22wLj6K2vJjpBW', 'active', '2022-05-02 11:39:13', NULL, 'asobingai@gmail.com', 'local');
INSERT INTO `t_users` VALUES ('54a6038b-3e1e-4cbd-a81f-91b30c6a92b7', 'Oswald Ebu Ndorrh', 'fdgfdghdfg', '+237650931656', '$2a$10$36HnsIVYwrYcwwbzvTr25euNZ7BeBz9n.yFZ5wHS7Zt/8xxTYPFNC', 'active', '2023-07-12 12:17:38', NULL, 'ndorrdfgjhnnbh@gmail.com', 'local');
INSERT INTO `t_users` VALUES ('6ecd851f-3b2f-4b7d-b431-10826213b604', 'Doe Elizabeth', 'DoeElizabeth', '+237650931636', '$2a$10$Z9vYvHTspr8u6FTRc5rhVebyBN5BGFnK7mjTBouHu24iYDOR9IGAa', 'active', '2023-06-11 11:55:51', NULL, 'elizabeth.doe@gmail.com', 'local');
INSERT INTO `t_users` VALUES ('7efefc76-4561-4b03-8811-f6a7df171b03', 'Oswald Ebu Ndorrh', 'ndorrh789', '+237651590', '$2a$10$CAnq5/iA6TnbNPCp2ucJ.eIHIRVM4CjCkl0YEKYILqA.7gutp/fyO', 'active', '2023-07-13 07:51:15', NULL, 'ndoraxcvrh@gmail.com', 'local');
INSERT INTO `t_users` VALUES ('933459c8-ecbd-48e5-8213-bd963b4bfb4e', 'Ndorrh oswald ebu', 'ndorrh', '+237651251818', '$2a$10$M1vHDTRMnqaQfa/cAw.Ol.JEWOYqnRe3iPSkDDaor1ZRwZpBvRI8G', 'active', '2023-07-12 10:03:03', NULL, 'ndorrh@gmail.com', 'local');
INSERT INTO `t_users` VALUES ('b92816a5-bb59-49bf-8a9d-36d829e625b1', 'Geometry ebu', 'geomask', '+237651251819', '$2a$10$GlJ5k3OaKUCXoVCLhxCgcOVk95jfAdg7dUM5lOamfpzT0ozVqT5ka', 'active', '2023-07-12 12:27:27', NULL, 'geoheart@gmal.com', 'local');
INSERT INTO `t_users` VALUES ('b9ef7273-640f-4749-a30b-ca8812d782e4', 'John Elizabeth', 'JohnElizabeth', '+237650931636', '$2a$10$.98vrts2jazz0pi4XKiUjetuiR7x7tQftvUekEIjOSUuuI9AHBO3i', 'active', '2023-06-11 12:47:00', NULL, 'elizabeth.john@gmail.com', 'local');
INSERT INTO `t_users` VALUES ('dae77ad9-90d8-489f-a61f-858164556c52', 'Ndorrh oswald ebu', 'ndorrh44', '+237671251818', '$2a$10$lWG/nMDFKaHykwqqUUqbL.hz0wMqvXEHLgVumXs4CI5WFj/7Gq7U6', 'active', '2023-07-12 11:01:28', NULL, 'ndorrh23@gmail.com', 'local');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
