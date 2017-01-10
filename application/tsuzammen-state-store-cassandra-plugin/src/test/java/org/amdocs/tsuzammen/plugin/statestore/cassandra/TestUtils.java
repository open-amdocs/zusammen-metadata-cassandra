package org.amdocs.tsuzammen.plugin.statestore.cassandra;

import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.UserInfo;
import org.amdocs.tsuzammen.datatypes.item.ElementContext;
import org.amdocs.tsuzammen.datatypes.item.Info;

public class TestUtils {

  public static SessionContext createSessionContext(UserInfo user, String tenant) {
    SessionContext context = new SessionContext();
    context.setUser(user);
    context.setTenant(tenant);
    return context;
  }

  public static ElementContext createElementContext(Id itemId, Id versionId) {
    ElementContext elementContext = new ElementContext();
    elementContext.setItemId(itemId);
    elementContext.setVersionId(versionId);
    return elementContext;
  }

  public static Info createInfo(String value) {
    Info info = new Info();
    info.setName(value);
    info.addProperty("Name", "name_" + value);
    info.addProperty("Desc", "desc_" + value);
    return info;
  }
}
