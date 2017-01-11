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
import org.amdocs.tsuzammen.datatypes.item.ElementInfo;
import org.amdocs.tsuzammen.datatypes.item.Relation;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ElementStateStoreTest {
  private static final String TENANT = "test";
  private static final String USER = "ElementStateStoreTest_user";
  private static final SessionContext context =
      TestUtils.createSessionContext(new UserInfo(USER), TENANT);
  public static final String ELEMENT_NOT_EXIST_ERR =
      "Item Id .*, version Id .*, Element Id .* does not exist in space .*";

  @Spy
  private ElementStateStore elementStateStore;
  @Mock
  private ElementRepository elementRepositoryMock;
  @Captor
  private ArgumentCaptor<ElementEntity> elementEntityCaptor;

  @BeforeMethod
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(elementStateStore.getElementRepository(anyObject())).thenReturn(elementRepositoryMock);
  }

  @Test
  public void testIsElementExist() throws Exception {
    ElementEntity elementEntity = createElementEntity();

    doReturn(Optional.of(elementEntity))
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    boolean elementExist =
        elementStateStore.isElementExist(context, elementContext, elementEntity.getId());

    Assert.assertEquals(elementExist, true);
  }

  @Test
  public void testIsElementExistWhenNot() throws Exception {
    doReturn(Optional.empty())
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    boolean elementExist = elementStateStore.isElementExist(context, elementContext, new Id());

    Assert.assertEquals(elementExist, false);
  }

  @Test
  public void testGetElement() throws Exception {
    ElementEntity elementEntity = createElementEntity();

    doReturn(Optional.of(elementEntity))
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    ElementInfo element =
        elementStateStore.getElement(context, elementContext, elementEntity.getId(), null);

    Assert.assertNotNull(element);
    Assert.assertEquals(element.getId(), elementEntity.getId());
    Assert.assertEquals(element.getInfo(), elementEntity.getInfo());
    Assert.assertEquals(element.getRelations(), elementEntity.getRelations());
    Assert.assertEquals(element.getSubElements().size(), elementEntity.getSubElementIds().size());
    element.getSubElements()
        .forEach(subElement -> elementEntity.getSubElementIds().contains(subElement.getId()));
  }

  @Test(expectedExceptions = RuntimeException.class,
      expectedExceptionsMessageRegExp = ELEMENT_NOT_EXIST_ERR)
  public void testGetNonExistingElement() throws Exception {
    doReturn(Optional.empty())
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    elementStateStore.getElement(context, elementContext, new Id(), null);
  }

  @Test
  public void testCreateElement() throws Exception {
    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    ElementInfo elementInfo = new ElementInfo(new Id(), new Id(), new Id(), new Id());
    elementInfo.setInfo(TestUtils.createInfo("elm1"));
    elementInfo.setRelations(Arrays.asList(createRelation("r1"), createRelation("r2")));

    elementStateStore.createElement(context, elementInfo);

    verify(elementRepositoryMock).create(anyObject(), anyObject(), elementEntityCaptor.capture());

    Assert.assertEquals(elementEntityCaptor.getValue().getId(), elementInfo.getId());
    Assert.assertEquals(elementEntityCaptor.getValue().getParentId(), elementInfo.getParentId());
    Assert.assertEquals(elementEntityCaptor.getValue().getInfo(), elementInfo.getInfo());
    Assert.assertEquals(elementEntityCaptor.getValue().getRelations(), elementInfo.getRelations());
  }

  @Test
  public void testSaveElement() throws Exception {
    ElementEntity elementEntity = createElementEntity();

    doReturn(Optional.of(elementEntity))
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementInfo elementInfo = new ElementInfo(new Id(), new Id(), elementEntity.getId(), new Id());
    elementInfo.setInfo(TestUtils.createInfo("elm1 updated"));
    elementInfo.setRelations(Arrays.asList(createRelation("r1"), createRelation("r2")));

    elementStateStore.saveElement(context, elementInfo);

    verify(elementRepositoryMock).update(anyObject(), anyObject(), elementEntityCaptor.capture());

    Assert.assertEquals(elementEntityCaptor.getValue().getId(), elementInfo.getId());
    Assert.assertEquals(elementEntityCaptor.getValue().getParentId(), elementInfo.getParentId());
    Assert.assertEquals(elementEntityCaptor.getValue().getInfo(), elementInfo.getInfo());
    Assert.assertEquals(elementEntityCaptor.getValue().getRelations(), elementInfo.getRelations());
  }

  @Test
  public void testDeleteElement() throws Exception {

  }

  private ElementEntity createElementEntity() {
    Id elementId = new Id();
    ElementEntity elementEntity = new ElementEntity(elementId);
    elementEntity.setNamespace(new Namespace(new Namespace(), elementId));
    elementEntity.setInfo(TestUtils.createInfo("elm1"));
    elementEntity.setRelations(Arrays.asList(createRelation("r1"), createRelation("r2")));
    Set<Id> subElementIds = new HashSet<>();
    subElementIds.add(new Id());
    subElementIds.add(new Id());
    elementEntity.setSubElementIds(subElementIds);
    return elementEntity;
  }

  private Relation createRelation(String relationName) {
    Relation relation = new Relation();
    relation.setInfo(TestUtils.createInfo(relationName));
    return relation;
  }

}