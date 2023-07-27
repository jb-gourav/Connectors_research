
package org.jitterbit.connector.docusign.gateway;

import org.jitterbit.connector.docusign.DocuSignConnectionFactory;
import org.jitterbit.connectors.common.verbose.VerboseLogger;
import org.jitterbit.connectors.common.verbose.VerboseLoggerManager;

public class VersionControllerImpl implements VersionController {
    private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(DocuSignConnectionFactory.class);
    private String connectorApiVersion;
    public VersionControllerImpl(String connectorApiVersion) {
        this.connectorApiVersion = connectorApiVersion;
    }

    //This method is responsible to pick the SDK version by User selection
    @Override
    public SDKTestConnection pickSdkVersion() {
        if ("V2".equals(connectorApiVersion))  {
            LOGGER.info("Calling version V2");
            return new DocuSignSDK();
        } else {
            LOGGER.info("Calling version V2.1");
            return new DocuSignSDKUpdated();
        }
    }
}
