/*
 * Copyright © 2016-2017 European Support Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.Namespace;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.UserInfo;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.Relation;
import org.amdocs.zusammen.plugin.statestore.cassandra.TestUtils;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;
import org.testng.annotations.Test;

import java.util.Arrays;

public class CassandraElementRepositoryTest {

  private static final String USER = "CassandraElementRepositoryTest";

  @Test
  public void testCreate() throws Exception {
    ElementRepository elementRepository = new CassandraElementRepository();
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