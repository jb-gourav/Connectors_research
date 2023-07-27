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

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.OAuthToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.Properties;

public class TestConnection {

  private String privateKey;
  private String userGuid;
  private String redirectUri;
  private String integrationKey;
  private String tokenExpire;
  private String oAuthBasePath;
  private String testToken;

  private ApiClient apiClientMock = Mockito.mock(ApiClient.class);

  private OAuth.OAuthToken oAuthToken;

  private DocuSignV2Connection docuSignV2Connection;

  @Before
  public void setup() throws Exception {
    getConnectionParams();
    docuSignV2Connection = Mockito
        .spy(new DocuSignV2Connection(privateKey, userGuid, redirectUri, integrationKey, tokenExpire, oAuthBasePath, ""));
  }

  @Test
  public void testConnectionInvalidCredentials() {
    try {

      Mockito.doReturn(apiClientMock).when(docuSignV2Connection).createClient(oAuthBasePath);
      Mockito.when(apiClientMock.requestJWTUserToken(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(),
          Mockito.any(), Mockito.anyLong())).thenReturn(null);
      docuSignV2Connection.open();
      Assert.assertTrue(false);
    } catch (Exception x) {
      x.printStackTrace();
      Assert.assertTrue(true);
    }
  }

  @Test
  public void testConnectionValidCredentials() {
    try {
      Mockito.doReturn(apiClientMock).when(docuSignV2Connection).createClient(oAuthBasePath);
      Mockito.when(apiClientMock.requestJWTUserToken(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(),
          Mockito.any(), Mockito.anyLong())).thenReturn(buildResponseToken());
      docuSignV2Connection.open();
      Assert.assertTrue(true);
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Test Connection failed for valid credentials");
    }
  }

  private OAuth.OAuthToken buildResponseToken() throws InterruptedException {
    oAuthToken = new OAuthToken();
    oAuthToken.setAccessToken(testToken);
    oAuthToken.setExpiresIn(Long.parseLong(tokenExpire));
    return oAuthToken;
  }

  private void getConnectionParams() throws Exception {
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
