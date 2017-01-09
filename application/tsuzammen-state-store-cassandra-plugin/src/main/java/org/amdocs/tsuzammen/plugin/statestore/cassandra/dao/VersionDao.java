package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;


import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.item.Info;
import org.amdocs.tsuzammen.datatypes.item.ItemVersion;

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