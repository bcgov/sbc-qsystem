#select  sum(cnt) from
#(
SELECT SUBSTRING_INDEX(ip, ".", 2) as supip,  count(ip) as cnt FROM `qsky`.`pager_results` group by supip order by cnt desc
#) as apl
;