package cat.login;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class CatReset extends JFrame {
	private JTextField textField;
	private JTextField textField_1;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	
	private final static String deny_Nick = "NoNickName";
	private final static String deny_Pwd = "WrongPwd";
	private final static String admit = "Yes";
	private final static String UnRead = "未读消息";
	private final static String OffLine = "已下线";
	private final static String illegal = "命名不合法";
	private final static String resetSuccess = "修改密码成功！";
	private final static String wa = "The answer is wrong!";
	
	public CatReset() {
		setForeground(Color.ORANGE);
		setBounds(350, 250, 350, 417);
		setResizable(false);
		
		setTitle("\u91CD\u7F6E\u5BC6\u7801");
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel label = new JLabel("\u8D26\u53F7\u540D\u79F0\uFF1A");
		label.setBounds(31, 34, 87, 15);
		getContentPane().add(label);
		
		textField = new JTextField();
		textField.setBounds(147, 31, 66, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		final JLabel lblNewLabel = new JLabel("\u7236\u4EB2\u59D3\u540D\uFF1A");
		lblNewLabel.setBounds(31, 89, 76, 15);
		getContentPane().add(lblNewLabel);
		
		textField_1 = new JTextField();
		textField_1.setBounds(147, 86, 66, 21);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JLabel label_1 = new JLabel("\u65B0\u5BC6\u7801\uFF1A");
		label_1.setBounds(31, 151, 54, 15);
		getContentPane().add(label_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(147, 148, 125, 21);
		getContentPane().add(passwordField);
		
		JLabel lblNewLabel_1 = new JLabel("\u786E\u8BA4\u5BC6\u7801\uFF1A");
		lblNewLabel_1.setBounds(31, 224, 76, 15);
		getContentPane().add(lblNewLabel_1);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(147, 221, 125, 21);
		getContentPane().add(passwordField_1);
		
		final JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setForeground(Color.RED);
		lblNewLabel_2.setBounds(42, 348, 256, 31);
		getContentPane().add(lblNewLabel_2);
		
		
		Button button = new Button("\u786E\u8BA4");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String u_name = textField.getText();
				String u_father = textField_1.getText();
				String u_pwd = new String(passwordField.getPassword() );
				String u_pwd_ag = new String(passwordField_1.getPassword() );
				
				u_name = u_name.trim();
				u_father = u_father.trim();
				
				if(0 == u_name.length() || 0 == u_father.length())
				{
					lblNewLabel_2.setText("用户名和用户父亲姓名均不能为空！");
				}
				else if(!u_pwd.equals(u_pwd_ag))
				{
					lblNewLabel_2.setText("两次密码不一致！");
				}//else if
				else
				{
					int res = isReset(u_name, u_pwd, u_father);
					if(-1==res) 
						lblNewLabel_2.setText("该账号不存在！");
					else if(-2 == res) 
						lblNewLabel_2.setText("问题回答错误！");
					else
					{
						JOptionPane.showMessageDialog(getParent(), "修改密码成功！");
						try {
							Thread.sleep(200);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						CatLogin cl = new CatLogin();
						cl.setVisible(true);
						setVisible(false);
					}//else
				}//else
			}
		});
		button.setBounds(42, 305, 76, 23);
		getContentPane().add(button);
		
		
		final Button button_1 = new Button("\u8FD4\u56DE");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button_1.setEnabled(false);
				//返回登陆界面 原来的那个 Catlogin 对象不用了 创建一个新的
				CatLogin frame = new CatLogin();
				frame.setVisible(true);
				setVisible(false);
			}
		});
		button_1.setBounds(147, 305, 76, 23);
		getContentPane().add(button_1);
		

	}//construct 
	/**
	 * 
	 * @param u_name 用户名
	 * @param u_pwd	用户新密码
	 * @param u_father 用户父亲 姓名
	 * @return -1：账号不存在 -2：问题回答错误 0：密码充值成功
	 */
	private static int isReset(String u_name, String u_pwd, String u_father)
	{
		//构造连接字符串
		String toAccountServer = (u_name + "=" + u_pwd + "=");
		String ans = new String();
		
		toAccountServer += "mode=3=";
		toAccountServer += (u_father);
		try {
			Socket s = new Socket("localhost", 8000);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			dos.writeUTF(toAccountServer);
			dos.flush();
			ans = dis.readUTF();
			
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(ans.equals(deny_Nick))//账号错误
			return -1;
		else if(ans.equals(wa))//问题回答错误
			return -2;
		else				//问题回答正确
			return 0;
	}

}