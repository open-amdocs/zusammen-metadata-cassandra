package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.Id;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.RelationInfo;

import java.util.Map;

public interface RelationDao {

  void save(SessionContext context, String space, Id itemId, Id versionId, Id parentEntityId,
            String contentName, Id entityId, Id relationId, RelationInfo relation);

  void save(SessionContext context, String space, Id itemId, Id versionId, Id parentEntityId,
            String parentContentName, Id entityId, Map<Id, RelationInfo> relations);


  Map<Id, RelationInfo> list(SessionContext context, String space, Id itemId, Id versionId,
                             Id parentEntityId, String contentName, Id entityId);
}
