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

package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types;

import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.item.ElementContext;

public class ElementEntityContext {
  private String space;
  private Id itemId;
  private Id versionId;

  public ElementEntityContext(String space, ElementContext elementContext) {
    this(space, elementContext.getItemId(), elementContext.getVersionId());
  }

  public ElementEntityContext(String space, Id itemId, Id versionId) {
    this.space = space;
    this.itemId = itemId;
    this.versionId = versionId;
  }

  public String getSpace() {
    return space;
  }

  public void setSpace(String space) {
    this.space = space;
  }

  public Id getItemId() {
    return itemId;
  }

  public void setItemId(Id itemId) {
    this.itemId = itemId;
  }

  public Id getVersionId() {
    return versionId;
  }

  public void setVersionId(Id versionId) {
    this.versionId = versionId;
  }
}
