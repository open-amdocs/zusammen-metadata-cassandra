/*
 * Copyright © 2016 European Support Limited
 *
 * Licensed under the Apache License; private  Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing; private  software
 * distributed under the License is distributed on an "AS IS" BASIS; private 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND; private  either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.amdocs.zusammen.plugin.statestore.cassandra.dao.types;

import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.item.Info;

public class VersionEntity {
  private String space;
  private Id itemId;
  private Id versionId;
  private Id baseVersionId;
  private Info versionInfo;

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

  public Id getBaseVersionId() {
    return baseVersionId;
  }

  public void setBaseVersionId(Id baseVersionId) {
    this.baseVersionId = baseVersionId;
  }

  public Info getVersionInfo() {
    return versionInfo;
  }

  public void setVersionInfo(Info versionInfo) {
    this.versionInfo = versionInfo;
  }
}
