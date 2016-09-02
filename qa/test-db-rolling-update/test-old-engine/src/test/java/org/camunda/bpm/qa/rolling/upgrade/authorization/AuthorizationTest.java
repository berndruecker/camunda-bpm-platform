/*
 * Copyright 2016 camunda services GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package org.camunda.bpm.qa.rolling.upgrade.authorization;

import java.util.Arrays;
import java.util.List;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.qa.rolling.upgrade.EngineVersions;
import org.camunda.bpm.qa.rolling.upgrade.RollingUpgradeConstants;
import org.camunda.bpm.qa.rolling.upgrade.RollingUpgradeRule;
import org.camunda.bpm.qa.upgrade.ScenarioUnderTest;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author Christopher Zell <christopher.zell@camunda.com>
 */
@ScenarioUnderTest("AuthorizationScenario")
@EngineVersions({ RollingUpgradeConstants.OLD_ENGINE_TAG, RollingUpgradeConstants.NEW_ENGINE_TAG})
public class AuthorizationTest {

  public static final String PROCESS_DEF_KEY = "oneTaskProcess";
  
  @Rule
  public RollingUpgradeRule rule = new RollingUpgradeRule("camunda.auth.cfg.xml");

  protected IdentityService identityService;
  protected RepositoryService repositoryService;
  protected RuntimeService runtimeService;
  protected TaskService taskService;
  protected HistoryService historyService;
  protected FormService formService;

  @Before
  public void setUp() {
    ProcessEngine processEngine = rule.getProcessEngine();

    identityService = processEngine.getIdentityService();
    repositoryService = processEngine.getRepositoryService();
    runtimeService = processEngine.getRuntimeService();
    taskService = processEngine.getTaskService();
    historyService = processEngine.getHistoryService();
    formService = processEngine.getFormService();

    identityService.clearAuthentication();
    identityService.setAuthentication("test", Arrays.asList("accounting"));
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testGetDeployment() {
    List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
    assertFalse(deployments.isEmpty());
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testGetProcessDefinition() {
    ProcessDefinition definition = repositoryService
        .createProcessDefinitionQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .singleResult();
    assertNotNull(definition);
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testGetProcessInstance() {
    List<ProcessInstance> instances = runtimeService
        .createProcessInstanceQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .list();
    assertFalse(instances.isEmpty());
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testGetExecution() {
    List<Execution> executions = runtimeService
        .createExecutionQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .list();
    assertFalse(executions.isEmpty());
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testGetTask() {
    List<Task> tasks = taskService
        .createTaskQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .list();
    assertFalse(tasks.isEmpty());
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testGetHistoricProcessInstance() {
    List<HistoricProcessInstance> instances= historyService
        .createHistoricProcessInstanceQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .list();
    assertFalse(instances.isEmpty());
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testGetHistoricActivityInstance() {
    List<HistoricActivityInstance> instances= historyService
        .createHistoricActivityInstanceQuery()
        .list();
    assertFalse(instances.isEmpty());
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testGetHistoricTaskInstance() {
    List<HistoricTaskInstance> instances= historyService
        .createHistoricTaskInstanceQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .list();
    assertFalse(instances.isEmpty());
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testStartProcessInstance() {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_DEF_KEY);
    assertNotNull(instance);
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testSubmitStartForm() {
    String processDefinitionId = repositoryService
        .createProcessDefinitionQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .singleResult()
        .getId();
    ProcessInstance instance = formService.submitStartForm(processDefinitionId, null);
    assertNotNull(instance);
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testCompleteTaskInstance() {
    String taskId = taskService
        .createTaskQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .listPage(0, 1)
        .get(0)
        .getId();
    taskService.complete(taskId);
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testSubmitTaskForm() {
    String taskId = taskService
        .createTaskQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .listPage(0, 1)
        .get(0)
        .getId();
    formService.submitTaskForm(taskId, null);
  }

  @Test
  @ScenarioUnderTest("startProcessInstance.1")
  public void testSetVariable() {
    String processInstanceId = runtimeService
        .createProcessInstanceQuery()
        .processDefinitionKey(PROCESS_DEF_KEY)
        .listPage(0, 1)
        .get(0)
        .getId();
    runtimeService.setVariable(processInstanceId, "abc", "def");
  }
}
