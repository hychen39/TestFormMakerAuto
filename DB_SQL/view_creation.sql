create view testform_item_topic_vu as
    select ti.testform_id, ti.item_id, it.topic_id
        from testform_items ti
            join item_topic it on ti.item_id = it.item_id;
