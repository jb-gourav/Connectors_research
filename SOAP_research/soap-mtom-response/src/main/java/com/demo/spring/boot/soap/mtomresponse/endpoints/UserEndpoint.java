package com.demo.spring.boot.soap.mtomresponse.endpoints;

import com.demo.spring.boot.soap.mtomresponse.models.user.GetUserRequest;
import com.demo.spring.boot.soap.mtomresponse.models.user.GetUserResponse;
import com.demo.spring.boot.soap.mtomresponse.models.user.UploadUserRequest;
import com.demo.spring.boot.soap.mtomresponse.models.user.UploadUserResponse;
import com.demo.spring.boot.soap.mtomresponse.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class UserEndpoint {
    private static final String NAMESPACE_URI = "http://demo.com/spring/boot/soap/mtomresponse/models/user";

    private UserRepository userRepository;

    @Autowired
    public UserEndpoint(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        GetUserResponse response = new GetUserResponse();
        response.setUser(userRepository.getUserById(request.getId()));

        return response;
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "uploadUserRequest")
    @ResponsePayload
    public UploadUserResponse uploadUser (@RequestPayload UploadUserRequest user) {
      return this.userRepository.uploadUserRepo(user);
    }
}
