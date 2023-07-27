package org.jitterbit.connector.docusign.execution;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.EnvelopesApi.ListStatusChangesOptions;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth.Account;
import com.docusign.esign.model.CarbonCopy;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.EnvelopeUpdateSummary;
import com.docusign.esign.model.EnvelopesInformation;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.schema.EnvelopeCreateRequest;
import org.jitterbit.connector.docusign.schema.EnvelopeCreateResponse;
import org.jitterbit.connector.docusign.schema.EnvelopeGetRequest;
import org.jitterbit.connector.docusign.schema.EnvelopeGetResponse;
import org.jitterbit.connector.docusign.schema.EnvelopeSendRequest;
import org.jitterbit.connector.docusign.schema.EnvelopeSendResponse;
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
public class EnvelopesOperation implements DSExecution, DocuSignConstants {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(EnvelopesOperation.class);

  @Override
  public void executeGetRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    LOGGER.debug("Executing Get Request For EnvelopesOperation");
    EnvelopesApi envelopeApi = new EnvelopesApi(apiClient);
    ListStatusChangesOptions options = envelopeApi.new ListStatusChangesOptions();
    EnvelopeGetRequest request = JAXBHelper.unmarshall(EnvelopeGetRequest.class,
        context.getRequestPayload().getInputStream());

    EnvelopeGetResponse envelopeGetResponseXSD = new EnvelopeGetResponse();
    List<EnvelopeGetResponse.EnvelopeGetResponseList> envelopesList = new ArrayList<EnvelopeGetResponse.EnvelopeGetResponseList>();
    List<EnvelopeGetResponse.EnvelopeGetResponseList.Envelopes.Envelope> responseList;

    EnvelopeGetRequest.QueryParameters params = request.getQueryParameters();
    EnvelopesInformation envelopeInformation;
    BeanUtils.copyProperties(options, params);
    EnvelopeGetResponse.EnvelopeGetResponseList envelopeTranfResponse = new EnvelopeGetResponse.EnvelopeGetResponseList();

    try {
      envelopeInformation = envelopeApi.listStatusChanges(accountInfo.getAccountId(), options);
      responseList = new ArrayList<EnvelopeGetResponse.EnvelopeGetResponseList.Envelopes.Envelope>();
      envelopeTranfResponse.setNextUri(envelopeInformation.getNextUri());
      envelopeTranfResponse.setEndPosition(envelopeInformation.getEndPosition());
      envelopeTranfResponse.setPreviousUri(envelopeInformation.getPreviousUri());
      envelopeTranfResponse.setResultSetSize(envelopeInformation.getResultSetSize());
      envelopeTranfResponse.setStartPosition(envelopeInformation.getStartPosition());
      envelopeTranfResponse.setTotalSetSize(envelopeInformation.getTotalSetSize());

      EnvelopeGetResponse.EnvelopeGetResponseList.Envelopes envelopes = new EnvelopeGetResponse.EnvelopeGetResponseList.Envelopes();
      for (com.docusign.esign.model.Envelope envs : envelopeInformation.getEnvelopes()) {
        EnvelopeGetResponse.EnvelopeGetResponseList.Envelopes.Envelope response = new EnvelopeGetResponse.EnvelopeGetResponseList.Envelopes.Envelope();
        BeanUtils.copyProperties(response, envs);
        responseList.add(response);
      }
      envelopes.getEnvelope().addAll(responseList);
      envelopeTranfResponse.setEnvelopes(envelopes);
      envelopesList.add(envelopeTranfResponse);
    } catch (ApiException ex) {
      LOGGER.error(ex.getLocalizedMessage(), ex);
      EnvelopeGetResponse.EnvelopeGetResponseList.ErrorDetails errorDetails = new EnvelopeGetResponse.EnvelopeGetResponseList.ErrorDetails();
      errorDetails.setErrorCode(ex.getCode());
      errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
      envelopeTranfResponse.setErrorDetails(errorDetails);
      envelopesList.add(envelopeTranfResponse);
    }
    envelopeGetResponseXSD.setEnvelopeGetResponseList(envelopeTranfResponse);

