package org.jitterbit.connector.docusign.gateway;

import org.jitterbit.connector.sdk.Connection;

public interface SDKTestConnection {
    void testConnection (String privateKey, String userGuid, String redirectUri, String integrationKey,
                         String tokenExpire, String oAuthBasePath, String version) throws Connection.ConnectionException;

}
