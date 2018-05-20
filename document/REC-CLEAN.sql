-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        10.2.5-MariaDB - MariaDB Server
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 ryrec 的数据库结构
DROP DATABASE IF EXISTS `ryrec`;
CREATE DATABASE IF NOT EXISTS `ryrec` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ryrec`;

-- 导出  表 ryrec.actionrule 结构
DROP TABLE IF EXISTS `actionrule`;
CREATE TABLE IF NOT EXISTS `actionrule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `device` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '信号发出的Device',
  `sig` int(10) NOT NULL DEFAULT 0 COMMENT '信号类型',
  PRIMARY KEY (`id`),
  KEY `FK_actionrule_device` (`device`),
  CONSTRAINT `FK_actionrule_device` FOREIGN KEY (`device`) REFERENCES `device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='联动规则';

-- 正在导出表  ryrec.actionrule 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `actionrule` DISABLE KEYS */;
/*!40000 ALTER TABLE `actionrule` ENABLE KEYS */;

-- 导出  表 ryrec.actions 结构
DROP TABLE IF EXISTS `actions`;
CREATE TABLE IF NOT EXISTS `actions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rule` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '外键，actionrule',
  `target` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '外键，相关的Device',
  `act` int(10) NOT NULL DEFAULT 0 COMMENT '动作',
  `parm` varchar(255) DEFAULT NULL COMMENT '动作参数，可能是一个Json对象',
  PRIMARY KEY (`id`),
  KEY `FK_actions_device` (`target`),
  KEY `FK_actions_actionrule` (`rule`),
  CONSTRAINT `FK_actions_actionrule` FOREIGN KEY (`rule`) REFERENCES `actionrule` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_actions_device` FOREIGN KEY (`target`) REFERENCES `device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='联动的动作';

-- 正在导出表  ryrec.actions 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `actions` DISABLE KEYS */;
/*!40000 ALTER TABLE `actions` ENABLE KEYS */;

-- 导出  表 ryrec.alarm 结构
DROP TABLE IF EXISTS `alarm`;
CREATE TABLE IF NOT EXISTS `alarm` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `device` int(10) unsigned NOT NULL DEFAULT 0,
  `sig` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '告警信号',
  `value` varchar(50) DEFAULT NULL COMMENT '告警值',
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_alarm_device` (`device`),
  CONSTRAINT `FK_alarm_device` FOREIGN KEY (`device`) REFERENCES `device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='告警历史记录';

-- 正在导出表  ryrec.alarm 的数据：~9 rows (大约)
/*!40000 ALTER TABLE `alarm` DISABLE KEYS */;
/*!40000 ALTER TABLE `alarm` ENABLE KEYS */;

-- 导出  视图 ryrec.alarmvideo 结构
DROP VIEW IF EXISTS `alarmvideo`;
-- 创建临时表以解决视图依赖性错误
CREATE TABLE `alarmvideo` (
	`device` INT(10) UNSIGNED NOT NULL COMMENT '信号发出的Device',
	`sig` INT(10) NOT NULL COMMENT '信号类型',
	`target` INT(10) UNSIGNED NULL COMMENT '外键，相关的Device'
) ENGINE=MyISAM;

-- 导出  表 ryrec.channel 结构
DROP TABLE IF EXISTS `channel`;
CREATE TABLE IF NOT EXISTS `channel` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增量，唯一ID',
  `name` varchar(200) NOT NULL DEFAULT '#通道名称#' COMMENT '通道名称',
  `ip` varchar(50) DEFAULT NULL COMMENT ' ',
  `port` int(11) NOT NULL DEFAULT 0,
  `login` varchar(50) DEFAULT NULL,
  `pass` varchar(50) DEFAULT NULL,
  `type` int(10) unsigned NOT NULL DEFAULT 1,
  `opt` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='通道表，主要用于描述网络通道。主要用于保存可以连接的信息。';

-- 正在导出表  ryrec.channel 的数据：~33 rows (大约)
/*!40000 ALTER TABLE `channel` DISABLE KEYS */;
/*!40000 ALTER TABLE `channel` ENABLE KEYS */;

-- 导出  视图 ryrec.channelnode 结构
DROP VIEW IF EXISTS `channelnode`;
-- 创建临时表以解决视图依赖性错误
CREATE TABLE `channelnode` (
	`id` INT(10) UNSIGNED NOT NULL COMMENT '自增量，唯一ID',
	`cname` VARCHAR(200) NOT NULL COMMENT '通道名称' COLLATE 'utf8_general_ci',
	`ip` VARCHAR(50) NULL COMMENT ' ' COLLATE 'utf8_general_ci',
	`port` INT(11) NOT NULL,
	`login` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`pass` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`ctype` INT(10) UNSIGNED NOT NULL,
	`channelConf` VARCHAR(1024) NULL COLLATE 'utf8_general_ci',
	`nid` INT(10) UNSIGNED NULL COMMENT '设备的唯一编号',
	`adr` INT(10) UNSIGNED NULL COMMENT '地址，或者是个控制器的编号。在Modbus中是一个设备的485地址',
	`no` INT(10) UNSIGNED NULL COMMENT '预留设备的地址，目前好像不需要',
	`nname` VARCHAR(200) NULL COLLATE 'utf8_general_ci',
	`ntype` INT(10) UNSIGNED NULL,
	`nodeConf` VARCHAR(1024) NULL COLLATE 'utf8_general_ci',
	`device` INT(10) UNSIGNED NULL COMMENT '属于哪个Device',
	`deviceFun` INT(10) UNSIGNED NULL COMMENT '哪个Device的哪个功能',
	`enable` TINYINT(3) UNSIGNED NULL
) ENGINE=MyISAM;

-- 导出  表 ryrec.config 结构
DROP TABLE IF EXISTS `config`;
CREATE TABLE IF NOT EXISTS `config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cat` int(11) NOT NULL DEFAULT 0 COMMENT '配置的组',
  `name` varchar(50) NOT NULL DEFAULT 'key' COMMENT '配置名称',
  `value` varchar(50) NOT NULL DEFAULT 'value' COMMENT '配置的值',
  `type` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '设备类型',
  `fun` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '设备功能',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='配置文件，常量等\r\n\r\nGroup 分组\r\n1：Channel 类型\r\n2：Node 类型\r\n3：Device 类型';

-- 正在导出表  ryrec.config 的数据：~18 rows (大约)
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` (`id`, `cat`, `name`, `value`, `type`, `fun`) VALUES
	(1, 11, '摄像机', '101', 0, 0),
	(2, 11, '灯光', '201', 401, 0),
	(3, 11, '通用告警', '1001', 202, 2),
	(4, 11, '全景', '9999', 0, 0),
	(5, 11, '风机', '202', 105, 0),
	(6, 11, '红外入侵', '301', 102, 2),
	(7, 11, '水泵', '203', 104, 0),
	(8, 11, '一氧化碳', '401', 313, 101),
	(9, 11, '硫化氢', '402', 314, 101),
	(10, 11, '甲烷', '403', 312, 101),
	(11, 11, '氧气', '404', 311, 101),
	(12, 11, '水位', '421', 315, 101),
	(13, 11, '风速', '405', 0, 0),
	(14, 11, '温度', '406', 301, 101),
	(15, 11, '湿度', '407', 302, 101),
	(16, 11, '空调', '501', 0, 0),
	(17, 11, '环流', '601', 316, 0),
	(18, 11, '光纤测温', '602', 317, 0);
