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

ALTER TABLE `statistic`
MODIFY COLUMN client_stand_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN user_start_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN user_finish_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN client_welcome_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN client_invite_time DATETIME NULL;

ALTER TABLE `statistic`
MODIFY COLUMN user_work_period INT NULL;

ALTER TABLE `statistic`
MODIFY COLUMN client_wait_period INT NULL;

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

DELIMITER $$
DROP TRIGGER IF EXISTS `insert_to_statistic`$$

CREATE DEFINER=`root`@`%` TRIGGER insert_to_statistic
    AFTER INSERT ON clients
    FOR EACH ROW
BEGIN
    SET @finish_start= TIMEDIFF(NEW.finish_time, NEW.start_time);
    SET @start_starnd = TIMEDIFF(NEW.start_time, NEW.stand_time);
    INSERT
        INTO statistic(state_in, results_id, user_id, client_id, service_id, user_start_time, user_finish_time, client_stand_time, user_work_period, client_wait_period, client_welcome_time, client_invite_time, service_quantity, channelsIndex, channels, office_id)
    VALUES
        (NEW.state_in, NEW.result_id, NEW.user_id, NEW.id, NEW.service_id, NEW.start_time, NEW.finish_time, NEW.stand_time,
        round(
                (HOUR(@finish_start) * 60 * 60 +
                 MINUTE(@finish_start) * 60 +
                 SECOND(@finish_start) + 59)/60),
        round(
                (HOUR(@start_starnd) * 60 * 60 +
                MINUTE(@start_starnd) * 60 +
                SECOND(@start_starnd) + 59)/60)
        , NEW.welcome_time, New.invite_time, New.service_quantity, New.channelsIndex, New.channels, New.office_id);
END$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS `update_to_statistic`$$

CREATE DEFINER=`root`@`%` TRIGGER update_to_statistic
    AFTER UPDATE ON clients
    FOR EACH ROW
BEGIN
    SET @finish_start= TIMEDIFF(NEW.finish_time, NEW.start_time);
    SET @start_starnd = TIMEDIFF(NEW.start_time, NEW.stand_time);
    INSERT
        INTO statistic(state_in, results_id, user_id, client_id, service_id, user_start_time, user_finish_time, client_stand_time, user_work_period, client_wait_period, client_welcome_time, client_invite_time, service_quantity, channelsIndex, channels, office_id)
    VALUES
        (NEW.state_in, NEW.result_id, NEW.user_id, NEW.id, NEW.service_id, NEW.start_time, NEW.finish_time, NEW.stand_time,
        round(
                (HOUR(@finish_start) * 60 * 60 +
                 MINUTE(@finish_start) * 60 +
                 SECOND(@finish_start) + 59)/60),
        round(
                (HOUR(@start_starnd) * 60 * 60 +
                MINUTE(@start_starnd) * 60 +
                SECOND(@start_starnd) + 59)/60)  
        , NEW.welcome_time, New.invite_time, New.service_quantity, New.channelsIndex, New.channels, New.office_id);
END$$
DELIMITER ;
