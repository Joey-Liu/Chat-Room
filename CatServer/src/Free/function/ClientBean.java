package Free.function;

import java.net.Socket;

//存储有关 客户 昵称 ，套接字信息
public class ClientBean {
	private String name;
	private Socket socket;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
