USE `qsystem`;

CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`%` 
    SQL SECURITY DEFINER
VIEW `qstatistic`.`ga_office_transactions` AS
    SELECT 
        `qstatistic`.`transactions`.`id` AS `id`,
        `qstatistic`.`transactions`.`visit_id` AS `visit_id`,
        `qstatistic`.`transactions`.`office_id` AS `office_id`,
        `qstatistic`.`transactions`.`office` AS `office`,
        `qstatistic`.`transactions`.`office_type` AS `office_type`,
        `qstatistic`.`transactions`.`user_id` AS `user_id`,
        `qstatistic`.`transactions`.`client_id` AS `client_id`,
        `qstatistic`.`transactions`.`date_id` AS `date_id`,
        `qstatistic`.`transactions`.`channel` AS `channel`,
        `qstatistic`.`transactions`.`program_name` AS `program_name`,
        `qstatistic`.`transactions`.`service_id` AS `service_id`,
        `qstatistic`.`transactions`.`service_name` AS `service_name`,
        `qstatistic`.`transactions`.`service_transaction_code` AS `service_transaction_code`,
        `qstatistic`.`transactions`.`service_quantity` AS `service_quantity`,
        `qstatistic`.`transactions`.`inaccurate_time_ind` AS `inaccurate_time_ind`,
        `qstatistic`.`transactions`.`start_time` AS `start_time`,
        `qstatistic`.`transactions`.`start_interval` AS `start_interval`,
        `qstatistic`.`transactions`.`finish_time` AS `finish_time`,
        `qstatistic`.`transactions`.`citizen_wait_time` AS `citizen_wait_time`,
        `qstatistic`.`transactions`.`wait_time_interval` AS `wait_time_interval`,
        `qstatistic`.`transactions`.`prep_time` AS `prep_time`,
        `qstatistic`.`transactions`.`prep_interval` AS `prep_interval`,
        `qstatistic`.`transactions`.`service_time` AS `service_time`,
        `qstatistic`.`transactions`.`service_interval` AS `service_interval`,
        `qstatistic`.`transactions`.`hold_time` AS `hold_time`,
        `qstatistic`.`transactions`.`hold_time_interval` AS `hold_time_interval`,
        `qstatistic`.`transactions`.`citizen_left_ind` AS `citizen_left_ind`,
        `qstatistic`.`transactions`.`hold_count` AS `hold_count`
    FROM
        `qstatistic`.`transactions`
	where office <> 'Test Office'
	;

CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`127.0.0.1` 
    SQL SECURITY DEFINER
VIEW `qstatistic`.`ga_office_visits` AS
    SELECT 
        `qstatistic`.`visits`.`id` AS `id`,
        `qstatistic`.`visits`.`office_id` AS `office_id`,
        `qstatistic`.`visits`.`office` AS `office`,
        `qstatistic`.`visits`.`office_type` AS `office_type`,
        `qstatistic`.`visits`.`reception_id` AS `reception_id`,
        `qstatistic`.`visits`.`reception_name` AS `reception_name`,
        `qstatistic`.`visits`.`user_id` AS `user_id`,
        `qstatistic`.`visits`.`user_name` AS `user_name`,
        `qstatistic`.`visits`.`client_id` AS `client_id`,
        `qstatistic`.`visits`.`date_id` AS `date_id`,
        `qstatistic`.`visits`.`channel` AS `channel`,
        `qstatistic`.`visits`.`service_id` AS `service_id`,
        `qstatistic`.`visits`.`service_name` AS `service_name`,
        `qstatistic`.`visits`.`service_quantity` AS `service_quantity`,
        `qstatistic`.`visits`.`service_transaction_code` AS `service_transaction_code`,
        `qstatistic`.`visits`.`program_id` AS `program_id`,
        `qstatistic`.`visits`.`program_name` AS `program_name`,
        `qstatistic`.`visits`.`program_quantity` AS `program_quantity`,
        `qstatistic`.`visits`.`transaction_quantity` AS `transaction_quantity`,
        `qstatistic`.`visits`.`hold_quantity` AS `hold_quantity`,
        `qstatistic`.`visits`.`start_time` AS `start_time`,
        `qstatistic`.`visits`.`finish_time` AS `finish_time`,
        `qstatistic`.`visits`.`reception_time_sec` AS `reception_time`,
        `qstatistic`.`visits`.`citizen_wait_time_sec` AS `citizen_wait_time`,
        `qstatistic`.`visits`.`citizen_service_time_sec` AS `citizen_service_time`,
        `qstatistic`.`visits`.`citizen_hold_time_sec` AS `citizen_hold_time`,
        `qstatistic`.`visits`.`citizen_prep_time_sec` AS `citizen_prep_time`,
        `qstatistic`.`visits`.`citizen_experience_time_sec` AS `citizen_experience_time`,
        `qstatistic`.`visits`.`inaccurate_time_ind` AS `inaccurate_time_ind`,
        `qstatistic`.`visits`.`left_ind` AS `left_ind`,
        `qstatistic`.`visits`.`client_welcome_time` AS `client_welcome_time`,
        `qstatistic`.`visits`.`visit_interval` AS `visit_interval`,
        `qstatistic`.`visits`.`reception_interval` AS `reception_interval`,
        `qstatistic`.`visits`.`wait_time_interval` AS `wait_time_interval`,
        `qstatistic`.`visits`.`service_interval` AS `service_interval`,
        `qstatistic`.`visits`.`hold_time_interval` AS `hold_time_interval`,
        `qstatistic`.`visits`.`client_time_interval` AS `client_time_interval`
    FROM
        `qstatistic`.`visits`
	where office <> 'Test Office'; 

