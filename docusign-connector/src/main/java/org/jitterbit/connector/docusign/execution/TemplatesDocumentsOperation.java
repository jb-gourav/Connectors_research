package org.jitterbit.connector.docusign.execution;

import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth.Account;
import com.docusign.esign.model.TemplateDocumentsResult;

import org.apache.commons.beanutils.BeanUtils;
import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.schema.TemplatedocumentGetRequest;
import org.jitterbit.connector.docusign.schema.TemplatedocumentGetResponse;
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
public class TemplatesDocumentsOperation implements DSExecution, DocuSignConstants {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(TemplatesDocumentsOperation.class);
  DSExecutionUtils executionUtils;
  
  @Override
  public void executeGetRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    LOGGER.debug("Executing Get Request For EnvelopesRecipientOperation");
    executionUtils = new DSExecutionUtils();
    TemplatesApi templateApi = new TemplatesApi(apiClient);
    TemplatedocumentGetRequest request = JAXBHelper.unmarshall(TemplatedocumentGetRequest.class,
        context.getRequestPayload().getInputStream());
    TemplatedocumentGetResponse templateDocumentResponseXSD = new TemplatedocumentGetResponse();
    List<TemplatedocumentGetResponse.TemplateDocumentResponseList> templatesDocList = new ArrayList<TemplatedocumentGetResponse.TemplateDocumentResponseList>();
    List<TemplatedocumentGetResponse.TemplateDocumentResponseList.TemplateDocuments.EnvelopeDocument> responseList;
      for (TemplatedocumentGetRequest.ENTITY entity : request.getENTITY()) {
      TemplatedocumentGetResponse.TemplateDocumentResponseList templateDocTranfResponse = new TemplatedocumentGetResponse.TemplateDocumentResponseList();
      templateDocTranfResponse.setTemplateId(entity.getTemplateId());
      try {
        TemplateDocumentsResult templateDocumentResult = templateApi.listDocuments(accountInfo.getAccountId(),
            entity.getTemplateId());
        responseList = new ArrayList<TemplatedocumentGetResponse.TemplateDocumentResponseList.TemplateDocuments.EnvelopeDocument>();
        templateDocTranfResponse.setTemplateId(templateDocumentResult.getTemplateId());

        TemplatedocumentGetResponse.TemplateDocumentResponseList.TemplateDocuments templateDocs = new TemplatedocumentGetResponse.TemplateDocumentResponseList.TemplateDocuments();
        for (com.docusign.esign.model.EnvelopeDocument document : templateDocumentResult.getTemplateDocuments()) {
          TemplatedocumentGetResponse.TemplateDocumentResponseList.TemplateDocuments.EnvelopeDocument envDoc = new TemplatedocumentGetResponse.TemplateDocumentResponseList.TemplateDocuments.EnvelopeDocument();
          BeanUtils.copyProperties(envDoc, document);
          envDoc.setDocumentContent(
              executionUtils.getDocumentContent(templateApi, accountInfo.getAccountId()
                  , templateDocumentResult.getTemplateId(), String.valueOf(envDoc.getDocumentId())));

          responseList.add(envDoc);
        }
        templateDocs.getEnvelopeDocument().addAll(responseList);
        templateDocTranfResponse.setTemplateDocuments(templateDocs);
        templatesDocList.add(templateDocTranfResponse);

      } catch (ApiException ex) {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        TemplatedocumentGetResponse.TemplateDocumentResponseList.ErrorDetails errorDetails = new TemplatedocumentGetResponse.TemplateDocumentResponseList.ErrorDetails();
        errorDetails.setErrorCode(ex.getCode());
        errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
        templateDocTranfResponse.setErrorDetails(errorDetails);
        templatesDocList.add(templateDocTranfResponse);
      }
    }
    templateDocumentResponseXSD.getTemplateDocumentResponseList().addAll(templatesDocList);

    JAXBHelper.marshall(TemplatedocumentGetResponse.class, templateDocumentResponseXSD,
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
