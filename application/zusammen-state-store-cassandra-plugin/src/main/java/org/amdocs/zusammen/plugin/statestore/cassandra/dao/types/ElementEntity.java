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

package org.amdocs.zusammen.plugin.statestore.cassandra.dao.types;

import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.Namespace;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.Relation;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ElementEntity {
  private Id id;
  private Id parentId;
  private Namespace namespace;
  private Info info;
  private Collection<Relation> relations = Collections.emptyList();
  private Set<Id> subElementIds = Collections.emptySet();

  public ElementEntity(Id id) {
    this.id = id;
  }

  public Id getParentId() {
    return parentId;
  }

  public void setParentId(Id parentId) {
    this.parentId = parentId;
  }

  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  public Namespace getNamespace() {
    return namespace;
  }

  public void setNamespace(Namespace namespace) {
    this.namespace = namespace;
  }

  public Info getInfo() {
    return info;
  }

  public void setInfo(Info info) {
    this.info = info;
  }

  public Collection<Relation> getRelations() {
    return relations;
  }

  public void setRelations(Collection<Relation> relations) {
    this.relations = relations;
  }

  public Set<Id> getSubElementIds() {
    return subElementIds;
  }

  public void setSubElementIds(Set<Id> subElementIds) {
    this.subElementIds = subElementIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ElementEntity that = (ElementEntity) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
