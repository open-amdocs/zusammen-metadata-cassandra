package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.tsuzammen.utils.facade.api.AbstractFactory;

public abstract class ElementInfoRepositoryFactory extends AbstractComponentFactory<ElementRepository> {
  public static ElementInfoRepositoryFactory getInstance() {
    return AbstractFactory.getInstance(ElementInfoRepositoryFactory.class);
  }

  public abstract ElementRepository createInterface(SessionContext context);
}

