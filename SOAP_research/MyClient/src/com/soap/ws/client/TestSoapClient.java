package com.soap.ws.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestSoapClient {
	
	public static void main(String[] args) {
		String soapEndpointUrl = "http://localhost:8080/ws/users";
        String soapAction = "";

        callSoapWebService(soapEndpointUrl, soapAction);
	}
	
	 private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
	        SOAPPart soapPart = soapMessage.getSOAPPart();

	        String myNamespace = "user";
	        String myNamespaceURI = "http://innova.com/spring/boot/soap/mtomresponse/models/user";

	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

	            /*
	            Constructed SOAP Request Message:
	            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:myNamespace="http://www.webserviceX.NET">
	                <SOAP-ENV:Header/>
	                <SOAP-ENV:Body>
	                    <myNamespace:GetInfoByCity>
	                        <myNamespace:USCity>New York</myNamespace:USCity>
	                    </myNamespace:GetInfoByCity>
	                </SOAP-ENV:Body>
	            </SOAP-ENV:Envelope>
	            */
  
	        // SOAP Body
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement soapBodyElem = soapBody.addChildElement("getUserRequest", myNamespace);
	        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("id", myNamespace);
	        soapBodyElem1.addTextNode("1");
	    }
	
	public static void callSoapWebService(String soapEndpointUrl, String soapAction) {
		try {
			//QName serviceName = new QName("UserPortService");

		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);
		// Print the SOAP Response
        System.out.println("Response SOAP Message:");
        //soapResponse.writeTo(System.out);
        
        SOAPBody body = soapResponse.getSOAPBody();
        NodeList list = body.getElementsByTagName("ns2:profilePicture1");
        for (int i=0; i<list.getLength(); i++) {
        	Node node = list.item(i);
        	if (node.getNodeType() == Node.ELEMENT_NODE) {
        		Element eElement = (Element) node;
        		 System.out.println("imageName : " 
                         + eElement
                         .getElementsByTagName("ns2:name")
                         .item(0)
                         .getTextContent());
        		 System.out.println("content : " 
                         + eElement
                         .getElementsByTagName("xop:Include")
                         .item(0)
                         .getTextContent());
        	}
        }
        Iterator itr = soapResponse.getAttachments();
        while(itr.hasNext()) {
        	Object ob = itr.next();
        	AttachmentPart message = ((AttachmentPart)ob);
				/*
				 * MimeHeaders header = message.getContentId(); String[] str =
				 * header.getHeader("Content-ID");
				 */
        	String arr = "Content-Transfer-Encoding";
        	System.out.println(message.getContentId());
        	System.out.println(message.getContentType());
        	System.out.println(message.getContentLocation());
        	Iterator itr2 = message.getAllMimeHeaders();
        	while(itr2.hasNext()) {
        		MimeHeader header = (MimeHeader)itr2.next();
        		String name = header.getName();
        		String value = header.getValue();
        		System.out.println(name + ": " + value);
        		
        	}
        }
        java.util.Iterator iterator = soapResponse.getAttachments();
        while (iterator.hasNext()) {
            AttachmentPart attachment = (AttachmentPart)iterator.next();
            String id = attachment.getContentId();
            String type = attachment.getContentType();
            System.out.print("Attachment " + id + " has content type " + type);
            if (type.equals("text/plain")) {
                Object content = attachment.getContent();
                System.out.println("Attachment contains:\n" + content);
                
            }
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
	        headers.addHeader("Enable MTOM","false");
	        headers.addHeader("Force MTOM","false");
	        headers.addHeader("Expand MTOM Attachment","false");

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
