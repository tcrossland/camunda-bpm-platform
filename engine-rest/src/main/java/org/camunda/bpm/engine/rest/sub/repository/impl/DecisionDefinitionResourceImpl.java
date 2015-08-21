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
package org.camunda.bpm.engine.rest.sub.repository.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.exception.NotFoundException;
import org.camunda.bpm.engine.exception.NotValidException;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.engine.rest.dto.repository.DecisionDefinitionDiagramDto;
import org.camunda.bpm.engine.rest.dto.repository.DecisionDefinitionDto;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.camunda.bpm.engine.rest.exception.RestException;
import org.camunda.bpm.engine.rest.sub.repository.DecisionDefinitionResource;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DecisionDefinitionResourceImpl implements DecisionDefinitionResource {

  protected ProcessEngine engine;
  protected String decisionDefinitionId;
  protected String rootResourcePath;
  protected ObjectMapper objectMapper;

  public DecisionDefinitionResourceImpl(ProcessEngine engine, String decisionDefinitionId, String rootResourcePath, ObjectMapper objectMapper) {
    this.engine = engine;
    this.decisionDefinitionId = decisionDefinitionId;
    this.rootResourcePath = rootResourcePath;
    this.objectMapper = objectMapper;
  }

  @Override
  public DecisionDefinitionDto getDecisionDefinition() {
    RepositoryService repositoryService = engine.getRepositoryService();

    DecisionDefinition definition = null;

    try {
      definition = repositoryService.getDecisionDefinition(decisionDefinitionId);

    } catch (NotFoundException e) {
      throw new InvalidRequestException(Status.NOT_FOUND, e, e.getMessage());

    } catch (NotValidException e) {
      throw new InvalidRequestException(Status.BAD_REQUEST, e, e.getMessage());

    } catch (ProcessEngineException e) {
      throw new RestException(Status.INTERNAL_SERVER_ERROR, e);

    }

    return DecisionDefinitionDto.fromDecisionDefinition(definition);
  }

  @Override
  public DecisionDefinitionDiagramDto getDecisionDefinitionDmnXml() {
    InputStream decisionModelInputStream = null;
    try {
      decisionModelInputStream = engine.getRepositoryService().getDecisionModel(decisionDefinitionId);

      byte[] decisionModel = IoUtil.readInputStream(decisionModelInputStream, "decisionModelDmnXml");
      return DecisionDefinitionDiagramDto.create(decisionDefinitionId, new String(decisionModel, "UTF-8"));

    } catch (NotFoundException e) {
      throw new InvalidRequestException(Status.NOT_FOUND, e, e.getMessage());

    } catch (NotValidException e) {
      throw new InvalidRequestException(Status.BAD_REQUEST, e, e.getMessage());

    } catch (ProcessEngineException e) {
      throw new RestException(Status.INTERNAL_SERVER_ERROR, e);

    } catch (UnsupportedEncodingException e) {
      throw new RestException(Status.INTERNAL_SERVER_ERROR, e);

    } finally {
      IoUtil.closeSilently(decisionModelInputStream);
    }
  }

  @Override
  public Response getDecisionDefinitionDiagram() {
    DecisionDefinition definition = engine.getRepositoryService().getDecisionDefinition(decisionDefinitionId);
    InputStream decisionDiagram = engine.getRepositoryService().getDecisionDiagram(decisionDefinitionId);
    if (decisionDiagram == null) {
      return Response.noContent().build();
    } else {
      String fileName = definition.getDiagramResourceName();
      return Response.ok(decisionDiagram).header("Content-Disposition", "attachment; filename=" + fileName)
          .type(ProcessDefinitionResourceImpl.getMediaTypeForFileSuffix(fileName)).build();
    }
  }

}
