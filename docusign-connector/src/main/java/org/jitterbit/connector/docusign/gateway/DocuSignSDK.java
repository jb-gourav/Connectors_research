package org.jitterbit.connector.docusign.gateway;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.DocuSignV2Connection;
import org.jitterbit.connectors.common.verbose.VerboseLogger;
import org.jitterbit.connectors.common.verbose.VerboseLoggerManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DocuSignSDK implements SDKTestConnection, DocuSignConstants {

    private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(DocuSignV2Connection.class);
    private ApiClient apiClient;
    private OAuth.OAuthToken oAuthToken;
    private final List<String> scopes = Arrays.asList(OAuth.Scope_SIGNATURE, OAuth.Scope_IMPERSONATION);

    @Override
    public void testConnection(String privateKey, String userGuid, String redirectUri, String integrationKey,
                               String tokenExpire, String oAuthBasePath, String version) {
        LOGGER.info("Creating Test Connection For API Version 2");
        try {
            apiClient = createClient(oAuthBasePath);
            byte[] privateKeyBytes = resolveNewLine(privateKey).getBytes();
            oAuthToken = requestForToken(integrationKey, userGuid, scopes, privateKeyBytes,
                    Long.valueOf(tokenExpire));
            apiClient.setAccessToken(oAuthToken.getAccessToken(), oAuthToken.getExpiresIn());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String buildConsentUrlMessage(String clientId, String redirectUri) {
        return String.format(TEST_CONNECTION_MSG_FORMAT, INVALID_CRED, "account-d.docusign.com", clientId, redirectUri,
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
