package com.soap.ws.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WsdlParser {
	
	private Definition getDefinition() {
		Definition def = null;
		try {
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();

			//String wsdlurl = "C:\\Users\\user\\Downloads\\EWWSv2Service_gsmatest-agiloft-com_GSMA_20200907(2).wsdl";
			//String wsdlurl = "https://webservices.netsuite.com/wsdl/v2016_2_0/netsuite.wsdl";
			String wsdlurl = "http://localhost:8080/ws/users.wsdl";
			// reading the WSDL Document
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);
			def = reader.readWSDL(null, wsdlurl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public String getServiceName(Definition def) {
		String localPart = null;
		Map service1 = def.getServices();
		Iterator itr = service1.values().iterator();
		while (itr.hasNext()) {
			Service type = (Service) itr.next();
			if (type != null) {
				QName qName = type.getQName();
				System.out.println("Serviee Qname " + qName);
				localPart = qName.getLocalPart();
			}
		}
		return localPart;
	}
	
	public String getPortName(Definition def) {
		String portName = null;
		Map service1 = def.getServices();
		Iterator itr = service1.values().iterator();
		while (itr.hasNext()) {
			Service type = (Service) itr.next();
			Map port = type.getPorts();
			Iterator portItr = port.values().iterator();
			while (portItr.hasNext()) {
				Port p = (Port) portItr.next();
				if (p != null) {
					portName = p.getName();
				}
			}
		}
		return portName;
	}
	
	
	public List<String> getOperationList(Definition def) {
		List<String> operationList = new ArrayList<String>();
		Iterator opIterator = getOperators (def);
		while(opIterator.hasNext()) {
			Operation operation = (Operation) opIterator.next();
			operationList.add(operation.getName());
		}
		return operationList;
	}
	
	public String getTargetNameSpace(Definition def) {
		return def.getTargetNamespace();
	}
	
	public Iterator getOperators (Definition def) {
		Service service = def.getService(new QName(def.getTargetNamespace(), getServiceName(def)));
		Port port = service.getPort(getPortName(def));
		Binding binding = port.getBinding();
		PortType portType = binding.getPortType();
		List operations = portType.getOperations();
		return operations.iterator();
	}
	
	public Input getInput(Definition def, String operationName) {
		System.out.println("----" + operationName + "-----");
		Input input = null;
		Iterator opIterator = getOperators(def);
		while (opIterator.hasNext()) {
			Operation operation = (Operation) opIterator.next();
			if (!operation.isUndefined()) {
				if(operation.getName().equalsIgnoreCase(operationName)) {
					input = operation.getInput();
					Iterator ite = input.getMessage().getParts().values().iterator();
					while (ite.hasNext()) {
						Part part = (Part) ite.next();
						//case part can be null means it is element node
						if (part.getTypeName() != null && part.getTypeName().getLocalPart() != null) {
							System.out.println(part.getName() + " -/ typeName- " + part.getTypeName().getLocalPart());
							getTypes(def, part.getTypeName().getLocalPart());
						} else {
							System.out.println(part.getName());
						}
						
						
					}

					System.out.println("----------output-------------------");
					Output output = operation.getOutput();
					
					Iterator out = output.getMessage().getParts().values().iterator();
					while (out.hasNext()) {
						Part part1 = (Part) out.next();
						System.out.println(part1.getName() + " -/ typeName- " + part1.getTypeName().getLocalPart());
						getTypes(def, part1.getTypeName().getLocalPart());
					}
					
				}
				
			}
			
		}
		return input;
	}
	
	public void getTypes(Definition def, String wsdlType) {
		for( Object o : def.getTypes().getExtensibilityElements()) {
		    if( o instanceof javax.wsdl.extensions.schema.Schema ) {
		        Element ele = ((javax.wsdl.extensions.schema.Schema) o).getElement();
		        // Navigate in the DOM model of the schema
		        // You can use Schema#getImport() to work with imports
		        NodeList rootNode = ele.getElementsByTagName("xs:complexType");
		        int rootNodeLength = rootNode.getLength();
		        for (int i = 0; i< rootNodeLength; i++) {
		        	Node node = rootNode.item(i);
		        	if (node.getNodeType() == Element.ELEMENT_NODE) {
			        	Element el = (Element) node;
			        	String elem = el.getAttribute("name");
			        	if (wsdlType.equalsIgnoreCase(elem)) {
			        		NodeList seqList = el.getElementsByTagName("xs:sequence");
			        		int length = seqList.getLength();
			        		for (int j =0; j < length; j++) {
			        			Node seqNode = seqList.item(j);
			        			if (seqNode.getNodeType() == Element.ELEMENT_NODE) {
						        	Element seqEl = (Element) node;
						        	NodeList elementNodeList = seqEl.getElementsByTagName("xs:element");
						        	int elementNodeLength = elementNodeList.getLength();
						        	for (int k = 0; k < elementNodeLength; k++) {
						        		Node elementNode = elementNodeList.item(k);	
						        		if (elementNode.getNodeType() == Element.ELEMENT_NODE) {
						        			Element element = (Element) elementNode;
						        			String name = element.getAttribute("name");
						        			String type = element.getAttribute("type");
						        			String minOccurs = element.getAttribute("minOccurs");
						        			String mimeType = element.getAttribute("xmime:expectedContentTypes");
						        			System.out.println("<xs:element name=" + name +
						        					" type=" + type + " minOccurs=" + minOccurs + 
						        					" xmime:expectedContentTypes:" + mimeType + "/>");
						        		}
						        	}
			        			}
			        		}
			        		
			        	}
		        	}
		        }
		        
		    }
		}
	}
	
	public Output getOutput (Definition def) {
		Output output = null;
		Iterator opIterator = getOperators(def);
		while (opIterator.hasNext()) {
			Operation operation = (Operation) opIterator.next();
			if (!operation.isUndefined()) {
				output = operation.getOutput();
			}
		}
		return output;
	}

	public static void main(String[] args) {
		WsdlParser parser = new WsdlParser();
		//parser.readWsdl();
		Definition def = parser.getDefinition();
		System.out.println("Service Name: " + parser.getServiceName(def));
		System.out.println("Port Name: " + parser.getPortName(def));
		System.out.println("Target Name Space: " + parser.getTargetNameSpace(def));
		System.out.println("Operation: " + parser.getOperationList(def));
		parser.getInput(def, "getUser");
		
	}
}
