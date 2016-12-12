package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.Session;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.db.api.cassandra.CassandraConnectorFactory;

class CassandraDaoUtils {

  static <T> T getAccessor(SessionContext context, Class<T> classOfT) {
    return CassandraConnectorFactory.getInstance().createInterface(context).getMappingManager()
        .createAccessor(classOfT);
  }

  static Session getSession(SessionContext context) {
    return CassandraConnectorFactory.getInstance().createInterface(context).getMappingManager()
        .getSession();
  }
}
