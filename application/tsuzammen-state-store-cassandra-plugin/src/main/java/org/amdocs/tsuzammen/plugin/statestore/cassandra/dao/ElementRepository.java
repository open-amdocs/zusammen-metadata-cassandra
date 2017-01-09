package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;

import java.util.Collection;
import java.util.Optional;

public interface ElementRepository {

  Collection<ElementEntity> list(SessionContext context, ElementEntityContext elementContext);

  void create(SessionContext context, ElementEntityContext elementContext, ElementEntity element);

/*  void create(SessionContext context, ElementEntityContext elementContext,
              Collection<ElementEntity> elements); //impl using batch*/

  void update(SessionContext context, ElementEntityContext elementContext, ElementEntity element);

  void delete(SessionContext context, ElementEntityContext elementContext, ElementEntity element);

  Optional<ElementEntity> get(SessionContext context, ElementEntityContext elementContext,
                              ElementEntity element);
}
