-- Link types seed data
insert into link_types(name) values ('Translated');
insert into link_types(name) value ('Similar High');
insert into link_types(name) value ('Similar Low');
insert into link_types(name) value ('Similar');
commit;

delete from link_types;
