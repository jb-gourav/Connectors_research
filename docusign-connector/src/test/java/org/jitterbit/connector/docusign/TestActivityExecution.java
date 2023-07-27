/*
 * Copyright © 2020 Jitterbit, Inc.
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

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.OAuthToken;
import com.docusign.esign.model.CarbonCopy;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.Envelope;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.EnvelopeTemplateResult;
import com.docusign.esign.model.EnvelopeTemplateResults;
import com.docusign.esign.model.EnvelopesInformation;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.TemplateSummary;
import com.docusign.esign.model.UserInfo;

import org.jitterbit.connector.docusign.activity.DocuSignCreateActivity;
import org.jitterbit.connector.docusign.activity.DocuSignGetActivity;
import org.jitterbit.connector.docusign.activity.DocuSignSendActivity;
import org.jitterbit.connector.sdk.JitterbitActivity;
import org.jitterbit.connector.sdk.exceptions.ActivityExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class TestActivityExecution {
  private String privateKey;
  private String userGuid;
  private String redirectUri;
  private String integrationKey;
  private String tokenExpire;
  private String oAuthBasePath;
  private String testToken;

  @Mock
  private ApiClient apiClientMock = Mockito.mock(ApiClient.class);

  private OAuth.OAuthToken oAuthToken;

  private DocuSignV2Connection docuSignV2Connection;

  @Mock
  private EnvelopesApi envelopeApiMock = Mockito.mock(EnvelopesApi.class);

  private DocuSignGetActivity getActivity;

  private DocuSignSendActivity sendActivity;

  private DocuSignCreateActivity createActivity;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    getConnectionParams();

    docuSignV2Connection = Mockito.spy(new DocuSignV2Connection(privateKey, userGuid, redirectUri, integrationKey,
        tokenExpire, oAuthBasePath, ""));

    getActivity = Mockito.spy(new DocuSignGetActivity());
    sendActivity = Mockito.spy(new DocuSignSendActivity());
    createActivity = Mockito.spy(new DocuSignCreateActivity());

    Mockito.doReturn(apiClientMock).when(docuSignV2Connection).createClient(oAuthBasePath);
    Mockito.when(apiClientMock.requestJWTUserToken(Mockito.anyString(), Mockito.anyString(),
        Mockito.anyList(), Mockito.any(), Mockito.anyLong())).thenReturn(buildResponseToken());
    Mockito.doReturn(escapeString()).when(apiClientMock).escapeString(Mockito.anyString());

  }

  @Test
  public void executeGetActivityPositiveCaseEnvelope() {
    try {
      Mockito.doReturn(buildAccountResponse()).when(getActivity).getAccountInfo(apiClientMock);
      Mockito
          .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
              Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
          .thenReturn(buildEnvelopeInformation());

      Map<String, String> functionParams = new HashMap<String, String>();
      functionParams.put("object", "{\"name\":\"Envelope\"}");
      JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
          getConnectionParams1(), functionParams, docuSignV2Connection);
      context.getRequestPayload()
          .setContent(MockConnectorEngine.getPayloadFromFile("get-envelope-request.xml"));
      getActivity.execute(context);
      String actualResponse = context.getResponsePayload().getContent();
      String expectedResponse = readFileAsString("ExecuteGetActivityEnvelopeResponse.xml");
      Assert.assertTrue(actualResponse.equalsIgnoreCase(expectedResponse));
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Get activity failed for positive case Envelope");
    }
  }

  @Test
  public void executeGetActivityPositiveCaseTemplate() {
    try {
      Mockito.doReturn(buildAccountResponse()).when(getActivity).getAccountInfo(apiClientMock);
      Mockito
          .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
              Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
          .thenReturn(buildTemplateResult());

      Map<String, String> functionParams = new HashMap<String, String>();
      functionParams.put("object", "{\"name\":\"Template\"}");
      JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
          getConnectionParams1(), functionParams, docuSignV2Connection);
      context.getRequestPayload()
          .setContent(MockConnectorEngine.getPayloadFromFile("get-template-request.xml"));
      getActivity.execute(context);
      String actualResponse = context.getResponsePayload().getContent();
      String expectedResponse = readFileAsString("ExecuteGetActivityTemplateResponse.xml");
      Assert.assertTrue(actualResponse.equalsIgnoreCase(expectedResponse));
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Get activity failed for positive case Template");
    }
  }

  @Test
  public void executeGetActivityPositiveCaseEnvelopeRecipient() {
    try {
      Mockito.doReturn(buildAccountResponse()).when(getActivity).getAccountInfo(apiClientMock);
      Mockito
          .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
              Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
          .thenReturn(buildEnvelopeRecipientsResult());

      Map<String, String> functionParams = new HashMap<String, String>();
      functionParams.put("object", "{\"name\":\"Envelope Recipient\"}");
      JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
          getConnectionParams1(), functionParams, docuSignV2Connection);
      context.getRequestPayload()
          .setContent(MockConnectorEngine.getPayloadFromFile("get-envelope-recipient-request.xml"));
      getActivity.execute(context);
      String actualResponse = context.getResponsePayload().getContent();
      String expectedResponse = readFileAsString("ExecuteGetActivityEnvelopeRecipientResponse.xml");
      Assert.assertTrue(actualResponse.equalsIgnoreCase(expectedResponse));
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Get activity failed for positive case Envelope Recipient");
    }
  }

  @Test
  public void executeGetActivityPositiveCaseTemplateRecipient() {
    try {
      Mockito.doReturn(buildAccountResponse()).when(getActivity).getAccountInfo(apiClientMock);
      Mockito
          .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
              Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
          .thenReturn(buildEnvelopeRecipientsResult());

      Map<String, String> functionParams = new HashMap<String, String>();
      functionParams.put("object", "{\"name\":\"Template Recipient\"}");
      JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
          getConnectionParams1(), functionParams, docuSignV2Connection);
      context.getRequestPayload()
          .setContent(MockConnectorEngine.getPayloadFromFile("get-template-recipient-request.xml"));
      getActivity.execute(context);
      String actualResponse = context.getResponsePayload().getContent();
      String expectedResponse = readFileAsString("ExecuteGetActivityTemplateRecipientResponse.xml");
      Assert.assertTrue(actualResponse.equalsIgnoreCase(expectedResponse));
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Get activity failed for positive case Template Recipient");
    }
  }

  // Invalid Object Name
  @Test(expected = ActivityExecutionException.class)
  public void executeGetActivityNegativeCaseFakeEnvelope() throws Exception {
    Mockito.doReturn(buildAccountResponse()).when(getActivity).getAccountInfo(apiClientMock);
    Mockito
        .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(buildEnvelopeInformation());

    Map<String, String> functionParams = new HashMap<String, String>();
    functionParams.put("object", "{\"name\":\"fakeEnvelope\"}");
    JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
        getConnectionParams1(), functionParams, docuSignV2Connection);
    getActivity.execute(context);
  }

  // Invalid Request
  @Test(expected = ActivityExecutionException.class)
  public void executeGetActivityNegativeCaseEnvelope() throws Exception {

    Mockito.doReturn(buildAccountResponse()).when(getActivity).getAccountInfo(apiClientMock);
    Mockito
        .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(buildEnvelopeInformation());

    Map<String, String> functionParams = new HashMap<String, String>();
    functionParams.put("object", "{\"name\":\"Envelope\"}");
    JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
        getConnectionParams1(), functionParams, docuSignV2Connection);
    getActivity.execute(context);

  }

  @Test
  public void executeSendActivityPositiveCaseEnvelope() {
    try {
      Mockito.doReturn(buildAccountResponse()).when(sendActivity).getAccountInfo(apiClientMock);
      Mockito
          .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
              Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
          .thenReturn(buildEnvelopeSummary());

      Map<String, String> functionParams = new HashMap<String, String>();
      functionParams.put("object", "{\"name\":\"Envelope\"}");
      JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
          getConnectionParams1(), functionParams, docuSignV2Connection);
      context.getRequestPayload()
          .setContent(MockConnectorEngine.getPayloadFromFile("send-activity-request.xml"));
      sendActivity.execute(context);
      String actualResponse = context.getResponsePayload().getContent();
      String expectedResponse = readFileAsString("ExecuteSendActivityEnvelopeResponse.xml");
      Assert.assertTrue(actualResponse.equalsIgnoreCase(expectedResponse));
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Send activity failed for positive case Envelope");
    }
  }

  @Test(expected = ActivityExecutionException.class)
  public void executeSendActivityPositiveCaseTemplate() throws Exception {
    Mockito.doReturn(buildAccountResponse()).when(sendActivity).getAccountInfo(apiClientMock);
    Mockito
        .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(buildEnvelopeSummary());

    Map<String, String> functionParams = new HashMap<String, String>();
    functionParams.put("object", "{\"name\":\"Template\"}");
    JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
        getConnectionParams1(), functionParams, docuSignV2Connection);
    sendActivity.execute(context);
  }

  // Invalid Request
  @Test(expected = ActivityExecutionException.class)
  public void executeSendActivityNegativeCaseEnvelope() throws Exception {

    Mockito.doReturn(buildAccountResponse()).when(sendActivity).getAccountInfo(apiClientMock);
    Mockito
        .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(buildEnvelopeSummary());

    Map<String, String> functionParams = new HashMap<String, String>();
    functionParams.put("object", "{\"name\":\"Envelope\"}");
    JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
        getConnectionParams1(), functionParams, docuSignV2Connection);
    sendActivity.execute(context);

  }

  // Invalid Object Name
  @Test(expected = ActivityExecutionException.class)
  public void executeSendActivityNegativeCaseFakeEnvelope() throws Exception {

    Mockito.doReturn(buildAccountResponse()).when(sendActivity).getAccountInfo(apiClientMock);
    Mockito
        .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(buildEnvelopeSummary());

    Map<String, String> functionParams = new HashMap<String, String>();
    functionParams.put("object", "{\"name\":\"fakeEnvelope\"}");
    JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
        getConnectionParams1(), functionParams, docuSignV2Connection);
    context.getRequestPayload()
        .setContent(MockConnectorEngine.getPayloadFromFile("send-activity-request.xml"));
    sendActivity.execute(context);

  }

  @Test
  public void executeCreateActivityPositiveCaseEnvelope() {
    try {
      Mockito.doReturn(buildAccountResponse()).when(createActivity).getAccountInfo(apiClientMock);
      Mockito
          .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
              Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
          .thenReturn(buildEnvelopeSummary());

      Map<String, String> functionParams = new HashMap<String, String>();
      functionParams.put("object", "{\"name\":\"Envelope\"}");
      JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
          getConnectionParams1(), functionParams, docuSignV2Connection);
      context.getRequestPayload()
          .setContent(MockConnectorEngine.getPayloadFromFile("create-envelope-request.xml"));
      createActivity.execute(context);
      String actualResponse = context.getResponsePayload().getContent();
      String expectedResponse = readFileAsString("ExecuteCreateActivityEnvelopeResponse.xml");
      Assert.assertTrue(actualResponse.equalsIgnoreCase(expectedResponse));
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Create activity failed for positive case Envelope");
    }
  }

  @Test
  public void executeCreateActivityPositiveCaseTemplate() {
    try {
      Mockito.doReturn(buildAccountResponse()).when(createActivity).getAccountInfo(apiClientMock);
      Mockito
          .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
              Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
          .thenReturn(buildTemplateSummary());

      Map<String, String> functionParams = new HashMap<String, String>();
      functionParams.put("object", "{\"name\":\"Template\"}");
      JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
          getConnectionParams1(), functionParams, docuSignV2Connection);
      context.getRequestPayload()
          .setContent(MockConnectorEngine.getPayloadFromFile("create-template-request.xml"));
      createActivity.execute(context);
      String actualResponse = context.getResponsePayload().getContent();
      String expectedResponse = readFileAsString("ExecuteCreateActivityTemplateResponse.xml");
      Assert.assertTrue(actualResponse.equalsIgnoreCase(expectedResponse));
    } catch (Exception x) {
      x.printStackTrace();
      Assert.fail("Create activity failed for positive case Template");
    }
  }

  // Invalid Request
  @Test(expected = ActivityExecutionException.class)
  public void executeCreateActivityNegativeCaseEnvelope() throws Exception {

    Mockito.doReturn(buildAccountResponse()).when(createActivity).getAccountInfo(apiClientMock);
    Mockito
        .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(buildEnvelopeSummary());

    Map<String, String> functionParams = new HashMap<String, String>();
    functionParams.put("object", "{\"name\":\"Envelope\"}");
    JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
        getConnectionParams1(), functionParams, docuSignV2Connection);
    createActivity.execute(context);

  }

  // Invalid Object Name
  @Test(expected = ActivityExecutionException.class)
  public void executeCreateActivityNegativeCaseFakeEnvelope() throws Exception {

    Mockito.doReturn(buildAccountResponse()).when(createActivity).getAccountInfo(apiClientMock);
    Mockito
        .when(apiClientMock.invokeAPI(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(buildEnvelopeSummary());

    Map<String, String> functionParams = new HashMap<String, String>();
    functionParams.put("object", "{\"name\":\"fakeEnvelope\"}");
    JitterbitActivity.ExecutionContext context = new MockConnectorEngine.MockExecutionContext(
        getConnectionParams1(), functionParams, docuSignV2Connection);
    createActivity.execute(context);

  }

  private OAuth.OAuthToken buildResponseToken() throws InterruptedException {
    oAuthToken = new OAuthToken();
    oAuthToken.setAccessToken(testToken);
    oAuthToken.setExpiresIn(Long.parseLong(tokenExpire));
    return oAuthToken;
  }

  private OAuth.Account buildAccountResponse() {
    OAuth.Account accountInfo = new OAuth.Account();
    accountInfo.setAccountId("8719807");
    accountInfo.setBaseUri("baseUri");
    return accountInfo;
  }

  private EnvelopesInformation buildEnvelopeInformation() {
    EnvelopesInformation envelopeInfo = new EnvelopesInformation();
    envelopeInfo.setNextUri("123456");
    Envelope envelope = new Envelope();
    envelope.setEnvelopeId("123");
    envelopeInfo.setEnvelopes(Arrays.asList(envelope));
    return envelopeInfo;
  }

  private EnvelopeSummary buildEnvelopeSummary() {
    EnvelopeSummary summary = new EnvelopeSummary();
    summary.setEnvelopeId("envelopeId");
    summary.setStatusDateTime("2019/06/06");
    return summary;
  }

  private TemplateSummary buildTemplateSummary() {
    TemplateSummary templateSummary = new TemplateSummary();
    templateSummary.setApplied("applied");
    templateSummary.setDocumentId("1");
    templateSummary.setDocumentName("Document Name");
    return templateSummary;
  }

  private EnvelopeTemplateResults buildTemplateResult() {
    EnvelopeTemplateResults result = new EnvelopeTemplateResults();
    result.setEndPosition("1");
    EnvelopeTemplateResult tempalteResult = new EnvelopeTemplateResult();
    tempalteResult.setEmailSubject("Email Subject");
    tempalteResult.setPageCount(1);
    UserInfo userInfo = new UserInfo();
    userInfo.setEmail("");
    tempalteResult.setOwner(userInfo);
    Document document = new Document();
    document.setDocumentId("");
    document.setOrder("");
    document.setName("");
    document.setDocumentBase64("");
    document.setPages("1");
    document.setUri("");
    tempalteResult.setDocuments(Arrays.asList(document));
    result.setEnvelopeTemplates(Arrays.asList(tempalteResult));
    return result;
  }

  private Recipients buildEnvelopeRecipientsResult() {
    Recipients recipients = new Recipients();
    CarbonCopy carbonCopy = new CarbonCopy();
    carbonCopy.setName("");
    carbonCopy.setEmail("");
    recipients.setCarbonCopies(Arrays.asList(carbonCopy));
    Signer signer = new Signer();
    signer.setName("");
    signer.setEmail("");
    recipients.setSigners(Arrays.asList(signer));
    return recipients;
  }

  private void getConnectionParams() throws Exception {
    Properties prop = new Properties();
    InputStream is = this.getClass().getResourceAsStream("/docusign-params.properties");
    prop.load(is);

    privateKey = prop.getProperty("docusign.params.privateKey", "");
    userGuid = prop.getProperty("docusign.params.userGuid", "");
    redirectUri = prop.getProperty("docusign.params.redirectUri", "");
    integrationKey = prop.getProperty("docusign.params.integrationKey", "");
    tokenExpire = prop.getProperty("docusign.params.tokenExpire", "");
    oAuthBasePath = prop.getProperty("docusign.params.oAuthBasePath", "");
    testToken = prop.getProperty("docusign.params.testToken", "");

  }

  private Map<String, String> getConnectionParams1() throws Exception {
    Properties prop = new Properties();
    InputStream is = this.getClass().getResourceAsStream("/docusign-params.properties");
    prop.load(is);

    Map<String, String> validParams = new HashMap<>();

    validParams.put(DocuSignConstants.PRIVATE_KEY, prop.getProperty("docusign.params.privateKey", ""));
    validParams.put(DocuSignConstants.USER_GUID, prop.getProperty("docusign.params.userGuid", ""));
    validParams.put(DocuSignConstants.REDIRECT_URI, prop.getProperty("docusign.params.redirectUri", ""));
    validParams.put(DocuSignConstants.INTREGATION_KEY,
        prop.getProperty("docusign.params.integrationKey", ""));
    validParams.put(DocuSignConstants.TOKEN_EXPIRE, prop.getProperty("docusign.params.tokenExpire", ""));
    validParams.put(DocuSignConstants.OAUTH_BASE_PATH, prop.getProperty("docusign.params.oAuthBasePath", ""));

    return validParams;
  }

  private String escapeString() throws UnsupportedEncodingException {
    return URLEncoder.encode("123456789", "utf8").replaceAll("\\+", "%20");
  }

  public String readFileAsString(String fileName) throws Exception {
    File file = new File(TestActivityExecution.class.getClassLoader().getResource(fileName).getFile());
    FileInputStream fileInputStream = new FileInputStream(file);
    return new BufferedReader(new InputStreamReader(fileInputStream)).lines().parallel()
        .collect(Collectors.joining("\n"));
  }
}
