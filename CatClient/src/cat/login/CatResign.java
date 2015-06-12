package cat.login;

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
import cat.util.CatUtil;

// resign �����ʾ ע���û���,Ӧ����register
public class CatResign extends JFrame {

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
	public CatResign() {
		setTitle("Registered cat chat room\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 250, 450, 300);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\\u6CE8\u518C\u754C\u9762.jpg").getImage(), 0,0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(150, 42, 104, 21);
		textField.setOpaque(false);
		contentPane.add(textField);
		textField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		passwordField.setOpaque(false);
		passwordField.setBounds(192, 95, 104, 21);
		contentPane.add(passwordField);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(192, 143, 104, 21);
		passwordField_1.setOpaque(false);
		contentPane.add(passwordField_1);

		//ע�ᰴť
		final JButton btnNewButton_1 = new JButton();
		btnNewButton_1.setIcon(new ImageIcon("images\\ע��1.jpg"));
		btnNewButton_1.setBounds(320, 198, 80, 40);
		getRootPane().setDefaultButton(btnNewButton_1);
		contentPane.add(btnNewButton_1);

		//���ذ�ť
		final JButton btnNewButton = new JButton("");
		btnNewButton.setIcon(new ImageIcon("images\\����.jpg"));
		btnNewButton.setBounds(230, 198, 80, 40);
		contentPane.add(btnNewButton);

		//��ʾ��Ϣ
		lblNewLabel = new JLabel();
		lblNewLabel.setBounds(55, 218, 185, 20);
		lblNewLabel.setForeground(Color.red);
		contentPane.add(lblNewLabel);
		
		textField_1 = new JTextField();
		textField_1.setBounds(192, 174, 66, 21);
		textField_1.setOpaque(false);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel label = new JLabel("\u7236\u4EB2\u59D3\u540D");
		label.setBounds(54, 177, 66, 15);
		contentPane.add(label);
		
		//���ذ�ť����
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(false);
				//���ص�½���� ԭ�����Ǹ� Catlogin �������� ����һ���µ�
				CatLogin frame = new CatLogin();
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


						CatLogin frame = new CatLogin();
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
				} 
				else 
				{
					lblNewLabel.setText("�û����͸�����������Ϊ�գ�");
				}
			}

			
			//ʹ��Properties ����洢�û���������
			//���� ִ�гɹ� �����ɹ��ؽ��˺� ���� ���� �������ˣ�����1 
			//���� �û�����ע�� ���� -1
			//   �������벻һ�·��� -2
			//	 ����Ϊ�� ���� -3
			//  �����쳣 ���� 100
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
							Socket s = new Socket("localhost",8000);
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
