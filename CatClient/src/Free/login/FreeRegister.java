package Free.login;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;

import jdk.internal.org.objectweb.asm.util.Textifiable;
import Free.util.FreeUtil;

// resign �����ʾ ע���û���,Ӧ����register
public class FreeRegister extends JFrame {
	
	private static String host = new String("localhost");

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel lblNewLabel;
	////////////////////////////////////////////////////
	private final String deny_Nick = "NoNickName";
	private final String deny_Pwd = "WrongPwd";
	private final String admit = "Yes";
	private final String OffLine = "������";
	private final String illegal = "�������Ϸ�";
	private JTextField textField_1;
	////////////////////////////////////////////////////
	public FreeRegister() {
		setTitle("Registered cat chat room\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 250, 450, 300);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images/register/boundary.jpg").getImage(), 0,0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(175, 91, 104, 20);
		textField.setOpaque(false);
		contentPane.add(textField);
		textField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		passwordField.setOpaque(false);
		passwordField.setBounds(175, 122, 104, 20);
		contentPane.add(passwordField);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(175, 155, 104, 20);
		passwordField_1.setOpaque(false);
		contentPane.add(passwordField_1);

		//ע�ᰴť
		final JButton btnNewButton_1 = new JButton();
		btnNewButton_1.setIcon(new ImageIcon("D:\\Program Files (x86)\\git Repostitory\\CatClient\\images\\LoginIamge1_2\\\u6CE8\u518C1_2.jpg"));
		btnNewButton_1.setBounds(335, 109, 80, 33);
		getRootPane().setDefaultButton(btnNewButton_1);
		contentPane.add(btnNewButton_1);

		//���ذ�ť
		final JButton btnNewButton = new JButton("");
		btnNewButton.setIcon(new ImageIcon("D:\\Program Files (x86)\\git Repostitory\\CatClient\\images\\register\\return.jpg"));
		btnNewButton.setBounds(335, 157, 80, 33);
		contentPane.add(btnNewButton);

		//��ʾ��Ϣ
		lblNewLabel = new JLabel();
		lblNewLabel.setBounds(230, 232, 185, 20);
		lblNewLabel.setForeground(Color.red);
		contentPane.add(lblNewLabel);
		
		textField_1 = new JTextField();
		textField_1.setBounds(175, 189, 104, 20);
		textField_1.setOpaque(false);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		//���ذ�ť����
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(false);
				//���ص�½���� ԭ�����Ǹ� Catlogin �������� ����һ���µ�
				FreeLogin frame = new FreeLogin();
				frame.setVisible(true);
				setVisible(false);
			}
		});
		
		//ע�ᰴť����,ͬ���ķ��� ���û��� ���봫�� �˻�������
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String u_name = textField.getText();
				String u_pwd = new String(passwordField.getPassword());
				String u_pwd_ag = new String(passwordField_1.getPassword());
				String u_father = new String(textField_1.getText());
				
				u_name = u_name.trim();
				u_father = u_father.trim();
				// ���� �˻�ע��
				if (u_name.length() != 0 && u_father.length() != 0)
				{
					/*if (userPro.containsKey(u_name)) {
						lblNewLabel.setText("�û����Ѵ���!");
					} else {
						isPassword(userPro, file, u_name, u_pwd, u_pwd_ag);
					}*/
					int res = isPassword(u_name ,u_pwd ,u_pwd_ag, u_father);
					if(1==res)
					{
						//System.out.println("�û�ע��ɹ���");
						//JOptionPane.showInputDialog(null, "�û�ע��ɹ���");
						JOptionPane.showInternalMessageDialog(contentPane, "�˺�ע��ɹ���",
								"information", JOptionPane.INFORMATION_MESSAGE);


						FreeLogin frame = new FreeLogin();
						frame.setVisible(true);
						setVisible(false);
					}
					else if(-2==res)
					{
						lblNewLabel.setText("�������벻һ�£�");
					}
					else if(-1==res)
					{
						lblNewLabel.setText("���û����ѱ�ע�ᣡ");
					}
					else if(-3==res)
					{
						lblNewLabel.setText("���벻��Ϊ��");
					}
					else if(-4 == res)
					{
						lblNewLabel.setText("�û��������Ƿ��ַ�");
					}
				} 
				else 
				{
					lblNewLabel.setText("�û����͸�����������Ϊ�գ�");
				}
			}

			
			/**ʹ��Properties ����洢�û���������
			 *	���� ִ�гɹ� �����ɹ��ؽ��˺� ���� ���� �������ˣ�����1 
			*	���� �û�����ע�� ���� -1
			*	  �������벻һ�·��� -2
			*		 ����Ϊ�� ���� -3
			*		�����Ƿ��ַ� ���� -4
				�����쳣 ���� 100
			*/  
			private int isPassword(String u_name, String u_pwd, String u_pwd_ag, String u_father) {
				if (u_pwd.equals(u_pwd_ag)) {
					if (u_pwd.length() != 0) {		//������Ч
						//userPro.setProperty(u_name, u_pwd_ag);
						
						//���� �����ַ���///////////////////////////////////////////////
						String toAccountServer = (u_name + "=" + u_pwd + "=");
						toAccountServer += "mode=2";
						String ans = null;
						
						try {
							/*userPro.store(new FileOutputStream(file),
									"Copyright (c) Boxcode Studio,Improved by xdu");*/
							Socket s = new Socket(host,8000);
							DataInputStream dis = new DataInputStream(s.getInputStream());
							DataOutputStream dos = new DataOutputStream(s.getOutputStream());
							dos.writeUTF(toAccountServer);
							dos.flush();
							ans = dis.readUTF();
							
							//s.close();
							if(ans.equals(admit))		//�û�ע��ɹ� ,�ڷ����������Ӹ�����Ϣ,֮��Ӧ�÷��ص���¼����
							{	
								dos.writeUTF(u_father);
								dos.flush();
								s.close();
								return 1;
							}
							else if(ans.equals(illegal))	//�û��������Ƿ��ַ�
							{
								return -4;
							}
							else						//���û��� �Ѵ���
							{
								s.close();
								return -1;
							}
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						//btnNewButton_1.setEnabled(false);
						//���ص�½����
						/*CatLogin frame = new CatLogin();
						frame.setVisible(true);
						setVisible(false);*/
						return 100;							//Ӧ�� ��ִ�в�����һ�е�,���ǳ����쳣
					} else {
						//lblNewLabel.setText("����Ϊ�գ�");
						return -3;
					}
				} else {
					//lblNewLabel.setText("���벻һ�£�");
					return -2;
				}
				//return 0;
			}//private isPassword
		});//button1 add action listener
	}//public catResign 
}
