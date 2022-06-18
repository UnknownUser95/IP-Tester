package net.unknownuser.iptester;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

public class TesterGUI extends JFrame {
	
	private JPanel contentPane;
	private JTextField textIP;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				TesterGUI frame = new TesterGUI();
				frame.setVisible(true);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public TesterGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 290, 175);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textIP = new JTextField();
		textIP.setText("127.0.0.1/16");
		textIP.setForeground(new Color(0, 0x88, 0));
		textIP.setBounds(12, 12, 114, 21);
		contentPane.add(textIP);
		textIP.setColumns(10);
		
		JLabel labelBinIP = new JLabel("");
		labelBinIP.setFont(new Font("Liberation Mono", Font.PLAIN, 12));
		labelBinIP.setBounds(20, 50, 255, 20);
		contentPane.add(labelBinIP);
		
		JLabel labelBinNetMask = new JLabel("");
		labelBinNetMask.setFont(new Font("Liberation Mono", Font.PLAIN, 12));
		labelBinNetMask.setBounds(20, 70, 255, 20);
		contentPane.add(labelBinNetMask);
		
		JLabel labelBinHostAddress = new JLabel("");
		labelBinHostAddress.setFont(new Font("Liberation Mono", Font.PLAIN, 12));
		labelBinHostAddress.setBounds(20, 90, 255, 20);
		contentPane.add(labelBinHostAddress);
		
		JLabel labelBinNetAddress = new JLabel("");
		labelBinNetAddress.setFont(new Font("Liberation Mono", Font.PLAIN, 12));
		labelBinNetAddress.setBounds(20, 110, 255, 20);
		contentPane.add(labelBinNetAddress);
		
		JPanel panelHighlightBackground = new JPanel();
		panelHighlightBackground.setBackground(Color.GREEN);
		panelHighlightBackground.setBounds(15, 45, 260, 85);
		contentPane.add(panelHighlightBackground);
		
		JPanel panelStandardBackground = new JPanel();
		panelStandardBackground.setBackground(Color.CYAN);
		panelStandardBackground.setBounds(15, 45, 260, 85);
		contentPane.add(panelStandardBackground);
		
		Runnable update = () -> {
			// the text needs time to update
			// putting this in a keylistener is too fast
			try {
				Thread.sleep(1);
			} catch(InterruptedException ignore) {}
			
			String ipStr = textIP.getText();
			boolean validIP = IPTester.test(ipStr);
			
			textIP.setForeground((validIP) ? new Color(0, 0x88, 0) : Color.RED);
			
			if(validIP) {
				int ip = toInt(ipStr);
				int netmask = getNetMask(Integer.parseInt(ipStr.substring(ipStr.indexOf('/') + 1)));
				int netID = ip & netmask;
				int hostID = ip & (~netmask);
				
				labelBinIP.setText(insertDots(ip));
				labelBinNetMask.setText(insertDots(netmask));
				labelBinNetAddress.setText(insertDots(netID));
				labelBinHostAddress.setText(insertDots(hostID));
				
				String mask = insertDots(netmask);
				int width = insertDots(netmask).indexOf('0') * 7 + 5;
				int zeroIndex = mask.indexOf('0');
				System.out.println(zeroIndex);
				if(zeroIndex == 9 || zeroIndex == 18 || zeroIndex == 27) {
					width -= 4;
				}
				panelHighlightBackground.setBounds(panelHighlightBackground.getX(), panelHighlightBackground.getY(), width, panelHighlightBackground.getHeight());
			} else {
				// reset content
				labelBinIP.setText("");
				labelBinNetMask.setText("");
				labelBinNetAddress.setText("");
				labelBinHostAddress.setText("");
				
				panelHighlightBackground.setBounds(panelHighlightBackground.getX(), panelHighlightBackground.getY(), 260, panelHighlightBackground.getHeight());
			}
		};
		
		textIP.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				new Thread(update).start();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		
		new Thread(update).start();
	}
	
//	private static String toBinaryIP(String ip) {
//		if(ip.contains("/")) {
//			ip = ip.substring(0, ip.indexOf('/'));
//		}
//		StringBuilder sb = new StringBuilder();
//		
//		for(String str : ip.split("[.]")) {
//			String bin = Integer.toBinaryString(Integer.parseInt(str));
//			bin = "0".repeat(8 - bin.length()) + bin;
//			sb.append(bin);
//			sb.append('.');
//		}
//		sb.deleteCharAt(sb.length() - 1);
//		
//		return sb.toString();
//	}
	
	private static String insertDots(int ip) {
		StringBuilder sb = new StringBuilder();
		String ipStr = Integer.toBinaryString(ip);
		sb.append("0".repeat(32 - ipStr.length()));
		sb.append(ipStr);
		
		sb.insert(24, ".");
		sb.insert(16, ".");
		sb.insert(8, ".");
		
		return sb.toString();
	}
	
	private static int toInt(String ip) {
		if(ip.contains("/")) {
			ip = ip.substring(0, ip.indexOf('/'));
		}
		
		int result = 0;
		String[] parts = ip.split("[.]");
		
		for(int i = 3; i >= 0; i--) {
			long res = (Integer.parseInt(parts[i]) << ((3 - i) * 8));
			result += res;
		}
		
		return result;
	}
	
	private static int getNetMask(int masklength) {
		String mask = "1".repeat(masklength) + "0".repeat(32 - masklength);
		int res = 0;
		
		for(int i = 0; i < 32; i++) {
			if(mask.charAt(i) == '1') {
				res += (int) Math.round(Math.pow(2, 31 - i));
			}
		}
		
		return res;
	}
}

@FunctionalInterface
interface NoInput {
	public void apply();
}