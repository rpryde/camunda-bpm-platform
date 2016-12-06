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

package org.camunda.bpm.qa.performance.engine.bpmn;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.*;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.camunda.bpm.model.xml.validation.ModelElementValidator;
import org.camunda.bpm.model.xml.validation.ValidationResults;

import java.util.Collection;

public class ModifiableBpmnModelInstance implements BpmnModelInstance {

  protected BpmnModelInstance modelInstance;

  public ModifiableBpmnModelInstance(BpmnModelInstance modelInstance) {
    this.modelInstance = modelInstance;
  }

  /**
   * Copies the argument; following modifications are not applied to the original model instance
   */
  public static ModifiableBpmnModelInstance modify(BpmnModelInstance modelInstance) {
    return new ModifiableBpmnModelInstance(modelInstance.clone());
  }

  /**
   * wraps the argument; following modifications are applied to the original model instance
   */
  public static ModifiableBpmnModelInstance wrap(BpmnModelInstance modelInstance) {
    return new ModifiableBpmnModelInstance(modelInstance);
  }

  public Definitions getDefinitions() {
    return modelInstance.getDefinitions();
  }

  public void setDefinitions(Definitions definitions) {
    modelInstance.setDefinitions(definitions);
  }

  @Override
  public BpmnModelInstance clone() {
    return modelInstance.clone();
  }

  public DomDocument getDocument() {
    return modelInstance.getDocument();
  }

  public ModelElementInstance getDocumentElement() {
    return modelInstance.getDocumentElement();
  }

  public void setDocumentElement(ModelElementInstance documentElement) {
    modelInstance.setDocumentElement(documentElement);
  }

  public <T extends ModelElementInstance> T newInstance(Class<T> type) {
    return modelInstance.newInstance(type);
  }

  public <T extends ModelElementInstance> T newInstance(ModelElementType type) {
    return modelInstance.newInstance(type);
  }

  public Model getModel() {
    return modelInstance.getModel();
  }

  public <T extends ModelElementInstance> T getModelElementById(String id) {
    return modelInstance.getModelElementById(id);
  }

  public Collection<ModelElementInstance> getModelElementsByType(ModelElementType referencingType) {
    return modelInstance.getModelElementsByType(referencingType);
  }

  public <T extends ModelElementInstance> Collection<T> getModelElementsByType(Class<T> referencingClass) {
    return modelInstance.getModelElementsByType(referencingClass);
  }

  @SuppressWarnings("unchecked")
  public <T extends AbstractBaseElementBuilder> T getBuilderForElementById(String id, Class<T> builderClass) {
    BaseElement modelElementById = modelInstance.getModelElementById(id);
    return (T) modelElementById.builder();
  }

  public AbstractActivityBuilder activityBuilder(String activityId) {
    return getBuilderForElementById(activityId, AbstractActivityBuilder.class);
  }

  public AbstractFlowNodeBuilder flowNodeBuilder(String flowNodeId) {
    return getBuilderForElementById(flowNodeId, AbstractFlowNodeBuilder.class);
  }

  public UserTaskBuilder userTaskBuilder(String userTaskId) {
    return getBuilderForElementById(userTaskId, UserTaskBuilder.class);
  }

  public ServiceTaskBuilder serviceTaskBuilder(String serviceTaskId) {
    return getBuilderForElementById(serviceTaskId, ServiceTaskBuilder.class);
  }

  public CallActivityBuilder callActivityBuilder(String callActivityId) {
    return getBuilderForElementById(callActivityId, CallActivityBuilder.class);
  }

  public IntermediateCatchEventBuilder intermediateCatchEventBuilder(String eventId) {
    return getBuilderForElementById(eventId, IntermediateCatchEventBuilder.class);
  }

  public StartEventBuilder startEventBuilder(String eventId) {
    return getBuilderForElementById(eventId, StartEventBuilder.class);
  }

  public EndEventBuilder endEventBuilder(String eventId) {
    return getBuilderForElementById(eventId, EndEventBuilder.class);
  }

