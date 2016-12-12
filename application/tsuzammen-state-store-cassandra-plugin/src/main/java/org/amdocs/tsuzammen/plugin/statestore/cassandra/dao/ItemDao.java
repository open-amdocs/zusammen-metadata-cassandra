package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.Id;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;

public interface ItemDao {
  void save(SessionContext context, Id itemId, Info itemInfo);

  void delete(SessionContext context, Id itemId);

  Info get(SessionContext context, Id itemId);
}
