package org.jitterbit.connector.docusign.execution;

import org.jitterbit.connector.docusign.DocuSignConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Saurabh
 *
 */
public class DSGetExecutionFactory implements DocuSignConstants {

  static Map<String, DSExecution> objectNameMap = new HashMap<>();

  static {
    objectNameMap.put(ENVELOPE, new EnvelopesOperation());
    objectNameMap.put(ENVELOPE_DOCUMENT, new EnvelopesDocumentsOperation());
    objectNameMap.put(ENVELOPE_RECIPIENT, new EnvelopesRecipientOperation());
    objectNameMap.put(TEMPLATE, new TemplatesOperation());
    objectNameMap.put(TEMPLATE_DOCUMENT, new TemplatesDocumentsOperation());
    objectNameMap.put(TEMPLATE_RECIPIENT, new TemplatesRecipientOperation());
  }

  public static DSExecution getOperation(String objectName) {
    return objectNameMap.get(objectName);
  }

}
