package com.soap.ws.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class TestSoapClient {
	private static final String ENDPOINT_URL = "http://localhost:8080/ws/users";
	private static final String SOAP_ACTION = "";
	private static final String NAMESPACE = "user";
	private static final String NAMESPACE_URI = "http://demo.com/spring/boot/soap/mtomresponse/models/user";
	public static void main(String[] args) {
        callSoapWebService(ENDPOINT_URL, SOAP_ACTION);
	}
	
	/**
	 * Creating soap request
	 * 
	 *   
   *         Constructed SOAP Request Message:
   *           <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
   *            xmlns:myNamespace="http://www.webserviceX.NET">
   *              <SOAP-ENV:Header/>
   *               <SOAP-ENV:Body>
   *                  <myNamespace:GetInfoByCity>
   *                      <myNamespace:USCity>New York</myNamespace:USCity>
   *                  </myNamespace:GetInfoByCity>
   *            </SOAP-ENV:Body>
   *          </SOAP-ENV:Envelope>
   *           
	 * 
	 * @param soapMessage
	 * @throws SOAPException
	 */
	 private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration(NAMESPACE, NAMESPACE_URI);
  
	        // SOAP Body
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement soapBodyElem = soapBody.addChildElement("getUserRequest", NAMESPACE);
	        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("id", NAMESPACE);
	        soapBodyElem1.addTextNode("1");
	    }
	
	 /**
	  * calling service
	  * @param soapEndpointUrl
	  * @param soapAction
	  */
	public static void callSoapWebService(String soapEndpointUrl, String soapAction) {
		try {
			//QName serviceName = new QName("UserPortService");
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);
		// Print the SOAP Response
        System.out.println("Response SOAP Message:");
        //soapResponse.writeTo(System.out);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
        
        Iterator itr = soapResponse.getAttachments();
        while(itr.hasNext()) {
        	Object ob = itr.next();
        	AttachmentPart message = ((AttachmentPart)ob);
				/*
				 * MimeHeaders header = message.getContentId(); String[] str =
				 * header.getHeader("Content-ID");
				 */
        	Iterator itr2 = message.getAllMimeHeaders();
        	while(itr2.hasNext()) {
        		MimeHeader header = (MimeHeader)itr2.next();
        		String name = header.getName();
        		String value = header.getValue();
        		System.out.println("\nKey " + name + ": value: " + value);
        		
        	}
        }
        java.util.Iterator iterator = soapResponse.getAttachments();
        while (iterator.hasNext()) {
            AttachmentPart attachment = (AttachmentPart)iterator.next();
            String id = attachment.getContentId();
            String type = attachment.getContentType();
            System.out.print("Attachment " + id + " has content type " + type);
            byte[] handler = attachment.getRawContentBytes();
            String encodedStr = Base64.getMimeEncoder().encodeToString(handler);
            System.out.println("encode: " + encodedStr);
           
        }
        soapConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 private static SOAPMessage createSOAPRequest(String soapAction) throws Exception {
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        createSoapEnvelope(soapMessage);

	        MimeHeaders headers = soapMessage.getMimeHeaders();
	        headers.addHeader("SOAPAction", soapAction);

	        soapMessage.saveChanges();

	        /* Print the request message, just for debugging purposes */
	        System.out.println("Request SOAP Message:");
	        soapMessage.writeTo(System.out);
	        
	        System.out.println("\n");

	        return soapMessage;
	    }
	 
	 private static void writeFile(String respString) throws IOException {
		 File file = new File("soapResponse.txt");
		 FileWriter writer = new FileWriter(file);
		 writer.write(respString);
		 writer.close();
		 
	 }

	
	
}
