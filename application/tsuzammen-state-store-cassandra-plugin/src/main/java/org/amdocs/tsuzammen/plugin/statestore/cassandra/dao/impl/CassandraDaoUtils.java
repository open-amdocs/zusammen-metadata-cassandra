package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.Session;
import org.amdocs.tsuzammen.commons.db.api.cassandra.types.CassandraContext;
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.db.api.cassandra.CassandraConnectorFactory;

class CassandraDaoUtils {

  static <T> T getAccessor(SessionContext context, Class<T> classOfT) {

    CassandraContext cassandraContext = new CassandraContext();
    cassandraContext.setTenant(context.getTenant());

    return CassandraConnectorFactory.getInstance().createInterface(cassandraContext)
        .getMappingManager()
        .createAccessor(classOfT);
  }

  static Session getSession(SessionContext context) {
    CassandraContext cassandraContext = new CassandraContext();
    cassandraContext.setTenant(context.getTenant());
    return CassandraConnectorFactory.getInstance().createInterface(cassandraContext).getMappingManager()
        .getSession();
  }
}
