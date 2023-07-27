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

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * DocuSign Connector-related message templates used in thrown exceptions.
 */

public class DocuSignMessage {
  public static final String DOCUSIGN_CODE01 = "DocuSign";
  public static final String DOCUSIGN_CODE01_MSG = "DocuSign.01";

  public static final String DOCUSIGN_CODE02 = "DocuSign02";
  public static final String DOCUSIGN_CODE02_MSG = "DocuSign.02";

  public static final String DOCUSIGN_CODE03 = "DocuSign03";
  public static final String DOCUSIGN_CODE03_MSG = "DocuSign.03";

  public static final String DOCUSIGN_CODE04 = "DocuSign04";
  public static final String DOCUSIGN_CODE04_MSG = "DocuSign.04";

  public static final String DOCUSIGN_CODE05 = "DocuSign05";
  public static final String DOCUSIGN_CODE05_MSG = "DocuSign.05";

  public static final String DOCUSIGN_CODE06 = "DocuSign06";
  public static final String DOCUSIGN_CODE06_MSG = "DocuSign.06";

  /**
   * Returns a formatted message using a template from a keyed message store
   * and specified parameters.
   *
   * Eventually, these messages will need to be read from a property bundle file.
   *
   * @param key message template key
   * @param params parameters for the message template
   * @return formatted message
   */
  public static String getMessage(String key, Object[] params) {
    ResourceBundle bundle = ResourceBundle.getBundle("docusign_message");
    String msg = bundle.getString(key);
    return MessageFormat.format(msg, params);
  }
}
