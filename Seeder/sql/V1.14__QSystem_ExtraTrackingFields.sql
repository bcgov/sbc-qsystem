ALTER TABLE `trackactions`

ADD COLUMN `channel` VARCHAR(45) NULL DEFAULT '' AFTER `client_quick`,

ADD COLUMN `quantity` VARCHAR(45) NOT NULL DEFAULT '1' AFTER `channel`,

ADD COLUMN `priority` INT(11) NOT NULL DEFAULT '1' AFTER `quantity`,

ADD COLUMN `srv_user_id` BIGINT(20) NULL DEFAULT NULL AFTER `priority`,
ADD COLUMN `srv_quick` TINYINT(1) NOT NULL DEFAULT '0' AFTER `srv_user_id`;
