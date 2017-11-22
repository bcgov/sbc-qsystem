CREATE TABLE IF NOT EXISTS `offices` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `smartboard_type` VARCHAR(45) NOT NULL DEFAULT 'callbyticket',
  `deleted` DATETIME,
  CONSTRAINT `offices.pk` PRIMARY KEY (`id`)
  );

INSERT INTO `offices` (name)    
select 'Test Office' from dual   
WHERE NOT EXISTS (SELECT 1 FROM `offices` where name = 'Test Office');

ALTER TABLE `users` ADD COLUMN office_id int(11) NOT NULL DEFAULT 1;
ALTER TABLE `users` 
ADD INDEX `fk_users_office_idx` (`office_id` ASC);
ALTER TABLE `users` 
ADD CONSTRAINT `fk_users_office`
  FOREIGN KEY (`office_id`)
  REFERENCES `offices`(`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `clients`
MODIFY COLUMN stand_time DATETIME NULL;

ALTER TABLE `clients`
MODIFY COLUMN start_time DATETIME NULL;

ALTER TABLE `clients`
MODIFY COLUMN finish_time DATETIME NULL;

ALTER TABLE `clients`
MODIFY COLUMN welcome_time DATETIME NULL;

ALTER TABLE `clients`
MODIFY COLUMN invite_time DATETIME NULL;

ALTER TABLE `clients`
MODIFY COLUMN stand_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN user_start_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN user_finish_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN client_welcome_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN client_invite_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN user_work_period DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN client_wait_period DATETIME NULL;

ALTER TABLE `statistic`
ADD COLUMN office_id int(11) NOT NULL DEFAULT 1;

ALTER TABLE `clients`
ADD COLUMN office_id int(11) NOT NULL DEFAULT 1;

ALTER TABLE `clients` 
ADD INDEX `fk_clients_office_idx` (`office_id` ASC);

ALTER TABLE `clients` 
ADD CONSTRAINT `fk_clients_office`
  FOREIGN KEY (`office_id`)
  REFERENCES `offices` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

CREATE TABLE `services_offices` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_id` bigint(20) NOT NULL,
  `office_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_service_office_service_id` (`service_id`),
  KEY `idx_service_office_office_id` (`office_id`),
  CONSTRAINT `fk_service_office_service_id` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_service_office_office_id` FOREIGN KEY (`office_id`) REFERENCES `offices` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE `services` ADD COLUMN smartboard_yn char(1) NOT NULL DEFAULT 'Y';

ALTER TABLE `clients` ADD COLUMN comments varchar(2500);
