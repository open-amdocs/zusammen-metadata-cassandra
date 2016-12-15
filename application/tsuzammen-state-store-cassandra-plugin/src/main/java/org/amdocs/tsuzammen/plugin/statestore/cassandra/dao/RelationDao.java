package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;


import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Relation;

import java.util.Map;

public interface RelationDao {

  void save(SessionContext context, String space, String itemId, String versionId,
            String parentEntityId,
            String contentName, String entityId, String relationId, Relation relation);

  void save(SessionContext context, String space, String itemId, String versionId,
            String parentEntityId,
            String parentContentName, String entityId, Map<String, Relation> relations);


  Map<String, Relation> list(SessionContext context, String space, String itemId, String versionId,
                             String parentEntityId, String contentName, String entityId);
}
