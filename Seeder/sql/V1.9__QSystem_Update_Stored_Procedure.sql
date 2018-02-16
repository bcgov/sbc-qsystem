USE `qsystem`;

DROP PROCEDURE IF EXISTS `insert_transaction_data`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `insert_transaction_data`(in p_client_id BIGINT, in p_service_id bigint, in p_visit_id BIGINT)
BEGIN
Declare v_hold_time int;
Declare v_client_id Bigint;
Declare v_date_id Bigint(20);
Declare v_load_date datetime;
Declare v_finished integer default 0;
Declare v_start_time datetime;
Declare v_found_ind boolean default TRUE;

Declare v_user_id integer(20) default 0;
Declare v_user_name varchar(150) default null;
Declare v_program_id bigint(20) default 0;
Declare v_channel varchar(45);
Declare v_program_name varchar(80);
Declare v_service_transaction varchar(100) default null;
Declare v_service_name varchar(80);
Declare v_service_quantity int(11) default 0;

Declare v_wait_interval int(5) default 30;
Declare v_time_interval int(5) default 5;


DECLARE CONTINUE HANDLER for NOT FOUND
Begin
  set v_found_ind = false;
End;

Begin

select user_id, user_start_time, channels, get_program_name(a.service_id), trim(b.name), get_service_quantity(a.client_id, a.service_id),
       b.prent_id, c.name, get_service_transaction(a.service_id)
    into v_user_id, v_start_time, v_channel, v_program_name, v_service_name, v_service_quantity,  v_program_id, v_user_name, v_service_transaction
      from
         statistic a,
         services b,
         users c
  where
      a.client_id = p_client_id
  and b.id = a.service_id
  and a.user_id = c.id
  and a.id = (select min(id) from statistic where client_id = a.client_id and service_id = a.service_id and (user_start_time is not null and state_in <> 12)
  and time_to_sec(time(user_start_time)) > 0
  and a.service_id = p_service_id);

select Year(CONVERT_TZ(v_start_time,'+00:00','-8:00'))*10000+Month(CONVERT_TZ(v_start_time,'+00:00','-8:00'))*100+Day(CONVERT_TZ(v_start_time,'+00:00','-8:00'))
 into v_date_id
from dual;

If  p_service_id is not null and v_found_ind then
insert into qstatistic.transactions
  select
       null,
       p_visit_id,
       office_id,
       office,
       office_type,
       v_user_id,
       v_user_name,
       client_id,
       v_date_id,
       if(v_program_name='Back Office',v_program_name,v_channel) channel,
       v_service_transaction,
       v_program_name,
       service_id,
       v_service_name,
       v_service_quantity,
       inaccurate_time_ind,
       CONVERT_TZ(v_start_time,'+00:00','-8:00'),
       start_interval,
       CONVERT_TZ(finish_time,'+00:00','-8:00'),
       citizen_wait_time,
       get_time_interval(citizen_wait_time,v_time_interval) wait_time_interval,
       prep_time,
       get_time_interval(prep_time,v_time_interval) prep_time_interval,
       service_time,
       get_time_interval(service_time,v_time_interval) service_time_interval,
       hold_time,
       get_time_interval(hold_time,v_time_interval) hold_time_interval,
       citizen_left_ind,
       hold_count
 from
(select
       office_id,
       get_office(office_id) office,
       get_office_type(office_id) office_type,
       user_id,
       client_id,
       service_id,
       if(MAX(state_in)=13,'Y','N') inaccurate_time_ind,
       get_start_time_interval(client_id,service_id, v_wait_interval) start_interval,
       if(state_in=10,user_finish_time,max(user_finish_time)) finish_time,
       get_wait_time(client_id, service_id) citizen_wait_time,
       get_prep_time(client_id,service_id) prep_time,
       get_service_time(client_id, service_id) service_time,
       get_hold_time(client_id, service_id) hold_time,
       if(min(state_in)=0,'Y','N') citizen_left_ind,
       get_hold_count(client_id,service_id) hold_count
from
 (select *
      from statistic
  where
      client_id = p_client_id
  and service_id = p_service_id
  and (user_start_time is not null and state_in <> 12)
  order by client_id, id) a
) b;

update qstatistic.transactions
 set inaccurate_time_ind = 'B'
 where
     client_id = p_client_id
 and date(CONVERT_TZ(start_time,'+00:00','-8:00')) <> date(CONVERT_TZ(finish_time,'+00:00','-8:00'));

End if;
End;

END ;;
DELIMITER ;


DROP PROCEDURE IF EXISTS `insert_visit_data`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `insert_visit_data`(in p_client_id BIGINT, out p_visit_id BIGINT)
BEGIN
Declare v_hold_time int;
Declare v_client_id Bigint;
Declare v_service_id Bigint;
Declare v_load_date datetime;
Declare v_start_time datetime;
Declare v_client_welcome_time datetime;
Declare v_finished integer default 0;
Declare v_reception_id integer(20) default 0;
Declare v_reception_name varchar(150) default null;
Declare v_user_id integer(20) default 0;
Declare v_user_name varchar(150) default null;
Declare v_program_id bigint(20) default 0;
Declare v_date_id bigint(20) default 0;
Declare v_channel varchar(45);
Declare v_program_name varchar(80) default null;
Declare v_service_transaction varchar(100);
Declare v_service_name varchar(80);
Declare v_service_quantity int(11) default 0;
Declare v_transaction_quantity int(11) default 0;
Declare v_wait_interval int(5) default 30;
Declare v_time_interval int(5) default 5;
Declare v_found_ind boolean default TRUE;

Begin
set v_reception_id = null;
set v_reception_name = null;

