USE `qsystem`;

DROP PROCEDURE IF EXISTS `get_hold_time`;
DELIMITER $$
CREATE DEFINER=`root`@`%` PROCEDURE `get_hold_time`(in p_client_id BIGINT, in p_service_id bigint, out p_hold_time int(11))
BEGIN
Declare v_hold_time int(11);
Declare v_reinvite_state int default 5;
Declare v_fetch_hold_time int(11);
Declare v_state_in int default 11;
Declare v_client_id Bigint;
declare v_service_id bigint;
Declare v_finished boolean default False;
Declare v_user_finish_time timestamp;
Declare v_id bigint;

Declare get_client_hold cursor for
SELECT id, client_id, user_finish_time
      FROM
        statistic
      WHERE state_in = v_state_in
       and client_id = p_client_id
       order by id;

Declare get_service_hold cursor for
SELECT id, client_id, user_finish_time
      FROM
        statistic
      WHERE state_in = v_state_in
       and client_id = p_client_id
       and service_id = p_service_id
       order by id;

Declare Continue Handler
    For Not Found Set v_finished = True;

Declare Exit Handler for SQLEXCEPTION
Begin
   GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE,
    @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
  SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @text);
  SELECT @full_error;
End;

set v_finished = False;
set v_hold_time = 0;

IF p_service_id IS NULL THEN
   open get_client_hold;
   Fetch get_client_hold into v_id, v_client_id, v_user_finish_time;

   get_client_hold_loop: Loop

      If v_finished then
         Close get_client_hold;
         Leave get_client_hold_loop;
      End if;

      select IFNULL(time_to_sec(time(TimeDiff(a.client_invite_time,v_user_finish_time))),0)
             into v_fetch_hold_time
      from
         (SELECT id, client_invite_time
          FROM statistic
          WHERE client_id = p_client_id
            and state_in = v_reinvite_state
            and id = (select min(id) from statistic
                      where client_id = p_client_id
                      and state_in = v_reinvite_state
                      and id > v_id)) a;

      set v_hold_time = v_hold_time + v_fetch_hold_time;
      Fetch get_client_hold into v_id, v_client_id, v_user_finish_time;

   End Loop get_client_hold_loop;
ELSE
   open get_service_hold;
   Fetch get_service_hold into v_id,v_client_id, v_user_finish_time;

   get_service_hold_loop: Loop

      If v_finished then
         Close get_service_hold;
         Leave get_service_hold_loop;
      End if;

      select IFNULL(time_to_sec(time(TimeDiff(a.client_invite_time,v_user_finish_time))),0)
             into v_fetch_hold_time
      from
         (SELECT id, client_invite_time
          FROM statistic
          WHERE client_id = p_client_id
		    and state_in = v_reinvite_state
			and service_id = p_service_id
            and id = (select min(id) from statistic
                      where client_id = p_client_id
					  and state_in = v_reinvite_state
					  and service_id = p_service_id
                      and id > v_id)) a;

      set v_hold_time = v_hold_time + v_fetch_hold_time;
      Fetch get_service_hold into v_id, v_client_id, v_user_finish_time;

   End Loop get_service_hold_loop;
END IF;

set p_hold_time = v_hold_time;

END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `insert_visit_data`;
DELIMITER $$
CREATE DEFINER=`root`@`%` PROCEDURE `insert_visit_data`(in p_client_id BIGINT, out p_visit_id BIGINT)
BEGIN
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
Declare v_hold_time int(11) default 0;
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
       0,
       prep_time,
       client_time,
       inaccurate_time_ind,
       citizen_left_ind,
       CONVERT_TZ(v_client_welcome_time,'+00:00','-8:00'),
       visit_interval,
       get_time_interval(reception_time,v_time_interval) reception_interval,
       get_time_interval(citizen_wait_time,v_time_interval) wait_time_interval,
       get_time_interval(service_time,v_time_interval) service_interval,
       get_time_interval(0,v_time_interval) hold_time_interval,
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

Call get_hold_time(p_client_id, null, v_hold_time);

if v_hold_time > 0 then
update qstatistic.visits
 set citizen_hold_time_sec = v_hold_time,
     hold_time_interval = get_time_interval(v_hold_time,v_time_interval)
 where
     client_id = p_client_id;
End if;

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
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `get_service_quantity`;
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `get_service_quantity`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_service_quantity int(11);

if p_service_id is null then
select sum(a.service_quantity)
INTO v_service_quantity
 from
    (SELECT service_id, CAST(service_quantity AS SIGNED INTEGER) service_quantity
       FROM
         statistic
     WHERE
         client_id = p_client_id
     and state_in in (10,13,0)
	 and id = (select max(id) from statistic
	           where client_id = p_client_id
			   and state_in in (10,13,0))
   ) a;
ELSE
select sum(CAST(service_quantity AS SIGNED INTEGER))
INTO v_service_quantity
 from statistic
     WHERE
         client_id = p_client_id
	 and service_id = p_service_id
     and state_in in (10,13,0)
	 and id = (select max(id) from statistic
	           where client_id = p_client_id
			   and service_id = p_service_id
			   and state_in in (10,13,0))
	 ;

End If;

       return (v_service_quantity);
RETURN 1;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `get_wait_time`;
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
         and state_in = 7
         ) a;

return (v_wait_time);
RETURN 1;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `insert_transaction_data`;

DELIMITER $$
CREATE DEFINER=`root`@`%` PROCEDURE `insert_transaction_data`(in p_client_id BIGINT, in p_service_id bigint, in p_visit_id BIGINT)
BEGIN
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
DECLARE v_hold_time int(11) default 0;

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
       0,
       get_time_interval(0,v_time_interval) hold_time_interval,
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

Call get_hold_time(p_client_id, p_service_id, v_hold_time);

if v_hold_time > 0 then
update qstatistic.transactions
 set hold_time = v_hold_time,
     hold_time_interval = get_time_interval(v_hold_time,v_time_interval)
 where
     client_id = p_client_id
	 and service_id = p_service_id;
end if;

update qstatistic.transactions
 set inaccurate_time_ind = 'B'
 where
     client_id = p_client_id
 and service_id = p_service_id
 and date(CONVERT_TZ(start_time,'+00:00','-8:00')) <> date(CONVERT_TZ(finish_time,'+00:00','-8:00'));

End if;
End;

END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `get_service_time`;
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `get_service_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_service_time int(11);
IF p_service_id IS NULL THEN
    select sum(a.service_time)
      INTO v_service_time
    from
       (SELECT service_id,
               time_to_sec(time(TimeDiff(user_finish_time,user_start_time))) service_time
        FROM statistic
        WHERE client_id = p_client_id
        and (user_start_time is not null and user_finish_time is not null and state_in in (7,8,10,11))
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
        and (user_start_time is not null and user_finish_time is not null and state_in in (7,8,10,11))
        and time_to_sec(time(user_start_time)) > 0
		and time_to_sec(time(user_finish_time)) > 0
        and time_to_sec(time(user_finish_time)) > time_to_sec(time(user_start_time))) a
        group by service_id;
END IF;

      return (v_service_time);
RETURN 1;
END$$
DELIMITER ;
