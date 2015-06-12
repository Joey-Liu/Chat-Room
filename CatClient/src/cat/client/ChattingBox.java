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
	
	private static JPanel contentPane;		//�������
	private static Socket clientSocket;		//�ͻ����׽���
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String name;				//����ID
	private static JTextArea textArea;		//������Ϣ��ʾ
	private static AbstractListModel listmodel;
	private static String filePath;				//�����ļ�·��
	private static JLabel lblNewLabel;
	private static JProgressBar progressBar;	
	private static Vector onlines;				//�����û�
	private static boolean isSendFile = false;
	private static boolean isReceiveFile = false;
	
	private static UserList ul;			//��ܼ����ģʽ@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	private static String friend;			//�Է��û�ID ���ڱ��Ի��������ĸ����ѽ���ͨ��	
	private static HashSet<String > friends;		//Ⱥ��ʱ������� �û�name
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

	// ����
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
	 * @param u_name	����ID
	 * @param friend	�Է�����ID
	 * @param client	�����׽��� 				PAY ATTENTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ���жԻ���Ӧ�� ����һ���׽���
	 */
	public ChattingBox(String u_name, String _friend, Socket client) {
		// ��ֵ
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
			private static final long serialVersionUID = 1L;		//��֪����ɶ�ô�
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\������1.jpg").getImage(), 0, 0,
						getWidth(), getHeight(), null);
			}
		};
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ������Ϣ��ʾ����
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 410, 300);
		getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);//�����Զ����й��� 
		textArea.setWrapStyleWord(true);//������в����ֹ��� 
		textArea.setFont(new Font("sdf", Font.BOLD, 13));
		scrollPane.setViewportView(textArea);

		// ��������
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 347, 410, 97);
		getContentPane().add(scrollPane_1);

		final JTextArea textArea_1 = new JTextArea();
		textArea_1.setLineWrap(true);				//�����Զ����й��� 
		textArea_1.setWrapStyleWord(true);			//������в����ֹ��� 
		scrollPane_1.setViewportView(textArea_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(430, 10, 242, 468);
		contentPane.add(scrollPane_2);
		
		//�����������һ�����������������ʦ�Ļ����Ե�Ҫ�����ȿ��������������Ū��ѡ������������ҷ�һ������
		//���룬���ڸĸ�
		JLabel lblNewLabel_1 = new JLabel();
		ImageIcon image = new ImageIcon("images\\������.jpg");
		lblNewLabel_1.setIcon(image);
		lblNewLabel_1.setSize(scrollPane_2.WIDTH, scrollPane_2.HEIGHT);
		scrollPane_2.setViewportView(lblNewLabel_1);

		// �رհ�ť
		final JButton btnNewButton = new JButton("SendFile");
		btnNewButton.setBounds(239, 448, 100, 30);
		getContentPane().add(btnNewButton);

		// ���Ͱ�ť
		JButton btnNewButton_1 = new JButton("\u53D1\u9001");
		btnNewButton_1.setBounds(360, 448, 60, 30);
		getRootPane().setDefaultButton(btnNewButton_1);
		getContentPane().add(btnNewButton_1);

		// �ļ�������
		progressBar = new JProgressBar();
		progressBar.setBounds(175, 320, 245, 15);
		progressBar.setMinimum(1);
		progressBar.setMaximum(100);
		getContentPane().add(progressBar);

		// �ļ�������ʾ
		lblNewLabel = new JLabel(
				"\u6587\u4EF6\u4F20\u9001\u4FE1\u606F\u680F:");
		lblNewLabel.setFont(new Font("����", Font.PLAIN, 12));
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setBounds(10, 322, 155, 15);
		getContentPane().add(lblNewLabel);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// ���Ͱ�ť �ڶԻ����н���Ϣ���͸� friend ָ���Ķ���
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String info = textArea_1.getText();
				CatBean infoBean = new CatBean();		//������ϢinfoBean
				
				if (info.equals("")) {
					JOptionPane.showMessageDialog(getContentPane(), "���ܷ��Ϳ���Ϣ");
					return;
				}

			
				infoBean.setType(1);		//����
				infoBean.setName(name);		//��Դ
				String time = CatUtil.getTimer();
				infoBean.setTimer(time);	//����ʱ���
				infoBean.setInfo(info);			//����������Ϣ
				HashSet<String > set = new HashSet<String >();
				set.add(friend);				//Ϊ�˺ͷ������� ���ݣ�ֻ��ʹ�����ַ���
				infoBean.setClients(set);		//���ý��շ�

				// �Լ���������ҲҪ��ʵ���Լ�����Ļ����
				textArea.append(time + " �Ҷ�" + friend + "˵:\r\n" + info + "\r\n");

				sendMessage(infoBean);
				textArea_1.setText(null);
				textArea_1.requestFocus();
			}
		});//���Ͱ�ť������
		
		//�����ļ� ������
		btnNewButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("��ѡ���ļ�");	//����
		chooser.showDialog(getContentPane(), "ѡ��");//��ť���ı�
		
		// �ж��Ƿ�ѡ�����ļ�
		if(chooser.getSelectedFile() != null)
		{
			filePath = chooser.getSelectedFile().getPath();
			File file = new File(filePath);
			
			//�ļ�Ϊ��
			if(file.length() == 0)
			{
				JOptionPane.showMessageDialog(getContentPane()
						, filePath + "�ļ�Ϊ�գ���������");
				return;
			}
			
			CatBean clientBean = new CatBean();
			clientBean.setType(2);
			clientBean.setName(name);
			clientBean.setTimer(CatUtil.getTimer());
			clientBean.setFileName(file.getName());
			clientBean.setInfo("�������ļ�");
			double __size = (double) file.length();
			clientBean.setSize(__size);				//ע����Ҫ���� �ļ���С 
			
			//�жϷ����ļ��Ķ��� , ��Ϊ����ӿڼ��ݣ�������������
			HashSet<String> set = new HashSet<String >();
			set.add(friend);
			clientBean.setClients(set);
			sendMessage(clientBean);
			
			
		}}});//btnNewButton.addActionListener
		
		//֪ͨʱ���ṩ�ļ����书��
		if(friend.equals(UserList.GroupChat))
			btnNewButton.setVisible(false);
	}//���췽��


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
			
			int result = JOptionPane.showConfirmDialog(this.getContentPane(), "ȷ��Ҫ�ر���" + this.friend + "�ĶԻ���");
			if(0 == result)
			{
				ul.ChatBoxes.remove(e.getSource());						//ʹ�ô�ܼ�ģʽ ��userlist�а��Լ�ȥ��
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
	
	//�Ի���õ�bean֮���ȡ�Ķ���
	public void getBean(final CatBean cBean)
	{
		switch(cBean.getType())
		{
		case 1:			//����
			String info = cBean.getTimer() + " " + cBean.getName() + "����˵��\r\n";
			textArea.append(info + cBean.getInfo() + "\r\n");
			textArea.selectAll();
			break;
		case 2: {
			//�к��� �����ļ�   ������е�����
			// ���ڵȴ�Ŀ��ͻ�ȷ���Ƿ�����ļ��Ǹ�����״̬�������������̴߳���
			new Thread() {
				public void run() {
					//��ʾ�Ƿ�����ļ��Ի���
					String __info = cBean.getInfo();
					int result = JOptionPane.showConfirmDialog(
							getContentPane(), __info);
					switch(result){
					case 0: {  //�����ļ�
						JFileChooser chooser = new JFileChooser();
						chooser.setDialogTitle("�����ļ���"); // ����Ŷ...
						//Ĭ���ļ����ƻ��з��ڵ�ǰĿ¼��
						chooser.setSelectedFile(new File(cBean
								.getFileName()));
						chooser.showDialog(getContentPane(), "����"); // ���ǰ�ť������..
						//����·��
						String saveFilePath =chooser.getSelectedFile().toString();
					
						//�����ͻ�CatBean
						CatBean clientBean = new CatBean();
						clientBean.setType(3);				//ȷ�������ļ�
						clientBean.setName(name);  //�����ļ��Ŀͻ�����
						clientBean.setTimer(CatUtil.getTimer());
						clientBean.setFileName(saveFilePath);
						clientBean.setInfo("ȷ�������ļ�");

						// �ж�Ҫ���͸�˭
						HashSet<String> set = new HashSet<String>();
						set.add(cBean.getName());			//��Դ			
						clientBean.setClients(set);  //�ļ���Դ
						clientBean.setTo(cBean.getClients());//����Щ�ͻ������ļ�����������������������������������������������������������������������������������
						
						
						
						// �����µ�tcp socket ��������, ���Ƕ������ӵĹ���, ���������... ����������������������������������������������������������������������
						try {
							ServerSocket ss = new ServerSocket(0); // 0���Ի�ȡ���еĶ˿ں�
							
							clientBean.setIp(clientSocket.getInetAddress().getHostAddress());
							clientBean.setPort(ss.getLocalPort());
							sendMessage(clientBean); // ��ͨ�����������߷��ͷ�, �����ֱ�ӷ����ļ�����������...
							
							
							
							isReceiveFile=true;
							//�ȴ��ļ���Դ�Ŀͻ��������ļ�....Ŀ��ͻ��������϶�ȡ�ļ�����д�ڱ�����
							Socket sk = ss.accept();
                            textArea.append(CatUtil.getTimer() + "  " + cBean.getFileName()
									+ "�ļ�������.\r\n");
							DataInputStream dis = new DataInputStream(  //�������϶�ȡ�ļ�
									new BufferedInputStream(sk.getInputStream()));
							DataOutputStream dos = new DataOutputStream(  //д�ڱ�����
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
									lblNewLabel.setText("���ؽ���:" + count
											+ "/" + cBean.getSize() + "  ����" + index
											+ "%");
								}else{
									lblNewLabel.setText("���ؽ���:" + count
											+ "/" + cBean.getSize() +"  ����:"+new Double(new Double(count).doubleValue()/new Double(cBean.getSize()).doubleValue()*100).intValue()+"%");
									if(count==cBean.getSize()){
										progressBar.setValue(100);
									}
								}
	
							}
							
							//���ļ���Դ�ͻ�������ʾ���ļ��������
							PrintWriter out = new PrintWriter(sk.getOutputStream(),true);
							out.println(CatUtil.getTimer() + " ���͸�"+name+"���ļ�[" + cBean.getFileName()+"]"
									+ "�ļ��������.\r\n");
							out.flush();
							dos.flush();
							dos.close();
							out.close();
							dis.close();
							sk.close();
							ss.close();
							textArea.append(CatUtil.getTimer() + "  " + cBean.getFileName()
									+ "�ļ��������.���λ��Ϊ:"+saveFilePath+"\r\n");
							isReceiveFile = false;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						break;
					}
					default: {		//��� "��" ���� "ȡ��" 
						CatBean clientBean = new CatBean();
						clientBean.setType(4);
						clientBean.setName(name);  //�����ļ��Ŀͻ�����
						clientBean.setTimer(CatUtil.getTimer());
						clientBean.setFileName(cBean.getFileName());
						clientBean.setInfo(CatUtil.getTimer() + "  "
								+ name + "ȡ�������ļ�["
								+ cBean.getFileName() + "]");

						isReceiveFile = false;
						// �ж�Ҫ���͸�˭
						HashSet<String> set = new HashSet<String>();
						set.add(cBean.getName());
						clientBean.setClients(set);  //�ļ���Դ
						clientBean.setTo(cBean.getClients());//����Щ�ͻ������ļ�
						
						sendMessage(clientBean);
					 	
						break;
					
					}
				}
				};	
			}.start();
			break;
		}
		case 3:
			 //Ŀ��ͻ�Ը������ļ���Դ�ͻ���ʼ��ȡ�����ļ������͵������� ��ͬ����Ҫʹ�� ���̷߳�����ֹ����
			textArea.append(cBean.getTimer() + "  "+ cBean.getName() + "ȷ�������ļ�" + ",�ļ�������..\r\n");
			new Thread(){
				public void run() {
					
					try {
						isSendFile = true;
						//����Ҫ�����ļ��Ŀͻ��׽��֣�ֱ������֮�� �����ļ����䣬���ھ���������
						Socket s = new Socket(cBean.getIp(),cBean.getPort());
						DataInputStream dis = new DataInputStream(
								new FileInputStream(filePath));  //���ض�ȡ�ÿͻ��ղ�ѡ�е��ļ�
						DataOutputStream dos = new DataOutputStream(
								new BufferedOutputStream(s
										.getOutputStream()));  //����д���ļ�
						
					
						int size = dis.available();
						
						double count = 0;
						int num = size / 100;
						int index = 0;
						while (count < size) {
							
							int t = dis.read();
							dos.write(t);
							count++;  //ÿ��ֻ��ȡһ���ֽ�

							if(num>0) {
								if (count % num < 5 && count % num > 0 && index < 100) {
									progressBar.setValue(++index);
								}
								lblNewLabel.setText("�ϴ�����:" + count + "/"
												+ size + "  ����" + index
												+ "%");
							} else {
								lblNewLabel.setText("�ϴ�����:" + count + "/"
										+ size +"  ����:"+new Double(new Double(count).doubleValue()/new Double(size).doubleValue()*100).intValue()+"%"
										);
								if(count==size){
									progressBar.setValue(100);
								}
							}
						}//while(count < size)
						dos.flush();
						dis.close();
					  //��ȡĿ��ͻ�����ʾ������ϵ���Ϣ...
					    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					    textArea.append( br.readLine() + "\r\n");//�����쳣
					    isSendFile = false;
						br.close();
					    s.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				
				};
			}.start();
			break;
		case 4:								//�Է�ȡ�������ļ�
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