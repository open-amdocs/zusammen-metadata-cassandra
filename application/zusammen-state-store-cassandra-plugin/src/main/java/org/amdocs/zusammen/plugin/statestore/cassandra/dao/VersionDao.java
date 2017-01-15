package org.amdocs.zusammen.plugin.statestore.cassandra.dao;


import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.ItemVersion;

import java.util.Collection;
import java.util.Optional;

public interface VersionDao {
  Collection<ItemVersion> list(SessionContext context, String space, Id itemId);

  Optional<ItemVersion> get(SessionContext context, String space, Id itemId, Id versionId);

  void create(SessionContext context, String space, Id itemId, Id versionId, Id baseVersionId,
              Info versionInfo);

  void update(SessionContext context, String space, Id itemId, Id versionId, Info versionInfo);

  void delete(SessionContext context, String space, Id itemId, Id versionId);
}
