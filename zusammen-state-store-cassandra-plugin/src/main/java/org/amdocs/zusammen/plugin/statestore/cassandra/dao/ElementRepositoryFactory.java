package org.amdocs.zusammen.plugin.statestore.cassandra.dao;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.zusammen.utils.facade.api.AbstractFactory;

public abstract class ElementRepositoryFactory extends AbstractComponentFactory<ElementRepository> {
  public static ElementRepositoryFactory getInstance() {
    return AbstractFactory.getInstance(ElementRepositoryFactory.class);
  }

  public abstract ElementRepository createInterface(SessionContext context);
}

