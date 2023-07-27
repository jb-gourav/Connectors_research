package org.jitterbit.connector.docusign.execution;

import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth.Account;
import com.docusign.esign.model.CarbonCopy;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeTemplate;
import com.docusign.esign.model.EnvelopeTemplateResult;
import com.docusign.esign.model.EnvelopeTemplateResults;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.docusign.esign.model.TemplateSummary;

import org.apache.commons.beanutils.BeanUtils;
import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.schema.TemplateCreateRequest;
import org.jitterbit.connector.docusign.schema.TemplateCreateResponse;
import org.jitterbit.connector.docusign.schema.TemplateGetRequest;
import org.jitterbit.connector.docusign.schema.TemplateGetResponse;
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
public class TemplatesOperation implements DSExecution, DocuSignConstants {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(TemplatesOperation.class);

  @Override
  public void executeGetRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    LOGGER.debug("Executing Get Request For: TemplatesOperation");
    TemplatesApi templateApi = new TemplatesApi(apiClient);

    TemplateGetRequest request = JAXBHelper.unmarshall(TemplateGetRequest.class,
        context.getRequestPayload().getInputStream());

    com.docusign.esign.api.TemplatesApi.ListTemplatesOptions options = templateApi.new ListTemplatesOptions();
    TemplateGetResponse templateGetResponseXSD = new TemplateGetResponse();
    List<TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition> envelopeTemplateDefinitionList;
    TemplateGetRequest.QueryParameters params = request.getQueryParameters();
    BeanUtils.copyProperties(options, params);
    TemplateGetResponse.TemplateGetResponseList templateTranfResponse = new TemplateGetResponse.TemplateGetResponseList();
    try {
      EnvelopeTemplateResults templateResult = templateApi.listTemplates(accountInfo.getAccountId(), options);
      envelopeTemplateDefinitionList = new ArrayList<TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition>();
      templateTranfResponse.setResultSetSize(templateResult.getResultSetSize());
      templateTranfResponse.setEndPosition(templateResult.getEndPosition());
      templateTranfResponse.setStartPosition(templateResult.getStartPosition());
      templateTranfResponse.setTotalSetSize(templateResult.getTotalSetSize());

      TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates templates = new TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates();
      List<TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition.Documents> templatesParentDocumentList = new ArrayList<TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition.Documents>();
      TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition.Documents templatesParentDocument = new TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition.Documents();

      for (EnvelopeTemplateResult envResult : templateResult.getEnvelopeTemplates()) {

        TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition env = new TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition();
        BeanUtils.copyProperties(env, envResult);
        TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition.UserInfo owner = new TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition.UserInfo();
        BeanUtils.copyProperties(owner, envResult.getOwner());

        for (Document document : envResult.getDocuments()) {
          TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition.Documents.Document documentTrans = new TemplateGetResponse.TemplateGetResponseList.EnvelopeTemplates.EnvelopeTemplateDefinition.Documents.Document();
          BeanUtils.copyProperties(documentTrans, document);
          templatesParentDocument.setDocument(documentTrans);
          templatesParentDocumentList.add(templatesParentDocument);
        }
        env.setUserInfo(owner);
        env.getDocuments().addAll(templatesParentDocumentList);
        envelopeTemplateDefinitionList.add(env);
      }

      templates.getEnvelopeTemplateDefinition().addAll(envelopeTemplateDefinitionList);
      templateTranfResponse.setEnvelopeTemplates(templates);
    } catch (ApiException ex) {
      LOGGER.error(ex.getLocalizedMessage(), ex);
      TemplateGetResponse.TemplateGetResponseList.ErrorDetails errorDetails = new TemplateGetResponse.TemplateGetResponseList.ErrorDetails();
      errorDetails.setErrorCode(ex.getCode());
      errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
      templateTranfResponse.setErrorDetails(errorDetails);
    }

