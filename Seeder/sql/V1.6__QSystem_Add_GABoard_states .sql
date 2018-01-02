ALTER TABLE `users` 
ADD COLUMN `current_state` TINYINT(1)  NOT NULL DEFAULT '0';

ALTER TABLE `users` 
ADD COLUMN `current_service` VARCHAR(145)  NULL DEFAULT NULL AFTER `office_id`;