  public ModifiableBpmnModelInstance changeElementId(String oldId, String newId) {
    BaseElement element = getModelElementById(oldId);
    element.setId(newId);
    return this;
  }

  public ModifiableBpmnModelInstance changeElementName(String elementId, String newName) {
    FlowElement flowElement = getModelElementById(elementId);
    flowElement.setName(newName);
    return this;
  }

  public ModifiableBpmnModelInstance removeChildren(String elementId) {
    BaseElement element = getModelElementById(elementId);

    Collection<BaseElement> children = element.getChildElementsByType(BaseElement.class);
    for (BaseElement child : children) {
      element.removeChildElement(child);
    }

    return this;
  }

  public ModifiableBpmnModelInstance renameMessage(String oldMessageName, String newMessageName) {
    Collection<Message> messages = modelInstance.getModelElementsByType(Message.class);

    for (Message message : messages) {
      if (message.getName().equals(oldMessageName)) {
        message.setName(newMessageName);
      }
    }

    return this;
  }

  public ModifiableBpmnModelInstance addDocumentation(String content) {
    Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
    Documentation documentation = modelInstance.newInstance(Documentation.class);
    documentation.setTextContent(content);
    for (Process process : processes) {
      process.addChildElement(documentation);
    }
    return this;
  }

  public ModifiableBpmnModelInstance renameSignal(String oldSignalName, String newSignalName) {
    Collection<Signal> signals = modelInstance.getModelElementsByType(Signal.class);

    for (Signal signal : signals) {
      if (signal.getName().equals(oldSignalName)) {
        signal.setName(newSignalName);
      }
    }

    return this;
  }

  public ModifiableBpmnModelInstance swapElementIds(String firstElementId, String secondElementId) {
    BaseElement firstElement = getModelElementById(firstElementId);
    BaseElement secondElement = getModelElementById(secondElementId);

    secondElement.setId("___TEMP___ID___");
    firstElement.setId(secondElementId);
    secondElement.setId(firstElementId);

    return this;
  }

  public SubProcessBuilder addSubProcessTo(String parentId) {
    SubProcess eventSubProcess = modelInstance.newInstance(SubProcess.class);

    BpmnModelElementInstance parent = getModelElementById(parentId);
    parent.addChildElement(eventSubProcess);

    return eventSubProcess.builder();
  }

  public ModifiableBpmnModelInstance removeFlowNode(String flowNodeId) {
    FlowNode flowNode = getModelElementById(flowNodeId);
    ModelElementInstance scope = flowNode.getParentElement();

    for (SequenceFlow outgoingFlow : flowNode.getOutgoing()) {
      scope.removeChildElement(outgoingFlow);
    }
    for (SequenceFlow incomingFlow : flowNode.getIncoming()) {
      scope.removeChildElement(incomingFlow);
    }
    Collection<Association> associations = scope.getChildElementsByType(Association.class);
    for (Association association : associations) {
      if (flowNode.equals(association.getSource()) || flowNode.equals(association.getTarget())) {
        scope.removeChildElement(association);
      }
    }
    scope.removeChildElement(flowNode);

    return this;
  }

  public ModifiableBpmnModelInstance asyncBeforeInnerMiActivity(String activityId) {
    Activity activity = modelInstance.getModelElementById(activityId);

    MultiInstanceLoopCharacteristics miCharacteristics = (MultiInstanceLoopCharacteristics) activity.getUniqueChildElementByType(MultiInstanceLoopCharacteristics.class);
    miCharacteristics.setCamundaAsyncBefore(true);

    return this;
  }

  public ModifiableBpmnModelInstance asyncAfterInnerMiActivity(String activityId) {
    Activity activity = modelInstance.getModelElementById(activityId);

    MultiInstanceLoopCharacteristics miCharacteristics = (MultiInstanceLoopCharacteristics) activity.getUniqueChildElementByType(MultiInstanceLoopCharacteristics.class);
    miCharacteristics.setCamundaAsyncAfter(true);

    return this;
  }

  public ValidationResults validate(Collection<ModelElementValidator<?>> validators) {
    return null;
  }

}