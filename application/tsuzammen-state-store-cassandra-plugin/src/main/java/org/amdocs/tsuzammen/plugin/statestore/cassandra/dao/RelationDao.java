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

package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;


import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Relation;

import java.util.Map;

public interface RelationDao {

  void save(SessionContext context, String space, String itemId, String versionId,
            String parentElementId,
            String contentName, String elementId, String relationId, Relation relation);

  void save(SessionContext context, String space, String itemId, String versionId,
            String parentElementId,
            String parentContentName, String elementId, Map<String, Relation> relations);


  Map<String, Relation> list(SessionContext context, String space, String itemId, String versionId,
                             String parentElementId, String contentName, String elementId);
}
