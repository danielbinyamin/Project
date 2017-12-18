package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;

import program.Records;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Button;
import java.awt.List;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;

public class mainWindowUI {

	private JFrame frame;
	private Records mainDataBase;
	private File outputDir;
	private JTextField txtDirLoaded;
	private JTextField txtOutputCsvCreated;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainWindowUI window = new mainWindowUI();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public mainWindowUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 13));
		frame.setBounds(100, 100, 720, 472);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainDataBase = new Records();
		outputDir = new File("");
		

		//create csv output button
		JButton createOutputCsvButton = new JButton("Create output csv");
		if (mainDataBase.isEmpty())
			createOutputCsvButton.setVisible(false);
		createOutputCsvButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChooser2 dir = new dirChooser2();
				outputDir = dir.run();
				mainDataBase.toCSV(outputDir);
				txtOutputCsvCreated.setText("output csv created at: "+ outputDir.getPath());
				txtOutputCsvCreated.setVisible(true);
				System.out.println("Succesfully created output file at: " + outputDir.getPath());
			}		
		});
		createOutputCsvButton.setBounds(426, 121, 195, 38);
		frame.getContentPane().add(createOutputCsvButton);

		//dir loaded text
		txtDirLoaded = new JTextField();
		txtDirLoaded.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtDirLoaded.setEditable(false);
		txtDirLoaded.setText("dir loaded!");
		txtDirLoaded.setBounds(479, 90, 89, 22);
		frame.getContentPane().add(txtDirLoaded);
		txtDirLoaded.setColumns(10);
		txtDirLoaded.setVisible(false);


		//clear button
		JButton clearButton = new JButton("Clear");
		clearButton.setVisible(false);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainDataBase = new Records();
				clearButton.setVisible(false);
				createOutputCsvButton.setVisible(false);
				txtDirLoaded.setVisible(false);
				txtOutputCsvCreated.setVisible(false);
			}
		});
		clearButton.setBounds(426, 204, 195, 38);
		frame.getContentPane().add(clearButton);



		//load wiggle dir button
		JButton btnLoadWigglewifiDirectory = new JButton("Load WiggleWifi Directory");
		btnLoadWigglewifiDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dirChooser1 dir = new dirChooser1();
				File wiggleDir = dir.run();
				mainDataBase.CSV2Records(wiggleDir);
				txtDirLoaded.setVisible(true);
				txtOutputCsvCreated.setVisible(false);
				if(!mainDataBase.isEmpty()) {
					createOutputCsvButton.setVisible(true);
					clearButton.setVisible(true);
				}
			}
		});
		frame.getContentPane().setLayout(null);
		btnLoadWigglewifiDirectory.setBounds(426, 53, 195, 38);
		frame.getContentPane().add(btnLoadWigglewifiDirectory);
		
		//created csv path text
		txtOutputCsvCreated = new JTextField();
		txtOutputCsvCreated.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtOutputCsvCreated.setEditable(false);
		txtOutputCsvCreated.setForeground(SystemColor.desktop);
		txtOutputCsvCreated.setBounds(281, 157, 397, 22);
		frame.getContentPane().add(txtOutputCsvCreated);
		txtOutputCsvCreated.setColumns(10);
		txtOutputCsvCreated.setVisible(false);





	}
}
