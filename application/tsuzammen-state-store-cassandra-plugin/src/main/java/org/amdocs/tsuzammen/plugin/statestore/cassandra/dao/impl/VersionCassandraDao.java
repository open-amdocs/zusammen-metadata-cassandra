package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.tsuzammen.commons.datatypes.Id;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

public class VersionCassandraDao implements VersionDao {

  @Override
  public void create(SessionContext context, String space, Id itemId, Id versionId,
                     Id baseVersionId, Info versionInfo) {
    getAccessor(context).create(space, itemId.getValue(), versionId.getValue(),
        baseVersionId.getValue(), JsonUtil.object2Json(versionInfo));
  }

  @Override
  public void delete(SessionContext context, String space, Id itemId, Id versionId) {
    getAccessor(context)
        .delete(space, itemId.getValue(), versionId.getValue());
  }

  @Override
  public Info get(SessionContext context, String space, Id itemId, Id versionId) {
    return null;
  }

  private VersionAccessor getAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, VersionAccessor.class);
  }


  @Accessor
  interface VersionAccessor {

    @Query("insert into version (space, item_id, version_id, base_version_id, version_info) " +
        "values (?, ?, ?, ?, ?)")
    void create(String space, String itemId, String versionId, String baseVersionId,
                String versionInfo);

    @Query("select base_version_id, version_info from version " +
        "where space=? and item_id=? and version_id=?")
    ResultSet get(String space, String itemId, String versionId);

    @Query("delete from version where space=? and item_id=? and version_id=?")
    void delete(String space, String itemId, String versionId);
  }


}
