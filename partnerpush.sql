/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50726
Source Host           : localhost:3306
Source Database       : partnerpush

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2022-09-03 00:37:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for scheduled_job
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_job`;
CREATE TABLE `scheduled_job` (
  `job_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `job_key` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '定时任务完整类名',
  `cron_expression` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'cron表达式',
  `task_explain` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '任务描述',
  `push_wx` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '需要定时发送的微信号',
  `push_context` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '推送内容',
  `push_template` varchar(255) COLLATE utf8_bin NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态,1:正常;-1:停用',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='定时任务表';
