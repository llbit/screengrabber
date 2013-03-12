/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of ScreenGrabber.
 *
 * ScreenGrabber is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ScreenGrabber is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ScreenGrabber.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.screengrabber;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Grabs a screenshot every 5th second of the entire screen
 * @author Jesper
 */
@SuppressWarnings("serial")
public class ScreenGrabber extends JFrame {
	
	protected Thread screenShotThread = null;
	
	class ScreenShotThread extends Thread {
		private final int delay;
		
		public ScreenShotThread(int delay) {
			this.delay = delay;
		}
		
		@Override
		public void run() {
			try {
				int i = 0;
				while (true) {
					Robot robot = new Robot();
					BufferedImage img = robot.createScreenCapture(
							new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
					ImageIO.write(img, "PNG", new File("Screen" + i + ".png"));
					i += 1;
					Thread.sleep(delay);
				}
			} catch (AWTException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
			}
		}
	};
	
	/**
	 * Create a new screen grabber window
	 */
	public ScreenGrabber() {
		setTitle("Screen Grabber");
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JButton exitBtn = new JButton("Exit");
		exitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JLabel delayLbl = new JLabel("Delay: ");
		final JComboBox delayCB = new JComboBox();
		delayCB.addItem("0.5");
		delayCB.addItem("1.0");
		delayCB.addItem("5.0");
		delayCB.setSelectedIndex(1);
		
		final JButton startBtn = new JButton("Start");
		final JButton stopBtn = new JButton("Stop");
		stopBtn.setEnabled(false);
		
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (screenShotThread == null) {
					int delay = 5000;
					delay = (int) (Double.parseDouble("" + delayCB.getSelectedItem()) * 1000);
					screenShotThread = new ScreenShotThread(delay);
					screenShotThread.start();
					startBtn.setEnabled(false);
					stopBtn.setEnabled(true);
				}
			}
		});
		
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (screenShotThread != null && screenShotThread.isAlive()) {
					screenShotThread.interrupt();
				}
				screenShotThread = null;
				startBtn.setEnabled(true);
				stopBtn.setEnabled(false);
			}
		});
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(delayLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(delayCB)
				)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(startBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(stopBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(exitBtn)
				)
			)
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(delayLbl)
				.addComponent(delayCB)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(startBtn)
				.addComponent(stopBtn)
				.addComponent(exitBtn)
			)
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		setContentPane(panel);
		pack();
		
		setLocationByPlatform(true);
	}
	
	/**
	 * Entry point
	 * @param args
	 */
	public static void main(String[] args) {
		new ScreenGrabber().setVisible(true);
	}
}
