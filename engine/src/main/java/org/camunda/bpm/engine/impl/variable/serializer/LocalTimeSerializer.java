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

package org.camunda.bpm.engine.impl.variable.serializer;

import org.camunda.bpm.engine.impl.core.variable.value.UntypedValueImpl;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.LocalTimeValue;

/**
 * Serializes LocalTime as String value.
 *
 * @author Philipp Ossler
 */
public class LocalTimeSerializer extends PrimitiveValueSerializer<LocalTimeValue> {

  public LocalTimeSerializer() {
    super(ValueType.LOCAL_TIME);
  }

  @Override
  public void writeValue(LocalTimeValue value, ValueFields valueFields) {
    valueFields.setTextValue(value.getValue());
  }

  @Override
  public LocalTimeValue convertToTypedValue(UntypedValueImpl untypedValue) {
    return Variables.localTimeValue((String) untypedValue.getValue());
  }

  @Override
  public LocalTimeValue readValue(ValueFields valueFields) {
    return Variables.localTimeValue(valueFields.getTextValue());
  }

}
