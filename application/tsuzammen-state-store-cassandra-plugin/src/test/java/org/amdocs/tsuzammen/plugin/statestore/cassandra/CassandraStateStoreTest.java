package org.amdocs.tsuzammen.plugin.statestore.cassandra;


import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.UserInfo;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.testng.annotations.Test;

public class CassandraStateStoreTest {

  @Test
  public void testCreateItem() throws Exception {
    Info itemInfo = new Info();
    itemInfo.addProperty("Name", "name_value");
    itemInfo.addProperty("Desc", "desc_value");

    CassandraStateStore stateStore = new CassandraStateStore();
    SessionContext context = TestUtils.createSessionContext(new UserInfo("testUser"), "test");
    stateStore.createItem(context, "itemId", itemInfo);
  }

  @Test
  public void testCreateItemVersion() throws Exception {
    Info versionInfo = new Info();
    versionInfo.addProperty("Name", "name_value");
    versionInfo.addProperty("Desc", "desc_value");

    CassandraStateStore stateStore = new CassandraStateStore();
    SessionContext context = TestUtils.createSessionContext(new UserInfo("testUser"), "test");
    stateStore
        .createItemVersion(context, "itemId", "baseVersionId", "versionId", versionInfo);
  }
}