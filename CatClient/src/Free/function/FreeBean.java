package Free.function;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class FreeBean implements Serializable {
	private int type; // 1私聊 0上下线更新 -1下线请求 2请求发送文件 3.确定接收文件4.取消接收文件

	private HashSet<String> clients; // 存放选中的客户

	private HashSet<String> to;					//目前来说不知道 干啥用
	
	public HashMap<String, ClientBean> onlines;		//在线 用户 名 ，在线用户clientbean

	private String info;		//信息，如用户之间发送的消息

	private String timer;		//时间戳

	private String name;		//bean 的来源

	private String fileName;	//文件名

	private double size;			//文件大小

	private String ip;			//接受方IP

	private int port;			//接收方port

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public HashSet<String> getTo() {
		return to;
	}

	public void setTo(HashSet<String> to) {
		this.to = to;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public HashSet<String> getClients() {
		return clients;
	}

	public void setClients(HashSet<String> clients) {
		this.clients = clients;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTimer() {
		return timer;
	}

	public void setTimer(String timer) {
		this.timer = timer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public HashMap<String, ClientBean> getOnlines() {
		return onlines;
	}

	public void setOnlines(HashMap<String, ClientBean> onlines) {
		this.onlines = onlines;
	}

}
