/*
 * Copyright © 2016 European Support Limited
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

package org.amdocs.zusammen.plugin.statestore.cassandra;

import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.Namespace;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.UserInfo;
import org.amdocs.zusammen.datatypes.item.ElementContext;
import org.amdocs.zusammen.datatypes.item.ElementInfo;
import org.amdocs.zusammen.datatypes.item.Relation;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ElementStateStoreTest {
  private static final String TENANT = "test";
  private static final String USER = "ElementStateStoreTest_user";
  private static final SessionContext context =
      TestUtils.createSessionContext(new UserInfo(USER), TENANT);
  private static final String ELEMENT_NOT_EXIST_ERR =
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
    ElementEntity retrievedElement = getRetrievedElement();

    doReturn(Optional.of(retrievedElement))
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    boolean elementExist =
        elementStateStore.isElementExist(context, elementContext, retrievedElement.getId());

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
    ElementEntity retrievedElement = getRetrievedElement();

    doReturn(Optional.of(retrievedElement))
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementContext elementContext = TestUtils.createElementContext(new Id(), new Id());
    ElementInfo element =
        elementStateStore.getElement(context, elementContext, retrievedElement.getId(), null);

    Assert.assertNotNull(element);
    Assert.assertEquals(element.getId(), retrievedElement.getId());
    Assert.assertEquals(element.getInfo(), retrievedElement.getInfo());
    Assert.assertEquals(element.getRelations(), retrievedElement.getRelations());
    Assert
        .assertEquals(element.getSubElements().size(), retrievedElement.getSubElementIds().size());
    element.getSubElements()
        .forEach(subElement -> retrievedElement.getSubElementIds().contains(subElement.getId()));
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
    ElementEntity retrievedElement = getRetrievedElement();
    doReturn(Optional.of(retrievedElement))
        .when(elementRepositoryMock).get(anyObject(), anyObject(), anyObject());

    ElementInfo elementInfo =
        new ElementInfo(new Id(), new Id(), retrievedElement.getId(), new Id());
    elementInfo.setInfo(TestUtils.createInfo("elm1 updated"));
    elementInfo.setRelations(Arrays.asList(createRelation("r1"), createRelation("r2")));

    elementStateStore.updateElement(context, elementInfo);

    verify(elementRepositoryMock).update(anyObject(), anyObject(), elementEntityCaptor.capture());

    Assert.assertEquals(elementEntityCaptor.getValue().getId(), elementInfo.getId());
    Assert.assertEquals(elementEntityCaptor.getValue().getParentId(), elementInfo.getParentId());
    Assert.assertEquals(elementEntityCaptor.getValue().getInfo(), elementInfo.getInfo());
    Assert.assertEquals(elementEntityCaptor.getValue().getRelations(), elementInfo.getRelations());
  }

  @Test
  public void testDeleteElement() throws Exception {
    ElementEntity retrievedElement = getRetrievedElement();
    ElementInfo elementInfo =
        new ElementInfo(new Id(), new Id(), retrievedElement.getId(),
            retrievedElement.getParentId());
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(USER, elementInfo.getItemId(), elementInfo.getVersionId());
    doReturn(Optional.of(retrievedElement))
        .when(elementRepositoryMock)
        .get(eq(context), eq(elementEntityContext), eq(retrievedElement));

    retrievedElement.getSubElementIds().stream()
        .map(ElementEntity::new)
        .forEach(reqForRetrieveSubElement -> {
          ElementEntity retrievedSubElement = new ElementEntity(reqForRetrieveSubElement.getId());
          retrievedSubElement.setParentId(retrievedElement.getId());
          doReturn(Optional.of(retrievedSubElement))
              .when(elementRepositoryMock)
              .get(eq(context), eq(elementEntityContext), eq(reqForRetrieveSubElement));
        });

    elementStateStore.deleteElement(context, elementInfo);

    verify(elementRepositoryMock, times(3)).delete(anyObject(), anyObject(), anyObject());
    verify(elementRepositoryMock)
        .delete(eq(context), eq(elementEntityContext), eq(retrievedElement));
    retrievedElement.getSubElementIds().stream()
        .map(ElementEntity::new)
        .forEach(subElement ->
            verify(elementRepositoryMock)
                .delete(eq(context), eq(elementEntityContext), eq(subElement)));
  }

  private ElementEntity getRetrievedElement() {
    ElementEntity elementEntity = new ElementEntity(new Id());
    elementEntity.setParentId(new Id());
    elementEntity.setNamespace(new Namespace(new Namespace(), elementEntity.getId()));
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