package com.soap.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
 
public class TestClient {
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        TestClient testHelloAge = new TestClient();
        testHelloAge.geHelloAge();
    }
 
     
    public void geHelloAge() {
        String wsURL = "http://localhost:8080/ws/users";
       // String wsURL = "http://localhost:8080/ws/image/downloadImage";
        URL url = null;
        URLConnection connection = null;
        HttpURLConnection httpConn = null;
        String responseString = null;
        String outputString="";
        OutputStream out = null;
        InputStreamReader isr = null;
        BufferedReader in = null;
         
		
		  String xmlInput =
		  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://innova.com/spring/boot/soap/mtomresponse/models/user\">\r\n"
		  + "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n" +
		  "      <user:getUserRequest>\r\n" + "         <user:id>1</user:id>\r\n" +
		  "      </user:getUserRequest>\r\n" + "   </soapenv:Body>\r\n" +
		  "</soapenv:Envelope>";
		 
        
		/*
		 * String xmlInput =
		 * "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:imag=\"http://image.poc.soap.org/\">\r\n"
		 * + "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n" +
		 * "      <imag:downloadImage>\r\n" +
		 * "         <name>E:\\jitterbit\\mtom\\1.jpg</name>\r\n" +
		 * "      </imag:downloadImage>\r\n" + "   </soapenv:Body>\r\n" +
		 * "</soapenv:Envelope>";
		 */
        
        
         
        try
        {
            url = new URL(wsURL);
            connection = url.openConnection();
            httpConn = (HttpURLConnection) connection;
 
            byte[] buffer = new byte[xmlInput.length()];
            buffer = xmlInput.getBytes();
 
            String SOAPAction = "";
            // Set the appropriate HTTP parameters.
             httpConn.setRequestProperty("Content-Length", String
                     .valueOf(buffer.length));
            httpConn.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");
             
             
            httpConn.setRequestProperty("SOAPAction", SOAPAction);
            httpConn.setRequestProperty("Enable MTOM","true");
            httpConn.setRequestProperty("Force MTOM","true");
            httpConn.setRequestProperty("Expand MTOM Attachment","false");
            httpConn.setRequestProperty("WSS-PasswordType","PasswordDigest");
            httpConn.setRequestProperty("WSS TimeToLive","50");
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            out = httpConn.getOutputStream();
            out.write(buffer);
            out.close();
             
            // Read the response and write it to standard out.
            isr = new InputStreamReader(httpConn.getInputStream());
            in = new BufferedReader(isr);
             
            while ((responseString = in.readLine()) != null) 
            {
                outputString = outputString + responseString; 
            }
            System.out.println(outputString);
            System.out.println("");
             
            // Get the response from the web service call
			
			  //Document document = parseXmlFile(outputString);
			  
			  //NodeList nodeLst = document.getElementsByTagName("ns2:downloadImageResponse");
			  //String webServiceResponse1 = nodeLst.item(0).getTextContent();
			  //System.out.println("The response from the web service call is : " +
			  //webServiceResponse1); 
			  //for (int i =0; i < nodeLst.getLength(); i++) {
				//  Node node = nodeLst.item(i);
				  //if (node.getNodeType() == Node.ELEMENT_NODE) { 
					//  NodeList l = node.getChildNodes();
					  //String webServiceResponse = l.item(0).getTextContent();
			  //System.out.println("The response from the web service call is : " +
			  //webServiceResponse); 
			  //} 
		//}
			 
              
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
     
    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
             InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 
}