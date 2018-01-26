/*  File 01 start */
USE `qsystem`;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_client_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_client_time int(11);
IF p_service_id IS NULL THEN 
SELECT 
    time_to_sec(time(TIMEDIFF(MAX(user_finish_time),
            MIN(user_start_time))))
INTO v_client_time FROM
    statistic
WHERE
    client_id = p_client_id
    and (user_start_time is not null or state_in <> 12);
ELSE
SELECT 
    time_to_sec(time(TIMEDIFF(MAX(user_finish_time),
            MIN(user_start_time))))
INTO v_client_time FROM
    statistic
WHERE
    client_id = p_client_id
and (user_start_time is not null or state_in <> 12)    
and service_id = p_service_id;

End IF;

       return (v_client_time);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_hold_count`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_hold_count int(11);

if p_service_id is null then
select count(*)
  INTO v_hold_count 
 from
     statistic
 WHERE
     client_id = p_client_id
 and state_in = 11;
ELSE
select count(*)
  INTO v_hold_count 
 from
     statistic
 WHERE
     client_id = p_client_id
 and service_id = p_service_id
 and state_in = 11;
End If;

 return (v_hold_count);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_hold_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_hold_time int(11);
Declare v_state_in int default 11;
IF p_service_id IS NULL THEN 
 select  IFNULL(sum(time_to_sec(time(TimeDiff(b.client_invite_time,a.user_start_time)))),0)
      into v_hold_time
     from 
     (SELECT id, client_id, service_id,user_start_time, state_in
      FROM
       statistic
      WHERE state_in = v_state_in
       and client_id = p_client_id
       and time_to_sec(time(user_start_time)) > 0
       order by client_id, id, user_finish_time) a,
     (SELECT client_id, id, service_id, client_invite_time, state_in
      FROM
          statistic
      WHERE state_in <> v_state_in
      and client_id = p_client_id
      and (user_start_time is not null or state_in <> 12)
      and client_invite_time is not null
      and user_finish_time is not null
      and time_to_sec(time(client_invite_time)) > 0
      and time_to_sec(time(user_finish_time)) > 0
     order by client_id, id, service_id, user_start_time) b
    where
        a.client_id = b.client_id
    and a.service_id = b.service_id
   and b.id = (select min(id) from statistic
                where client_id = a.client_id
                and service_id = a.service_id
                and id > a.id
                and (user_start_time is not null or state_in <> 12)
                and time_to_sec(time(user_start_time)) > 0
                and state_in <> v_state_in);
ELSE 
 select  IFNULL(sum(time_to_sec(time(TimeDiff(b.client_invite_time,a.user_start_time)))),0)
      into v_hold_time
     from 
     (SELECT id, client_id, service_id,user_start_time, state_in
      FROM
        statistic
      WHERE state_in = v_state_in
       and client_id = p_client_id
       and service_id = p_service_id
       and time_to_sec(time(user_start_time)) > 0
       order by client_id, id, user_start_time) a,
     (SELECT client_id, id, service_id, client_invite_time, state_in
      FROM
          statistic
      WHERE state_in <> v_state_in
      and client_id = p_client_id
      and service_id = p_service_id
      and (user_start_time is not null or state_in <> 12)
      and time_to_sec(time(client_invite_time)) > 0
      and client_invite_time is not null
      and user_finish_time is not null      
     order by client_id, id, service_id, client_invite_time) b
    where
        a.client_id = b.client_id
    and a.service_id = b.service_id
    and b.id = (select min(id) from statistic
                where client_id = a.client_id
                and service_id = a.service_id
                and (user_start_time is not null or state_in <> 12)
                and client_invite_time is not null
                and user_finish_time is not null   
                and time_to_sec(time(client_invite_time)) > 0
                and time_to_sec(time(user_finish_time)) > 0
                and id > a.id
                and state_in <> v_state_in);
END IF;              
                
    return (v_hold_time);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_office`(p_office_id INT) RETURNS varchar(45) CHARSET latin1
