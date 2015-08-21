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

package org.camunda.bpm.engine.impl.tree;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;
import org.camunda.bpm.engine.impl.pvm.delegate.SubProcessActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.PvmExecutionImpl;

/**
 * Pass the output variables from the process instance of a subprocess to the
 * calling process instance.
 *
 * @author Philipp Ossler
 *
 */
public class OutputVariablesPropagator implements TreeVisitor<ActivityExecution> {

  @Override
  public void visit(ActivityExecution execution) {

    if (isProcessInstanceOfSubprocess(execution)) {

      PvmExecutionImpl superExecution = (PvmExecutionImpl) execution.getSuperExecution();
      ActivityImpl activity = superExecution.getActivity();
      SubProcessActivityBehavior subProcessActivityBehavior = (SubProcessActivityBehavior) activity.getActivityBehavior();

      subProcessActivityBehavior.passOutputVariablesFromSubprocess(superExecution, execution);
    }
  }

  protected boolean isProcessInstanceOfSubprocess(ActivityExecution execution) {
    return execution.isProcessInstanceExecution() && execution.getSuperExecution() != null;
  }

}
