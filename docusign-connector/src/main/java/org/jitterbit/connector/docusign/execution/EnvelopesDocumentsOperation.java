package org.jitterbit.connector.docusign.execution;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth.Account;
import com.docusign.esign.model.EnvelopeDocumentsResult;

import org.apache.commons.beanutils.BeanUtils;
import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.schema.EnvelopedocumentGetRequest;
import org.jitterbit.connector.docusign.schema.EnvelopedocumentGetResponse;
import org.jitterbit.connector.sdk.JitterbitActivity.ExecutionContext;
import org.jitterbit.connectors.common.JAXBHelper;
import org.jitterbit.connectors.common.verbose.VerboseLogger;
import org.jitterbit.connectors.common.verbose.VerboseLoggerManager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurabh
 *
 */
public class EnvelopesDocumentsOperation implements DSExecution, DocuSignConstants {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(EnvelopesDocumentsOperation.class);
  DSExecutionUtils executionUtils;

  @Override
  public void executeGetRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    LOGGER.debug("Executing Get Request For: EnveloperDocuments");
    executionUtils = new DSExecutionUtils();
    EnvelopesApi envelopeApi = new EnvelopesApi(apiClient);
    EnvelopedocumentGetRequest request = JAXBHelper.unmarshall(EnvelopedocumentGetRequest.class,
        context.getRequestPayload().getInputStream());

    EnvelopedocumentGetResponse envelopeDocumentResponseXSD = new EnvelopedocumentGetResponse();
    List<EnvelopedocumentGetResponse.EnvelopeDocumentResponseList> envelopesDocList = new ArrayList<EnvelopedocumentGetResponse.EnvelopeDocumentResponseList>();
    List<EnvelopedocumentGetResponse.EnvelopeDocumentResponseList.EnvelopeDocuments.EnvelopeDocument> responseList;
    for (EnvelopedocumentGetRequest.ENTITY entity : request.getENTITY()) {
      EnvelopedocumentGetResponse.EnvelopeDocumentResponseList envelopeDocTranfResponse = new EnvelopedocumentGetResponse.EnvelopeDocumentResponseList();
      envelopeDocTranfResponse.setEnvelopeId(entity.getEnvelopeId());
      try {
        EnvelopeDocumentsResult documentResult = envelopeApi.listDocuments(accountInfo.getAccountId(),
            entity.getEnvelopeId());
        responseList = new ArrayList<EnvelopedocumentGetResponse.EnvelopeDocumentResponseList.EnvelopeDocuments.EnvelopeDocument>();
        envelopeDocTranfResponse.setEnvelopeId(documentResult.getEnvelopeId());

        EnvelopedocumentGetResponse.EnvelopeDocumentResponseList.EnvelopeDocuments envelopeDocs = new EnvelopedocumentGetResponse.EnvelopeDocumentResponseList.EnvelopeDocuments();
        for (com.docusign.esign.model.EnvelopeDocument document : documentResult.getEnvelopeDocuments()) {
          EnvelopedocumentGetResponse.EnvelopeDocumentResponseList.EnvelopeDocuments.EnvelopeDocument envDoc = new EnvelopedocumentGetResponse.EnvelopeDocumentResponseList.EnvelopeDocuments.EnvelopeDocument();
          BeanUtils.copyProperties(envDoc, document);
          envDoc.setDocumentContent(
              executionUtils.getDocumentContent(envelopeApi, accountInfo.getAccountId()
                  , documentResult.getEnvelopeId(), envDoc.getDocumentId()));
          responseList.add(envDoc);
        }
        envelopeDocs.getEnvelopeDocument().addAll(responseList);
        envelopeDocTranfResponse.setEnvelopeDocuments(envelopeDocs);
        envelopesDocList.add(envelopeDocTranfResponse);

      } catch (ApiException ex) {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        EnvelopedocumentGetResponse.EnvelopeDocumentResponseList.ErrorDetails errorDetails = new EnvelopedocumentGetResponse.EnvelopeDocumentResponseList.ErrorDetails();
        errorDetails.setErrorCode(ex.getCode());
        errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
        envelopeDocTranfResponse.setErrorDetails(errorDetails);
        envelopesDocList.add(envelopeDocTranfResponse);
      }
    }
    envelopeDocumentResponseXSD.getEnvelopeDocumentResponseList().addAll(envelopesDocList);

    JAXBHelper.marshall(EnvelopedocumentGetResponse.class, envelopeDocumentResponseXSD,
        context.getResponsePayload().getOutputStream());
  }

  @Override
  public void executeSendRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    throw new Exception("Object Doesn't support the Send Request");
  }

  @Override
  public void executeCreateRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo)
      throws Exception {
    throw new Exception("Object Doesn't support the Create Request");
  }
}
