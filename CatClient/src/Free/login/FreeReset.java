package Free.login;

import java.awt.Button;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

public class FreeReset extends JFrame {
	
	private static String host = "localhost";
	
	private JTextField textField;
	private JTextField textField_1;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JPanel contentPane;
	private final static String deny_Nick = "NoNickName";
	private final static String deny_Pwd = "WrongPwd";
	private final static String admit = "Yes";
	private final static String UnRead = "未读消息";
	private final static String OffLine = "已下线";
	private final static String illegal = "命名不合法";
	private final static String resetSuccess = "修改密码成功！";
	private final static String wa = "The answer is wrong!";
	
	
	public FreeReset() {
		
		contentPane = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(
						"images/ultimate/reset.jpg").getImage(), 0,
						0, getWidth(), getHeight(), null);
			}
		};
		
		this.setContentPane(contentPane);
		setForeground(Color.ORANGE);
		setBounds(350, 250, 350, 417);
		setResizable(false);
		
		setTitle("\u91CD\u7F6E\u5BC6\u7801");
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		textField = new JTextField();
		textField.setBounds(158, 59, 66, 23);
		getContentPane().add(textField);
		textField.setColumns(10);
		textField.setOpaque(false);
		
		textField_1 = new JTextField();
		textField_1.setBounds(158, 110, 66, 23);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		textField_1.setOpaque(false);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(158, 166, 125, 23);
		passwordField.setOpaque(false);
		getContentPane().add(passwordField);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(158, 215, 125, 23);
		passwordField_1.setOpaque(false);
		getContentPane().add(passwordField_1);
		
		final JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setForeground(Color.RED);
		lblNewLabel_2.setBounds(42, 348, 256, 31);
		getContentPane().add(lblNewLabel_2);
		
		
		JButton button = new JButton("");
		button.setIcon(new ImageIcon("D:\\Program Files (x86)\\git Repostitory\\CatClient\\images\\ultimate\\register_confirm.jpg"));
		button.setActionCommand("");
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
					else if(0 == res)
					{
						JOptionPane.showMessageDialog(getParent(), "修改密码成功！");
						try {
							Thread.sleep(200);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						FreeLogin cl = new FreeLogin();
						cl.setVisible(true);
						setVisible(false);
					}//else
					else
					{
						JOptionPane.showMessageDialog(getParent(), "与服务器链接失败，请稍后再试！", "Error Message",
								JOptionPane.ERROR_MESSAGE);
					}
				}//else
			}
		});
		button.setBounds(42, 270, 76, 23);
		getContentPane().add(button);
		
		
		final JButton button_1 = new JButton("");
		button_1.setIcon(new ImageIcon("D:\\Program Files (x86)\\git Repostitory\\CatClient\\images\\ultimate\\register_return.jpg"));
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button_1.setEnabled(false);
				//返回登陆界面 原来的那个 Catlogin 对象不用了 创建一个新的
				FreeLogin frame = new FreeLogin();
				frame.setVisible(true);
				setVisible(false);
			}
		});
		button_1.setBounds(42, 315, 76, 23);
		getContentPane().add(button_1);
		

	}//construct 
	/**
	 * 
	 * @param u_name 用户名
	 * @param u_pwd	用户新密码
	 * @param u_father 用户父亲 姓名
	 * @return -1：账号不存在 -2：问题回答错误 0：密码充值成功 -3服务器连接失败
	 */
	private static int isReset(String u_name, String u_pwd, String u_father)
	{
		//构造连接字符串
		String toAccountServer = (u_name + "=" + u_pwd + "=");
		String ans = new String();
		
		toAccountServer += "mode=3=";
		toAccountServer += (u_father);
		try {
			Socket s = new Socket(host, 8000);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			dos.writeUTF(toAccountServer);
			dos.flush();
			ans = dis.readUTF();
			
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -3;
		}
		
		if(ans.equals(deny_Nick))//账号错误
			return -1;
		else if(ans.equals(wa))//问题回答错误
			return -2;
		else				//问题回答正确
			return 0;
	}

}