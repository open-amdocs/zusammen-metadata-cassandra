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
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.UserInfo;
import org.amdocs.tsuzammen.datatypes.item.Info;
import org.testng.annotations.Test;

public class CassandraStateStoreTest {
  @Test
  public void testListItems() throws Exception {

  }

  @Test
  public void testIsItemExist() throws Exception {

  }

  @Test
  public void testGetItem() throws Exception {

  }

  @Test
  public void testCreateItem1() throws Exception {

  }

  @Test
  public void testSaveItem() throws Exception {

  }

  @Test
  public void testDeleteItem() throws Exception {

  }

  @Test
  public void testListItemVersions() throws Exception {

  }

  @Test
  public void testIsItemVersionExist() throws Exception {

  }

  @Test
  public void testGetItemVersion() throws Exception {

  }

  @Test
  public void testCreateItemVersion1() throws Exception {

  }

  @Test
  public void testPublishItemVersion() throws Exception {

  }

  @Test
  public void testSyncItemVersion() throws Exception {

  }

  @Test
  public void testGetElementNamespace() throws Exception {

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

  @Test
  public void testCreateWorkspace() throws Exception {

  }

  @Test
  public void testSaveWorkspace() throws Exception {

  }

  @Test
  public void testDeleteWorkspace() throws Exception {

  }

  @Test
  public void testListWorkspaces() throws Exception {

  }

  @Test
  public void testCreateItem() throws Exception {
    Info itemInfo = new Info();
    itemInfo.addProperty("Name", "name_value");
    itemInfo.addProperty("Desc", "desc_value");

    StateStoreImpl stateStore = new StateStoreImpl();
    SessionContext context = TestUtils.createSessionContext(new UserInfo("testUser"), "test");
    stateStore.createItem(context, new Id(), itemInfo);
  }

  @Test
  public void testCreateItemVersion() throws Exception {
    Info versionInfo = new Info();
    versionInfo.addProperty("Name", "name_value");
    versionInfo.addProperty("Desc", "desc_value");

    StateStoreImpl stateStore = new StateStoreImpl();
    SessionContext context = TestUtils.createSessionContext(new UserInfo("testUser"), "test");
    stateStore
        .createItemVersion(context, new Id(), new Id(), new Id(), versionInfo);
  }
}