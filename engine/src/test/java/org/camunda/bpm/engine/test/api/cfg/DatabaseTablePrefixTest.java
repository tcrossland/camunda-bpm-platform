/* Licensed under the Apache License, Version 20.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.engine.test.api.cfg;

import java.sql.Connection;

import junit.framework.TestCase;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.util.ReflectUtil;

/**
 * @author Daniel Meyer
 */
public class DatabaseTablePrefixTest extends TestCase {

  public void testPerformDatabaseSchemaOperationCreate() throws Exception{

    // both process engines will be using this datasource.
    PooledDataSource pooledDataSource = new PooledDataSource(ReflectUtil.getClassLoader(),
            "org.h2.Driver",
            "jdbc:h2:mem:DatabaseTablePrefixTest;DB_CLOSE_DELAY=1000",
            "sa",
            "" );

    // create two schemas is the database
    Connection connection = pooledDataSource.getConnection();
    connection.createStatement().execute("drop schema if exists SCHEMA1");
    connection.createStatement().execute("drop schema if exists SCHEMA2");
    connection.createStatement().execute("create schema SCHEMA1");
    connection.createStatement().execute("create schema SCHEMA2");
    connection.close();

    // configure & build two different process engines, each having a separate table prefix
    ProcessEngineConfigurationImpl config1 = createCustomProcessEngineConfiguration()
            .setProcessEngineName("DatabaseTablePrefixTest-engine1")
            .setDataSource(pooledDataSource)
            .setDatabaseSchemaUpdate("NO_CHECK"); // disable auto create/drop schema
    config1.setDatabaseTablePrefix("SCHEMA1.");
    ProcessEngine engine1 = config1.buildProcessEngine();

    ProcessEngineConfigurationImpl config2 = createCustomProcessEngineConfiguration()
            .setProcessEngineName("DatabaseTablePrefixTest-engine2")
            .setDataSource(pooledDataSource)
            .setDatabaseSchemaUpdate("NO_CHECK"); // disable auto create/drop schema
    config2.setDatabaseTablePrefix("SCHEMA2.");
    ProcessEngine engine2 = config2.buildProcessEngine();

    // create the tables in SCHEMA1
    connection = pooledDataSource.getConnection();
    connection.createStatement().execute("set schema SCHEMA1");
    engine1.getManagementService().databaseSchemaUpgrade(connection, "", "SCHEMA1");
    connection.close();

    // create the tables in SCHEMA2
    connection = pooledDataSource.getConnection();
    connection.createStatement().execute("set schema SCHEMA2");
    engine2.getManagementService().databaseSchemaUpgrade(connection, "", "SCHEMA2");
    connection.close();

    // if I deploy a process to one engine, it is not visible to the other
    // engine:
    try {
      engine1.getRepositoryService()
        .createDeployment()
        .addClasspathResource("org/camunda/bpm/engine/test/api/cfg/oneJobProcess.bpmn20.xml")
        .deploy();

      assertEquals(1, engine1.getRepositoryService().createDeploymentQuery().count());
      assertEquals(0, engine2.getRepositoryService().createDeploymentQuery().count());

    } finally {
      engine1.close();
      engine2.close();
    }
  }


  //----------------------- TEST HELPERS -----------------------

  // allows to return a process engine configuration which doesn't create a schema when it's build.
  private static class CustomStandaloneInMemProcessEngineConfiguration extends StandaloneInMemProcessEngineConfiguration {

    public ProcessEngine buildProcessEngine() {
      init();
      return new NoSchemaProcessEngineImpl(this);
    }

    class NoSchemaProcessEngineImpl extends ProcessEngineImpl {
      public NoSchemaProcessEngineImpl(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
      }

      protected void executeSchemaOperations() {
        // nop - do not execute create schema operations
      }
    }

  }

  private static ProcessEngineConfigurationImpl createCustomProcessEngineConfiguration() {
    return new CustomStandaloneInMemProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_FULL);
  }

}
