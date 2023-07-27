package org.jitterbit.connector.docusign.gateway;

import org.jitterbit.connector.docusign.DocuSignConnection;
import org.jitterbit.connector.docusign.DocuSignConnectionFactory;
import org.jitterbit.connector.sdk.Connection;
import org.jitterbit.connectors.common.verbose.VerboseLogger;
import org.jitterbit.connectors.common.verbose.VerboseLoggerManager;

public class DocuSignSDKUpdated implements SDKTestConnection {
    private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(DocuSignConnectionFactory.class);

    /**
     * This method is doing the test connection
     * for SDK version 3.7.0-BETA
     * which internally uses REST API V2.1
     * @param privateKey
     * @param userGuid
     * @param redirectUri
     * @param integrationKey
     * @param tokenExpire
     * @param oAuthBasePath
     * @param version
     * @throws Connection.ConnectionException
     */
    @Override
    public void testConnection(String privateKey, String userGuid, String redirectUri, String integrationKey,
                               String tokenExpire, String oAuthBasePath, String version) throws Connection.ConnectionException {
        LOGGER.info("Creating Test Connection For API version 2.1");
        new DocuSignConnection(privateKey, userGuid, redirectUri, integrationKey,
                tokenExpire, oAuthBasePath,  version).open();
    }
}