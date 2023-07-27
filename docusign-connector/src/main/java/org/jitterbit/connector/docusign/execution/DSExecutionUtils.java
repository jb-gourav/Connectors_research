package org.jitterbit.connector.docusign.execution;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiException;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 
 * @author Saurabh
 *
 */

public class DSExecutionUtils {

  private final String docuSignConfigPath = "/docuSignObjects.json";

  public DocuSignConfig getConfig() throws Exception {
    InputStream is = this.getClass().getResourceAsStream(docuSignConfigPath);
    return new Gson().fromJson(IOUtils.toString(is, StandardCharsets.UTF_8.name()), DocuSignConfig.class);
  }

  public byte[] getDocumentContent(Object apiClient, String accountId, String objectId, String documentId)
      throws ApiException {
    if (apiClient instanceof EnvelopesApi) {
      return ((EnvelopesApi) apiClient).getDocument(accountId, objectId, documentId);
    } else {
      return ((TemplatesApi) apiClient).getDocument(accountId, objectId, documentId);
    }

  }
}
