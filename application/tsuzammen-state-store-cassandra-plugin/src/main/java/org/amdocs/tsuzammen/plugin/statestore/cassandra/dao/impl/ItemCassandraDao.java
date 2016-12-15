package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

public class ItemCassandraDao implements ItemDao {

  private static final class ItemField {
    private static final String ITEM_INFO = "item_info";
  }

  @Override
  public void create(SessionContext context, String itemId, Info itemInfo) {
    save(context, itemId, itemInfo);
  }

  @Override
  public void save(SessionContext context, String itemId, Info itemInfo) {
    CassandraDaoUtils.getAccessor(context, ItemAccessor.class).
        save(itemId, JsonUtil.object2Json(itemInfo));
  }

  @Override
  public void delete(SessionContext context, String itemId) {
    CassandraDaoUtils.getAccessor(context, ItemAccessor.class).delete(itemId);
  }

  @Override
  public Info get(SessionContext context, String itemId) {
    return JsonUtil.json2Object(CassandraDaoUtils.getAccessor(context, ItemAccessor.class).
        get(itemId).one().getString(ItemField.ITEM_INFO), Info.class);
  }

  @Accessor
  interface ItemAccessor {

    @Query("INSERT INTO item (item_id, item_info) VALUES (?,?)")
    void save(String itemId, String itemInfo);

    @Query("SELECT item_info FROM item WHERE item_id=?")
    ResultSet get(String itemId);

    @Query("DELETE FROM item WHERE item_id=?")
    void delete(String itemId);
  }
}
