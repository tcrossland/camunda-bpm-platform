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
package org.camunda.bpm.container.impl.deployment;

import org.camunda.bpm.container.impl.RuntimeContainerDelegateImpl;
import org.camunda.bpm.container.impl.jmx.services.JmxManagedBpmPlatformPlugins;
import org.camunda.bpm.container.impl.plugin.BpmPlatformPlugins;
import org.camunda.bpm.container.impl.spi.DeploymentOperation;
import org.camunda.bpm.container.impl.spi.DeploymentOperationStep;
import org.camunda.bpm.container.impl.spi.PlatformServiceContainer;
import org.camunda.bpm.container.impl.spi.ServiceTypes;
import org.camunda.bpm.engine.impl.util.ClassLoaderUtil;

/**
 * @author Thorben Lindhauer
 *
 */
public class DiscoverBpmPlatformPluginsStep extends DeploymentOperationStep {

  public String getName() {
    return "Discover BPM Platform Plugins";
  }

  public void performOperationStep(DeploymentOperation operationContext) {
    PlatformServiceContainer serviceContainer = operationContext.getServiceContainer();

    BpmPlatformPlugins plugins = BpmPlatformPlugins.load(ClassLoaderUtil.getContextClassloader());

    JmxManagedBpmPlatformPlugins jmxManagedPlugins = new JmxManagedBpmPlatformPlugins(plugins);
    serviceContainer.startService(ServiceTypes.BPM_PLATFORM, RuntimeContainerDelegateImpl.SERVICE_NAME_PLATFORM_PLUGINS, jmxManagedPlugins);

  }

}
