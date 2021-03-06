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
package org.camunda.bpm.integrationtest.functional.el;

import org.camunda.bpm.application.AbstractProcessApplication;
import org.camunda.bpm.application.ProcessApplicationElResolver;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;

/**
 * @author Thorben Lindhauer
 *
 */
public class NullELResolver implements ProcessApplicationElResolver {

  /**
   * Spring and CDI have 100 and 200 respectively
   */
  public Integer getPrecedence() {
    return 300;
  }

  public ELResolver getElResolver(AbstractProcessApplication processApplication) {
    return null;
  }

}
