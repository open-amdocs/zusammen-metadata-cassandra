package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.Namespace;
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.UserInfo;
import org.amdocs.tsuzammen.datatypes.item.Info;
import org.amdocs.tsuzammen.datatypes.item.Relation;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.TestUtils;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;
import org.testng.annotations.Test;

import java.util.Arrays;

public class ElementCassandraRepositoryTest {

  private static final String USER = "ElementCassandraRepositoryTest";

  @Test
  public void testCreate() throws Exception {
    ElementRepository elementRepository = new ElementCassandraRepository();
    SessionContext context =
        TestUtils.createSessionContext(new UserInfo(USER), "test");

    Info info = new Info();
    info.addProperty("Name", "name_value");
    info.addProperty("Desc", "desc_value");

    Id elementId = new Id();
    Id parentId = new Id();
    ElementEntity element = new ElementEntity(elementId);
    element.setParentId(parentId);
    element.setNamespace(new Namespace(new Namespace(new Namespace(), parentId), elementId));
    element.setInfo(info);
    element.setRelations(Arrays.asList(new Relation(), new Relation()));
    elementRepository.create(context, new ElementEntityContext(USER, new Id(), new Id()), element);
    /*
    elementRepository.create(context, USER, "item1", "version1", new URI("root/a/b/c"), "d",
        info);

    elementRepository.create(context, USER, "item3", "version1", new URI("a"), "b", info);
    elementRepository.create(context, USER, "item3", "version1", new URI("a/b/c"), "d", info);
*/
  }

  @Test
  public void testSave() throws Exception {

  }

  @Test
  public void testDelete() throws Exception {

  }

}