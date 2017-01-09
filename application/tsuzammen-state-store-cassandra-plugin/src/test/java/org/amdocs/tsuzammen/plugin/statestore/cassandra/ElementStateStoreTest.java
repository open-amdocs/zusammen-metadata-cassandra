/*
 * Copyright © 2016 Amdocs Software Systems Limited
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

package org.amdocs.tsuzammen.plugin.statestore.cassandra;

import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.Namespace;
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.UserInfo;
import org.amdocs.tsuzammen.datatypes.item.ElementContext;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ElementStateStoreTest {
  private static final String TENANT = "test";
  private static final String USER = "ElementStateStoreTest_user";
  private static final SessionContext context =
      TestUtils.createSessionContext(new UserInfo(USER), TENANT);

  @Spy
  private ElementStateStore elementStateStore;
  @Mock
  private ElementRepository elementRepositoryMock;

  @BeforeMethod
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(elementStateStore.getElementRepository(anyObject())).thenReturn(elementRepositoryMock);
  }

  @Test
  public void testGetElementNamespace() throws Exception {
    Id elementId = new Id();
    ElementEntity elementEntity = new ElementEntity(elementId);
    Namespace namespace = new Namespace(new Namespace(), elementId);
    elementEntity.setNamespace(namespace);

    doReturn(Optional.of(elementEntity))
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    Namespace elementNamespace =
        elementStateStore.getElementNamespace(context, elementContext, elementId);


    Assert.assertEquals(elementNamespace, namespace);
    verify(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void testGetNonExistingElementNamespace() throws Exception {
    doReturn(Optional.empty())
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    elementStateStore.getElementNamespace(context, elementContext, new Id());
  }

  @Test
  public void testIsElementExist() throws Exception {

  }

  @Test
  public void testGetElement() throws Exception {

  }

  @Test
  public void testCreateElement() throws Exception {

  }

  @Test
  public void testSaveElement() throws Exception {

  }

  @Test
  public void testDeleteElement() throws Exception {

  }

}