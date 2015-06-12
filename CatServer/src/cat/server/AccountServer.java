package cat.server;

//账号服务期 工作模式：
//mode = 1 表示 用户使用账号密码登录 
//mode = 2 表示 用户注册该账号


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import cat.util.*;

public class AccountServer {
	private final int port = 8000;		//端口号
	private final String deny_Nick = "NoNickName";
	private final String deny_Pwd = "WrongPwd";
	private final String admit = "Yes";
	private final String UnRead = "未读消息";
	private final String OffLine = "已下线";
	private final String illegal = "命名不合法";
	private final String resetSuccess = "修改密码成功！";
	private final String wa = "The answer is wrong!";
	private ServerSocket ss;
	
	
	public AccountServer() throws IOException
	{
		ss = new ServerSocket(port);
		System.out.println("账号服务器启动");
	}
	
	public void service()
	{
		while(true)
		{
			Socket so = null;
			try {
				so = ss.accept();
				Thread workThread = new Thread(new Handler(so));
				workThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//while
	}//public void service
	
	public static void main(String args[]) throws IOException
	{
		new AccountServer().service();
	}
	
	
	class Handler implements Runnable 
	{
		private Socket so;
		public Handler(Socket socket)
		{
			this.so = socket;
		}
		
		
		public void run()
		{
			System.out.println("New connnection accepted " + so.getInetAddress() 
					+ ": " + so.getPort());
			
			try {
				DataInputStream dis = new DataInputStream(so.getInputStream());
				DataOutputStream dos = new DataOutputStream(so.getOutputStream());
				String str = dis.readUTF();
				System.out.println(str);
				
				String info[] = new String[5];
				info = str.split("=");
				
				for(int i = 0;i < info.length;i++)
					System.out.println(info[i]);
				
				Properties userPro = new Properties();
				Properties fatherPro = new Properties();
				File file = new File("Users.properties");
				File father_file = new File("fathers.properties");
				
				CatUtil.loadPro(userPro, file);
				CatUtil.loadPro(fatherPro, father_file);
				if(info[3].equals("1"))			//如果是 登录 选项
				{
					if(file.length() != 0)
					{
						if(userPro.containsKey(info[0] ))
						{
							if(info[1].equals(userPro.getProperty(info[0])))
								dos.writeUTF(admit);
							else
								dos.writeUTF(deny_Pwd);
						}//if containsKey
						else
							dos.writeUTF(deny_Nick);
					}//if file.length()
					else
					{
						dos.writeUTF(deny_Nick);
					}//else
				}// if info[3]=="1"
				else if(info[3].equals("3")) //重置密码选项
				{
					if(father_file.length() != 0)
					{
						if(fatherPro.containsKey(info[0]))
						{
							if(info[4].equals(fatherPro.getProperty(info[0])) )//成功修改密码
							{
								userPro.setProperty(info[0], info[1]);
								userPro.store(new FileOutputStream(new File("Users.properties")),
										"Copyright (c) Boxcode Studio,Improved by xdu");
								dos.writeUTF(resetSuccess);
								dos.flush();
							}
							else
							{
								dos.writeUTF(wa);
								dos.flush();
							}
						}
						else
						{
							dos.writeUTF(deny_Nick);
							dos.flush();
						}
					}
					else
					{	
						dos.writeUTF(deny_Nick);
						dos.flush();
					}
				}//else if
				else						//用户注册选项
				{
					if(userPro.containsKey(info[0]))		//如果包含该用户名 返回 deny_Nick
					{	
						dos.writeUTF(deny_Nick);
						dos.flush();
					}
					else if(info[0].contains(UnRead) || info[0].contains(OffLine))
					{
						dos.writeUTF(illegal);
						dos.flush();
					}
					else									//将用户昵称 、密码写入 文件
					{
						userPro.setProperty(info[0], info[1]);
						userPro.store(new FileOutputStream(new File("Users.properties")),
								"Copyright (c) Boxcode Studio,Improved by xdu");
						dos.writeUTF(admit);
						dos.flush();
						
						//进行一下延迟
						for(int i = -99999;i < 99999;i++)
							continue;
						String father = dis.readUTF();
						fatherPro.setProperty(info[0], father);
						fatherPro.store(new FileOutputStream(new File("fathers.properties")),
								"Copyright (c) Boxcode Studio,Improved by xdu");
					}
				}//else
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}//class Handler
}
