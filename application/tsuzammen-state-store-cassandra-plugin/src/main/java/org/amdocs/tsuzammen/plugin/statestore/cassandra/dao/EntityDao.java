package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;

import java.net.URI;

public interface EntityDao {
  void create(SessionContext context, String space, String itemId, String versionId, URI namespace,
              String entityId, Info entityInfo);

  void save(SessionContext context, String space, String itemId, String versionId, URI namespace,
            String entityId,
            Info entityInfo);

  void delete(SessionContext context, String space, String itemId, String versionId, URI namespace,
              String entityId);

  Info get(SessionContext context, String space, String itemId, String versionId, URI namespace,
           String entityId);
}
