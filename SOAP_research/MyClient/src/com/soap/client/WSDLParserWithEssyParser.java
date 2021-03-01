package com.soap.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Input;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.Types;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.easywsdl.wsdl.api.WSDLReader.FeatureConstants;
import org.xml.sax.InputSource;

public class WSDLParserWithEssyParser {
	final static String wsdlurl = "C:\\Users\\user\\Downloads\\EWWSv2Service_gsmatest-agiloft-com_GSMA_20200907(2).wsdl";
	// final static String wsdlurl =
	// "https://webservices.netsuite.com/wsdl/v2016_2_0/netsuite.wsdl";
	private Description desc = null;

	public WSDLParserWithEssyParser(Description desc) {
		this.desc = desc;
	}

	private Service getService() {
		return desc.getServices().get(0);
	}

	private Endpoint getEndpoint() {
		return getService().getEndpoints().get(0);
	}

	public String getServiceName() {
		return getService().getQName().getLocalPart();
	}

	public String getTargetNameSpace() {
		return desc.getTargetNamespace();
	}

	// Returning port name.
	public String getPortName() {
		return getEndpoint().getName();
	}

	// Returning the web service url.
	public String weServiceUrl() {
		return getEndpoint().getAddress();
	}

	// Returning binding
	public String getBinding() {
		return getEndpoint().getBinding().getQName().getLocalPart();
	}
	
	public List<String> getOperationList() {
		List<String> operationList = new LinkedList<String>();
		List<BindingOperation> op = getEndpoint().getBinding().getBindingOperations();
		op.forEach(item -> {
			operationList.add(item.getOperation().getQName().getLocalPart());
		});
		return operationList;
	}
	
	public void getReqestSchema() {
		BindingOperation operation = getEndpoint().getBinding().getBindingOperation("EWDelete");
		try {
			Input input = operation.getOperation().getInput();
			//System.out.println(operation.getOperation().getInput());
			input.getParts().forEach(item -> {
				System.out.println(item.getPartQName().getLocalPart() + " / " + item.getType().getQName());
				desc.getTypes().getSchemas().forEach(type -> {
					Element ele = type.getElement(item.getType().getQName());
					if (ele != null) {
						System.out.println("name: " + ele.getQName().getLocalPart() +" maxOccurs: " + ele.getMaxOccurs() + " minOccurs: " + ele.getMinOccurs());
						try {
							System.out.println();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				//.out.println(desc.getTypes().getSchemas().get(0));
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void filterTypes () {
		desc.getTypes().getSchemas().forEach(item -> {
			System.out.println(item.getType(new QName("http://GSMA_Test.api.ws.enterprisewizard.com", "deleteRule")));
		});
	}

	public static void main(String[] args) throws Exception {
		File file = new File(wsdlurl);
		InputStream targetStream = new FileInputStream(file);
		InputSource source = new InputSource(targetStream);
		WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		reader.setFeature(FeatureConstants.VERBOSE, true);
		Description desc = reader.read(source);

		WSDLParserWithEssyParser parse = new WSDLParserWithEssyParser(desc);
		System.out.println("port:- { " + parse.getPortName() + " }");
		System.out.println("web service url:- { " + parse.getServiceName() + " }");
		System.out.println("Binding:- { " + parse.getBinding() + " }");
		System.out.println("Service:- { " + parse.getServiceName() + " }");
		System.out.println("target name space:- { " + parse.getTargetNameSpace() + " }");
		System.out.println("Operation List:- " + parse.getOperationList().toString());
		parse.getReqestSchema();
	}

}
