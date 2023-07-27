/*
 * Copyright © 2019 Jitterbit, Inc.
 *
 * Licensed under the JITTERBIT MASTER SUBSCRIPTION AGREEMENT
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * https://www.jitterbit.com/cloud-eula
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.jitterbit.connector.docusign.activity;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.auth.OAuth;

import org.jitterbit.connector.docusign.DocuSignV2Connection;
import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.DocuSignMessage;
import org.jitterbit.connector.docusign.DocuSignUtils;
import org.jitterbit.connector.docusign.execution.DSExecutionUtils;
import org.jitterbit.connector.docusign.execution.DSExecution;
import org.jitterbit.connector.docusign.execution.DSGetExecutionFactory;
import org.jitterbit.connector.sdk.Discoverable;
import org.jitterbit.connector.sdk.JitterbitActivity;
import org.jitterbit.connector.sdk.annotation.Activity;
import org.jitterbit.connector.sdk.exceptions.ActivityExecutionException;
import org.jitterbit.connector.sdk.metadata.ActivityFunctionParameters;
import org.jitterbit.connector.sdk.metadata.ActivityRequestResponseMetaData;
import org.jitterbit.connector.sdk.metadata.DiscoverableObject;
import org.jitterbit.connector.sdk.metadata.DiscoverableObjectRequest;
import org.jitterbit.connectors.common.Constants;
import org.jitterbit.connectors.common.verbose.VerboseLogger;
import org.jitterbit.connectors.common.verbose.VerboseLoggerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

/**
 * Create Activity allows user to create new Objects(Envelope, Template) for
 * DocuSign Connector.
 * <p>
 * When the activity is being executed by the runtime, the <code>context</code>
 * parameter can be obtained as part of the configuration of the function that
 * is exposed in the Jitterbit Cloud Studio UI from the execution context: see
 * {@link #execute(ExecutionContext)}.
 * </p>
 * <p>
 * The response of this activity will be written to the response payload as an
 * XML document that conforms with the
 * <code>resources/envelope-create-activity-request.xsd,
 *  resources/envelope-create-activity-response.xsd</code>.
 * </p>
 */
@Activity(name = Constants.CREATE_ACTIVITY, factory = DocuSignCreateActivity.DocuSignCreateActivityFactory.class)
public class DocuSignCreateActivity extends DocuSignBaseActivity {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(DocuSignCreateActivity.class);

  private ApiClient apiClient;

  private DSExecutionUtils utils;

  public DocuSignCreateActivity() {
  }

  @Override
  public String getName() {
    return Constants.CREATE_ACTIVITY;
  }

  /**
   * Returns the request/response associated with this activity. For the
   * <code>Create</code> activity, both the request and response data structures
   * are being returned.
   *
   * @param activityConfigProps the properties for the activity
   * @return the response metadata of the activity
   * @throws DiscoveryException if there is an error while configuring the
   *                            activity
   */
  @Override
  public ActivityRequestResponseMetaData getActivityRequestResponseMetadata(
      Discoverable.DiscoverContextRequest<ActivityFunctionParameters> activityConfigProps) throws DiscoveryException {
    LOGGER.info("Generating Request Response For: {}", getName());
    ActivityRequestResponseMetaData activitySchemaResponse = new ActivityRequestResponseMetaData();
    try {
      String selectedObject = activityConfigProps.getRequest().getObjectName();

      DocuSignUtils.setRequestResponseSchemas(activitySchemaResponse, XSDS,
          DocuSignUtils.getXsdFileName(CREATE_REQ, selectedObject.replace(WHITE_SPACE, DASH).toLowerCase()),
          DocuSignUtils.getXsdFileName(CREATE_RES, selectedObject.replace(WHITE_SPACE, DASH).toLowerCase()));

      activitySchemaResponse
          .setRequestRootElement(QName.valueOf("{"
              + DocuSignUtils.getXsdFileName(CREATE_NAMESPACE_REQ,
                  selectedObject.replaceAll(BLANK_SPACE_REG, ""))
              + "}"
              + DocuSignUtils.getXsdFileName(CREATE_REQ_ROOT, selectedObject.replaceAll(BLANK_SPACE_REG, ""))));

      activitySchemaResponse
          .setResponseRootElement(QName.valueOf("{"
              + DocuSignUtils.getXsdFileName(CREATE_NAMESPACE_RES,
                  selectedObject.replaceAll(BLANK_SPACE_REG, ""))
              + "}"
              + DocuSignUtils.getXsdFileName(CREATE_RES_ROOT, selectedObject.replaceAll(BLANK_SPACE_REG, ""))));

      return activitySchemaResponse;
    } catch (Exception x) {
      LOGGER.error(x.getLocalizedMessage(), x);
      throw new DiscoveryException(DocuSignMessage.DOCUSIGN_CODE01,
          DocuSignMessage.getMessage(DocuSignMessage.DOCUSIGN_CODE01_MSG, new Object[] { getName() }), x);
    }
  }

  @Override
  public List<DiscoverableObject> getObjectList(DiscoverContextRequest<DiscoverableObjectRequest> objectListRequest)
      throws DiscoveryException {
    LOGGER.info("Getting Object List For: {}", getName());
    List<DiscoverableObject> objectList = new ArrayList<>();
    try {
      utils = new DSExecutionUtils();
      objectList = utils.getConfig().getCreateObjects().stream()
          .map(object -> new DiscoverableObject().setObjectName(object.getDisplayName())
              .setObjectDesc(object.getDescription()).setObjectType(object.getName()))
          .collect(Collectors.toList());
    } catch (Exception ex) {
      LOGGER.error(ex.getLocalizedMessage(), ex);
      throw new DiscoveryException(DocuSignMessage.DOCUSIGN_CODE04,
          DocuSignMessage.getMessage(DocuSignMessage.DOCUSIGN_CODE04_MSG, new Object[] { getName() }), ex);
    }
    return objectList;
  }

  @Override
  public void execute(ExecutionContext context) throws ActivityExecutionException {
    DocuSignV2Connection connection = (DocuSignV2Connection) context.getConnection();
    LOGGER.debug("Executing Activity: {}", getName());
    try {
      apiClient = connection.getClient();
      OAuth.Account accountInfo = this.getAccountInfo(apiClient);
      apiClient.setBasePath(accountInfo.getBaseUri() + REST_API);
      String selectedObject = getSelectedObject(context.getFunctionParameters().get(DocuSignConstants.OBJECT_KEY));
      LOGGER.debug("Selected Object {}", selectedObject);
      DSExecution targetOperation = DSGetExecutionFactory.getOperation(selectedObject);
      targetOperation.executeCreateRequest(apiClient, context, accountInfo);
    } catch (Exception e) {
      LOGGER.error(e.getLocalizedMessage(), e);
      throw new ActivityExecutionException(DocuSignMessage.DOCUSIGN_CODE03,
          DocuSignMessage.getMessage(DocuSignMessage.DOCUSIGN_CODE03_MSG, new Object[] { e.getMessage() }), e);
    }

  }

  /**
   * Factory for creating the activity.
   */
  public static class DocuSignCreateActivityFactory implements JitterbitActivity.Factory {
    @Override
    public JitterbitActivity createActivity() {
      return new DocuSignCreateActivity();
    }
  }

}
