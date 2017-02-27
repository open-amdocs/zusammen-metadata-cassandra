package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.Session;
import org.amdocs.zusammen.commons.db.api.cassandra.CassandraConnectorFactory;
import org.amdocs.zusammen.commons.db.api.cassandra.types.CassandraContext;
import org.amdocs.zusammen.datatypes.SessionContext;

class CassandraDaoUtils {

  static <T> T getAccessor(SessionContext context, Class<T> classOfT) {
    return CassandraConnectorFactory.getInstance().createInterface(getCassandraContext(context))
        .getMappingManager()
        .createAccessor(classOfT);
  }

  static Session getSession(SessionContext context) {
    return CassandraConnectorFactory.getInstance().createInterface(getCassandraContext(context))
        .getMappingManager()
        .getSession();
  }

  private static CassandraContext getCassandraContext(SessionContext context) {
    CassandraContext cassandraContext = new CassandraContext();
    cassandraContext.setTenant(context.getTenant());
    return cassandraContext;
  }
}
