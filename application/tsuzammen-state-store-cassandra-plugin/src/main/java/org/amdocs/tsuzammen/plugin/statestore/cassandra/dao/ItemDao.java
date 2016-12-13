package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;


import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;

public interface ItemDao {
  void save(SessionContext context, String itemId, Info itemInfo);

  void delete(SessionContext context, String itemId);

  Info get(SessionContext context, String itemId);
}
