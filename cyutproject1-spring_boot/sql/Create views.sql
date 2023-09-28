create definer = app01@`%` view db_spring_rest.item_no_content as
select `db_spring_rest`.`item`.`id`                                                                      AS `id`,
       `db_spring_rest`.`item`.`question_number`                                                         AS `question_number`,
       `db_spring_rest`.`item`.`title`                                                                   AS `title`,
       regexp_replace(`db_spring_rest`.`item`.`content`, '!\\[\\]\\(.+\\)|<img .+/>', 'img_placeholder') AS `content`,
       `db_spring_rest`.`item`.`a`                                                                       AS `a`,
       `db_spring_rest`.`item`.`b`                                                                       AS `b`,
       `db_spring_rest`.`item`.`c`                                                                       AS `c`,
       `db_spring_rest`.`item`.`max_info_theta`                                                          AS `max_info_theta`,
       `db_spring_rest`.`item`.`course_id`                                                               AS `course_id`
from `db_spring_rest`.`item`;

create definer = app01@`%` view db_spring_rest.testform_item_topic_vu as
select `ti`.`testform_id` AS `testform_id`, `ti`.`item_id` AS `item_id`, `it`.`topic_id` AS `topic_id`
from (`db_spring_rest`.`testform_items` `ti` join `db_spring_rest`.`item_topic` `it`
      on ((`ti`.`item_id` = `it`.`item_id`)));

