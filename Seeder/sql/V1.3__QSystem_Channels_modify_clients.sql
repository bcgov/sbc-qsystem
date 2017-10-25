ALTER TABLE `qsystem`.`clients` 
CHANGE COLUMN `channels` `channels` VARCHAR(45) NULL DEFAULT NULL ,
ADD COLUMN `channelsIndex` BIGINT(20) NOT NULL DEFAULT '1' AFTER `channels`;

ALTER TABLE `qsystem`.`statistic` 
CHANGE COLUMN `channels` `channels` VARCHAR(45) NULL DEFAULT NULL ,
ADD COLUMN `channelsIndex` BIGINT(20) NOT NULL DEFAULT '1' AFTER `channels`;


DELIMITER $$
DROP TRIGGER IF EXISTS `insert_to_statistic`$$

CREATE DEFINER=`root`@`%` TRIGGER insert_to_statistic
    AFTER INSERT ON clients
    FOR EACH ROW
BEGIN
    SET @finish_start= TIMEDIFF(NEW.finish_time, NEW.start_time);
    SET @start_starnd = TIMEDIFF(NEW.start_time, NEW.stand_time);
    INSERT
        INTO statistic(state_in, results_id, user_id, client_id, service_id, user_start_time, user_finish_time, client_stand_time, user_work_period, client_wait_period, client_welcome_time, client_invite_time, service_quantity, channelsIndex, channels)
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
        , NEW.welcome_time, New.invite_time, New.service_quantity, New.channelsIndex, New.channels);
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
        INTO statistic(state_in, results_id, user_id, client_id, service_id, user_start_time, user_finish_time, client_stand_time, user_work_period, client_wait_period, client_welcome_time, client_invite_time, service_quantity, channelsIndex, channels)
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
        , NEW.welcome_time, New.invite_time, New.service_quantity, New.channelsIndex, New.channels);
END$$
DELIMITER ;
