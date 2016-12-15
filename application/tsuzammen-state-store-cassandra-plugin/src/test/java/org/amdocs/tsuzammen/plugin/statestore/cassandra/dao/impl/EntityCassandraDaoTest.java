package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.UserInfo;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.TestUtils;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.EntityDao;
import org.testng.annotations.Test;

import java.net.URI;

public class EntityCassandraDaoTest {

  private static final String USER = "EntityCassandraDaoTest";

  @Test
  public void testCreate() throws Exception {
    EntityDao entityDao = new EntityCassandraDao();
    SessionContext context =
        TestUtils.createSessionContext(new UserInfo(USER), "test");

    Info entityInfo = new Info();
    entityInfo.addProperty("Name", "name_value");
    entityInfo.addProperty("Desc", "desc_value");

/*    entityDao.create(context, USER, "item1", "version1", new URI("root/a"), "b", entityInfo);
    entityDao.create(context, USER, "item1", "version1", new URI("root/a/b/c"), "d",
        entityInfo);*/

    entityDao.create(context, USER, "item3", "version1", new URI("a"), "b", entityInfo);
    entityDao.create(context, USER, "item3", "version1", new URI("a/b/c"), "d", entityInfo);

  }

  @Test
  public void testSave() throws Exception {

  }

  @Test
  public void testDelete() throws Exception {

  }

}