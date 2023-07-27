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

import org.jitterbit.connector.sdk.metadata.ActivityRequestResponseMetaData;
import org.jitterbit.connector.sdk.metadata.SchemaMetaData;
import org.jitterbit.connectors.common.CommonHelper;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;


/**
 * Contains utilities methods used within the DocuSign Connector package.
 */
public class DocuSignUtils {


  /**
   * Loads schemas from a class loader and sets them as the request/response
   * schemas for a specified activity.
   *
   * @param activitySchemaResponse reference to the response where the XML schema
   *                               is being set
   * @param requestResourceName    path to the request XML schema
   * @param responseResourceName   path to the response XML schema
   * @throws IOException if there is an error loading the schema
   */
  public static void setRequestResponseSchemas(ActivityRequestResponseMetaData activitySchemaResponse,
      String pathResource, String requestResourceName, String responseResourceName) throws IOException {
    if (requestResourceName != null) {
      activitySchemaResponse.setRequestSchema(
          new SchemaMetaData().setName(DocuSignConstants.CONNECTOR_NAME + "_" + requestResourceName).setContent(
              CommonHelper.loadResource(DocuSignUtils.class.getClassLoader(),
                      pathResource + "/" + requestResourceName)));
    }

    if (responseResourceName != null) {
      activitySchemaResponse.setResponseSchema(
          new SchemaMetaData().setName(DocuSignConstants.CONNECTOR_NAME + "_" + responseResourceName).setContent(
              CommonHelper.loadResource(DocuSignUtils.class.getClassLoader(),
                      pathResource + "/" + responseResourceName)));
    }
  }

  /**
   * Create XSD activity name as per objectName.
   * 
   * @param key
   * @param objectName
   * @return
   */
  public static String getXsdFileName(String key, String objectName) {
    ResourceBundle bundle = ResourceBundle.getBundle("docusign_xsds_filename");
    String msg = bundle.getString(key);
    return MessageFormat.format(msg, new Object[] { objectName });
  }

}
