package Free.client;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

import java.util.*;

import Free.client.CatChatroom.ClientInputThread;
import Free.function.FreeBean;

import com.sun.javafx.collections.MappingChange.Map;

import Free.util.FreeUtil;


public class UserList extends JFrame 
{

	private static JPanel contentPane;
	private static Socket clientSocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String name;
	
	private static AbstractListModel listModel;
	private static JList list;	//在线人员名单;
	private static Vector<String> onlines = new Vector<String >();		//在线用户
	
	private static boolean isSendFile = false;
	private static boolean isReceiveFile = false;
	
	//以打开 的用户 聊天对话框
	static List<ChattingBox > ChatBoxes = new LinkedList<ChattingBox >();
	
	//已打开通知 的对话框
	
	
	//用户 与 未读消息队列 Map
	static HashMap<String, Queue<FreeBean> > MessageMap = new HashMap<String, Queue<FreeBean> >();
	//as you can see
	public final static String UnRead = " 未读消息";
	public final static String OffLine = " 已下线";
	public final static String GroupChat = "LotOfGuys";
	public UserList(String u_name, Socket client)
	{
		name = u_name;
		clientSocket = client;
		
		SwingUtilities.updateComponentTreeUI(this);
		//设置 CatChatRoom look and feel
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
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
		
		
		this.setTitle(name);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(200, 100, 282, 451);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(
						"images/UserList.jpg").getImage(), 0,
						0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setOpaque(false);
		this.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//设置用户列表
		listModel = new UUListModel(onlines);
		list = new JList(listModel);
		//设置用户绘制 用户列表中每个 单元的委托
		list.setCellRenderer(new CellRenderer());
		list.setOpaque(false);
		Border etch = BorderFactory.createEtchedBorder();
		list.setBorder(BorderFactory.createTitledBorder(etch, "<"+u_name+">"
				+ "在线客户:", TitledBorder.CENTER, TitledBorder.TOP, new Font(
				"sdf", Font.BOLD, 20), Color.green));
		
		
		JScrollPane scrollPane_2 = new JScrollPane(list);
		scrollPane_2.setBounds(10, 10, 245, 375);
		scrollPane_2.setOpaque(false);
		scrollPane_2.getViewport().setOpaque(false);
		getContentPane().add(scrollPane_2);
		
		JLabel lblNewLabel = new JLabel("\u53D1\u8D77\u7FA4\u804A");
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		lblNewLabel.setBounds(10, 398, 118, 17);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("\u6253\u5F00\u5BF9\u8BDD\u6846");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String > to = new LinkedList<String>();
				to = list.getSelectedValuesList();
				if(1==to.size() || 0==to.size())
				{
					JOptionPane.showMessageDialog(getContentPane(), "通知人数仅为0和1时，不建议使用通知功能");
					return;
				}
				
				HashSet<String > friends = new HashSet<String>();
				for(int i = 0;i < to.size();i++)
					friends.add(to.get(i));
				
				ChattingBox cBox = new ChattingBox(name, GroupChat,clientSocket);
				cBox.setFriends(friends);
				cBox.setVisible(true);
			}
		});
		
		
		btnNewButton.setBounds(157, 395, 93, 23);
		contentPane.add(btnNewButton);
		
		//发送上线通知给服务器
		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			// 记录上线客户的信息在catbean中，并发送给服务器
			FreeBean bean = new FreeBean();
			bean.setType(0);
			bean.setName(name);
			bean.setTimer(FreeUtil.getTimer());
			oos.writeObject(bean);
			oos.flush();

			// 启动客户接收线程
			new ClientInputThread().start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String friend = (String )list.getSelectedValue();		//获得所选项目 的名称 和位置 实际上为了保证 健壮性 不应该在这 就定位出 index
				//int index = list.getSelectedIndex();
				
				if (e.getClickCount() == 2) {					//双击 用户名 与用户对话
					//判断是否 有未读消息
					boolean flag = false;
					if(friend.contains("(我)"))
					{
						JOptionPane.showMessageDialog(getContentPane(), "不能像自己发送信息");
						return;
					}
					//遍历 ChatBox 如果已经打开 friend 的对话框 则return
					for(int i = 0;i < ChatBoxes.size();i++)
					{
						if(friend.equals(ChatBoxes.get(i).getFriend()) )
						{
							if(ChatBoxes.get(i).getExtendedState() == JFrame.ICONIFIED)
								ChatBoxes.get(i).setExtendedState(JFrame.NORMAL);
							ChatBoxes.get(i).toFront();
							ChatBoxes.get(i).setLocation(200, 200);
							return;
						}
					}
					
					//如果点击 的项目中的名字 包含"未读消息"字样 则应该把消息读出 ，然后再把 项目名称更改回来
					String oldFriend = friend;
					if(friend.contains(UnRead) && !friend.contains(OffLine))
					{
						flag = true;
						friend = friend.substring(0, friend.indexOf(UnRead));
						int index = onlines.indexOf(oldFriend);
						onlines.set(index, friend);
						//将 用户 名单更新回来
						listModel = new UUListModel(onlines);
						list.setModel(listModel);
					
						ChattingBox cBox = new ChattingBox(name, friend,clientSocket);
						ChatBoxes.add(cBox);
						cBox.setVisible(true);
						
						if(flag)
						{
							Queue<FreeBean> qc = MessageMap.get(friend);
							while(!qc.isEmpty())
								cBox.getBean(qc.poll());
						}
					}
					else if(friend.contains(UnRead) && friend.contains(OffLine))
					{
						friend = friend.substring(0,friend.indexOf(UnRead));
						int index = onlines.indexOf(oldFriend);
						onlines.remove(index);
						
						listModel = new UUListModel(onlines);
						list.setModel(listModel);
						
						ChattingBox cBox = new ChattingBox(name, friend, clientSocket);
						ChatBoxes.add(cBox);
						cBox.setVisible(true);
						
						Queue<FreeBean> qc = MessageMap.get(friend);
						FreeBean cb = new FreeBean();
						cb.setType(1);
						cb.setTimer(FreeUtil.getTimer());
						cb.setName("系统消息");
						cb.setInfo("该用户此时可能已经下线，你所发送的信息可能不会被对方接收到");
						qc.offer(cb);
						while(!qc.isEmpty())
							cBox.getBean(qc.poll());
					}
					else
					{
						ChattingBox cBox = new ChattingBox(name, friend,clientSocket);
						ChatBoxes.add(cBox);
						cBox.setVisible(true);
					}
					
				}//if e.getClickCount()
			}
		
			
		});  //this.addMouseListener()
	}//public UserList
	
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
					
					int result = JOptionPane.showConfirmDialog(this.getContentPane(), "确定要退出聊天室吗?");
					if(0 == result)
					{
						FreeBean clientBean = new FreeBean();
						clientBean.setType(-1);
						clientBean.setName(name);
						clientBean.setTimer(FreeUtil.getTimer());
						sendMessage(clientBean);
						
						//关闭用户的socket 加上这些后服务器端各种报错
						/*
						 * try {
							this.clientSocket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						*/
						super.processWindowEvent(e);	
					}
					else
						return;
				}
	}//private void processWindowEvent

	private void sendMessage(FreeBean clientBean) {
		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(clientBean);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param str 用户姓名
	 * @return 传入的用户是否在线
	 */
	private boolean CheckUser(String str) {
		for(int i = 0;i < this.onlines.size();i++) {
			if(onlines.get(i).contains(str) && !onlines.get(i).contains(OffLine))
				return true;
		}
		return false;
	}// private 
	
	class ClientInputThread extends Thread {
		
		@Override
		public void run() {
			try {
				// 不停的从服务器接收信息
				while (true) {
					ois = new ObjectInputStream(clientSocket.getInputStream());
					final FreeBean  bean = (FreeBean) ois.readObject();
					switch (bean.getType()) {
					case 0: {									//有人上线
						// 更新列表
						Vector<String > backup = (Vector<String>) onlines.clone();
						onlines.clear();
						HashSet<String> clients = bean.getClients();
						Iterator<String> it = clients.iterator();
						while (it.hasNext()) {
							String ele = it.next();
							if (name.equals(ele)) {
								onlines.add(ele + "(我)");
							} else {
								onlines.add(ele);
							}
						}
						
						//虽然已经下线 但是仍有消息
						it = backup.iterator();
						while(it.hasNext())
						{
							String ele = it.next();
							//有未读消息  但不含已经下线的
							if(ele.contains(UnRead))
							{
								String OriName = ele.substring(0, ele.indexOf(UnRead));
								
								if(onlines.contains(OriName))				//有未读消息且还在线
									onlines.set(onlines.indexOf(OriName), OriName + UnRead + " " + MessageMap.get(OriName).size());
								else										//有未读消息已经下线
								{
									onlines.add(OriName + UnRead + " " + MessageMap.get(OriName).size() + OffLine);
									for(int i = 0;i < ChatBoxes.size();i++)
									{
										if( ChatBoxes.get(i).getFriend().equals(OriName))
										{
											ChatBoxes.get(i).setUse(false);
											break;
										}
									}
								}//else
							}//if
						}//while
						
						//更新上线 对话框中的信息
						for(int i = 0;i < ChatBoxes.size();i++)
						{
							if(!ChatBoxes.get(i).isUse() && CheckUser(ChatBoxes.get(i).getFriend()) )
							{
								ChatBoxes.get(i).setUse(true);
								
								FreeBean cb = new FreeBean();
								String time = FreeUtil.getTimer();
								cb.setTimer(time);	//设置时间戳
								cb.setType(1);
								cb.setInfo("对方已经上线");
								cb.setName("服务器提醒");
								
								ChatBoxes.get(i).getBean(cb);
							}
						}
						
						listModel = new UUListModel(onlines);
						list.setModel(listModel);
						break;
					}//case 0;
					
					case 1 : {				//私聊信息
						boolean flag = false;				//该用户没有打开对话框
						String from = bean.getName();		//得到来源
						for(int i = 0;i < ChatBoxes.size();i++)
						{
							if(from.equals(ChatBoxes.get(i).getFriend()) )
							{
								////////////////////////////////////
								ChatBoxes.get(i).getBean(bean);
								flag = true;
								break;
							}
						}
						
						//如果未打开  与该用户的对话框
						if(!flag)
						{
							int index = onlines.indexOf(from);					//index = -1
							if(-1==index)										//由于本程序的 限制性  消息的来源一定是在线的 如果 index ==-1 说明 该名称 加上了 UnRead 和 相应数字
							{
								String tmp = from + UnRead;
								for(int i = 0;i < onlines.size();i++)
								{
									if(onlines.get(i).contains(tmp))
									{
										index = i;
										break;
									}
								}
							}
							
							if(!((String)onlines.get(index)).contains("未读消息"))
								onlines.set(index, from + " 未读消息" + " 1");
							else
							{
								String regex = new String(" ");
								String str = onlines.get(index);
								
								String arr[] = str.split(regex);
								Integer num = Integer.parseInt(arr[arr.length - 1]);
								num++;
								String nname = arr[0] + UnRead + ' ' + num.toString();
								onlines.set(index, nname);
							}
							
							listModel = new UUListModel(onlines);
							list.setModel(listModel);
							
							/*
							 *	如果之前 含有这个 的映射 ，直接在qc中加入 CatBean
							 *	否则 在map中创建 相应 键值对
							 */
							if(MessageMap.containsKey(from))
							{
								Queue<FreeBean> qc = MessageMap.get(from);
								qc.offer(bean);
							}
							else
							{
								Queue<FreeBean> qc = new LinkedList<FreeBean>();
								qc.offer(bean);
								MessageMap.put(from, qc);
							}
						}
						break;
					}
					case -1: {			//下线请求 如果打开了 与相应用户的对话框则在 对话框上显示消息 对方已下线
						boolean flag = false;
						String from = bean.getName();
						if(name.equals(from))
							return;
						
						for(int i = 0;i < ChatBoxes.size();i++)
						{
							if(from.equals(ChatBoxes.get(i).getFriend()) )
							{
								flag = true;
								
								FreeBean cb = new FreeBean();
								String time = FreeUtil.getTimer();
								cb.setTimer(time);	//设置时间戳
								cb.setType(1);
								cb.setInfo("对方已经下线，之后发送的消息对方将将无法接收,你未发送出的文件已经被取消");
								cb.setName("服务器提醒");
								
								ChatBoxes.get(i).setUse(false);
								ChatBoxes.get(i).getBean(cb);
								break;
							}
						}//for
						break;
					}
					
					//2为有关于文件传输的通知 3.确定接收文件4.取消接收文件
					//
					case 2:
					case 3:
					case 4:
					{
						boolean flag = false;				//该用户没有打开对话框
						String from = bean.getName();		//得到来源
						for(int i = 0;i < ChatBoxes.size();i++)
						{
							if(from.equals(ChatBoxes.get(i).getFriend()) )
							{
								////////////////////////////////////
								ChatBoxes.get(i).getBean(bean);
								flag = true;
								break;
							}
						}
						
						if(!flag)
						{
							int index = onlines.indexOf(from);					//index = -1
							if(-1==index)										//由于本程序的 限制性  消息的来源一定是在线的 如果 index ==-1 说明 该名称 加上了 UnRead
							{
								String tmp = from + UnRead;
								index = onlines.indexOf(tmp);
							}
							
							if(!((String)onlines.get(index)).contains("未读消息"))
								onlines.set(index, from + " 未读消息");
							
							listModel = new UUListModel(onlines);
							list.setModel(listModel);
							
							/*
							 *	如果之前 含有这个 的映射 ，直接在qc中加入 CatBean
							 *	否则 在map中创建 相应 键值对
							 */
							if(MessageMap.containsKey(from))
							{
								Queue<FreeBean> qc = MessageMap.get(from);
								qc.offer(bean);
							}
							else
							{
								Queue<FreeBean> qc = new LinkedList<FreeBean>();
								qc.offer(bean);
								MessageMap.put(from, qc);
							}
						}
						break;
					}
					
					default: {
						break;
					}
					}
	
				}//while true
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (clientSocket != null) {
					try {
						clientSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		}//run()方法

	}//clientthread
}