/*!40000 ALTER TABLE `config` ENABLE KEYS */;

-- 导出  事件 ryrec.del_alarm 结构
DROP EVENT IF EXISTS `del_alarm`;
DELIMITER //
CREATE DEFINER=`root`@`%` EVENT `del_alarm` ON SCHEDULE EVERY 1 DAY STARTS '2018-01-15 00:00:00' ON COMPLETION PRESERVE ENABLE COMMENT '定时删除告警' DO BEGIN
delete from alarm where time<date_add(now(), interval -30 day);
END//
DELIMITER ;

-- 导出  表 ryrec.device 结构
DROP TABLE IF EXISTS `device`;
CREATE TABLE IF NOT EXISTS `device` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `no` int(10) unsigned DEFAULT 0 COMMENT '设备编号',
  `name` varchar(50) DEFAULT NULL,
  `type` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '设备类型',
  `icon` int(10) unsigned DEFAULT 0 COMMENT '设备图标',
  `opt` varchar(1024) DEFAULT NULL COMMENT '配置参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备列表\r\n一组功能node构成了一个功能集合的设备\r\n\r\n一个Device由1～n个nodes组成';

-- 正在导出表  ryrec.device 的数据：~190 rows (大约)
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
/*!40000 ALTER TABLE `device` ENABLE KEYS */;

