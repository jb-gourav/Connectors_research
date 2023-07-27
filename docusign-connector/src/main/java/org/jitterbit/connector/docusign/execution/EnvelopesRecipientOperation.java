package org.jitterbit.connector.docusign.execution;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth.Account;
import com.docusign.esign.model.Recipients;

import org.apache.commons.beanutils.BeanUtils;
import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.schema.EnveloperecipientGetRequest;
import org.jitterbit.connector.docusign.schema.EnveloperecipientGetResponse;
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
public class EnvelopesRecipientOperation implements DSExecution, DocuSignConstants {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(EnvelopesRecipientOperation.class);

  @Override
  public void executeGetRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    LOGGER.debug("Executing Get Request For EnvelopesRecipientOperation");
    EnvelopesApi envelopeApi = new EnvelopesApi(apiClient);
    EnveloperecipientGetRequest request = JAXBHelper.unmarshall(EnveloperecipientGetRequest.class,
        context.getRequestPayload().getInputStream());

    EnveloperecipientGetResponse envelopeRecipientGetResponseXSD = new EnveloperecipientGetResponse();
    List<EnveloperecipientGetResponse.EnvelopeRecipientResponseList> envelopesRecipientList = new ArrayList<EnveloperecipientGetResponse.EnvelopeRecipientResponseList>();
    EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients tranRecipients;
    List<EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.Signers.Signer> signerList;
    List<EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.CarbonCopies.CarbonCopy> carbonCopyList;

    for (EnveloperecipientGetRequest.ENTITY entity : request.getENTITY()) {
      Recipients recipientInformation;
      EnveloperecipientGetResponse.EnvelopeRecipientResponseList recipientTranfResponse = new EnveloperecipientGetResponse.EnvelopeRecipientResponseList();
      recipientTranfResponse.setEnvelopeId(entity.getEnvelopeId());
      try {
        recipientInformation = envelopeApi.listRecipients(accountInfo.getAccountId(), entity.getEnvelopeId());
        signerList = new ArrayList<EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.Signers.Signer>();
        carbonCopyList = new ArrayList<EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.CarbonCopies.CarbonCopy>();
        EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.CarbonCopies carbonCopies = new EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.CarbonCopies();
        EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.Signers signers = new EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.Signers();
        tranRecipients = new EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients();
        recipientInformation.setRecipientCount(recipientInformation.getRecipientCount());
        recipientInformation.setCurrentRoutingOrder(recipientInformation.getCurrentRoutingOrder());
        for (com.docusign.esign.model.CarbonCopy carbonCopy : recipientInformation.getCarbonCopies()) {
          EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.CarbonCopies.CarbonCopy ccResponse = new EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.CarbonCopies.CarbonCopy();
          BeanUtils.copyProperties(ccResponse, carbonCopy);
          carbonCopyList.add(ccResponse);
        }
        carbonCopies.getCarbonCopy().addAll(carbonCopyList);
        for (com.docusign.esign.model.Signer signer : recipientInformation.getSigners()) {
          EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.Signers.Signer signerResponse = new EnveloperecipientGetResponse.EnvelopeRecipientResponseList.Recipients.Signers.Signer();
          BeanUtils.copyProperties(signerResponse, signer);
          signerList.add(signerResponse);
        }
        signers.getSigner().addAll(signerList);

        tranRecipients.setCarbonCopies(carbonCopies);
        tranRecipients.setSigners(signers);

        recipientTranfResponse.setRecipients(tranRecipients);
        envelopesRecipientList.add(recipientTranfResponse);

      } catch (ApiException ex) {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        EnveloperecipientGetResponse.EnvelopeRecipientResponseList.ErrorDetails errorDetails = new EnveloperecipientGetResponse.EnvelopeRecipientResponseList.ErrorDetails();
        errorDetails.setErrorCode(ex.getCode());
        errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
        recipientTranfResponse.setErrorDetails(errorDetails);
        envelopesRecipientList.add(recipientTranfResponse);
      }
    }
    envelopeRecipientGetResponseXSD.getEnvelopeRecipientResponseList().addAll(envelopesRecipientList);

    JAXBHelper.marshall(EnveloperecipientGetResponse.class, envelopeRecipientGetResponseXSD,
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