Begin
       Declare v_office varchar(45);

SELECT trim(name)  
  into v_office  
 FROM offices
 WHERE id = p_office_id;

       return (v_office);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_office_type`(p_office_id INT) RETURNS varchar(1) CHARSET latin1
BEGIN
Declare v_office_type varchar(01);

SELECT if(upper(smartboard_type)='CALLBYNAME','R','N')
INTO v_office_type
 FROM offices
WHERE id = p_office_id;

 return (v_office_type);

RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_prep_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_prep_time int(11);

IF p_service_id IS NULL THEN
   select  Sum(time_to_sec(time(TimeDiff(user_start_time,client_invite_time))))
        INTO v_prep_time
    from
       (select distinct user_start_time, client_invite_time from statistic
        WHERE client_id = p_client_id
         and (user_start_time is not null and state_in <> 12)
         and time_to_sec(time(user_start_time)) > 0
         and state_in not in (11,5)) a;
ELSE
   select  Sum(time_to_sec(time(TimeDiff(user_start_time,client_invite_time))))
      INTO v_prep_time
    from
      (select distinct user_start_time, client_invite_time from statistic
        WHERE client_id = p_client_id
         and (user_start_time is not null and state_in <> 12)
         and time_to_sec(time(user_start_time)) > 0
         and state_in not in (11,5)) a;
END IF;

return (v_prep_time);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_program_name`(p_service_id BIGINT) RETURNS varchar(80) CHARSET latin1
BEGIN
Declare v_program_name varchar(80);

select trim(a.name)
INTO v_program_name 
from
    services a,
    services b
WHERE
    b.id = p_service_id
and a.id = b.prent_id;

return (v_program_name);

RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_program_quantity`(p_client_id BIGINT) RETURNS int(11)
BEGIN
Declare v_program_quantity int(11);

select count(distinct prent_id)
INTO v_program_quantity 
 from statistic a,
      services b
     WHERE
         a.client_id = p_client_id
	 and b.id = a.service_id;

return (v_program_quantity);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_reception_time`(p_client_id BIGINT) RETURNS int(11)
BEGIN
Declare v_reception_time int(11) default 0;
Declare v_found_ind boolean default TRUE;

DECLARE CONTINUE HANDLER for NOT FOUND
  set v_found_ind = false;

   select  time_to_sec(time(TimeDiff(client_stand_time,client_welcome_time)))
      INTO v_reception_time 
   from  statistic a
   WHERE client_id = p_client_id
     and state_in = 1
     and time_to_sec(time(client_welcome_time)) > 0
     and time_to_sec(time(client_stand_time)) > 0
     and id = (select max(id) from statistic
               where client_id = a.client_id
               and state_in = 1);

  if not v_found_ind then
    select  time_to_sec(time(TimeDiff(client_stand_time,client_welcome_time)))
      INTO v_reception_time 
   from  statistic a
   WHERE client_id = p_client_id
     and state_in in (10,13)
     and time_to_sec(time(client_welcome_time)) > 0
     and time_to_sec(time(client_stand_time)) > 0
     and id = (select min(id) from statistic
               where client_id = a.client_id
               and state_in in (10,13));
  End if;

  if (v_reception_time is null) then
     set v_reception_time = 0;
  End if;
  
