package net.codejava;

public class SendingSomeThing {

	public static void main(String[] args) {
		HelloService service = new HelloService();
		Hello hello = service.getHelloPort();
		
		String response = hello.sendMeSomething("gourav");
		System.out.println(response);

	}

}
