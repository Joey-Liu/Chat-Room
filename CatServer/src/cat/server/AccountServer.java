package cat.server;

//�˺ŷ����� ����ģʽ��
//mode = 1 ��ʾ �û�ʹ���˺������¼ 
//mode = 2 ��ʾ �û�ע����˺�


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
	private final int port = 8000;		//�˿ں�
	private final String deny_Nick = "NoNickName";
	private final String deny_Pwd = "WrongPwd";
	private final String admit = "Yes";
	private final String UnRead = "δ����Ϣ";
	private final String OffLine = "������";
	private final String illegal = "�������Ϸ�";
	private final String resetSuccess = "�޸�����ɹ���";
	private final String wa = "The answer is wrong!";
	private ServerSocket ss;
	
	
	public AccountServer() throws IOException
	{
		ss = new ServerSocket(port);
		System.out.println("�˺ŷ���������");
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
				if(info[3].equals("1"))			//����� ��¼ ѡ��
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
				else if(info[3].equals("3")) //��������ѡ��
				{
					if(father_file.length() != 0)
					{
						if(fatherPro.containsKey(info[0]))
						{
							if(info[4].equals(fatherPro.getProperty(info[0])) )//�ɹ��޸�����
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
				else						//�û�ע��ѡ��
				{
					if(userPro.containsKey(info[0]))		//����������û��� ���� deny_Nick
					{	
						dos.writeUTF(deny_Nick);
						dos.flush();
					}
					else if(info[0].contains(UnRead) || info[0].contains(OffLine))
					{
						dos.writeUTF(illegal);
						dos.flush();
					}
					else									//���û��ǳ� ������д�� �ļ�
					{
						userPro.setProperty(info[0], info[1]);
						userPro.store(new FileOutputStream(new File("Users.properties")),
								"Copyright (c) Boxcode Studio,Improved by xdu");
						dos.writeUTF(admit);
						dos.flush();
						
						//����һ���ӳ�
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