return (v_reception_time);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_reception_time_interval`(p_client_id BIGINT, p_time_interval int(5)) RETURNS varchar(20) CHARSET latin1
BEGIN
Declare v_interval varchar(20);

Select if(floor(a.reception_time/(p_time_interval*60))*p_time_interval=floor(ceil(a.reception_time/(p_time_interval*60))*(p_time_interval*60)/60),
    concat(floor(a.reception_time/(p_time_interval*60))*p_time_interval,'-',p_time_interval+floor(ceil(a.reception_time/(p_time_interval*60))*(p_time_interval*60)/60)),
    concat(floor(a.reception_time/(p_time_interval*60))*p_time_interval,'-',floor(ceil(a.reception_time/(p_time_interval*60))*(p_time_interval*60)/60))) intervals
  into v_interval
FROM 
(select time_to_sec(time(TimeDiff(client_stand_time,client_welcome_time))) reception_time 
 from statistic
where 
     client_id = p_client_id
 and time_to_sec(time(client_stand_time)) > 0
 and time_to_sec(time(client_welcome_time)) > 0
 and state_in = 1) as a;

SELECT if(SUBSTRING(v_interval,LOCATE('-', v_interval)+1)>60,'Over 1HR',v_interval)
   into v_interval
from dual;

return (v_interval);

RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_service_name`(p_service_id BIGINT) RETURNS varchar(80) CHARSET latin1
BEGIN
Declare v_service_name varchar(80);

select trim(name)
INTO v_service_name 
from
    services
WHERE
    id = p_service_id;

  return (v_service_name);

RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_service_quantity`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_service_quantity int(11);

if p_service_id is null then
select sum(a.service_quantity)
INTO v_service_quantity 
 from
    (SELECT distinct service_id, CAST(service_quantity AS SIGNED INTEGER) service_quantity
       FROM
         statistic
     WHERE
         client_id = p_client_id
     and state_in in (10,13,0)) a;
ELSE
select sum(distinct CAST(service_quantity AS SIGNED INTEGER))
INTO v_service_quantity 
 from statistic
     WHERE
         client_id = p_client_id
	 and service_id = p_service_id
     and state_in in (10,13,0);

End If;

       return (v_service_quantity);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_service_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_service_time int(11);
IF p_service_id IS NULL THEN 
    select sum(a.service_time)
      INTO v_service_time 
    from
       (SELECT service_id,
               time_to_sec(time(TimeDiff(max(user_finish_time),min(user_start_time)))) service_time
        FROM statistic
        WHERE client_id = p_client_id
        and (user_start_time is not null and state_in <> 12)  
        and time_to_sec(time(user_start_time)) > 0
        and time_to_sec(time(user_finish_time)) > 0
        group by service_id) a;
ELSE
select sum(a.service_time)
      INTO v_service_time 
    from
       (SELECT service_id,
               time_to_sec(time(TimeDiff(max(user_finish_time),min(user_start_time)))) service_time
        FROM statistic
        WHERE client_id = p_client_id
        and service_id = p_service_id
        and (user_start_time is not null and state_in <> 12)
        and time_to_sec(time(user_start_time)) > 0
        and time_to_sec(time(user_finish_time)) > 0        
        group by service_id) a;
END IF;

      return (v_service_time);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_service_transaction`(p_service_id BIGINT) RETURNS varchar(100) CHARSET latin1
BEGIN
Declare v_service_transaction varchar(100) default null;

select trim(transaction_code)
INTO v_service_transaction
from
    qstatistic.services_transaction_code
WHERE
    id = p_service_id;

  return (v_service_transaction);

RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_start_time_interval`(p_client_id BIGINT, p_service_id BIGINT, p_time_interval int(5)) RETURNS varchar(20) CHARSET latin1
BEGIN
Declare v_interval varchar(20);

Select 
concat(time_format(sec_to_time(FLOOR(time_to_sec(time(CONVERT_TZ(user_start_time,'+00:00','-8:00')))/(p_time_interval*60))*(p_time_interval*60)),'%H:%i') ,'-',
time_format(sec_to_time(FLOOR(time_to_sec(time(CONVERT_TZ(user_start_time,'+00:00','-8:00')))/(p_time_interval*60))*(p_time_interval*60)+(p_time_interval*60)),'%H:%i')) 
  into v_interval
FROM statistic 
  where id = (select min(id) from statistic where client_id = p_client_id and service_id = p_service_id 
  and (user_start_time is not null and state_in <> 12));

SELECT if(SUBSTRING(v_interval,LOCATE('-', v_interval)+1)>60,'Over 1HR',v_interval)
   into v_interval
from dual;

return (v_interval);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
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
  
return (v_interval);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_wait_time`(p_client_id BIGINT, p_service_id bigint) RETURNS int(11)
BEGIN
Declare v_wait_time int(11);

