/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.engine.repository;

import org.camunda.bpm.engine.RepositoryService;

/**
 * Definition of a resource which was deployed
 */
public interface ResourceDefinition {

  /** unique identifier */
  String getId();

  /** category name which is derived from the targetNamespace attribute in the definitions element */
  String getCategory();

  /** label used for display purposes */
  String getName();

  /** unique name for all versions this definition */
  String getKey();

  /** version of this definition */
  int getVersion();

  /** name of {@link RepositoryService#getResourceAsStream(String, String) the resource} of this definition */
  String getResourceName();

  /** The deployment in which this definition is contained. */
  String getDeploymentId();

  /** The diagram resource name for this decision if exist */
  String getDiagramResourceName();

}
