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

import org.jitterbit.connector.sdk.Connection;
import org.jitterbit.connector.sdk.ConnectionFactory;
import org.jitterbit.connectors.common.verbose.VerboseLogger;
import org.jitterbit.connectors.common.verbose.VerboseLoggerManager;

import java.util.Map;

/**
 * Factory that creates a {@link DocuSignV2Connection } instance
 */
public class DocuSignConnectionFactory implements DocuSignConstants, ConnectionFactory {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(DocuSignConnectionFactory.class);

  public static final DocuSignConnectionFactory INSTANCE = new DocuSignConnectionFactory();

  private DocuSignConnectionFactory() {
  }

  /**
   * Returns a connection to a DocuSign end-point, created from the specified
   * properties.
   *
   * @param props properties for configuring and creating a DocuSign connection
   * @return the configured connection
   * @throws RuntimeException if the Private Key, User Guid, Redirect Uri,
   *                          Intregation Key, Token Expire properties of the
   *                          specified properties are empty or null
   */
  @Override
  public Connection createConnection(Map<String, String> props) {
    LOGGER.debug("Creating Connection For DocuSign");
    return new DocuSignV2Connection(props.get(PRIVATE_KEY), props.get(USER_GUID), props.get(REDIRECT_URI),
        props.get(INTREGATION_KEY), props.get(TOKEN_EXPIRE), props.get(OAUTH_BASE_PATH), props.get("version"));
  }

  /**
   * Returns the pool size configuration.
   *
   * @return the pool size configuration
   */
  @Override
  public PoolSizeConfiguration getPoolSizeConfiguration() {
    return new PoolSizeConfiguration();
  }
}
