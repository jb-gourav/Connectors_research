package org.jitterbit.connector.docusign.execution;

import org.jitterbit.connector.docusign.pojo.DocuSignObject;

import java.util.List;

/**
 * Class to hold a list of DocuSign Objects.
 */
public class DocuSignConfig {

  private List<DocuSignObject> getObjects;
  private List<DocuSignObject> sendObjects;
  private List<DocuSignObject> createObjects;

  public DocuSignConfig(List<DocuSignObject> getObjects, List<DocuSignObject> sendObjects,
      List<DocuSignObject> createObjects) {
    this.getObjects = getObjects;
    this.sendObjects = sendObjects;
    this.createObjects = createObjects;
  }

  public List<DocuSignObject> getGetObjects() {
    return getObjects;
  }


  public List<DocuSignObject> getSendObjects() {
    return sendObjects;
  }
  
 public List<DocuSignObject> getCreateObjects() {
    return createObjects;
  }

  @Override
  public String toString() {
    return "DocuSignConfig{" + "sendObjects=" + sendObjects +
         ", getObjects=" + getObjects + 
         ", createObjects=" + createObjects + '}';
  }
}
