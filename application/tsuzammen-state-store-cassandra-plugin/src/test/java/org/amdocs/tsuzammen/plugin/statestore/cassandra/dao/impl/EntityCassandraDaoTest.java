package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.UserInfo;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.TestUtils;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementDao;
import org.testng.annotations.Test;

import java.net.URI;

public class ElementCassandraDaoTest {

  private static final String USER = "ElementCassandraDaoTest";

  @Test
  public void testCreate() throws Exception {
    ElementDao elementDao = new ElementCassandraDao();
    SessionContext context =
        TestUtils.createSessionContext(new UserInfo(USER), "test");

    Info elementInfo = new Info();
    elementInfo.addProperty("Name", "name_value");
    elementInfo.addProperty("Desc", "desc_value");

/*    elementDao.create(context, USER, "item1", "version1", new URI("root/a"), "b", elementInfo);
    elementDao.create(context, USER, "item1", "version1", new URI("root/a/b/c"), "d",
        elementInfo);*/

    elementDao.create(context, USER, "item3", "version1", new URI("a"), "b", elementInfo);
    elementDao.create(context, USER, "item3", "version1", new URI("a/b/c"), "d", elementInfo);

  }

  @Test
  public void testSave() throws Exception {

  }

  @Test
  public void testDelete() throws Exception {

  }

}