SELECT pr.ip IP, lo.country state, lo.place area, pr.event_time, pr.qsys_version,  pr.input_data inputed, pager_quiz_items.item_text, pager_data.text_data
FROM pager_results pr
left join pager_data on pager_data.id =  pr.pager_data_id
left join pager_quiz_items on pager_quiz_items.id =  pr.quiz_id
left join location lo on pr.ip=lo.ip
where !isnull( pager_data.text_data)
and pr.event_time >'2015-10-01 00:00:01' and pr.event_time<'2015-12-30 23:59:59'
order by pr.event_time desc;