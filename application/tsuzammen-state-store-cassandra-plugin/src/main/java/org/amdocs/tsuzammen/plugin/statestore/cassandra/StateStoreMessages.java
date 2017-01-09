package org.amdocs.tsuzammen.plugin.statestore.cassandra;

class StateStoreMessages {

  static final String ITEM_NOT_EXIST = "Item with id %s does not exists.";
  static final String ITEM_VERSION_NOT_EXIST =
      "Item Id %s, version Id %s does not exist in space %s";
  static final String ELEMENT_NOT_EXIST =
      "Item Id %s, version Id %s, Element Id %s does not exist in space %s";
}
