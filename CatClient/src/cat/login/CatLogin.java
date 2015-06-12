package cat.login;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import cat.client.CatChatroom;
import cat.client.UserList;
import cat.function.CatBean;
import cat.function.ClientBean;
import cat.util.CatUtil;

public class CatLogin extends JFrame {

	
	private JPanel contentPane;				//��JPanel �Ϸ���component
	private JTextField textField;			//�˺�
	private JPasswordField passwordField;	//����
	public static HashMap<String, ClientBean> onlines;	//�����û� Map

	
	////////////////////////////
	private final String deny_Nick = "NoNickName";
	private final String deny_Pwd = "WrongPwd";
	private final String admit = "Yes";
	///////////////////////////
	/**
	 * Launch the application.
	 * http://bbs.csdn.net/topics/360007320
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// ������½����
					CatLogin frame = new CatLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the Login frame.
	 */
	public CatLogin() {
		setTitle("Landing cat chat room\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 250, 450, 300);
		this.setResizable(false);
		
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(
						"images\\\u767B\u9646\u754C\u9762.jpg").getImage(), 0,
						0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//�� �˺š������� ����contentPanel ��
		textField = new JTextField();
		textField.setBounds(128, 153, 104, 21);
		textField.setOpaque(false);
		contentPane.add(textField);
		textField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setForeground(Color.BLACK);
		passwordField.setEchoChar('*');
		passwordField.setOpaque(false);
		passwordField.setBounds(128, 189, 104, 21);
		contentPane.add(passwordField);
		
		//��¼ ע�ᰴť
		final JButton btnNewButton = new JButton();
		btnNewButton.setIcon(new ImageIcon("images\\\u767B\u9646.jpg"));
		btnNewButton.setBounds(246, 227, 50, 25);
		getRootPane().setDefaultButton(btnNewButton);
		contentPane.add(btnNewButton);

		final JButton btnNewButton_1 = new JButton();
		btnNewButton_1.setIcon(new ImageIcon("images\\\u6CE8\u518C.jpg"));
		btnNewButton_1.setBounds(317, 227, 50, 25);
		contentPane.add(btnNewButton_1);

		// ��ʾ��Ϣ
		final JLabel lblNewLabel = new JLabel();
		lblNewLabel.setBounds(60, 220, 151, 21);
		lblNewLabel.setForeground(Color.red);
		getContentPane().add(lblNewLabel);
		
		final JButton btnNewButton_2 = new JButton("\u5FD8\u8BB0\u5BC6\u7801\uFF1F");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton_2.setEnabled(false);
				CatReset cr = new CatReset();
				cr.setVisible(true);
				setVisible(false);
			}
		});
		btnNewButton_2.setBounds(274, 114, 93, 23);
		contentPane.add(btnNewButton_2);

		// ������½��ť
		//����ط��е�ӵ��� �ͻ���ֱ��ʵ�ڱ������ļ��в���û���������
		//�뷨���ڵ�¼��ť�� ���ͻ������ �˺� ������ ���͵���������ʹ�÷������� �������ݿⲢ���ؽ��
		//mode = 1
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				Properties userPro = new Properties();
				File file = new File("Users.properties");
				CatUtil.loadPro(userPro, file);
				*/
				
				//ȥ���� �˺ŷ����� ���ݷ���ֵ ���ж� �Ƿ�ȥ���� CatServer
				String u_name = textField.getText();
				u_name = u_name.trim();
				String u_pwd = new String(passwordField.getPassword());
				
				//String toAccountServer = "";
				String toAccountServer = (u_name + "=" + u_pwd + "=");
				toAccountServer += "mode=1";
				
				String ans = null;
				
				try {
					Socket s = new Socket("localhost",8000);
					DataInputStream dis = new DataInputStream(s.getInputStream());
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());
					dos.writeUTF(toAccountServer);
					dos.flush();
					ans = dis.readUTF();
					//System.out.println(ans);
				} catch (UnknownHostException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				if (ans != null) 
				{
					if (ans.equals(admit)) 
					{
						//ʹ���׽������ӵ� ����������
						try {
							Socket client = new Socket("localhost", 8520);

							btnNewButton.setEnabled(false);
							//CatChatroom frame = new CatChatroom(u_name,
							//			client);
							UserList frame = new UserList(u_name, client);
							frame.setVisible(true);// ��ʾ�������
							setVisible(false);// ���ص���½����

						} catch (UnknownHostException e1) {
								// TODO Auto-generated catch block
							errorTip("The connection with the server is interrupted, please login again");
						} catch (IOException e1) {
								// TODO Auto-generated catch block
							errorTip("The connection with the server is interrupted, please login again");
						}

					} 
					else if(ans.equals(deny_Pwd)) 
					{
							lblNewLabel.setText("���������������");
							textField.setText("");
							passwordField.setText("");
							textField.requestFocus();	
					}
					else 
					{
						lblNewLabel.setText("�������ǳƲ����ڣ�");
						textField.setText("");
						passwordField.setText("");
						textField.requestFocus();
					}
				} 
				else 
				{
					lblNewLabel.setText("�������ǳƲ����ڣ�");
					textField.setText("");
					passwordField.setText("");
					textField.requestFocus();
				}
		}
		});

		//ע�ᰴť����
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton_1.setEnabled(false);
				CatResign frame = new CatResign();
				frame.setVisible(true);// ��ʾע�����
				setVisible(false);// ʹ���ⲿ�� ���ص���½����
			}
		});
	}//���췽��

	protected void errorTip(String str) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(contentPane, str, "Error Message",
				JOptionPane.ERROR_MESSAGE);
		textField.setText("");
		passwordField.setText("");
		textField.requestFocus();
	}
}