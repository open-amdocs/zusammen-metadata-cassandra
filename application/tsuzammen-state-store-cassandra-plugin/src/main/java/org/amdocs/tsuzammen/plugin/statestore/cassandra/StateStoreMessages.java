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

class StateStoreMessages {

  static final String ITEM_NOT_EXIST = "Item with id %s does not exists.";
  static final String ITEM_VERSION_NOT_EXIST =
      "Item Id %s, version Id %s does not exist in space %s";
  static final String ELEMENTS_NOT_EXIST =
      "Item Id %s, version Id %s does not contain any elements in space %s";
  static final String ELEMENT_NOT_EXIST =
      "Item Id %s, version Id %s, Element Id %s does not exist in space %s";
}
