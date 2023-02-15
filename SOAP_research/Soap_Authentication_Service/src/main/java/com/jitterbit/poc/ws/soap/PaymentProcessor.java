package com.jitterbit.poc.ws.soap;

import com.jitterbit.poc.ws.soap.dto.PaymentProcessorRequest;
import com.jitterbit.poc.ws.soap.dto.PaymentProcessorResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

// Key annotation is @WebService to explose this class as web-service.
@WebService(name = "PaymentProcessor")
public interface PaymentProcessor {

    // @WebMethod is an optional annotation.
    @WebMethod
    public @WebResult(name = "response") PaymentProcessorResponse processPayment(
            @WebParam(name = "paymentProcessorRequest") PaymentProcessorRequest paymentProcessorRequest);
}
