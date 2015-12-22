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
package org.camunda.spin.plugin.impl;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.variable.serializer.AbstractObjectValueSerializer;

/**
 * @author Thorben Lindhauer
 */
public class FallbackSpinObjectValueSerializer extends AbstractObjectValueSerializer {

  public static final String DESERIALIZED_OBJECTS_EXCEPTION_MESSAGE = "Fallback serializer cannot handle deserialized objects";

  protected String serializationFormat;

  public FallbackSpinObjectValueSerializer(String serializationFormat) {
    super(serializationFormat);
    this.serializationFormat = serializationFormat;
  }

  public String getName() {
    return "spin://" + serializationFormat;
  }

  protected String getTypeNameForDeserialized(Object deserializedObject) {
    throw new ProcessEngineException(DESERIALIZED_OBJECTS_EXCEPTION_MESSAGE);
  }

  protected byte[] serializeToByteArray(Object deserializedObject) throws Exception {
    throw new ProcessEngineException(DESERIALIZED_OBJECTS_EXCEPTION_MESSAGE);
  }

  protected Object deserializeFromByteArray(byte[] object, String objectTypeName) throws Exception {
    throw new ProcessEngineException(DESERIALIZED_OBJECTS_EXCEPTION_MESSAGE);
  }

  protected boolean isSerializationTextBased() {
    return true;
  }

  protected boolean canSerializeValue(Object value) {
    throw new ProcessEngineException(DESERIALIZED_OBJECTS_EXCEPTION_MESSAGE);
  }

}
