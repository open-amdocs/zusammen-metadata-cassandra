package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

public class VersionCassandraDao implements VersionDao {

  @Override
  public void create(SessionContext context, String space, String itemId, String versionId,
                     String baseVersionId, Info versionInfo) {
    getAccessor(context).create(space, itemId, versionId,
        baseVersionId, JsonUtil.object2Json(versionInfo));
  }

  @Override
  public void save(SessionContext context, String space, String itemId, String versionId,
                   Info versionInfo) {
    getAccessor(context).save(JsonUtil.object2Json(versionInfo), space, itemId, versionId);
  }

  @Override
  public void delete(SessionContext context, String space, String itemId, String versionId) {
    getAccessor(context)
        .delete(space, itemId, versionId);
  }

  @Override
  public Info get(SessionContext context, String space, String itemId, String versionId) {
    ResultSet rows = getAccessor(context).get(space, itemId, versionId);
    return JsonUtil.json2Object(rows.one().getString(VersionField.VERSION_INFO), Info.class);
  }

  private VersionAccessor getAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, VersionAccessor.class);
  }

  @Accessor
  interface VersionAccessor {

    @Query("INSERT INTO version (space, item_id, version_id, base_version_id, version_info) " +
        "VALUES (?, ?, ?, ?, ?)")
    void create(String space, String itemId, String versionId, String baseVersionId,
                String versionInfo);

    @Query("UPDATE version SET version_info=? WHERE space=? AND item_id=? AND version_id=?")
    void save(String versionInfo, String space, String itemId, String versionId);

    @Query("SELECT base_version_id, version_info FROM version " +
        "WHERE space=? AND item_id=? AND version_id=?")
    ResultSet get(String space, String itemId, String versionId);

    @Query("DELETE FROM version WHERE space=? AND item_id=? AND version_id=?")
    void delete(String space, String itemId, String versionId);
  }

  private static final class VersionField {
    private static final String VERSION_INFO = "version_info";
  }

}
