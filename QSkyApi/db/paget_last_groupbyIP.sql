SELECT max(pa.qsys_checkdb) custs, max(pa.qsys_usrs) usrs, max(pa.qsys_srvs) srvs, pa.ip, count(qsys_mac) cnt, 
 pa.qsys_version ver, IFNULL(ma.marker, pa.qsys_token) token, pa.qsys_mac mac, lo.country, lo.place, 
 (select pr.qsys_nm from pager_results pr where pr.pager_data_id is null and  pr.qsys_mac =pa.qsys_mac and  pr.qsys_token = pa.qsys_token and pr.qsys_version = pa.qsys_version order by pr.id desc limit 1) company
FROM pager_results pa
left join location lo on pa.ip=lo.ip
left join marker ma on pa.qsys_token=ma.token
where 
-- qsys_version='1.4.2'  and 
pa.pager_data_id is null and
pa.event_time >'2015-10-24 00:00:01' and pa.event_time<'2015-12-30 23:59:59'
-- and lo.place is null
 group by qsys_mac, qsys_token, pa.ip, ver order by lo.country, lo.place, cnt desc;