package com.soap.client;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

public class CallClient {

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://localhost:8080/ws/image?wsdl");
		QName qName = new QName("http://ws.poc.soap.org/", "ImageServerImplService");
		Service service = Service.create(url, qName);
		ImageServer imageServer = service.getPort(ImageServer.class);
		
		/***** test download *******************/
		byte[] image = imageServer.downloadImage("C:\\Users\\user\\Pictures\\15.png");
		BindingProvider bp = (BindingProvider) imageServer;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);
		//display
		JFrame frame = new JFrame();
		frame.setSize(300, 300);
		JLabel label = new JLabel(new ImageIcon(image));
		frame.add(label);
		frame.setVisible(true);
		
		System.out.println("imageServer.downloadImage() :  Download SuccessFul!");
		
	}

}