IF p_service_id IS NULL THEN
     select sum(time_to_sec(time(TimeDiff(client_invite_time,client_stand_time))))
      INTO v_wait_time
      FROM statistic
        WHERE client_id = p_client_id
         and user_start_time is not null
         and time_to_sec(time(user_start_time)) > 0
         and time_to_sec(time(client_stand_time)) > 0
         and time_to_sec(time(client_invite_time)) > 0
         and state_in in (10,0,13);
ELSE
     select sum(time_to_sec(time(TimeDiff(client_invite_time,client_stand_time))))
      INTO v_wait_time
      FROM statistic
        WHERE client_id = p_client_id
		 and service_id = p_service_id
         and user_start_time is not null
         and time_to_sec(time(user_start_time)) > 0
         and time_to_sec(time(client_stand_time)) > 0
         and time_to_sec(time(client_invite_time)) > 0         
         and state_in in (10,0,13);
END IF;

return (v_wait_time);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_welcome_time_interval`(p_client_id BIGINT, p_time_interval int(5)) RETURNS varchar(20) CHARSET latin1
BEGIN
Declare v_interval varchar(20) default ' ';
Declare v_found_ind boolean default TRUE;

DECLARE CONTINUE HANDLER for NOT FOUND
  set v_found_ind = false;

Select 
concat(time_format(sec_to_time(FLOOR(time_to_sec(time(CONVERT_TZ(client_welcome_time,'+00:00','-8:00')))/(p_time_interval*60))*(p_time_interval*60)),'%H:%i') ,'-',
time_format(sec_to_time(FLOOR(time_to_sec(time(CONVERT_TZ(client_welcome_time,'+00:00','-8:00')))/(p_time_interval*60))*(p_time_interval*60)+(p_time_interval*60)),'%H:%i')) 
  into v_interval
FROM statistic 
  where id = (select min(id) from statistic where client_id = p_client_id and client_welcome_time is not null and state_in = 1
  and time_to_sec(time(client_welcome_time)) > 0);



  if not v_found_ind then
Select 
concat(time_format(sec_to_time(FLOOR(time_to_sec(time(CONVERT_TZ(client_welcome_time,'+00:00','-8:00')))/(p_time_interval*60))*(p_time_interval*60)),'%H:%i') ,'-',
time_format(sec_to_time(FLOOR(time_to_sec(time(CONVERT_TZ(client_welcome_time,'+00:00','-8:00')))/(p_time_interval*60))*(p_time_interval*60)+(p_time_interval*60)),'%H:%i')) 
  into v_interval
FROM statistic 
  where id = (select min(id) from statistic where client_id = p_client_id and client_welcome_time is not null and  state_in in (0,10,13)
   and time_to_sec(time(client_welcome_time)) > 0);
  End if;


return (v_interval);
RETURN 1;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `get_hold_time`(in p_client_id BIGINT, in p_service_id bigint, out p_hold_time int(11))
BEGIN
Declare v_hold_time int(11);
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
set v_hold_time = 0;

IF p_service_id IS NULL THEN 
   open get_client_hold;
   Fetch get_client_hold into v_id, v_user_finish_time;

   get_client_hold_loop: Loop

      If v_finished then
         Close get_client_hold;  
         Leave get_client_hold_loop;
      End if;

      select IFNULL(time_to_sec(time(TimeDiff(v_user_finish_time,a.user_finish_time))),0)
             into v_fetch_hold_time
      from 
         (SELECT id, user_start_time
          FROM statistic
          WHERE client_id = p_client_id
            and service_id = p_service_id
            and id = (select min(id) from statistic
                      where client_id = p_client_id
                      and service_id = p_service_id
                      and id > v_id)) a;

      set v_hold_time = v_hold_time + v_fetch_hold_time;
      Fetch get_client_hold into v_id, v_user_finish_time;

   End Loop get_client_hold_loop;
ELSE 
   open get_service_hold;
   Fetch get_service_hold into v_id, v_user_finish_time;

   get_service_hold_loop: Loop

      If v_finished then
         Close get_service_hold;  
         Leave get_service_hold_loop;
      End if;

      select IFNULL(time_to_sec(time(TimeDiff(v_user_finish_time,a.user_finish_time))),0)
             into v_fetch_hold_time
      from 
         (SELECT id, user_start_time
          FROM statistic
          WHERE client_id = p_client_id
            and id = (select min(id) from statistic
                      where client_id = p_client_id
                      and id > v_id)) a;

      set v_hold_time = v_hold_time + v_fetch_hold_time;
      Fetch get_service_hold into v_id, v_user_finish_time;

   End Loop get_service_hold_loop;
END IF;              

set p_hold_time = v_hold_time;

END ;;
DELIMITER ;

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

Declare Exit Handler for SQLEXCEPTION
   
Begin
  Rollback;
  Select 'An error has occurred in procedure insert_transaction_data.  The operation was rolled back and terminated';
  GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE, 
    @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
  SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @text);
  SELECT @full_error;
  
End;
 
 
Begin

DECLARE CONTINUE HANDLER for NOT FOUND
  set v_found_ind = false;

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

 
End if;
End;

END ;;
DELIMITER ;

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

Declare Exit Handler for SQLEXCEPTION
Begin
  Rollback;
  Select 'An error has occurred in procedure insert_visit_data.  The operation was rolled back and terminated';
  GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE, 
    @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
  SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @text);
  SELECT @full_error;
End;


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


Select LAST_INSERT_ID()
 into p_visit_id
from dual;

End if;
End;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `load_client_visit`(in p_client_id BIGINT, out p_return_code INT)
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
 
set p_return_code = 0;

END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `load_statistics_table`(out p_return_code INT)
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
   and client_welcome_time <= v_client_welcome_time 
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

Call load_client_visit(v_client_id, p_return_code);
Fetch get_client into v_client_id;

End Loop get_client_loop;
End; 
  
Update statistic_load_parm
   set load_date = v_client_welcome_time;

END ;;
DELIMITER ;
/*  File 01 end */

/*  File 02 start */
CREATE DATABASE  IF NOT EXISTS `qstatistic` ;
USE `qstatistic`;

DROP TABLE IF EXISTS `services_transaction_code`;
CREATE TABLE `services_transaction_code` (
  `id` bigint(20) NOT NULL,
  `name` varchar(2000) NOT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `prent_id` bigint(20) DEFAULT NULL,
  `transaction_code` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`prent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='?????? ?????';
