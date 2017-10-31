ALTER TABLE `clients` 
ADD COLUMN `previous_service` VARCHAR(145) NULL DEFAULT NULL AFTER `channelsIndex`;

