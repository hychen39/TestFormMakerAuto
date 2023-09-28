-- link types
create table link_types (
                            id bigint auto_increment primary key ,
                            name varchar(100)
)
    comment 'item 間的連結型態文字';

create table linked_items (
                              id bigint auto_increment primary key ,
                              item_id bigint references item(id) on delete set null ,
                              linked_item_id bigint references item(id) on delete set null ,
                              link_type_id bigint references link_types(id) on delete set null
)
    comment 'item 之間的連結，可多對多關係';
