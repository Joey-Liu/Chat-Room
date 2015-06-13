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
	private static JList list;	//������Ա����;
	private static Vector<String> onlines = new Vector<String >();		//�����û�
	
	private static boolean isSendFile = false;
	private static boolean isReceiveFile = false;
	
	//�Դ� ���û� ����Ի���
	static List<ChattingBox > ChatBoxes = new LinkedList<ChattingBox >();
	
	//�Ѵ�֪ͨ �ĶԻ���
	
	
	//�û� �� δ����Ϣ���� Map
	static HashMap<String, Queue<FreeBean> > MessageMap = new HashMap<String, Queue<FreeBean> >();
	//as you can see
	public final static String UnRead = " δ����Ϣ";
	public final static String OffLine = " ������";
	public final static String GroupChat = "LotOfGuys";
	public UserList(String u_name, Socket client)
	{
		name = u_name;
		clientSocket = client;
		
		SwingUtilities.updateComponentTreeUI(this);
		//���� CatChatRoom look and feel
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
		
		//�����û��б�
		listModel = new UUListModel(onlines);
		list = new JList(listModel);
		//�����û����� �û��б���ÿ�� ��Ԫ��ί��
		list.setCellRenderer(new CellRenderer());
		list.setOpaque(false);
		Border etch = BorderFactory.createEtchedBorder();
		list.setBorder(BorderFactory.createTitledBorder(etch, "<"+u_name+">"
				+ "���߿ͻ�:", TitledBorder.CENTER, TitledBorder.TOP, new Font(
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
					JOptionPane.showMessageDialog(getContentPane(), "֪ͨ������Ϊ0��1ʱ��������ʹ��֪ͨ����");
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
		
		//��������֪ͨ��������
		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			// ��¼���߿ͻ�����Ϣ��catbean�У������͸�������
			FreeBean bean = new FreeBean();
			bean.setType(0);
			bean.setName(name);
			bean.setTimer(FreeUtil.getTimer());
			oos.writeObject(bean);
			oos.flush();

			// �����ͻ������߳�
			new ClientInputThread().start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String friend = (String )list.getSelectedValue();		//�����ѡ��Ŀ ������ ��λ�� ʵ����Ϊ�˱�֤ ��׳�� ��Ӧ������ �Ͷ�λ�� index
				//int index = list.getSelectedIndex();
				
				if (e.getClickCount() == 2) {					//˫�� �û��� ���û��Ի�
					//�ж��Ƿ� ��δ����Ϣ
					boolean flag = false;
					if(friend.contains("(��)"))
					{
						JOptionPane.showMessageDialog(getContentPane(), "�������Լ�������Ϣ");
						return;
					}
					//���� ChatBox ����Ѿ��� friend �ĶԻ��� ��return
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
					
					//������ ����Ŀ�е����� ����"δ����Ϣ"���� ��Ӧ�ð���Ϣ���� ��Ȼ���ٰ� ��Ŀ���Ƹ��Ļ���
					String oldFriend = friend;
					if(friend.contains(UnRead) && !friend.contains(OffLine))
					{
						flag = true;
						friend = friend.substring(0, friend.indexOf(UnRead));
						int index = onlines.indexOf(oldFriend);
						onlines.set(index, friend);
						//�� �û� �������»���
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
						cb.setName("ϵͳ��Ϣ");
						cb.setInfo("���û���ʱ�����Ѿ����ߣ��������͵���Ϣ���ܲ��ᱻ�Է����յ�");
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
		//ֻ�� ���ڹر���Ϣ���д���
				if(e.getID() == WindowEvent.WINDOW_CLOSING)
				{
					//�շ��ļ�ʱ ���ܹرմ���
					if(isSendFile || isReceiveFile)
					{
						JOptionPane.showMessageDialog(contentPane,
								"���ڴ����ļ��У��������뿪...",
								"Error Message", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					int result = JOptionPane.showConfirmDialog(this.getContentPane(), "ȷ��Ҫ�˳���������?");
					if(0 == result)
					{
						FreeBean clientBean = new FreeBean();
						clientBean.setType(-1);
						clientBean.setName(name);
						clientBean.setTimer(FreeUtil.getTimer());
						sendMessage(clientBean);
						
						//�ر��û���socket ������Щ��������˸��ֱ���
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
	 * @param str �û�����
	 * @return ������û��Ƿ�����
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
				// ��ͣ�Ĵӷ�����������Ϣ
				while (true) {
					ois = new ObjectInputStream(clientSocket.getInputStream());
					final FreeBean  bean = (FreeBean) ois.readObject();
					switch (bean.getType()) {
					case 0: {									//��������
						// �����б�
						Vector<String > backup = (Vector<String>) onlines.clone();
						onlines.clear();
						HashSet<String> clients = bean.getClients();
						Iterator<String> it = clients.iterator();
						while (it.hasNext()) {
							String ele = it.next();
							if (name.equals(ele)) {
								onlines.add(ele + "(��)");
							} else {
								onlines.add(ele);
							}
						}
						
						//��Ȼ�Ѿ����� ����������Ϣ
						it = backup.iterator();
						while(it.hasNext())
						{
							String ele = it.next();
							//��δ����Ϣ  �������Ѿ����ߵ�
							if(ele.contains(UnRead))
							{
								String OriName = ele.substring(0, ele.indexOf(UnRead));
								
								if(onlines.contains(OriName))				//��δ����Ϣ�һ�����
									onlines.set(onlines.indexOf(OriName), OriName + UnRead + " " + MessageMap.get(OriName).size());
								else										//��δ����Ϣ�Ѿ�����
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
						
						//�������� �Ի����е���Ϣ
						for(int i = 0;i < ChatBoxes.size();i++)
						{
							if(!ChatBoxes.get(i).isUse() && CheckUser(ChatBoxes.get(i).getFriend()) )
							{
								ChatBoxes.get(i).setUse(true);
								
								FreeBean cb = new FreeBean();
								String time = FreeUtil.getTimer();
								cb.setTimer(time);	//����ʱ���
								cb.setType(1);
								cb.setInfo("�Է��Ѿ�����");
								cb.setName("����������");
								
								ChatBoxes.get(i).getBean(cb);
							}
						}
						
						listModel = new UUListModel(onlines);
						list.setModel(listModel);
						break;
					}//case 0;
					
					case 1 : {				//˽����Ϣ
						boolean flag = false;				//���û�û�д򿪶Ի���
						String from = bean.getName();		//�õ���Դ
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
						
						//���δ��  ����û��ĶԻ���
						if(!flag)
						{
							int index = onlines.indexOf(from);					//index = -1
							if(-1==index)										//���ڱ������ ������  ��Ϣ����Դһ�������ߵ� ��� index ==-1 ˵�� ������ ������ UnRead �� ��Ӧ����
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
							
							if(!((String)onlines.get(index)).contains("δ����Ϣ"))
								onlines.set(index, from + " δ����Ϣ" + " 1");
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
							 *	���֮ǰ ������� ��ӳ�� ��ֱ����qc�м��� CatBean
							 *	���� ��map�д��� ��Ӧ ��ֵ��
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
					case -1: {			//�������� ������� ����Ӧ�û��ĶԻ������� �Ի�������ʾ��Ϣ �Է�������
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
								cb.setTimer(time);	//����ʱ���
								cb.setType(1);
								cb.setInfo("�Է��Ѿ����ߣ�֮���͵���Ϣ�Է������޷�����,��δ���ͳ����ļ��Ѿ���ȡ��");
								cb.setName("����������");
								
								ChatBoxes.get(i).setUse(false);
								ChatBoxes.get(i).getBean(cb);
								break;
							}
						}//for
						break;
					}
					
					//2Ϊ�й����ļ������֪ͨ 3.ȷ�������ļ�4.ȡ�������ļ�
					//
					case 2:
					case 3:
					case 4:
					{
						boolean flag = false;				//���û�û�д򿪶Ի���
						String from = bean.getName();		//�õ���Դ
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
							if(-1==index)										//���ڱ������ ������  ��Ϣ����Դһ�������ߵ� ��� index ==-1 ˵�� ������ ������ UnRead
							{
								String tmp = from + UnRead;
								index = onlines.indexOf(tmp);
							}
							
							if(!((String)onlines.get(index)).contains("δ����Ϣ"))
								onlines.set(index, from + " δ����Ϣ");
							
							listModel = new UUListModel(onlines);
							list.setModel(listModel);
							
							/*
							 *	���֮ǰ ������� ��ӳ�� ��ֱ����qc�м��� CatBean
							 *	���� ��map�д��� ��Ӧ ��ֵ��
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
		}//run()����

	}//clientthread
}
