package org.amdocs.zusammen.plugin.statestore.cassandra.dao;


import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.Item;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ItemDao {
  void create(SessionContext context, Id itemId, Info itemInfo, Date creationTime);

  void update(SessionContext context, Id itemId, Info itemInfo, Date modificationTime);

  void delete(SessionContext context, Id itemId);

  Optional<Item> get(SessionContext context, Id itemId);

  List<Item> list(SessionContext context);

  void updateItemModificationTime(SessionContext context, Id itemId, Date modificationTime);
}
