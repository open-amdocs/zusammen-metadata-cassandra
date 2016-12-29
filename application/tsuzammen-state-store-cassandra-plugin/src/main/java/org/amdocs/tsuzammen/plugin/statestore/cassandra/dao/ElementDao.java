package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;

import java.net.URI;

public interface ElementDao {
  void create(SessionContext context, String space, String itemId, String versionId, URI namespace,
              String elementId, Info elementInfo);

  void save(SessionContext context, String space, String itemId, String versionId, URI namespace,
            String elementId,
            Info elementInfo);

  void delete(SessionContext context, String space, String itemId, String versionId, URI namespace,
              String elementId);

    Info get(SessionContext context, String space, String itemId, String versionId, URI namespace,
           String elementId);
}
