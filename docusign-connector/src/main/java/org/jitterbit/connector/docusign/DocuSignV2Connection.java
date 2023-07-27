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

package org.jitterbit.connector.docusign;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;

import org.jitterbit.connector.docusign.gateway.SDKTestConnection;
import org.jitterbit.connector.docusign.gateway.VersionControllerImpl;
import org.jitterbit.connector.sdk.Connection;
import org.jitterbit.connectors.common.verbose.VerboseLogger;
import org.jitterbit.connectors.common.verbose.VerboseLoggerManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Connection to a DocuSign endpoint.
 * 
 * @see <a href="https://developers.docusign.com/esign-rest-api/guides/
 *      authentication/oauth2-jsonwebtoken"> DocuSign API Endpoints and
 *      Methods</a>
 */
public class DocuSignV2Connection implements Connection, DocuSignConstants {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(DocuSignV2Connection.class);

  private final String privateKey;
  private final String userGuid;
  private final String redirectUri;
  private final String integrationKey;
  private final long tokenExpireInSecond;
  private final String oAuthBasePath;

  private ApiClient apiClient;
  private OAuth.OAuthToken oAuthToken;
  private String version;

  private final List<String> scopes = Arrays.asList(OAuth.Scope_SIGNATURE, OAuth.Scope_IMPERSONATION);

  /**
   * Constructs a DocuSign connection using DocuSign endpoints.
   *
   * @param privateKey
   * @param userGuid
   * @param redirectUri
   * @param integrationKey
   * @param tokenExpire
   */

  public DocuSignV2Connection(String privateKey, String userGuid, String redirectUri, String integrationKey,
                              String tokenExpire, String oAuthBasePath, String version) {
    this.privateKey = privateKey;
    this.userGuid = userGuid;
    this.redirectUri = redirectUri;
    this.integrationKey = integrationKey;
    this.tokenExpireInSecond = Long.parseLong(tokenExpire);
    this.oAuthBasePath = oAuthBasePath;
    this.version = version;
  }

  /**
   * Opens a DocuSign connection.
   */

  @Override
  public void open() throws ConnectionException {
    LOGGER.debug("Opening Connection For DocuSign");
    try {
      SDKTestConnection sdkConnection = new VersionControllerImpl(version).pickSdkVersion();
      sdkConnection.testConnection(privateKey, userGuid, redirectUri, integrationKey, String.valueOf(tokenExpireInSecond),
              oAuthBasePath, version);
    } catch (Exception e) {
      LOGGER.error(e.getLocalizedMessage(), e);
      throw new ConnectionException("Connection failed ", e.getLocalizedMessage(), e);
    }
  }

  public ApiClient getClient() throws ConnectionException {
    open();
    return apiClient;
  }

  @Override
  public void close() {
    apiClient = null;
  }

  private String buildConsentUrlMessage(String clientId, String redirectUri) {
    return String.format(TEST_CONNECTION_MSG_FORMAT, INVALID_CRED, this.oAuthBasePath, clientId, redirectUri,
        CONSENT_MSG);
  }

  public ApiClient createClient(String oAuthUrl) {
    LOGGER.debug("Client Is Created For Connection");
    ApiClient apiClient = new ApiClient();
    apiClient.setOAuthBasePath(oAuthUrl);
    return apiClient;
  }

  public OAuth.OAuthToken requestForToken(String integrationKey, String userGuid, List<String> scopes,
      byte[] privateKeyBytes, Long tokenExpireInSecond)
      throws IllegalArgumentException, IOException, ApiException {
    LOGGER.debug("Getting Token");
    return apiClient.requestJWTUserToken(integrationKey, userGuid, scopes, privateKeyBytes,
        tokenExpireInSecond);
  }

  private String resolveNewLine(String privateKey) {
    String resolvedString = privateKey.replace(LINE_BREAK_TAG, System.lineSeparator());
    return resolvedString;
  }

}
