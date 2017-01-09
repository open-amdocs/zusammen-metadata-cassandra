package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.tsuzammen.utils.facade.api.AbstractFactory;

public abstract class ElementRepositoryFactory extends AbstractComponentFactory<ElementRepository> {
  public static ElementRepositoryFactory getInstance() {
    return AbstractFactory.getInstance(ElementRepositoryFactory.class);
  }

  public abstract ElementRepository createInterface(SessionContext context);
}

