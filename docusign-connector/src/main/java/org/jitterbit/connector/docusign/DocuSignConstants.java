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

/**
 * docusign connector-related constants.
 */
public interface DocuSignConstants {

  String CONNECTOR_NAME = "DocuSign";
  String OBJECT_KEY = "object";

  String PRIVATE_KEY = "privateKey";
  String USER_GUID = "apiUserId";
  String REDIRECT_URI = "redirectUri";
  String INTREGATION_KEY = "integrationKey";
  String TOKEN_EXPIRE = "tokenExpire";
  String OAUTH_BASE_PATH = "oAuthBasePath";
  String REST_API = "/restapi";

  String SEND = "send";
  String SENT = "sent";
  String CREATED = "created";

  String ENVELOPE = "Envelope";
  String TEMPLATE = "Template";
  String ENVELOPE_DOCUMENT = "Envelope Document";
  String TEMPLATE_DOCUMENT = "Template Document";
  String ENVELOPE_RECIPIENT = "Envelope Recipient";
  String TEMPLATE_RECIPIENT = "Template Recipient";

  String BLANK_SPACE_REG = "\\s";
  String LINE_BREAK_TAG = "\\n";
  String DASH = "-";
  String WHITE_SPACE = " ";

  String TEST_CONNECTION_MSG_FORMAT = " %s \nhttps://%s/oauth/auth?response_type=code&scope=signature%%20impersonation"
      + "&client_id=%s&redirect_uri=%s \n%s";
  String CONSENT_MSG = "Go to the above URL, you will be asked to log in to DocuSign and to grant permission to the "
      + "connector";
  String INVALID_CRED = "Either invalid credentials entered or granting of consent is required";

  /**
   * supported object list
   */
  enum OBJECT {
    Envelope, Template
  };

  String MESSAGE = "message";
  String XSDS = "xsds";

  String GET_REQ_ROOT = "get-activity-req-root";
  String GET_RES_ROOT = "get-activity-res-root";
  String GET_REQ = "get-req";
  String GET_RES = "get-res";
  String GET_NAMESPACE_REQ = "get_namespace-req";
  String GET_NAMESPACE_RES = "get_namespace-res";

  String SEND_REQ_ROOT = "send-activity-req-root";
  String SEND_RES_ROOT = "send-activity-res-root";
  String SEND_REQ = "send-req";
  String SEND_RES = "send-res";
  String SEND_NAMESPACE_REQ = "send_namespace-req";
  String SEND_NAMESPACE_RES = "send_namespace-res";

  String CREATE_REQ_ROOT = "create-activity-req-root";
  String CREATE_RES_ROOT = "create-activity-res-root";
  String CREATE_REQ = "create-req";
  String CREATE_RES = "create-res";
  String CREATE_NAMESPACE_REQ = "create_namespace-req";
  String CREATE_NAMESPACE_RES = "create_namespace-res";
}