    templateGetResponseXSD.getTemplateGetResponseList().add(templateTranfResponse);
    JAXBHelper.marshall(TemplateGetResponse.class, templateGetResponseXSD,
        context.getResponsePayload().getOutputStream());

  }

  @Override
  public void executeSendRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    throw new Exception("Object Doesn't support the Send Request");
  }

  @Override
  public void executeCreateRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo)
      throws Exception {
    LOGGER.debug("Executing Create Request For: TemplatesOperation");
    TemplateCreateRequest request = JAXBHelper.unmarshall(TemplateCreateRequest.class,
        context.getRequestPayload().getInputStream());
    TemplatesApi templatesApi = new TemplatesApi(apiClient);
    EnvelopeTemplate templateDefinition;
    Document envDocument;
    List<Document> envDocumentList = new ArrayList<Document>();
    SignHere envSignHere;
    List<SignHere> envSignHereList = new ArrayList<SignHere>();
    CarbonCopy envCarbonCopy;
    List<CarbonCopy> envCarbonCopyList = new ArrayList<CarbonCopy>();
    Signer envSigner;
    List<Signer> envSignerList = new ArrayList<Signer>();
    Recipients envRecipients;
    com.docusign.esign.model.EnvelopeTemplateDefinition envDefinition;
    List<TemplateCreateResponse.TemplateCreateResponseList> envelopesSendResponseList = new ArrayList<TemplateCreateResponse.TemplateCreateResponseList>();
    TemplateCreateResponse.TemplateCreateResponseList templateCreateResponse = new TemplateCreateResponse.TemplateCreateResponseList();
    TemplateCreateResponse templateCreateResponseXSD = new TemplateCreateResponse();
    for (TemplateCreateRequest.ENTITY entity : request.getENTITY()) {
      try {
        envRecipients = new Recipients();
        templateDefinition = new EnvelopeTemplate();
        templateDefinition.setEmailSubject(entity.getEmailSubject());
        envDefinition = new com.docusign.esign.model.EnvelopeTemplateDefinition();
        envDefinition.setName(entity.getName());
        envDefinition.setDescription(entity.getDescription());
        templateDefinition.setStatus(CREATED);
        for (TemplateCreateRequest.ENTITY.Documents.Document documents : entity.getDocuments().getDocument()) {
          envDocument = new Document();
          BeanUtils.copyProperties(envDocument, documents);
          envDocumentList.add(envDocument);
        }
        templateDefinition.setDocuments(envDocumentList);
        for (TemplateCreateRequest.ENTITY.Recipients.CarbonCopies.CarbonCopy carbonCopy : entity.getRecipients()
            .getCarbonCopies().getCarbonCopy()) {
          envCarbonCopy = new CarbonCopy();
          BeanUtils.copyProperties(envCarbonCopy, carbonCopy);
          envCarbonCopyList.add(envCarbonCopy);
        }
        if (envCarbonCopyList.size() > 0) {
          envRecipients.setCarbonCopies(envCarbonCopyList);
        }
        for (TemplateCreateRequest.ENTITY.Recipients.Signers.Signer signer : entity.getRecipients().getSigners()
            .getSigner()) {
          envSigner = new Signer();
          Tabs tab = new Tabs();
          envSigner.setEmail(signer.getEmail());
          envSigner.setName(signer.getName());
          envSigner.setRecipientId(signer.getRecipientId());
          envSigner.setRoutingOrder(signer.getRoutingOrder());
          for (TemplateCreateRequest.ENTITY.Recipients.Signers.Signer.Tabs.SignHereTabs.SignHereTab signHereTab : signer
              .getTabs().getSignHereTabs().getSignHereTab()) {
            envSignHere = new SignHere();
            BeanUtils.copyProperties(envSignHere, signHereTab);
            envSignHereList.add(envSignHere);
          }
          if (envSignHereList.size() > 0) {

            tab.setSignHereTabs(envSignHereList);
            envSigner.setTabs(tab);
          }
          envSignerList.add(envSigner);
        }
        if (envSignerList.size() > 0) {
          envRecipients.setSigners(envSignerList);
        }
        if (envRecipients.getCarbonCopies() != null || envRecipients.getSigners() != null) {
          templateDefinition.setRecipients(envRecipients);
        }
        templateDefinition.setEnvelopeTemplateDefinition(envDefinition);
        TemplateSummary results = templatesApi.createTemplate(accountInfo.getAccountId(), templateDefinition);
        templateCreateResponse.setTemplateId(results.getTemplateId());
        templateCreateResponse.setUri(results.getUri());
        templateCreateResponse.setName(results.getName());
        envelopesSendResponseList.add(templateCreateResponse);
      } catch (ApiException ex) {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        TemplateCreateResponse.TemplateCreateResponseList.ErrorDetails errorDetails = new TemplateCreateResponse.TemplateCreateResponseList.ErrorDetails();
        errorDetails.setErrorCode(ex.getCode());
        errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
        templateCreateResponse.setErrorDetails(errorDetails);
        envelopesSendResponseList.add(templateCreateResponse);
      }
    }
    templateCreateResponseXSD.getTemplateCreateResponseList().addAll(envelopesSendResponseList);
    JAXBHelper.marshall(TemplateCreateResponse.class, templateCreateResponseXSD,
        context.getResponsePayload().getOutputStream());
  }

}
