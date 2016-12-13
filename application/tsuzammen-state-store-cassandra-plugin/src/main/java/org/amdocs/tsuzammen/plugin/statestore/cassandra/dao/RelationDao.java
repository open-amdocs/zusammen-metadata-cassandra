package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;


import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.RelationInfo;

import java.util.Map;

public interface RelationDao {

  void save(SessionContext context, String space, String itemId, String versionId, String parentEntityId,
            String contentName, String entityId, String relationId, RelationInfo relation);

  void save(SessionContext context, String space, String itemId, String versionId, String parentEntityId,
            String parentContentName, String entityId, Map<String, RelationInfo> relations);


  Map<String, RelationInfo> list(SessionContext context, String space, String itemId, String versionId,
                             String parentEntityId, String contentName, String entityId);
}
