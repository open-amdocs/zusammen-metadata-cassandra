package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.tsuzammen.commons.datatypes.Id;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.commons.datatypes.item.Item;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemCassandraDao implements ItemDao {

  private static final class ItemField {
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_INFO = "item_info";
  }

  @Override
  public void create(SessionContext context, Id itemId, Info itemInfo) {
    save(context, itemId, itemInfo);
  }

  @Override
  public void save(SessionContext context, Id itemId, Info itemInfo) {
    CassandraDaoUtils.getAccessor(context, ItemAccessor.class).
        save(itemId.getValue().toString(), JsonUtil.object2Json(itemInfo));
  }

  @Override
  public void delete(SessionContext context, Id itemId) {
    CassandraDaoUtils.getAccessor(context, ItemAccessor.class).delete(itemId.getValue().toString());
  }

  @Override
  public Optional<Item> get(SessionContext context, Id itemId) {
    Row row = CassandraDaoUtils.getAccessor(context, ItemAccessor.class).get(itemId.getValue().toString()).one();
    return row == null ? Optional.empty() : Optional.of(createItem(row));
  }

  @Override
  public List<Item> list(SessionContext context) {
    List<Row> rows = CassandraDaoUtils.getAccessor(context, ItemAccessor.class).list().all();
    return rows == null ? new ArrayList<>()
        : rows.stream().map(this::createItem).collect(Collectors.toList());
  }

  private Item createItem(Row row) {
    Item item = new Item();
    item.setId(row.getString(ItemField.ITEM_ID));
    item.setInfo(JsonUtil.json2Object(row.getString(ItemField.ITEM_INFO), Info.class));
    return item;
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
}
