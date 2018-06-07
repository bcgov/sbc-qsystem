USE `qsystem`;

DROP PROCEDURE IF EXISTS `get_wait_time`;
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `get_wait_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_wait_time int(11) default 0;

   select time_to_sec(time(TimeDiff(client_invite_time,client_stand_time)))
      INTO v_wait_time
     from
      (select  client_stand_time, max(client_invite_time) client_invite_time
       FROM statistic
        WHERE client_id = p_client_id
         and user_start_time is not null
         and time_to_sec(time(user_start_time)) > 0
         and time_to_sec(time(client_stand_time)) > 0
         and time_to_sec(time(client_invite_time)) > 0
         and state_in in (7,12)
		 group by client_stand_time
         ) a;

 if v_wait_time is null then
    select time_to_sec(time(TimeDiff(client_invite_time,client_stand_time)))
      INTO v_wait_time
     from
      (select  client_stand_time, max(client_invite_time) client_invite_time
       FROM statistic
        WHERE client_id = p_client_id
         and time_to_sec(time(client_stand_time)) > 0
         and time_to_sec(time(client_invite_time)) > 0
         and state_in in (0)
         group by client_stand_time
         ) a;
 end if;

 if v_wait_time is null then
    set v_wait_time = 0;
 end if;

return (v_wait_time);
RETURN 1;

END$$
DELIMITER ;