/*  File 02 end */

/*  File 03 start */
USE `qsystem`;

DROP TABLE IF EXISTS `statistic_load_parm`;
CREATE TABLE `statistic_load_parm` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `load_date` datetime NOT NULL COMMENT 'Last load date.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COMMENT='Table is used to capture the last load date for the statistic table';
/*  File 03 end */

/*  File 04 start */
CREATE DATABASE  IF NOT EXISTS `qstatistic` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `qstatistic`;

DROP TABLE IF EXISTS `transactions`;
CREATE TABLE `transactions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `visit_id` bigint(20) NOT NULL,
  `office_id` bigint(20) NOT NULL,
  `office` varchar(45) NOT NULL,
  `office_type` varchar(1) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `user_name` varchar(150) DEFAULT NULL,
  `client_id` bigint(20) NOT NULL,
  `date_id` bigint(20) NOT NULL,
  `channel` varchar(45) DEFAULT NULL,
  `service_transaction_code` varchar(100) DEFAULT NULL,
  `program_name` varchar(80) DEFAULT NULL,
  `service_id` bigint(20) NOT NULL,
  `service_name` varchar(80) DEFAULT NULL,
  `service_quantity` int(11) DEFAULT '0',
  `inaccurate_time_ind` varchar(1) NOT NULL,
  `start_time` datetime NOT NULL,
  `start_interval` varchar(20) NOT NULL,
  `finish_time` datetime NOT NULL COMMENT '????? ?????? ????????? ????????? ??????.',
  `citizen_wait_time` int(11) DEFAULT '0' COMMENT '????? ?????? ????????? ????????? ??????.',
  `wait_time_interval` varchar(20) NOT NULL,
  `prep_time` int(11) NOT NULL DEFAULT '0' COMMENT '????? ?????? ????????? ????????? ??????.',
  `prep_interval` varchar(20) NOT NULL,
  `service_time` int(11) DEFAULT '0' COMMENT '????? ?????? ????????? ????????? ??????.',
  `service_interval` varchar(20) NOT NULL,
  `hold_time` int(11) DEFAULT '0' COMMENT '????? ?????? ????????? ????????? ??????.',
  `hold_time_interval` varchar(20) NOT NULL,
  `citizen_left_ind` varchar(1) NOT NULL,
  `hold_count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_users_id` (`user_id`),
  KEY `idx_client_id` (`client_id`,`id`,`service_id`),
  KEY `idx_office` (`office`),
  KEY `idx_work_service_id_services_id` (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11035 DEFAULT CHARSET=latin1 COMMENT='??????? ?????? ???????????? ? ????????.??????????? ?????????';
/*  File 04 end */

/*  File 05 start */
CREATE DATABASE  IF NOT EXISTS `qstatistic` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `qstatistic`;


DROP TABLE IF EXISTS `visits`;
CREATE TABLE `visits` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `office` varchar(45) NOT NULL,
  `office_type` varchar(1) NOT NULL,
  `reception_id` bigint(20) DEFAULT NULL,
  `reception_name` varchar(150) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `user_name` varchar(150) DEFAULT NULL,
  `client_id` bigint(20) NOT NULL,
  `date_id` bigint(20) NOT NULL,
  `channel` varchar(45) DEFAULT NULL,
  `service_id` bigint(20) NOT NULL,
  `service_name` varchar(80) DEFAULT NULL,
  `service_quantity` int(11) DEFAULT '0',
  `service_transaction_code` varchar(100) DEFAULT NULL,
  `program_id` bigint(20) NOT NULL,
  `program_name` varchar(80) DEFAULT NULL,
  `program_quantity` int(11) DEFAULT '0',
  `transaction_quantity` int(11) DEFAULT '0',
  `hold_quantity` int(11) DEFAULT '0',
  `start_time` datetime NOT NULL,
  `finish_time` datetime NOT NULL,
  `reception_time_sec` int(11) NOT NULL DEFAULT '0',
  `citizen_wait_time_sec` int(11) DEFAULT '0',
  `citizen_service_time_sec` int(11) DEFAULT '0',
  `citizen_hold_time_sec` int(11) DEFAULT '0',
  `citizen_prep_time_sec` int(11) NOT NULL DEFAULT '0',
  `citizen_experience_time_sec` int(11) NOT NULL DEFAULT '0',
  `inaccurate_time_ind` varchar(1) NOT NULL,
  `left_ind` varchar(1) NOT NULL,
  `client_welcome_time` datetime NOT NULL,
  `visit_interval` varchar(20) NOT NULL,
  `reception_interval` varchar(20) NOT NULL,
  `wait_time_interval` varchar(20) NOT NULL,
  `service_interval` varchar(20) NOT NULL,
  `hold_time_interval` varchar(20) NOT NULL,
  `client_time_interval` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_users_id` (`user_id`,`id`),
  KEY `idx_client_id` (`client_id`,`id`),
  KEY `idx_office` (`office_id`,`id`),
  KEY `idx_services_id` (`service_id`,`id`),
  KEY `idx_program_name` (`program_id`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10394 DEFAULT CHARSET=latin1;
/*  File 05 end */

/*  File 06 start */
CREATE DATABASE  IF NOT EXISTS `qstatistic` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `qstatistic`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `transactions_days` AS select `transactions`.`id` AS `id`,`transactions`.`visit_id` AS `visit_id`,`transactions`.`office_id` AS `office_id`,`transactions`.`office` AS `office`,`transactions`.`office_type` AS `office_type`,`transactions`.`user_id` AS `user_id`,`transactions`.`client_id` AS `client_id`,`transactions`.`date_id` AS `date_id`,`transactions`.`channel` AS `channel`,`transactions`.`program_name` AS `program_name`,`transactions`.`service_id` AS `service_id`,`transactions`.`service_name` AS `service_name`,`transactions`.`service_transaction_code` AS `service_transaction_code`,`transactions`.`service_quantity` AS `service_quantity`,`transactions`.`inaccurate_time_ind` AS `inaccurate_time_ind`,`transactions`.`start_time` AS `start_time`,`transactions`.`start_interval` AS `start_interval`,`transactions`.`finish_time` AS `finish_time`,round((`transactions`.`citizen_wait_time` / 86400),7) AS `citizen_wait_time`,`transactions`.`wait_time_interval` AS `wait_time_interval`,round((`transactions`.`prep_time` / 86400),7) AS `prep_time`,`transactions`.`prep_interval` AS `prep_interval`,round((`transactions`.`service_time` / 86400),7) AS `service_time`,`transactions`.`service_interval` AS `service_interval`,round((`transactions`.`hold_time` / 86400),7) AS `hold_time`,`transactions`.`hold_time_interval` AS `hold_time_interval`,`transactions`.`citizen_left_ind` AS `citizen_left_ind`,`transactions`.`hold_count` AS `hold_count` from `transactions`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`127.0.0.1` SQL SECURITY DEFINER VIEW `visits_days` AS select `visits`.`id` AS `id`,`visits`.`office_id` AS `office_id`,`visits`.`office` AS `office`,`visits`.`office_type` AS `office_type`,`visits`.`reception_id` AS `reception_id`,`visits`.`reception_name` AS `reception_name`,`visits`.`user_id` AS `user_id`,`visits`.`user_name` AS `user_name`,`visits`.`client_id` AS `client_id`,`visits`.`date_id` AS `date_id`,`visits`.`channel` AS `channel`,`visits`.`service_id` AS `service_id`,`visits`.`service_name` AS `service_name`,`visits`.`service_quantity` AS `service_quantity`,`visits`.`service_transaction_code` AS `service_transaction_code`,`visits`.`program_id` AS `program_id`,`visits`.`program_name` AS `program_name`,`visits`.`program_quantity` AS `program_quantity`,`visits`.`transaction_quantity` AS `transaction_quantity`,`visits`.`hold_quantity` AS `hold_quantity`,`visits`.`start_time` AS `start_time`,`visits`.`finish_time` AS `finish_time`,round((`visits`.`reception_time_sec` / 86400),7) AS `reception_time`,round((`visits`.`citizen_wait_time_sec` / 86400),7) AS `citizen_wait_time`,round((`visits`.`citizen_service_time_sec` / 86400),7) AS `citizen_service_time`,round((`visits`.`citizen_hold_time_sec` / 86400),7) AS `citizen_hold_time`,round((`visits`.`citizen_prep_time_sec` / 86400),7) AS `citizen_prep_time`,round((`visits`.`citizen_experience_time_sec` / 86400),7) AS `citizen_experience_time`,`visits`.`inaccurate_time_ind` AS `inaccurate_time_ind`,`visits`.`left_ind` AS `left_ind`,`visits`.`client_welcome_time` AS `client_welcome_time`,`visits`.`visit_interval` AS `visit_interval`,`visits`.`reception_interval` AS `reception_interval`,`visits`.`wait_time_interval` AS `wait_time_interval`,`visits`.`service_interval` AS `service_interval`,`visits`.`hold_time_interval` AS `hold_time_interval`,`visits`.`client_time_interval` AS `client_time_interval` from `visits`;
/*  File 06 end */
