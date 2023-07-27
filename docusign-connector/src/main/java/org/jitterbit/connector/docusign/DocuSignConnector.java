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

import org.jitterbit.connector.sdk.BaseJitterbitConnector;
import org.jitterbit.connector.sdk.ConnectionFactory;
import org.jitterbit.connector.sdk.JitterbitConnector;
import org.jitterbit.connector.sdk.annotation.Connector;

/**
 * 
 * The DocuSign has three activities (functions): Create, Send and Get
 * 
 */
@Connector(name = DocuSignConstants.CONNECTOR_NAME, factory = DocuSignConnector.DocuSignConnectorFactory.class)
public class DocuSignConnector extends BaseJitterbitConnector implements DocuSignConstants {

  public static final DocuSignConnector INSTANCE = new DocuSignConnector();

  private static ConnectionFactory connectionFactory;

  static {
    connectionFactory = DocuSignConnectionFactory.INSTANCE;
  }

  @Override
  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  public String getName() {
    return CONNECTOR_NAME;
  }

  /**
   * Factory for creating activity
   */
  public static class DocuSignConnectorFactory implements JitterbitConnector.Factory {
    @Override
    public JitterbitConnector create() {
      return DocuSignConnector.INSTANCE;
    }

  }
}
