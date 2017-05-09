package org.amdocs.zusammen.plugin.statestore.cassandra.dao;

import org.amdocs.zusammen.datatypes.Namespace;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;

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

  Optional<Namespace> getNamespace(SessionContext context, ElementEntityContext elementContext,
                          ElementEntity element);
}
