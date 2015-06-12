package cat.client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cat.function.CatBean;
import cat.util.CatUtil;

import java.awt.Label;

import javax.swing.JTable;

public class ChattingBox extends JFrame {

	private static final long serialVersionUID = 6129126482250125466L;
	
	private static JPanel contentPane;		//内容面板
	private static Socket clientSocket;		//客户端套接字
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String name;				//本人ID
	private static JTextArea textArea;		//聊天信息显示
	private static AbstractListModel listmodel;
	private static String filePath;				//传输文件路径
	private static JLabel lblNewLabel;
	private static JProgressBar progressBar;	
	private static Vector onlines;				//在线用户
	private static boolean isSendFile = false;
	private static boolean isReceiveFile = false;
	
	private static UserList ul;			//大管家设计模式@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	private static String friend;			//对方用户ID 用于表达对话框中与哪个好友进行通信	
	private static HashSet<String > friends;		//群聊时保存各个 用户name
	private static boolean use = true;
	
	public static boolean isUse() {
		return use;
	}

	public static void setUse(boolean use) {
		ChattingBox.use = use;
	}

	public static HashSet<String> getFriends() {
		return friends;
	}

	public static void setFriends(HashSet<String> friends) {
		ChattingBox.friends = friends;
	}

	public static String getFriend() {
		return friend;
	}

	public static void setFriend(String friend) {
		ChattingBox.friend = friend;
	}

	// 声音
	private static File file, file2;
	private static URL cb, cb2;
	private static AudioClip aau, aau2;
	
	/**
	 * @wbp.parser.constructor
	 */
	public ChattingBox(String u_name, String _friend, Socket client,
			UserList ulist) {
		this(u_name, _friend, client);
		this.ul = ulist;
	}
	
