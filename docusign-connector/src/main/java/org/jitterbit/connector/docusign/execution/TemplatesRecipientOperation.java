package org.jitterbit.connector.docusign.execution;

import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth.Account;
import com.docusign.esign.model.Recipients;

import org.apache.commons.beanutils.BeanUtils;
import org.jitterbit.connector.docusign.DocuSignConstants;
import org.jitterbit.connector.docusign.schema.TemplaterecipientGetRequest;
import org.jitterbit.connector.docusign.schema.TemplaterecipientGetResponse;
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
public class TemplatesRecipientOperation implements DSExecution, DocuSignConstants {

  private static final VerboseLogger LOGGER = VerboseLoggerManager.getVerboseLogger(TemplatesRecipientOperation.class);

  @Override
  public void executeGetRequest(ApiClient apiClient, ExecutionContext context, Account accountInfo) throws Exception {
    LOGGER.debug("Executing Get Request For: TemplatesRecipientOperation");
    TemplatesApi templateApi = new TemplatesApi(apiClient);
    TemplaterecipientGetRequest request = JAXBHelper.unmarshall(TemplaterecipientGetRequest.class,
        context.getRequestPayload().getInputStream());

    TemplaterecipientGetResponse templateRecipientGetResponseXSD = new TemplaterecipientGetResponse();
    List<TemplaterecipientGetResponse.TemplateRecipientResponseList> templatesRecipientList = new ArrayList<TemplaterecipientGetResponse.TemplateRecipientResponseList>();
    TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients tranRecipients;
    List<TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.Signers.Signer> signerList;
    List<TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.CarbonCopies.CarbonCopy> carbonCopyList;

    for (TemplaterecipientGetRequest.ENTITY entity : request.getENTITY()) {
      Recipients recipientInformation;
      TemplaterecipientGetResponse.TemplateRecipientResponseList recipientTranfResponse = new TemplaterecipientGetResponse.TemplateRecipientResponseList();
      recipientTranfResponse.setTemplateId(entity.getTemplateId());
      try {
        recipientInformation = templateApi.listRecipients(accountInfo.getAccountId(),
            entity.getTemplateId());
        signerList = new ArrayList<TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.Signers.Signer>();
        carbonCopyList = new ArrayList<TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.CarbonCopies.CarbonCopy>();
        TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.CarbonCopies carbonCopies = new TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.CarbonCopies();
        TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.Signers signers = new TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.Signers();
        tranRecipients = new TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients();
        recipientInformation.setRecipientCount(recipientInformation.getRecipientCount());
        recipientInformation.setCurrentRoutingOrder(recipientInformation.getCurrentRoutingOrder());
        for (com.docusign.esign.model.CarbonCopy carbonCopy : recipientInformation.getCarbonCopies()) {
          TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.CarbonCopies.CarbonCopy ccResponse = new TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.CarbonCopies.CarbonCopy();
          BeanUtils.copyProperties(ccResponse, carbonCopy);
          carbonCopyList.add(ccResponse);
        }
        carbonCopies.getCarbonCopy().addAll(carbonCopyList);
        for (com.docusign.esign.model.Signer signer : recipientInformation.getSigners()) {
          TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.Signers.Signer signerResponse = new TemplaterecipientGetResponse.TemplateRecipientResponseList.Recipients.Signers.Signer();
          BeanUtils.copyProperties(signerResponse, signer);
          signerList.add(signerResponse);
        }
        signers.getSigner().addAll(signerList);

        tranRecipients.setCarbonCopies(carbonCopies);
        tranRecipients.setSigners(signers);

        recipientTranfResponse.setRecipients(tranRecipients);
        templatesRecipientList.add(recipientTranfResponse);

      } catch (ApiException ex) {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        TemplaterecipientGetResponse.TemplateRecipientResponseList.ErrorDetails errorDetails = new TemplaterecipientGetResponse.TemplateRecipientResponseList.ErrorDetails();
        errorDetails.setErrorCode(ex.getCode());
        errorDetails.setErrorMessage(new JSONObject(ex.getResponseBody()).getString(MESSAGE));
        recipientTranfResponse.setErrorDetails(errorDetails);
        templatesRecipientList.add(recipientTranfResponse);
      }
    }
    templateRecipientGetResponseXSD.getTemplateRecipientResponseList().addAll(templatesRecipientList);

    JAXBHelper.marshall(TemplaterecipientGetResponse.class, templateRecipientGetResponseXSD,
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
