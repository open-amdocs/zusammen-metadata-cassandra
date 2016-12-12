package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.Id;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;

public interface VersionDao {
  void create(SessionContext context, String space, Id itemId, Id versionId, Id baseVersionId,
              Info versionInfo);

  void delete(SessionContext context, String space, Id itemId, Id versionId);


  Info get(SessionContext context, String space, Id itemId, Id versionId);
}
