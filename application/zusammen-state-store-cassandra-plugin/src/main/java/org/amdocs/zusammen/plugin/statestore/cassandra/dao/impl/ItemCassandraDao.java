package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.Item;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.zusammen.utils.fileutils.json.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemCassandraDao implements ItemDao {

  @Override
  public void create(SessionContext context, Id itemId, Info itemInfo) {
    update(context, itemId, itemInfo);
  }

  @Override
  public void update(SessionContext context, Id itemId, Info itemInfo) {
    getAccessor(context).save(itemId.getValue(), JsonUtil.object2Json(itemInfo));
  }

  @Override
  public void delete(SessionContext context, Id itemId) {
    getAccessor(context).delete(itemId.getValue());
  }

  @Override
  public Optional<Item> get(SessionContext context, Id itemId) {
    Row row = getAccessor(context).get(itemId.getValue()).one();
    return row == null ? Optional.empty() : Optional.of(createItem(row));
  }

  @Override
  public List<Item> list(SessionContext context) {
    List<Row> rows = getAccessor(context).list().all();
    return rows == null ? new ArrayList<>()
        : rows.stream().map(this::createItem).collect(Collectors.toList());
  }

  private Item createItem(Row row) {
    Item item = new Item();
    item.setId(new Id(row.getString(ItemField.ITEM_ID)));
    item.setInfo(JsonUtil.json2Object(row.getString(ItemField.ITEM_INFO), Info.class));
    return item;
  }

  private ItemAccessor getAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, ItemAccessor.class);
  }

  @Accessor
  interface ItemAccessor {

    @Query("INSERT INTO item (item_id, item_info) VALUES (?,?)")
    void save(String itemId, String itemInfo);

    @Query("DELETE FROM item WHERE item_id=?")
    void delete(String itemId);

    @Query("SELECT item_id, item_info FROM item WHERE item_id=?")
    ResultSet get(String itemId);

    @Query("SELECT item_id, item_info FROM item")
    ResultSet list();
  }

  private static final class ItemField {
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_INFO = "item_info";
  }
}
