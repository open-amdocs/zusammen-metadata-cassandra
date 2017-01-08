package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;

public interface ElementRepository {

  void create(SessionContext context, ElementEntity elementEntity);

  void update(SessionContext context, ElementEntity elementEntity);

  void delete(SessionContext context, ElementEntity elementEntity);

  ElementEntity get(SessionContext context, ElementEntity elementEntity);
}
