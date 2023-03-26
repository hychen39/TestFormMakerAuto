
select * from testform;

-- Insert two rows to testform table
insert into testform values (1, sysdate(), 'Lorem ipsum dolor sit amet.', 'test form 1');
insert into testform values (2, sysdate(), 'She could not decide of the glass was half empty or half full so she drank it.', 'test form 2');
commit;

-- items in test forms
insert into testform_items values (1, 1);
insert into testform_items values (1, 2);
insert into testform_items values (1, 3);
insert into testform_items values (2, 4);
insert into testform_items values (2, 5);
insert into testform_items values (2, 1);
commit ;

-- items and topics
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (24, 7);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (34, 7);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (12, 7);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (12, 3);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (2, 24);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (2, 6);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (45, 7);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (78, 7);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (67, 6);
INSERT INTO db_spring_rest.item_topic (item_id, topic_id) VALUES (89, 11);
commit ;

select * from item;

select * from testform_items;


