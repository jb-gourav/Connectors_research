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
import org.jitterbit.connector.sdk.Discoverable;
import org.jitterbit.connector.sdk.JitterbitActivity;
import org.jitterbit.connector.sdk.Payload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;


/**
 * Tests for the DocuSign Connector.
 */
@SuppressWarnings("all")
public class MockConnectorEngine {

  public static Connection newConnection(Map<String, String> params, DocuSignV2Connection connection) {
    return  connection;
  }

  public static class MockExecutionContext implements JitterbitActivity.ExecutionContext {

    private Connection connection;
    private Map<String, String> functionParams;
    private Payload requestPayload = new Payload.StringPayload("");
    private Payload responsePayload = new Payload.StringPayload("");


    public MockExecutionContext(Map<String, String> endpointParams,
        Map<String, String> functionParams, DocuSignV2Connection connection) {
      this.connection = newConnection(endpointParams, connection);
      this.functionParams = functionParams;
    }

    @Override
    public Connection getConnection() {
      return connection;
    }

    @Override
    public Map<String, String> getFunctionParameters() {
      return functionParams;
    }

    @Override
    public boolean persistenceRequired() {
      return false;
    }

    @Override
    public Payload getRequestPayload() {
      return requestPayload;
    }

    @Override
    public Payload getResponsePayload() {
      return responsePayload;
    }

  }

  public static class MockDiscoverContextRequest<T> implements Discoverable.DiscoverContextRequest {

    private T req;
    private Connection con;

    public MockDiscoverContextRequest(T req, Map<String, String> endpointParams, DocuSignV2Connection connection) {
      this.req = req;
      this.con = newConnection(endpointParams, connection);
    }

    @Override
    public Object getRequest() {
      return req;
    }

    @Override
    public Connection getConnection() {
      return con;
    }

  }

  public static String getPayloadFromFile(String fileName) throws IOException {
    File file = new File(MockConnectorEngine.class.getClassLoader().getResource(fileName).getFile());
    FileInputStream fis = new FileInputStream(file);
    byte[] data = new byte[(int) file.length()];
    fis.read(data);
    fis.close();
    return new String(data, "UTF-8");

  }
  
}
