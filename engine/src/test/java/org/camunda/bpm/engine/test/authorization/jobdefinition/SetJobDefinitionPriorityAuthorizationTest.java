/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.camunda.bpm.engine.test.authorization.jobdefinition;

import static org.camunda.bpm.engine.test.authorization.util.AuthorizationScenario.scenario;
import static org.camunda.bpm.engine.test.authorization.util.AuthorizationSpec.grant;

import java.util.Collection;

import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.impl.test.PluggableProcessEngineTestCase;
import org.camunda.bpm.engine.management.JobDefinition;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.authorization.util.AuthorizationScenario;
import org.camunda.bpm.engine.test.authorization.util.AuthorizationTestRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Thorben Lindhauer
 *
 */
@RunWith(Parameterized.class)
public class SetJobDefinitionPriorityAuthorizationTest {

  public ProcessEngineRule engineRule = new ProcessEngineRule(PluggableProcessEngineTestCase.getProcessEngine(), true);
  public AuthorizationTestRule authRule = new AuthorizationTestRule(engineRule);

  @Rule
  public RuleChain chain = RuleChain.outerRule(engineRule).around(authRule);

  @Parameter
  public AuthorizationScenario scenario;

  @Parameters(name = "Scenario {index}")
  public static Collection<AuthorizationScenario[]> scenarios() {
    return AuthorizationTestRule.asParameters(
      scenario()
        .withoutAuthorizations()
        .failsDueToRequired(
          grant(Resources.PROCESS_DEFINITION, "processDefinitionKey", "userId", Permissions.UPDATE)),
      scenario()
        .withAuthorizations(
          grant(Resources.PROCESS_DEFINITION, "processDefinitionKey", "userId", Permissions.UPDATE))
        .succeeds(),
      scenario()
        .withAuthorizations(
          grant(Resources.PROCESS_DEFINITION, "*", "userId", Permissions.UPDATE))
        .succeeds()
      );
  }

  @Before
  public void setUp() {
    authRule.createUserAndGroup("userId", "groupId");
  }

  @After
  public void tearDown() {
    authRule.deleteUsersAndGroups();
  }

  @Test
  @Deployment(resources = "org/camunda/bpm/engine/test/authorization/oneIncidentProcess.bpmn20.xml")
  public void testSetJobDefinitionPriority() {

    // given
    JobDefinition jobDefinition = engineRule.getManagementService().createJobDefinitionQuery().singleResult();

    // when
    authRule
      .init(scenario)
      .withUser("userId")
      .bindResource("processDefinitionKey", "process")
      .start();

    engineRule.getManagementService().setOverridingJobPriorityForJobDefinition(jobDefinition.getId(), 42);

    // then
    if (authRule.assertScenario(scenario)) {
      JobDefinition updatedJobDefinition = engineRule.getManagementService().createJobDefinitionQuery().singleResult();
      Assert.assertEquals(42, (long) updatedJobDefinition.getOverridingJobPriority());
    }

  }

  @Test
  @Deployment(resources = "org/camunda/bpm/engine/test/authorization/oneIncidentProcess.bpmn20.xml")
  public void testResetJobDefinitionPriority() {
    // given
    JobDefinition jobDefinition = engineRule.getManagementService().createJobDefinitionQuery().singleResult();
    engineRule.getManagementService().setOverridingJobPriorityForJobDefinition(jobDefinition.getId(), 42);

    // when
    authRule
      .init(scenario)
      .withUser("userId")
      .bindResource("processDefinitionKey", "process")
      .start();

    engineRule.getManagementService().clearOverridingJobPriorityForJobDefinition(jobDefinition.getId());

    // then
    if (authRule.assertScenario(scenario)) {
      JobDefinition updatedJobDefinition = engineRule.getManagementService().createJobDefinitionQuery().singleResult();
      Assert.assertNull(updatedJobDefinition.getOverridingJobPriority());
    }
  }

}
