
-- Query table constraints
select *
from information_schema.table_constraints
where constraint_schema = 'db_spring_rest' and table_name = 'form_item';

-- drop the FK constraint from the child table
alter table form_item drop foreign key FKcixsxoa4nc7l3x7itkd4rdl1e;
-- drop the parent table
drop table test_form cascade;