    JAXBHelper.marshall(EnvelopeGetResponse.class, envelopeGetResponseXSD,
        context.getResponsePayload().getOutputStream());
  }

  @Override
  public void executeSendRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    LOGGER.debug("Executing Send Request For: EnvelopesOperation");
    EnvelopeSendRequest request = JAXBHelper.unmarshall(EnvelopeSendRequest.class,
        context.getRequestPayload().getInputStream());
    EnvelopesApi envelopeApi = new EnvelopesApi(apiClient);
    EnvelopeDefinition envelopeDefinition;
    com.docusign.esign.model.Envelope envelope;
    Document envDocument;
    List<Document> envDocumentList = new ArrayList<Document>();
    SignHere envSignHere;
    List<SignHere> envSignHereList = new ArrayList<SignHere>();
    CarbonCopy envCarbonCopy;
    List<CarbonCopy> envCarbonCopyList = new ArrayList<CarbonCopy>();
    Signer envSigner;
    List<Signer> envSignerList = new ArrayList<Signer>();
    Recipients envRecipients;
    List<EnvelopeSendResponse.EnvelopeSendResponseList> envelopesSendResponseList = new ArrayList<EnvelopeSendResponse.EnvelopeSendResponseList>();
    EnvelopeSendResponse.EnvelopeSendResponseList envelopeSendResponse = new EnvelopeSendResponse.EnvelopeSendResponseList();
    EnvelopeSendResponse envelopeSendResponseXSD = new EnvelopeSendResponse();
    for (EnvelopeSendRequest.ENTITY entity : request.getENTITY()) {
      try {
        // check if request is for update sent envelope
        if (entity.getUpdate() != null && !StringUtils.isEmpty(entity.getUpdate().getEnvelopeId())) {
          envelope = new com.docusign.esign.model.Envelope();
          BeanUtils.copyProperties(envelope, entity.getUpdate());
          EnvelopeUpdateSummary summary = envelopeApi.update(accountInfo.getAccountId(),
              entity.getUpdate().getEnvelopeId(), envelope);
          envelopeSendResponse.setEnvelopeId(summary.getEnvelopeId());
          envelopesSendResponseList.add(envelopeSendResponse);
        } else {
          envelopeDefinition = new EnvelopeDefinition();
          // check if request is for using created template
          if (entity.getSend().getUsePreviousTemplate() != null
              && !StringUtils.isEmpty(entity.getSend().getUsePreviousTemplate().getTemplateId())) {
            envelopeDefinition.setTemplateId(entity.getSend().getUsePreviousTemplate().getTemplateId());
            envelopeDefinition.setStatus(SENT);
          } else {
            envRecipients = new Recipients();
            envelopeDefinition.setStatus(SENT);
            envelopeDefinition.setEmailSubject(entity.getSend().getTemplate().getEmailSubject());
            for (EnvelopeSendRequest.ENTITY.Send.Template.Documents.Document documents : 
              entity.getSend().getTemplate().getDocuments().getDocument()) {
              envDocument = new Document();
              BeanUtils.copyProperties(envDocument, documents);
              envDocumentList.add(envDocument);
            }
            envelopeDefinition.setDocuments(envDocumentList);
            for (EnvelopeSendRequest.ENTITY.Send.Template.Recipients.CarbonCopies.CarbonCopy carbonCopy :
              entity.getSend().getTemplate().getRecipients().getCarbonCopies().getCarbonCopy()) {
              envCarbonCopy = new CarbonCopy();
              BeanUtils.copyProperties(envCarbonCopy, carbonCopy);
              envCarbonCopyList.add(envCarbonCopy);
            }
            if (envCarbonCopyList.size() > 0) {
              envRecipients.setCarbonCopies(envCarbonCopyList);
            }
            for (EnvelopeSendRequest.ENTITY.Send.Template.Recipients.Signers.Signer signer : entity.getSend().getTemplate().getRecipients().getSigners().getSigner()) {
              envSigner = new Signer();
              Tabs tab = new Tabs();
              envSigner.setEmail(signer.getEmail());
              envSigner.setName(signer.getName());
              envSigner.setRecipientId(signer.getRecipientId());
              envSigner.setRoutingOrder(signer.getRoutingOrder());
              for (EnvelopeSendRequest.ENTITY.Send.Template.Recipients.Signers.Signer.Tabs.SignHereTabs.SignHereTab signHereTab : signer.getTabs().getSignHereTabs().getSignHereTab()) {
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
              envelopeDefinition.setRecipients(envRecipients);
            }
          }

          EnvelopeSummary results = envelopeApi.createEnvelope(accountInfo.getAccountId(), envelopeDefinition);
          envelopeSendResponse.setEnvelopeId(results.getEnvelopeId());
          envelopeSendResponse.setStatus(results.getStatus());
          envelopeSendResponse.setStatusDateTime(results.getStatusDateTime());
          envelopeSendResponse.setUri(results.getUri());
          envelopesSendResponseList.add(envelopeSendResponse);
        }
      } catch (ApiException ex) {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        EnvelopeSendResponse.EnvelopeSendResponseList.ErrorDetails errorDetails = new EnvelopeSendResponse.EnvelopeSendResponseList.ErrorDetails();
        errorDetails.setErrorCode(ex.getCode());
        errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
        envelopeSendResponse.setErrorDetails(errorDetails);
        envelopesSendResponseList.add(envelopeSendResponse);
      }
    }
    envelopeSendResponseXSD.getEnvelopeSendResponseList().addAll(envelopesSendResponseList);
    JAXBHelper.marshall(EnvelopeSendResponse.class, envelopeSendResponseXSD,
        context.getResponsePayload().getOutputStream());
  }

  @Override
  public void executeCreateRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo)
      throws Exception {
    LOGGER.debug("Executing Create Request For: EnvelopesOperation");
    EnvelopeCreateRequest request = JAXBHelper.unmarshall(EnvelopeCreateRequest.class,
        context.getRequestPayload().getInputStream());
    EnvelopesApi envelopeApi = new EnvelopesApi(apiClient);
    EnvelopeDefinition envelopeDefinition;
    Document envDocument;
    List<Document> envDocumentList = new ArrayList<Document>();
    SignHere envSignHere;
    List<SignHere> envSignHereList = new ArrayList<SignHere>();
    CarbonCopy envCarbonCopy;
    List<CarbonCopy> envCarbonCopyList = new ArrayList<CarbonCopy>();
    Signer envSigner;
    List<Signer> envSignerList = new ArrayList<Signer>();
    Recipients envRecipients;
    List<EnvelopeCreateResponse.EnvelopeCreateResponseList> envelopesCreateResponseList = new ArrayList<EnvelopeCreateResponse.EnvelopeCreateResponseList>();
    EnvelopeCreateResponse.EnvelopeCreateResponseList envelopeCreateResponse = new EnvelopeCreateResponse.EnvelopeCreateResponseList();
    EnvelopeCreateResponse envelopeCreateResponseXSD = new EnvelopeCreateResponse();
    for (EnvelopeCreateRequest.ENTITY entity : request.getENTITY()) {
      try {

        envelopeDefinition = new EnvelopeDefinition();
        // check if request is for using created template
        if (entity.getUsePreviousTemplate() != null && 
            !StringUtils.isEmpty(entity.getUsePreviousTemplate().getTemplateId())) {
          envelopeDefinition.setTemplateId(entity.getUsePreviousTemplate().getTemplateId());
          envelopeDefinition.setStatus(CREATED);
        } else {
          envRecipients = new Recipients();
          envelopeDefinition.setEmailSubject(entity.getTemplate().getEmailSubject());
          envelopeDefinition.setStatus(CREATED);
          for (EnvelopeCreateRequest.ENTITY.Template.Documents.Document documents : 
            entity.getTemplate().getDocuments().getDocument()) {
            envDocument = new Document();
            BeanUtils.copyProperties(envDocument, documents);
            envDocumentList.add(envDocument);
          }
          envelopeDefinition.setDocuments(envDocumentList);
          for (EnvelopeCreateRequest.ENTITY.Template.Recipients.CarbonCopies.CarbonCopy carbonCopy
              : entity.getTemplate().getRecipients()
              .getCarbonCopies().getCarbonCopy()) {
            envCarbonCopy = new CarbonCopy();
            BeanUtils.copyProperties(envCarbonCopy, carbonCopy);
            envCarbonCopyList.add(envCarbonCopy);
          }
          if (envCarbonCopyList.size() > 0) {
            envRecipients.setCarbonCopies(envCarbonCopyList);
          }
          for (EnvelopeCreateRequest.ENTITY.Template.Recipients.Signers.Signer signer :
            entity.getTemplate().getRecipients().getSigners()
              .getSigner()) {
            envSigner = new Signer();
            Tabs tab = new Tabs();
            envSigner.setEmail(signer.getEmail());
            envSigner.setName(signer.getName());
            envSigner.setRecipientId(signer.getRecipientId());
            envSigner.setRoutingOrder(signer.getRoutingOrder());
            for (EnvelopeCreateRequest.ENTITY.Template.Recipients.Signers.Signer.Tabs.SignHereTabs.SignHereTab signHereTab
                : signer.getTabs().getSignHereTabs().getSignHereTab()) {
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
            envelopeDefinition.setRecipients(envRecipients);
          }
        }

        EnvelopeSummary results = envelopeApi.createEnvelope(accountInfo.getAccountId(), envelopeDefinition);
        envelopeCreateResponse.setEnvelopeId(results.getEnvelopeId());
        envelopeCreateResponse.setStatus(results.getStatus());
        envelopeCreateResponse.setStatusDateTime(results.getStatusDateTime());
        envelopeCreateResponse.setUri(results.getUri());
        envelopesCreateResponseList.add(envelopeCreateResponse);
      } catch (ApiException ex) {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        EnvelopeCreateResponse.EnvelopeCreateResponseList.ErrorDetails errorDetails = new EnvelopeCreateResponse.EnvelopeCreateResponseList.ErrorDetails();
        errorDetails.setErrorCode(ex.getCode());
        errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
        envelopeCreateResponse.setErrorDetails(errorDetails);
        envelopesCreateResponseList.add(envelopeCreateResponse);
      }
    }
    envelopeCreateResponseXSD.getEnvelopeCreateResponseList().addAll(envelopesCreateResponseList);
    JAXBHelper.marshall(EnvelopeCreateResponse.class, envelopeCreateResponseXSD,
        context.getResponsePayload().getOutputStream());
  }

}
