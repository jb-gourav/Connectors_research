package org.jitterbit.connector.docusign.execution;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.auth.OAuth;

import org.jitterbit.connector.sdk.JitterbitActivity.ExecutionContext;

/**
 * 
 * @author Saurabh
 *
 */
public interface DSExecution {

  void executeGetRequest(ApiClient apiClient, ExecutionContext context, OAuth.Account accountInfo) throws Exception;

  void executeSendRequest(ApiClient apiClient, ExecutionContext context, OAuth.Account accountInfo) throws Exception;

  void executeCreateRequest(ApiClient apiClient, ExecutionContext context, OAuth.Account accountInfo) throws Exception;

}
