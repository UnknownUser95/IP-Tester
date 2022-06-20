package net.unknownuser.iptester;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class TesterGUIScaled extends JFrame {
	
	private JPanel contentPane;
	private JTextField textIP;
	
	private boolean isSupernetting = false;
	private boolean isValidIP = false;
	
	private String ipStr = "";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				TesterGUIScaled frame = new TesterGUIScaled();
				frame.setVisible(true);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public TesterGUIScaled() {
		int labelWidth = 1150;
		
		setTitle("IP tester");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, labelWidth + 20, 345);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textIP = new JTextField();
		textIP.setFont(new Font("Liberation Sans", Font.PLAIN, 36));
		textIP.setText("192.0.0.1/24");
		textIP.setForeground(new Color(0, 0x88, 0));
		textIP.setBounds(20, 20, 342, 63);
		contentPane.add(textIP);
		textIP.setColumns(10);
		
		JLabel labelBinIP = new JLabel("");
		labelBinIP.setFont(new Font("Liberation Mono", Font.PLAIN, 36));
		labelBinIP.setBounds(30, 103, labelWidth, 40);
		contentPane.add(labelBinIP);
		
		JLabel labelBinNetMask = new JLabel("");
		labelBinNetMask.setFont(new Font("Liberation Mono", Font.PLAIN, 36));
		labelBinNetMask.setBounds(30, 153, labelWidth, 40);
		contentPane.add(labelBinNetMask);
		
		JLabel labelBinHostAddress = new JLabel("");
		labelBinHostAddress.setFont(new Font("Liberation Mono", Font.PLAIN, 36));
		labelBinHostAddress.setBounds(30, 203, labelWidth, 40);
		contentPane.add(labelBinHostAddress);
		
		JLabel labelBinNetAddress = new JLabel("");
		labelBinNetAddress.setFont(new Font("Liberation Mono", Font.PLAIN, 36));
		labelBinNetAddress.setBounds(30, 253, labelWidth, 40);
		contentPane.add(labelBinNetAddress);
		
		JPanel panelHighlightBackground = new JPanel();
		panelHighlightBackground.setBackground(Color.GREEN);
		panelHighlightBackground.setBounds(20, labelBinIP.getY() - 10, 790, 200);
		contentPane.add(panelHighlightBackground);
		
		JPanel panelStandardBackground = new JPanel();
		panelStandardBackground.setBackground(Color.CYAN);
		panelStandardBackground.setBounds(20, labelBinIP.getY() - 10, 790, 200);
		contentPane.add(panelStandardBackground);
		
		JLabel labelInfo = new JLabel("");
		labelInfo.setFont(new Font("Liberation Mono", Font.PLAIN, 36));
		labelInfo.setBounds(374, 20, 484, 63);
		contentPane.add(labelInfo);
		
		JButton btnShowSubnets = new JButton("show subnets");
		btnShowSubnets.setFont(new Font("Liberation Sans", Font.PLAIN, 36));
		btnShowSubnets.setBounds(816, 12, 342, 71);
		contentPane.add(btnShowSubnets);
		
		Runnable update = () -> {
			// the text needs time to update
			// putting this in a keylistener is too fast
			try {
				Thread.sleep(1);
			} catch(InterruptedException ignore) {}
			
			String ipText = textIP.getText();
			// isSuperneting will reset labelInfo
			// has to be prevented when using default mask
			boolean skipReset = false;
			
//			if(Pattern.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}", ipText)) {
//				System.out.println("is IP");
//			}
			
			if(IPMethods.isInteger(ipText) || ipText.isBlank() || Pattern.matches("[a-zA-Z]*", ipText)) {
				isValidIP = false;
				textIP.setForeground(Color.RED);
				return;
			}
			
			// no mask given, use default
			try {
				if(!ipText.contains("/")) {
					ipText = ipText + "/" + IPMethods.getDefaultNetmask(Integer.parseInt(ipText.substring(0, ipText.indexOf('.'))));
					labelInfo.setText("using default mask");
					skipReset = true;
				}
			} catch(NumberFormatException exc) {
				isValidIP = false;
			}
			
			isValidIP = IPTester.test(ipText);
			
			textIP.setForeground((isValidIP) ? new Color(0, 0x88, 0) : Color.RED);
			
			if(isValidIP) {
				// update the saved IP
				ipStr = ipText;
				// get the IP and network mask
				int ip = IPMethods.ipToInt(ipStr);
				String netmaskStr = ipStr.substring(ipStr.indexOf('/') + 1);
				int netmask = IPMethods.getNetMask(Integer.parseInt(netmaskStr));
				// logical AND for network ID and host ID
				int netID = ip & netmask;
				int hostID = ip & (~netmask);
				
				// content of labels
				labelBinIP.setText(IPMethods.insertDots(ip) + " IP");
				labelBinNetMask.setText(IPMethods.insertDots(netmask) + " Netmask");
				labelBinHostAddress.setText(IPMethods.insertDots(hostID) + " Host address");
				labelBinNetAddress.setText(IPMethods.insertDots(netID) + " Network address");
				
				// network mask, but with dots
				String mask = IPMethods.insertDots(netmask);
				// width of the marker
				int width = IPMethods.insertDots(netmask).indexOf('0') * 22 + 10;
				int zeroIndex = mask.indexOf('0');
				// different width in case only a dot is counted
				if(zeroIndex == 9 || zeroIndex == 18 || zeroIndex == 27) {
					width -= 22;
				}
				panelHighlightBackground.setBounds(panelHighlightBackground.getX(), panelHighlightBackground.getY(), width, panelHighlightBackground.getHeight());
				
				// whether the used IP address is supernetting
				int defaultMask = IPMethods.getDefaultNetmask(Integer.parseInt(ipStr.substring(0, ipStr.indexOf('.'))));
				if(defaultMask > Integer.parseInt(netmaskStr)) {
					// supernetting
					labelInfo.setText("supernetting");
					isSupernetting = true;
				} else {
					// not supernetting
					if(!skipReset) {
						labelInfo.setText("");
					}
					isSupernetting = false;
				}
			} else {
				// reset content, invalid IP
				labelBinIP.setText("");
				labelBinNetMask.setText("");
				labelBinHostAddress.setText("");
				labelBinNetAddress.setText("");
				
				labelInfo.setText("invalid IP");
				
				panelHighlightBackground.setBounds(panelHighlightBackground.getX(), panelHighlightBackground.getY(), 790, panelHighlightBackground.getHeight());
			}
		};
		
		textIP.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				new Thread(update).start();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// not needed
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// not needed
			}
		});
		
		btnShowSubnets.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!isValidIP || !ipStr.equals(textIP.getText())) {
					// if the IP is invalid there can't be any subnets
					return;
				}
				
				if(isSupernetting) {
					// supernetting is not supported for subnets
					labelInfo.setText("not supported");
					return;
				}
				
				// some default stuff
				// get IP and mask
				String netmaskStr = ipStr.substring(ipStr.indexOf('/') + 1);
				// turn them into integers
				int netmask = IPMethods.getNetMask(Integer.parseInt(netmaskStr));
				int ip = IPMethods.ipToInt(ipStr);
				
				// filter useless network masks
				if(netmask == IPMethods.getNetMask(0) || netmask == IPMethods.getNetMask(32)) {
					labelInfo.setText("useless");
					return;
				}
				
				// get the default mask and difference
				int defaultMaskLength = IPMethods.getDefaultNetmask(IPMethods.binIntStrToInt(IPMethods.getIP(ip).substring(0, 8)));
				int diffInt = IPMethods.getNetMask(defaultMaskLength) ^ netmask;
				
				// if there is no difference there are no subnets
				if(diffInt != 0) {
					// display for all subnets
					SubnetShow subnets = new SubnetShow(ip, netmask);
					subnets.setVisible(true);
				} else {
					// info about having to subnets
					labelInfo.setText("no subnets");
				}
			}
		});
		
		new Thread(update).start();
	}
}