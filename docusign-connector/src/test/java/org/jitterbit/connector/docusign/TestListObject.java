/*
 * Copyright © 2020 Jitterbit, Inc.
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

package org.jitterbit.connector.docusign;

import org.jitterbit.connector.docusign.activity.DocuSignGetActivity;
import org.jitterbit.connector.docusign.activity.DocuSignSendActivity;
import org.jitterbit.connector.sdk.Discoverable.DiscoverContextRequest;
import org.jitterbit.connector.sdk.JitterbitActivity;
import org.jitterbit.connector.sdk.metadata.ActivityFunctionParameters;
import org.jitterbit.connector.sdk.metadata.DiscoverableObject;
import org.jitterbit.connector.sdk.metadata.DiscoverableObjectRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestListObject {

 private DocuSignV2Connection docuSignV2Connection;
  
  private String privateKey;
  private String userGuid;
  private String redirectUri;
  private String integrationKey;
  private String tokenExpire;
  private String oAuthBasePath;
  private String testToken;
  
  @Before
  public void setup() throws Exception {
    getConnectionVariable();
    docuSignV2Connection = Mockito
        .spy(new DocuSignV2Connection(privateKey, userGuid, redirectUri, integrationKey, tokenExpire, oAuthBasePath, ""));
  }
  
  @Test
  public void testGetActivityListObject() throws Exception {
    JitterbitActivity activity = new DocuSignGetActivity.DocuSignGetActivityFactory().createActivity();
    ActivityFunctionParameters activityFuncParam = new ActivityFunctionParameters();
    DiscoverContextRequest<DiscoverableObjectRequest> objectListRequest  = 
        new MockConnectorEngine.MockDiscoverContextRequest<>(
        activityFuncParam, getConnectionParams(), docuSignV2Connection);
    try {
      List<DiscoverableObject> objectList = activity.getObjectList(objectListRequest);
      Assert.assertNotNull(objectList);
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Object list failed for valid " + DocuSignConstants.TEMPLATE + " Object");
    }
  }

  @Test
  public void testSendActivityListObject() throws Exception {
    JitterbitActivity activity = new DocuSignSendActivity.DocuSignSendActivityFactory().createActivity();
    ActivityFunctionParameters activityFuncParam = new ActivityFunctionParameters();
    DiscoverContextRequest<DiscoverableObjectRequest> objectListRequest  = 
        new MockConnectorEngine.MockDiscoverContextRequest<>(
        activityFuncParam, getConnectionParams(), docuSignV2Connection);
    try {
      List<DiscoverableObject> objectList = activity.getObjectList(objectListRequest);
      Assert.assertNotNull(objectList);
      } catch (Exception x) {
        x.printStackTrace();
        Assert.fail("Object list failed for valid " + DocuSignConstants.ENVELOPE + " Object");
      }
    
  }

  
  
  
  private Map<String, String> getConnectionParams() throws Exception {
    Properties prop = new Properties();
    InputStream is = this.getClass().getResourceAsStream("/docusign-params.properties");
    prop.load(is);

    Map<String, String> validParams = new HashMap<>();

    validParams.put(DocuSignConstants.PRIVATE_KEY, prop.getProperty("docusign.params.privateKey", ""));
    validParams.put(DocuSignConstants.USER_GUID, prop.getProperty("docusign.params.userGuid", ""));
    validParams.put(DocuSignConstants.REDIRECT_URI, prop.getProperty("docusign.params.redirectUri", ""));
    validParams.put(DocuSignConstants.INTREGATION_KEY, prop.getProperty("docusign.params.integrationKey", ""));
    validParams.put(DocuSignConstants.TOKEN_EXPIRE, prop.getProperty("docusign.params.tokenExpire", ""));
    validParams.put(DocuSignConstants.OAUTH_BASE_PATH, prop.getProperty("docusign.params.oAuthBasePath", ""));

    return validParams;
  }
  
  private void getConnectionVariable() throws Exception {
    Properties prop = new Properties();
    InputStream is = this.getClass().getResourceAsStream("/docusign-params.properties");
    prop.load(is);

    privateKey = prop.getProperty("docusign.params.privateKey", "");
    userGuid = prop.getProperty("docusign.params.userGuid", "");
    redirectUri = prop.getProperty("docusign.params.redirectUri", "");
    integrationKey = prop.getProperty("docusign.params.integrationKey", "");
    tokenExpire = prop.getProperty("docusign.params.tokenExpire", "");
    oAuthBasePath = prop.getProperty("docusign.params.oAuthBasePath", "");
    testToken = prop.getProperty("docusign.params.testToken", "");

  }


}
