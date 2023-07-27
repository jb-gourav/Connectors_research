package org.jitterbit.connector.docusign.pojo;

/**
 * Class to represent a DocuSign object for Jitterbit.
 */
public class DocuSignObject {

  private String name;
  private String displayName;
  private String description;

  public DocuSignObject(String name,
                       String displayName,
                       String description
                   ) {
    this.name = name;
    this.displayName = displayName;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }


  @Override
  public String toString() {
    return "DocuSignObject{" +
        "name='" + name + '\'' +
        ", displayName='" + displayName + '\'' +
        ", description='" + description + '\'' +
        '}';
  }
}
