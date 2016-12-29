package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.tsuzammen.utils.facade.api.AbstractFactory;

public abstract class ElementDaoFactory extends AbstractComponentFactory<ElementDao> {
  public static ElementDaoFactory getInstance() {
    return AbstractFactory.getInstance(ElementDaoFactory.class);
  }

  public abstract ElementDao createInterface(SessionContext context);
}