-- 导出  视图 ryrec.devicegis 结构
DROP VIEW IF EXISTS `devicegis`;
-- 创建临时表以解决视图依赖性错误
CREATE TABLE `devicegis` (
	`did` INT(10) UNSIGNED NOT NULL,
	`dno` INT(10) UNSIGNED NULL COMMENT '设备编号',
	`dname` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`dtype` INT(10) UNSIGNED NOT NULL COMMENT '设备类型',
	`icon` INT(10) UNSIGNED NULL COMMENT '设备图标',
	`gid` INT(10) UNSIGNED NULL COMMENT '主键',
	`gtype` INT(10) UNSIGNED NULL COMMENT '点线面的类型',
	`layer` INT(10) UNSIGNED NULL COMMENT '在那一层',
	`data` VARCHAR(1024) NULL COMMENT '具体的空间数据' COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

-- 导出  视图 ryrec.devicenode 结构
DROP VIEW IF EXISTS `devicenode`;
-- 创建临时表以解决视图依赖性错误
CREATE TABLE `devicenode` (
	`id` INT(10) UNSIGNED NOT NULL,
	`dno` INT(10) UNSIGNED NULL COMMENT '设备编号',
	`dname` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`dtype` INT(10) UNSIGNED NOT NULL COMMENT '设备类型',
	`nid` INT(10) UNSIGNED NULL COMMENT '设备的唯一编号',
	`cid` INT(10) UNSIGNED NULL,
	`nadd` INT(10) UNSIGNED NULL COMMENT '地址，或者是个控制器的编号。在Modbus中是一个设备的485地址',
	`nno` INT(10) UNSIGNED NULL COMMENT '预留设备的地址，目前好像不需要',
	`ntype` INT(10) UNSIGNED NULL,
	`conf` VARCHAR(1024) NULL COLLATE 'utf8_general_ci',
	`nfun` INT(10) UNSIGNED NULL COMMENT '哪个Device的哪个功能'
) ENGINE=MyISAM;

-- 导出  表 ryrec.gis 结构
DROP TABLE IF EXISTS `gis`;
CREATE TABLE IF NOT EXISTS `gis` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device` int(10) unsigned NOT NULL DEFAULT 0,
  `type` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '点线面的类型',
  `layer` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '在那一层',
  `data` varchar(1024) DEFAULT '0' COMMENT '具体的空间数据',
  `style` int(11) DEFAULT 0 COMMENT '展示方式，图标等',
  PRIMARY KEY (`id`),
  KEY `FK_gisdevice_device` (`device`),
  KEY `FK_gisdevice_gislayer` (`layer`),
  CONSTRAINT `FK_gisdevice_device` FOREIGN KEY (`device`) REFERENCES `device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_gisdevice_gislayer` FOREIGN KEY (`layer`) REFERENCES `gislayer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  ryrec.gis 的数据：~146 rows (大约)
/*!40000 ALTER TABLE `gis` DISABLE KEYS */;
/*!40000 ALTER TABLE `gis` ENABLE KEYS */;

-- 导出  表 ryrec.gislayer 结构
DROP TABLE IF EXISTS `gislayer`;
CREATE TABLE IF NOT EXISTS `gislayer` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(254) NOT NULL DEFAULT '图层名称',
  `file` varchar(254) DEFAULT '图片位置',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  ryrec.gislayer 的数据：~10 rows (大约)
/*!40000 ALTER TABLE `gislayer` DISABLE KEYS */;
/*!40000 ALTER TABLE `gislayer` ENABLE KEYS */;

-- 导出  表 ryrec.node 结构
DROP TABLE IF EXISTS `node`;
CREATE TABLE IF NOT EXISTS `node` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '设备的唯一编号',
  `cid` int(10) unsigned DEFAULT 0,
  `adr` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '地址，或者是个控制器的编号。在Modbus中是一个设备的485地址',
  `no` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '预留设备的地址，目前好像不需要',
  `name` varchar(200) NOT NULL,
  `type` int(10) unsigned NOT NULL DEFAULT 0,
  `opt` varchar(1024) DEFAULT NULL,
  `device` int(10) unsigned DEFAULT 0 COMMENT '属于哪个Device',
  `deviceFun` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '哪个Device的哪个功能',
  `enable` tinyint(3) unsigned NOT NULL DEFAULT 1,
  `addr104` int(10) unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `FK_device_channel` (`cid`),
  CONSTRAINT `FK_device_channel` FOREIGN KEY (`cid`) REFERENCES `channel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='节点列表\r\n包括所有的前端动环设备和摄像机';

-- 正在导出表  ryrec.node 的数据：~238 rows (大约)
/*!40000 ALTER TABLE `node` DISABLE KEYS */;
/*!40000 ALTER TABLE `node` ENABLE KEYS */;

-- 导出  表 ryrec.panorama 结构
DROP TABLE IF EXISTS `panorama`;
CREATE TABLE IF NOT EXISTS `panorama` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT '全景场景',
  `file` varchar(128) DEFAULT NULL,
  `device` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `device` (`device`),
  CONSTRAINT `FK_panorama_device` FOREIGN KEY (`device`) REFERENCES `device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='全景图片，关联到一个Device';

-- 正在导出表  ryrec.panorama 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `panorama` DISABLE KEYS */;
/*!40000 ALTER TABLE `panorama` ENABLE KEYS */;

-- 导出  表 ryrec.panoramadevice 结构
DROP TABLE IF EXISTS `panoramadevice`;
CREATE TABLE IF NOT EXISTS `panoramadevice` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `device` int(10) unsigned DEFAULT 0,
  `scene` int(10) unsigned DEFAULT 0,
  `pitch` float DEFAULT 0,
  `yaw` float DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK__device` (`device`),
  KEY `FK__panorama` (`scene`),
  CONSTRAINT `FK__device` FOREIGN KEY (`device`) REFERENCES `device` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK__panorama` FOREIGN KEY (`scene`) REFERENCES `panorama` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='全景图片上的device';

-- 正在导出表  ryrec.panoramadevice 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `panoramadevice` DISABLE KEYS */;
/*!40000 ALTER TABLE `panoramadevice` ENABLE KEYS */;

-- 导出  视图 ryrec.ruleaction 结构
DROP VIEW IF EXISTS `ruleaction`;
-- 创建临时表以解决视图依赖性错误
CREATE TABLE `ruleaction` (
	`rid` INT(10) UNSIGNED NOT NULL,
	`device` INT(10) UNSIGNED NOT NULL COMMENT '信号发出的Device',
	`sig` INT(10) NOT NULL COMMENT '信号类型',
	`aid` INT(10) UNSIGNED NULL,
	`target` INT(10) UNSIGNED NULL COMMENT '外键，相关的Device',
	`act` INT(10) NULL COMMENT '动作',
	`parm` VARCHAR(255) NULL COMMENT '动作参数，可能是一个Json对象' COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

-- 导出  表 ryrec.user 结构
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(10) unsigned NOT NULL DEFAULT 1 COMMENT '1普通用户（只能查看），2操作员（可以操作），11系统配置（后台管理）',
  `rank` int(10) unsigned NOT NULL DEFAULT 1 COMMENT '用户的等级，越大等级越高',
  `name` varchar(50) NOT NULL DEFAULT '用户名',
  `pass` varchar(50) NOT NULL DEFAULT '密码',
  `mobile` varchar(32) NOT NULL DEFAULT '13911111111',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- 正在导出表  ryrec.user 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

-- 导出  触发器 ryrec.device_before_delete 结构
DROP TRIGGER IF EXISTS `device_before_delete`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `device_before_delete` BEFORE DELETE ON `device` FOR EACH ROW BEGIN

update node set device = 0 where device=OLD.id;

END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 导出  触发器 ryrec.node_before_insert 结构
DROP TRIGGER IF EXISTS `node_before_insert`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `node_before_insert` BEFORE INSERT ON `node` FOR EACH ROW BEGIN

SET NEW.device=0;

END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 导出  视图 ryrec.alarmvideo 结构
DROP VIEW IF EXISTS `alarmvideo`;
-- 移除临时表并创建最终视图结构
DROP TABLE IF EXISTS `alarmvideo`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `alarmvideo` AS select `actionrule`.`device` AS `device`,`actionrule`.`sig` AS `sig`,`actions`.`target` AS `target` from ((`actionrule` left join `actions` on(`actionrule`.`id` = `actions`.`rule`)) left join `node` on(`node`.`device` = `actions`.`target`)) where `node`.`deviceFun` = 101 and `node`.`type` >= 4000 and `node`.`type` <= 5000;

-- 导出  视图 ryrec.channelnode 结构
DROP VIEW IF EXISTS `channelnode`;
-- 移除临时表并创建最终视图结构
DROP TABLE IF EXISTS `channelnode`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `channelnode` AS select `channel`.`id` AS `id`,`channel`.`name` AS `cname`,`channel`.`ip` AS `ip`,`channel`.`port` AS `port`,`channel`.`login` AS `login`,`channel`.`pass` AS `pass`,`channel`.`type` AS `ctype`,`channel`.`opt` AS `channelConf`,`node`.`id` AS `nid`,`node`.`adr` AS `adr`,`node`.`no` AS `no`,`node`.`name` AS `nname`,`node`.`type` AS `ntype`,`node`.`opt` AS `nodeConf`,`node`.`device` AS `device`,`node`.`deviceFun` AS `deviceFun`,`node`.`enable` AS `enable` from (`channel` left join `node` on(`channel`.`id` = `node`.`cid`));

-- 导出  视图 ryrec.devicegis 结构
DROP VIEW IF EXISTS `devicegis`;
-- 移除临时表并创建最终视图结构
DROP TABLE IF EXISTS `devicegis`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `devicegis` AS select `device`.`id` AS `did`,`device`.`no` AS `dno`,`device`.`name` AS `dname`,`device`.`type` AS `dtype`,`device`.`icon` AS `icon`,`gis`.`id` AS `gid`,`gis`.`type` AS `gtype`,`gis`.`layer` AS `layer`,`gis`.`data` AS `data` from (`device` left join `gis` on(`device`.`id` = `gis`.`device`));

-- 导出  视图 ryrec.devicenode 结构
DROP VIEW IF EXISTS `devicenode`;
-- 移除临时表并创建最终视图结构
DROP TABLE IF EXISTS `devicenode`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `devicenode` AS select `device`.`id` AS `id`,`device`.`no` AS `dno`,`device`.`name` AS `dname`,`device`.`type` AS `dtype`,`node`.`id` AS `nid`,`node`.`cid` AS `cid`,`node`.`adr` AS `nadd`,`node`.`no` AS `nno`,`node`.`type` AS `ntype`,`node`.`opt` AS `conf`,`node`.`deviceFun` AS `nfun` from (`device` left join `node` on(`device`.`id` = `node`.`device`));

-- 导出  视图 ryrec.ruleaction 结构
DROP VIEW IF EXISTS `ruleaction`;
-- 移除临时表并创建最终视图结构
DROP TABLE IF EXISTS `ruleaction`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `ruleaction` AS select `actionrule`.`id` AS `rid`,`actionrule`.`device` AS `device`,`actionrule`.`sig` AS `sig`,`actions`.`id` AS `aid`,`actions`.`target` AS `target`,`actions`.`act` AS `act`,`actions`.`parm` AS `parm` from (`actionrule` left join `actions` on(`actionrule`.`id` = `actions`.`rule`));

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