	/**
	 * 
	 * 
	 * @param u_name	本人ID
	 * @param friend	对方好友ID
	 * @param client	本人套接字 				PAY ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 所有对话框应该 共享一个套接字
	 */
	public ChattingBox(String u_name, String _friend, Socket client) {
		// 赋值
		name = u_name;
		clientSocket = client;
		friend = _friend;
		onlines = new Vector();
		
		SwingUtilities.updateComponentTreeUI(this);

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setTitle(friend);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(200, 100, 688, 510);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;		//不知道有啥用处
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\聊天室1.jpg").getImage(), 0, 0,
						getWidth(), getHeight(), null);
			}
		};
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// 聊天信息显示区域
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 410, 300);
		getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);//激活自动换行功能 
		textArea.setWrapStyleWord(true);//激活断行不断字功能 
		textArea.setFont(new Font("sdf", Font.BOLD, 13));
		scrollPane.setViewportView(textArea);

		// 打字区域
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 347, 410, 97);
		getContentPane().add(scrollPane_1);

		final JTextArea textArea_1 = new JTextArea();
		textArea_1.setLineWrap(true);				//激活自动换行功能 
		textArea_1.setWrapStyleWord(true);			//激活断行不断字功能 
		scrollPane_1.setViewportView(textArea_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(430, 10, 242, 468);
		contentPane.add(scrollPane_2);
		
		//我在这里加了一个广告栏，来满足老师的互动性的要求你先看看，如果不行你弄好选择聊天对象后给我发一下所有
		//代码，我在改改
		JLabel lblNewLabel_1 = new JLabel();
		ImageIcon image = new ImageIcon("images\\广告界面.jpg");
		lblNewLabel_1.setIcon(image);
		lblNewLabel_1.setSize(scrollPane_2.WIDTH, scrollPane_2.HEIGHT);
		scrollPane_2.setViewportView(lblNewLabel_1);

		// 关闭按钮
		final JButton btnNewButton = new JButton("SendFile");
		btnNewButton.setBounds(239, 448, 100, 30);
		getContentPane().add(btnNewButton);

		// 发送按钮
		JButton btnNewButton_1 = new JButton("\u53D1\u9001");
		btnNewButton_1.setBounds(360, 448, 60, 30);
		getRootPane().setDefaultButton(btnNewButton_1);
		getContentPane().add(btnNewButton_1);

		// 文件传输栏
		progressBar = new JProgressBar();
		progressBar.setBounds(175, 320, 245, 15);
		progressBar.setMinimum(1);
		progressBar.setMaximum(100);
		getContentPane().add(progressBar);

		// 文件传输提示
		lblNewLabel = new JLabel(
				"\u6587\u4EF6\u4F20\u9001\u4FE1\u606F\u680F:");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 12));
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setBounds(10, 322, 155, 15);
		getContentPane().add(lblNewLabel);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// 发送按钮 在对话框中将消息发送给 friend 指定的对象
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String info = textArea_1.getText();
				CatBean infoBean = new CatBean();		//聊天信息infoBean
				
				if (info.equals("")) {
					JOptionPane.showMessageDialog(getContentPane(), "不能发送空信息");
					return;
				}

			
				infoBean.setType(1);		//类型
				infoBean.setName(name);		//来源
				String time = CatUtil.getTimer();
				infoBean.setTimer(time);	//设置时间戳
				infoBean.setInfo(info);			//设置聊天信息
				HashSet<String > set = new HashSet<String >();
				set.add(friend);				//为了和服务器端 兼容，只能使用这种方法
				infoBean.setClients(set);		//设置接收方

				// 自己发的内容也要现实在自己的屏幕上面
				textArea.append(time + " 我对" + friend + "说:\r\n" + info + "\r\n");

				sendMessage(infoBean);
				textArea_1.setText(null);
				textArea_1.requestFocus();
			}
		});//发送按钮监听器
		
		//发送文件 监听器
		btnNewButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("请选择文件");	//标题
		chooser.showDialog(getContentPane(), "选择");//按钮的文本
		
		// 判断是否选择了文件
		if(chooser.getSelectedFile() != null)
		{
			filePath = chooser.getSelectedFile().getPath();
			File file = new File(filePath);
			
			//文件为空
			if(file.length() == 0)
			{
				JOptionPane.showMessageDialog(getContentPane()
						, filePath + "文件为空，不允许发送");
				return;
			}
			
			CatBean clientBean = new CatBean();
			clientBean.setType(2);
			clientBean.setName(name);
			clientBean.setTimer(CatUtil.getTimer());
			clientBean.setFileName(file.getName());
			clientBean.setInfo("请求发送文件");
			double __size = (double) file.length();
			clientBean.setSize(__size);				//注意需要设置 文件大小 
			
			//判断发送文件的对象 , 是为了与接口兼容，不得已这样做
			HashSet<String> set = new HashSet<String >();
			set.add(friend);
			clientBean.setClients(set);
			sendMessage(clientBean);
			
			
		}}});//btnNewButton.addActionListener
		
		//通知时不提供文件传输功能
		if(friend.equals(UserList.GroupChat))
			btnNewButton.setVisible(false);
	}//构造方法


	@Override
	protected void processWindowEvent(WindowEvent e) {
		// TODO Auto-generated method stub
		//只对 窗口关闭消息进行处理
		if(e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			//收发文件时 不能关闭窗口
			if(isSendFile || isReceiveFile)
			{
				JOptionPane.showMessageDialog(contentPane,
						"正在传输文件中，您不能离开...",
						"Error Message", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			int result = JOptionPane.showConfirmDialog(this.getContentPane(), "确定要关闭与" + this.friend + "的对话吗？");
			if(0 == result)
			{
				ul.ChatBoxes.remove(e.getSource());						//使用大管家模式 在userlist中把自己去掉
				setVisible(false);
				super.processWindowEvent(e);	
			}
			else
				return;
		}
	}

	private void sendMessage(CatBean clientBean) {
		try {
			if (!friend.equals(UserList.GroupChat)) {
				oos = new ObjectOutputStream(clientSocket.getOutputStream());
				oos.writeObject(clientBean);
				oos.flush();
			}
			else
			{
				clientBean.setClients(friends);
				oos = new ObjectOutputStream(clientSocket.getOutputStream());
				oos.writeObject(clientBean);
				oos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//对话框得到bean之后采取的动作
	public void getBean(final CatBean cBean)
	{
		switch(cBean.getType())
		{
		case 1:			//聊天
			String info = cBean.getTimer() + " " + cBean.getName() + "对我说：\r\n";
			textArea.append(info + cBean.getInfo() + "\r\n");
			textArea.selectAll();
			break;
		case 2: {
			//有好友 发送文件   这代码有点厉害
			// 由于等待目标客户确认是否接收文件是个阻塞状态，所以这里用线程处理
			new Thread() {
				public void run() {
					//显示是否接收文件对话框
					String __info = cBean.getInfo();
					int result = JOptionPane.showConfirmDialog(
							getContentPane(), __info);
					switch(result){
					case 0: {  //接收文件
						JFileChooser chooser = new JFileChooser();
						chooser.setDialogTitle("保存文件框"); // 标题哦...
						//默认文件名称还有放在当前目录下
						chooser.setSelectedFile(new File(cBean
								.getFileName()));
						chooser.showDialog(getContentPane(), "保存"); // 这是按钮的名字..
						//保存路径
						String saveFilePath =chooser.getSelectedFile().toString();
					
						//创建客户CatBean
						CatBean clientBean = new CatBean();
						clientBean.setType(3);				//确定接收文件
						clientBean.setName(name);  //接收文件的客户名字
						clientBean.setTimer(CatUtil.getTimer());
						clientBean.setFileName(saveFilePath);
						clientBean.setInfo("确定接收文件");

						// 判断要发送给谁
						HashSet<String> set = new HashSet<String>();
						set.add(cBean.getName());			//来源			
						clientBean.setClients(set);  //文件来源
						clientBean.setTo(cBean.getClients());//给这些客户发送文件？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
						
						
						
						// 创建新的tcp socket 接收数据, 这是额外增加的功能, 大家请留意... ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
						try {
							ServerSocket ss = new ServerSocket(0); // 0可以获取空闲的端口号
							
							clientBean.setIp(clientSocket.getInetAddress().getHostAddress());
							clientBean.setPort(ss.getLocalPort());
							sendMessage(clientBean); // 先通过服务器告诉发送方, 你可以直接发送文件到我这里了...
							
							
							
							isReceiveFile=true;
							//等待文件来源的客户，输送文件....目标客户从网络上读取文件，并写在本地上
							Socket sk = ss.accept();
                            textArea.append(CatUtil.getTimer() + "  " + cBean.getFileName()
									+ "文件保存中.\r\n");
							DataInputStream dis = new DataInputStream(  //从网络上读取文件
									new BufferedInputStream(sk.getInputStream()));
							DataOutputStream dos = new DataOutputStream(  //写在本地上
									new BufferedOutputStream(new FileOutputStream(
											saveFilePath)));
	
							double count = 0;
							double num = cBean.getSize() / 100;
							int index = 0;
							while (count < cBean.getSize()) {
								int t = dis.read();
								dos.write(t);
								count++;
								
								if(num>0){
									if (count % num < 5 && count % num > 0 && index < 100) {
										progressBar.setValue(++index);
									}
									lblNewLabel.setText("下载进度:" + count
											+ "/" + cBean.getSize() + "  整体" + index
											+ "%");
								}else{
									lblNewLabel.setText("下载进度:" + count
											+ "/" + cBean.getSize() +"  整体:"+new Double(new Double(count).doubleValue()/new Double(cBean.getSize()).doubleValue()*100).intValue()+"%");
									if(count==cBean.getSize()){
										progressBar.setValue(100);
									}
								}
	
							}
							
							//给文件来源客户发条提示，文件保存完毕
							PrintWriter out = new PrintWriter(sk.getOutputStream(),true);
							out.println(CatUtil.getTimer() + " 发送给"+name+"的文件[" + cBean.getFileName()+"]"
									+ "文件保存完毕.\r\n");
							out.flush();
							dos.flush();
							dos.close();
							out.close();
							dis.close();
							sk.close();
							ss.close();
							textArea.append(CatUtil.getTimer() + "  " + cBean.getFileName()
									+ "文件保存完毕.存放位置为:"+saveFilePath+"\r\n");
							isReceiveFile = false;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						break;
					}
					default: {		//点击 "否" 或者 "取消" 
						CatBean clientBean = new CatBean();
						clientBean.setType(4);
						clientBean.setName(name);  //接收文件的客户名字
						clientBean.setTimer(CatUtil.getTimer());
						clientBean.setFileName(cBean.getFileName());
						clientBean.setInfo(CatUtil.getTimer() + "  "
								+ name + "取消接收文件["
								+ cBean.getFileName() + "]");

						isReceiveFile = false;
						// 判断要发送给谁
						HashSet<String> set = new HashSet<String>();
						set.add(cBean.getName());
						clientBean.setClients(set);  //文件来源
						clientBean.setTo(cBean.getClients());//给这些客户发送文件
						
						sendMessage(clientBean);
					 	
						break;
					
					}
				}
				};	
			}.start();
			break;
		}
		case 3:
			 //目标客户愿意接收文件，源客户开始读取本地文件并发送到网络上 ，同样需要使用 多线程方法防止阻塞
			textArea.append(cBean.getTimer() + "  "+ cBean.getName() + "确定接收文件" + ",文件传送中..\r\n");
			new Thread(){
				public void run() {
					
					try {
						isSendFile = true;
						//创建要接收文件的客户套接字，直接两点之间 进行文件传输，不在经过服务器
						Socket s = new Socket(cBean.getIp(),cBean.getPort());
						DataInputStream dis = new DataInputStream(
								new FileInputStream(filePath));  //本地读取该客户刚才选中的文件
						DataOutputStream dos = new DataOutputStream(
								new BufferedOutputStream(s
										.getOutputStream()));  //网络写出文件
						
					
						int size = dis.available();
						
						double count = 0;
						int num = size / 100;
						int index = 0;
						while (count < size) {
							
							int t = dis.read();
							dos.write(t);
							count++;  //每次只读取一个字节

							if(num>0) {
								if (count % num < 5 && count % num > 0 && index < 100) {
									progressBar.setValue(++index);
								}
								lblNewLabel.setText("上传进度:" + count + "/"
												+ size + "  整体" + index
												+ "%");
							} else {
								lblNewLabel.setText("上传进度:" + count + "/"
										+ size +"  整体:"+new Double(new Double(count).doubleValue()/new Double(size).doubleValue()*100).intValue()+"%"
										);
								if(count==size){
									progressBar.setValue(100);
								}
							}
						}//while(count < size)
						dos.flush();
						dis.close();
					  //读取目标客户的提示保存完毕的信息...
					    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					    textArea.append( br.readLine() + "\r\n");//产生异常
					    isSendFile = false;
						br.close();
					    s.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				
				};
			}.start();
			break;
		case 4:								//对方取消接收文件
			textArea.append(cBean.getInfo() + "\r\n");
			break;
		}//switch
	}
	
	public static void main(String args[])
	{
		ChattingBox cb = new ChattingBox("a", "b", new Socket());
		cb.setVisible(true);
	}
}