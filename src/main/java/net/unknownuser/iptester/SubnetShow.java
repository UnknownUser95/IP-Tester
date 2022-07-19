package net.unknownuser.iptester;

import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class SubnetShow extends JFrame {
	
	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	public SubnetShow(int ip, int netmask) {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		// the IP address in binary form
		String binIP = Integer.toBinaryString(ip);
		binIP = "0".repeat(32 - binIP.length()) + binIP;
		
		// the default length of the network mask
		int defaultMaskLength = IPMethods.getDefaultNetmask(IPMethods.binIntStrToInt(binIP.substring(0, 8)));
		
		// the difference of the default network mask and the given network mask
		int diffInt = IPMethods.getNetMask(defaultMaskLength) ^ netmask;
		String diffStr = Integer.toBinaryString(diffInt);
		int diffLength = diffStr.substring(diffStr.indexOf('1'), diffStr.lastIndexOf('1') + 1).length();
		
		// the list for all entries
		DefaultListModel<String> list = new DefaultListModel<>();
		
		int subnets = (int) Math.round(Math.pow(2, diffLength));
		int hostsPerSubnet = (int) Math.round(Math.pow(2, diffStr.substring(diffStr.lastIndexOf('1') + 1).length()) - 2);
		int diffPerSubnet = (int) Math.round(Math.pow(2, (diffStr.length() - diffStr.lastIndexOf('1') - 1) % 8));
		list.addElement(String.format("%d subnets, %d hosts per subnet, %d diff per subnet", subnets, hostsPerSubnet, diffPerSubnet));
		
		for(int i = 0; i < (int) Math.round(Math.pow(2, diffLength)); i++) {
			// counting from 0 to the maximum
			String counter = Integer.toBinaryString(i);
			counter = "0".repeat(diffLength - counter.length()) + counter;
			
			// the creating the subnet and host ID's
			String subnetNetID = binIP.substring(0, defaultMaskLength) + counter + "0".repeat(binIP.length() - defaultMaskLength - counter.length());
			String subnetHostID = binIP.substring(0, defaultMaskLength) + counter + "1".repeat(binIP.length() - defaultMaskLength - counter.length());
			
			// insert the dots
			subnetNetID = IPMethods.insertDots(subnetNetID);
			subnetHostID = IPMethods.insertDots(subnetHostID);
			// add them to the list
			list.addElement(String.format("%s - %s / %s - %s", subnetNetID, subnetHostID, IPMethods.binaryIPtoNumbersStaticLength(subnetNetID), IPMethods.binaryIPtoNumbersStaticLength(subnetHostID)));
		}
		
		// some setting for a nice look
		JList<String> jlist = new JList<>(list);
		jlist.setFont(new Font("Liberation Mono", Font.PLAIN, 27));
		
		// create a pane with a scroll bar and add it
		JScrollPane pane = new JScrollPane(jlist);
		contentPane.add(pane);
		setBounds(10, 10, 1770, 525);
		
		// title and formatting for it
		setTitle(String.format("subnets for %s with mask %s", IPMethods.binaryIPtoNumbers(binIP), IPMethods.binaryIPtoNumbers(IPMethods.insertDots(netmask))));
		
		// slight offset is needed
		NoInput resizeList = () -> pane.setBounds(0, 0, getWidth() + 3, getHeight() - 25);
		resizeList.apply();
		
		// pane needs resizing on window resize
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				resizeList.apply();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {}
			
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}
}
