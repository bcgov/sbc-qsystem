CREATE TABLE IF NOT EXISTS `trackactions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `time_now` datetime DEFAULT NULL,
  `button_clicked` varchar(45) NOT NULL DEFAULT '',
  `start_finish` varchar(45) NOT NULL DEFAULT '',
  `office_id` int(11) NOT NULL DEFAULT '1',
  `user_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `ticket` varchar(45) DEFAULT '',
  `service_id` bigint(20) DEFAULT NULL,
  `state_in` int(11) NOT NULL DEFAULT '0',
  `user_quick` tinyint(1) NOT NULL DEFAULT '0',
  `client_quick` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=latin1;
