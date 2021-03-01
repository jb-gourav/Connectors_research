package com.soap.client;

import java.awt.Image;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

public class CallClientByEnablingMTOM {
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://localhost:8080/ws/image?wsdl");
		QName qName = new QName("http://ws.poc.soap.org/", "ImageServerImplService");
		Service service = Service.create(url, qName);
		ImageServer imageServer = service.getPort(ImageServer.class);
		
		
		/***** test download *******************/
		Image imgUpload = ImageIO.read(new File("C:\\Users\\user\\Pictures\\image.png"));
		
		//display
		 BindingProvider bp = (BindingProvider) imageServer;
	        SOAPBinding binding = (SOAPBinding) bp.getBinding();
	        binding.setMTOMEnabled(true);
	        String status = imageServer.uploadImage(imgUpload, "123", "test", "C:\\Users\\user\\Pictures\\upload2.txt");
	        System.out.println("imageServer.uploadImage() : " + status);
		
	}
}