select user_id, b.name
    into v_reception_id, v_reception_name
      from
          statistic a,
          users b
  where
      client_id = p_client_id
  and get_office_type(a.office_id)='R'
  and a.id = (select min(id) from statistic where client_id = a.client_id)
  and b.id = a.user_id;
End;

Begin
DECLARE CONTINUE HANDLER for NOT FOUND
  set v_found_ind = false;

select user_id, user_start_time, channels, service_id, get_program_name(service_id), trim(b.name), get_service_quantity(client_id, service_id),
get_service_quantity(client_id, null), b.prent_id, client_welcome_time, c.name, get_service_transaction(service_id)
    into v_user_id, v_start_time, v_channel, v_service_id, v_program_name, v_service_name, v_service_quantity,  v_transaction_quantity, v_program_id,
         v_client_welcome_time, v_user_name, v_service_transaction
      from
         statistic a,
         services b,
         users c
  where
      client_id = p_client_id
  and user_start_time is not null
  and b.id = a.service_id
  and c.id = a.user_id
  and a.id = (select min(id) from statistic where client_id = p_client_id and (user_start_time is not null and state_in <> 12)
  and time_to_sec(time(user_start_time)) > 0);

select Year(CONVERT_TZ(v_start_time,'+00:00','-8:00'))*10000+Month(CONVERT_TZ(v_start_time,'+00:00','-8:00'))*100+Day(CONVERT_TZ(v_start_time,'+00:00','-8:00'))
 into v_date_id
from dual;

If v_found_ind then
 insert into qstatistic.visits
  select
       null,
       office_id,
       office,
       office_type,
       ifNull(v_reception_id,null) reception_id,
       if(v_reception_id is not null, v_reception_name, null) reception_name,
       v_user_id,
       v_user_name,
       client_id,
       v_date_id,
       if(v_program_name='Back Office',v_program_name,v_channel),
       v_service_id,
       v_service_name,
       v_service_quantity,
       v_service_transaction,
       v_program_id,
       v_program_name,
       program_quantity,
       v_transaction_quantity,
       hold_count,
       CONVERT_TZ(v_start_time,'+00:00','-8:00'),
       CONVERT_TZ(ticket_finish_time,'+00:00','-8:00'),
       reception_time,
       citizen_wait_time,
       service_time,
       citizen_hold_time,
       prep_time,
       client_time,
       inaccurate_time_ind,
       citizen_left_ind,
       CONVERT_TZ(v_client_welcome_time,'+00:00','-8:00'),
       visit_interval,
       get_time_interval(reception_time,v_time_interval) reception_interval,
       get_time_interval(citizen_wait_time,v_time_interval) wait_time_interval,
       get_time_interval(service_time,v_time_interval) service_interval,
       get_time_interval(citizen_hold_time,v_time_interval) hold_time_interval,
       get_time_interval(client_time,v_time_interval) client_time_interval
 from
(select
       office_id,
       get_office(office_id) office,
       get_office_type(office_id) office_type,
       client_id,
       get_program_quantity(client_id) program_quantity,
       get_hold_count(client_id, null) hold_count,
       get_welcome_time_interval(client_id, v_wait_interval) visit_interval,
       ticket_finish_time,
       get_reception_time(client_id) reception_time,
       get_wait_time(client_id, null) citizen_wait_time,
       get_service_time(client_id,null) service_time,
       get_hold_time(client_id,null) citizen_hold_time,
       get_prep_time(client_id,null) prep_time,
       get_client_time(client_id, null) client_time,
       inaccurate_time_ind,
       citizen_left_ind
from
 (select distinct client_id, office_id,  if(max(state_in)=13,'Y','N') inaccurate_time_ind, if(min(state_in)=0,'Y','N') citizen_left_ind,
       max(user_finish_time) ticket_finish_time
       from statistic
  where
      client_id = p_client_id
  and (user_start_time is not null and state_in <> 12)
  order by client_id, id) a
where a.client_id is not null) b;

update qstatistic.visits
 set inaccurate_time_ind = 'B'
 where
     client_id = p_client_id
 and date(CONVERT_TZ(start_time,'+00:00','-8:00')) <> date(CONVERT_TZ(finish_time,'+00:00','-8:00'));

Select LAST_INSERT_ID()
 into p_visit_id
from dual;

End if;
End;
END ;;
DELIMITER ;


DROP PROCEDURE IF EXISTS `load_client_visit`;
DELIMITER ;;
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

END ;;
DELIMITER ;


DROP FUNCTION IF EXISTS `get_time_interval`;
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `get_time_interval`(p_time int(11), p_time_interval int(5)) RETURNS varchar(20) CHARSET latin1
BEGIN
Declare v_interval varchar(20);

Select if(floor(p_time/(p_time_interval*60))*p_time_interval=floor(ceil(p_time/(p_time_interval*60))*(p_time_interval*60)/60),
concat(floor(p_time/(p_time_interval*60))*p_time_interval,'-',p_time_interval+floor(ceil(p_time/(p_time_interval*60))*(p_time_interval*60)/60)),
concat(floor(p_time/(p_time_interval*60))*p_time_interval,'-',floor(ceil(p_time/(p_time_interval*60))*(p_time_interval*60)/60))) intervals
into v_interval
FROM dual;


SELECT if(SUBSTRING(v_interval,LOCATE('-', v_interval)+1)>60,'Over 1HR',v_interval)
   into v_interval
from dual;

SELECT if(SUBSTRING(v_interval,1,2)='5-',concat('0',v_interval),v_interval)
   into v_interval
from dual;


return (v_interval);
RETURN 1;
END$$
DELIMITER ;
