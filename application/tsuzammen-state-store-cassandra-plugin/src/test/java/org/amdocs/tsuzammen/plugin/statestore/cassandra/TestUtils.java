package org.amdocs.tsuzammen.plugin.statestore.cassandra;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.UserInfo;

public class TestUtils {

  public static SessionContext createSessionContext(UserInfo user, String tenant) {
    SessionContext context = new SessionContext();
    context.setUser(user);
    context.setTenant(tenant);
    return context;
  }
}
