-- 為 linked_items 加入 unique constraint of table level.
-- Unique(item_id, linked_item_id)
-- Manual added
-- 加入前，若有資料，應先清理。
alter table linked_items add constraint
    UniqueLinkRelation unique (item_id, linked_item_id);