DROP FUNCTION IF EXISTS `get_service_quantity`;
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `get_service_quantity`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_service_quantity int(11) default 1;

if p_service_id is null then
select max(service_quantity)
INTO v_service_quantity
 from
     statistic
  WHERE
         client_id = p_client_id
	 and id = (select min(id) from statistic
	           where client_id = p_client_id
			   and state_in in (10,13,0))
   ;
ELSE
select max(service_quantity) 
INTO v_service_quantity
 from statistic
     WHERE
         client_id = p_client_id
	 and service_id = p_service_id
	 ;

End If;

       return (v_service_quantity);
RETURN 1;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `load_client_visit`;
DELIMITER $$
CREATE DEFINER=`root`@`%` PROCEDURE `load_client_visit`(in p_client_id BIGINT, out p_return_code INT, out p_mysql_errno int, out p_mysql_errtext varchar(2000))
BEGIN
Declare v_client_id Bigint;
declare v_service_id bigint;
Declare v_visit_id bigint;
Declare v_finished_visit boolean default False;
Declare v_finished_transaction boolean default False;

Declare get_client cursor for
 select distinct client_id from statistic a
   where client_id = p_client_id
   and client_id in (select client_id from statistic
                      where
                      client_id = a.client_id and
                      state_in in (0,10,13))
   order by id;

Declare get_client_service cursor for
 select distinct client_id, service_id from statistic a
   where client_id = v_client_id
   and state_in in (0,10,13)
   order by client_id;

Declare Exit Handler for SQLEXCEPTION
Begin
   GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE,
    @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
  SET @full_error = CONCAT("LOAD CLIENT VISIT ERROR ", @errno, " (", @sqlstate, "): ", @text);
  SELECT @full_error;
  set p_return_code = 8;
  set p_mysql_errno = @errno;
  set p_mysql_errtext = @full_error;
End;

set p_return_code = 0;
set p_mysql_errno = 0;
set p_mysql_errtext = ' ';
set v_finished_visit = False;
open get_client;
Begin
Declare Continue Handler
    For Not Found Set v_finished_visit = True;

Fetch get_client into v_client_id;

get_client_loop: Loop

If v_finished_visit then
 Close get_client;
 Leave get_client_loop;
End if;

Call insert_visit_data(v_client_id, v_visit_id);

Set v_finished_transaction = False;
open get_client_service;


Begin
Declare Continue Handler
    For Not Found Set v_finished_transaction = True;

Fetch get_client_service into v_client_id, v_service_id;
get_client_service_loop: Loop

If v_finished_transaction then
 Close get_client_service;
 Leave get_client_service_loop;
End if;

Call insert_transaction_data(v_client_id, v_service_id, v_visit_id);

Fetch get_client_service into v_client_id, v_service_id;

End Loop get_client_service_loop;
End;

Fetch get_client into v_client_id;

End Loop get_client_loop;
End;

END$$
DELIMITER ;
