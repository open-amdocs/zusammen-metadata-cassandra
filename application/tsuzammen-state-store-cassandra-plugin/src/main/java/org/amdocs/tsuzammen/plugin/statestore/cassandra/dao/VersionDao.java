package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;


import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;

public interface VersionDao {
  void create(SessionContext context, String space, String itemId, String versionId, String baseVersionId,
              Info versionInfo);

  void save(SessionContext context, String space, String itemId, String versionId, Info versionInfo);

  void delete(SessionContext context, String space, String itemId, String versionId);


  Info get(SessionContext context, String space, String itemId, String versionId);
}
