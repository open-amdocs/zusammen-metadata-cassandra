package org.amdocs.tsuzammen.plugin.statestore.cassandra;



import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.UserInfo;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.testng.annotations.Test;

import java.util.Optional;

public class CassandraStateStoreTest {

  @Test
  public void testCreateItem() throws Exception {
    Info itemInfo = new Info();
    itemInfo.addProperty("Name", "name_value");
    itemInfo.addProperty("Desc", "desc_value");

    CassandraStateStore stateStore = new CassandraStateStore();
    SessionContext context = createSessionContext(new UserInfo("testUser"), "test");
    stateStore.createItem(context, new String("itemId"), itemInfo);
  }

  @Test
  public void testCreateItemVersion() throws Exception {
    Info versionInfo = new Info();
    versionInfo.addProperty("Name", "name_value");
    versionInfo.addProperty("Desc", "desc_value");

    CassandraStateStore stateStore = new CassandraStateStore();
    SessionContext context = createSessionContext(new UserInfo("testUser"), "test");
    stateStore.createItemVersion(context, new String("itemId"), new String("baseVersionId"), new String
            ("versionId"),
        versionInfo);
  }

  private SessionContext createSessionContext(UserInfo user, String tenant) {
    SessionContext context = new SessionContext();
    context.setUser(user);
    context.setTenant(Optional.ofNullable(tenant).get());
    return context;
  }

}