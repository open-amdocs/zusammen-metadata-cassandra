package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.zusammen.utils.fileutils.json.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.Objects.*;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.*;
public class KeepAliveCassandraDao implements KeepAliveDao {

    @Override

 public  boolean get(SessionContext context){
     String string = getAccessor(context).get();
     return  !isNull(string) && string.contains("item_id");
 }
  private KeepAliveAccessor getAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, KeepAliveAccessor.class);
  }

  @Accessor
  interface KeepAliveAccessor {

    @Query("DESCRIBE element")
    String get();

  }

}
