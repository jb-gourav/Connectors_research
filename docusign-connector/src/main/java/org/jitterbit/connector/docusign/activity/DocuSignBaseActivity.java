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
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.DocuSignMessage;
import org.jitterbit.connector.docusign.execution.DSExecutionUtils;
import org.jitterbit.connector.docusign.pojo.ObjectList;
import org.jitterbit.connector.sdk.DeployedEntity;
import org.jitterbit.connector.sdk.JitterbitActivity;
import org.jitterbit.connector.sdk.metadata.ActivityFunctionParameters;
import org.jitterbit.connector.sdk.metadata.ActivityRequestResponseMetaData;
import org.jitterbit.connector.sdk.metadata.DiscoverableObject;
import org.jitterbit.connector.sdk.metadata.DiscoverableObjectRequest;
import org.jitterbit.connectors.common.verbose.VerboseLogger;
import org.jitterbit.connectors.common.verbose.VerboseLoggerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.jitterbit.connectors.common.CommonHelper.isTrue;

/**
 * Abstract class that all DocuSign connector activities extend.
 */
public abstract class DocuSignBaseActivity implements JitterbitActivity, DocuSignConstants {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(DocuSignBaseActivity.class);

  protected State state = State.INIT;

  protected DeployedEntity entity;

  private DSExecutionUtils utils;

  public DocuSignBaseActivity() {
    state = State.STARTED;
  }

  @Override
  public List<DiscoverableObject> getObjectList(DiscoverContextRequest<DiscoverableObjectRequest> objectListRequest)
      throws DiscoveryException {
    LOGGER.info("Getting Object List For: {}", getName());
    List<DiscoverableObject> objectList = new ArrayList<>();
    try {
      utils = new DSExecutionUtils();
      objectList = utils.getConfig().getGetObjects().stream()
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
  public ActivityRequestResponseMetaData getActivityRequestResponseMetadata(
      DiscoverContextRequest<ActivityFunctionParameters> activityFunctionParams) throws DiscoveryException {
    return null;
  }

  @Override
  public void onStart() {
    LOGGER.debug("onStart() - {}", entity.toString());
  }

  @Override
  public void onStop() {
    LOGGER.debug("onStop() - {}", entity.toString());
  }

  @Override
  public void onDeploy(DeployedEntity entity) {
    this.entity = entity;
    LOGGER.debug("onDeploy() - {}", entity.toString());
  }

  @Override
  public void onUnDeploy(DeployedEntity entity) {
    LOGGER.debug("onUnDeploy() - {}", entity.toString());
  }

  @Override
  public State state() {
    return state;
  }

  protected String getSelectedObject(String selectedObjectJson)
      throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectList value = mapper.readValue(selectedObjectJson, ObjectList.class);
    return value.getName();
  }

  public OAuth.Account getAccountInfo(ApiClient client) throws IllegalArgumentException, ApiException {
    OAuth.UserInfo userInfo = client.getUserInfo(client.getAccessToken());
    OAuth.Account accountInfo = null;
    List<OAuth.Account> accounts = userInfo.getAccounts();
    for (OAuth.Account account : accounts) {
      if (isTrue(account.getIsDefault())) {
        accountInfo = account;
      }
    }

    return accountInfo;
  }

}
