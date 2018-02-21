USE `qsystem`;

DROP PROCEDURE IF EXISTS `load_statistics_table`;
DELIMITER $$
CREATE DEFINER=`root`@`%` PROCEDURE `load_statistics_table`(out p_return_code INT,out p_mysql_errno int, out p_mysql_errtext varchar(2000))
BEGIN
Declare v_client_id Bigint;
declare v_service_id bigint;
Declare v_visit_id bigint;
Declare v_finished_visit boolean default False;
Declare v_finished_transaction boolean default False;
Declare v_client_welcome_time datetime;

Declare get_client cursor for
 select distinct client_id from statistic a
   where client_welcome_time > (select max(load_date) from statistic_load_parm)
   and client_id in (select client_id from statistic
                      where
                      client_id = a.client_id and
                      state_in in (0,10,13))
   order by client_id;

Declare Exit Handler for SQLEXCEPTION
Begin
   GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE,
    @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
  SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @text);
  SELECT @full_error;
  set p_return_code = 8;
End;

Begin
set p_return_code = 0;
select max(client_welcome_time)
 into v_client_welcome_time
from statistic;
End;

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

Call load_client_visit(v_client_id, p_return_code, p_mysql_errno, p_mysql_errtext);
Fetch get_client into v_client_id;

End Loop get_client_loop;
End;

Update statistic_load_parm
   set load_date = v_client_welcome_time;

END$$
DELIMITER ;

DROP FUNCTION IF EXISTS `get_wait_time`;
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `get_wait_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_wait_time int(11) default 0;

   select sum(time_to_sec(time(TimeDiff(client_invite_time,client_stand_time))))
      INTO v_wait_time
     from
      (select distinct client_invite_time, client_stand_time
       FROM statistic
        WHERE client_id = p_client_id
         and user_start_time is not null
         and time_to_sec(time(user_start_time)) > 0
         and time_to_sec(time(client_stand_time)) > 0
         and time_to_sec(time(client_invite_time)) > 0
         and state_in in (7,12)
         ) a;

 if v_wait_time is null then
    select sum(time_to_sec(time(TimeDiff(client_invite_time,client_stand_time))))
      INTO v_wait_time
     from
      (select distinct client_invite_time, client_stand_time
       FROM statistic
        WHERE client_id = p_client_id
         and time_to_sec(time(client_stand_time)) > 0
         and time_to_sec(time(client_invite_time)) > 0
         and state_in in (0)
         ) a;
 end if;

 if v_wait_time is null then
    set v_wait_time = 0;
 end if;

return (v_wait_time);
RETURN 1;
END$$
DELIMITER ;

DROP FUNCTION IF EXISTS `get_service_time`;
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `get_service_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_service_time int(11) default 0;
IF p_service_id IS NULL THEN
    select sum(a.service_time)
      INTO v_service_time
    from
       (SELECT service_id,
               time_to_sec(time(TimeDiff(user_finish_time,user_start_time))) service_time
        FROM statistic
        WHERE client_id = p_client_id
        and (user_start_time is not null and user_finish_time is not null and state_in in (10,11))
        and time_to_sec(time(user_start_time)) > 0
		and time_to_sec(time(user_finish_time)) > 0
        and time_to_sec(time(user_finish_time)) > time_to_sec(time(user_start_time))) a;
ELSE
select sum(a.service_time)
      INTO v_service_time
    from
       (SELECT service_id,
               time_to_sec(time(TimeDiff(user_finish_time,user_start_time))) service_time
        FROM statistic
        WHERE client_id = p_client_id
        and service_id = p_service_id
        and (user_start_time is not null and user_finish_time is not null and state_in in (10,11))
        and time_to_sec(time(user_start_time)) > 0
		and time_to_sec(time(user_finish_time)) > 0
        and time_to_sec(time(user_finish_time)) > time_to_sec(time(user_start_time))) a
        group by service_id;
END IF;
      if v_service_time is null then
         set v_service_time = 0;
	  end if;
      return (v_service_time);
RETURN 1;
END$$
DELIMITER ;
