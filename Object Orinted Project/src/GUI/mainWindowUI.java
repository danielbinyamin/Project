package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import program.FilterForRecords;
import program.Records;
import program.programCoreV2;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.Button;
import java.awt.List;
import javax.swing.event.ChangeListener;

import org.omg.CORBA._PolicyStub;
import org.omg.stub.java.rmi._Remote_Stub;

import de.micromata.opengis.kml.v_2_2_0.DisplayMode;

import javax.swing.event.ChangeEvent;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.JEditorPane;
import javax.swing.DropMode;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import java.awt.Component;
import java.awt.Dialog;

import javax.swing.Box;
import javax.swing.ButtonGroup;

import java.awt.Rectangle;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;

public class mainWindowUI {

	private static JFrame frame;
	private static programCoreV2 _programCore;
	private static FilterForRecords _filters;
	private static String outputDir;
	private JTextField txtDirLoaded;
	private JTextField txtOutputCsvCreated;
	private JTextField txtSaveDataAs;
	private JTextField txtFindRouter;
	private JTextField txtEnterMac;
	private JTextField txtLon;
	private JTextField latAnswerAlgo1;
	private JTextField txtLat;
	private JTextField lonAnswerAlgo1;
	private JTextField txtAlt;
	private JTextField altAnswerAlgo1;
	private JTextField txtFindUser;
	private JTextField txtEnterMac_1;
	private JTextField txtEnterMac_2;
	private JTextField txtEnterMac_3;
	private JTextField txtEnterSignal;
	private JTextField txtEnterSignal_1;
	private JTextField txtEnterSignal_2;
	private JTextField textField_1;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField latAnswerAlgo2;
	private JTextField lonAnswerAlgo2;
	private JTextField altAnswerAlgo2;
	private JTextField txtFileAddedSuccesfully;
	private JTextField txtNumberOfScans;
	private static JTextField numOfScans;
	private JTextField txtNumberOfDiffrent;
	private static JTextField numOfRouters;
	private JTextField txtFilterInformation;
	private JTextPane txtpncurrentFilterInformation;
	private JButton btnApplyFilter;
	private JButton btnClearFilter;
	private JButton saveFilterBtn;
	private static JTextPane txtFilterInfoGeneral;
	private JButton revertBtn;
	private static Thread dirThread;
	private static String wiggleDir;
	private static ArrayList<String> combinedCSVFileList;
	private static Thread filesThread;

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
		numOfRouters.setText("N/A");
		combinedCSVFileList = new ArrayList<>();
		filesThread = new Thread();
		dirThread = new Thread();
	}

	private static void updateInfo() {
		int numOfScansInt = _programCore.scanCount();
		int numOfRoutersInt = _programCore.diffRouterCount();
		numOfScans.setText(Integer.toString(numOfScansInt));
		numOfRouters.setText(Integer.toString(numOfRoutersInt));
		txtFilterInfoGeneral.setText(_filters.toString());
	}

	public void updateFilterInfo() {
		txtpncurrentFilterInformation.setText(_filters.toString());
	}

	private static boolean dirWatch(String dir) throws IOException, InterruptedException {

		Path path = Paths.get(dir+"\\");
		WatchService watchService = path.getFileSystem().newWatchService();

		path.register(watchService,
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE);

		while(true) {
			WatchKey watchKey = watchService.take();

			for (WatchEvent<?> event : watchKey.pollEvents()) {
				WatchEvent.Kind<?> k = event.kind();
				if(k==StandardWatchEventKinds.ENTRY_CREATE) 
					return true;			
				else if(k==StandardWatchEventKinds.ENTRY_MODIFY) 
					return true;
				else if(k==StandardWatchEventKinds.ENTRY_DELETE) 
					return true;
			}

			if(!watchKey.reset()) {
				watchKey.cancel();
				watchService.close();
			}
		}
	}//end F

	private static void runDirWatch() {
		try {
			boolean b = dirWatch(wiggleDir);
			int result=0;
			while(!dirThread.isInterrupted()) {
				if(b) 
					result = JOptionPane.showOptionDialog(frame, "A change has been detected in WiggleWifi Directory. Would you want to update?", "Update", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,null,null);
				if(result == JOptionPane.OK_OPTION) {//accepted an update
					_programCore.loadRecordsFromWiggleDir(wiggleDir);
					if(!combinedCSVFileList.isEmpty())
						for (String string : combinedCSVFileList) {
							_programCore.addCombinedCSV(string);
						}
					updateInfo();
				}
				b = dirWatch(wiggleDir);

			}
		}
		catch (IOException e) {
			System.out.println("Error at dirWatch thread");
			e.printStackTrace();
		} catch (InterruptedException e) {
			//do nothing
		}
	}

	private static void runFilesWatch() throws InterruptedException {

		ArrayList<Long> combCSVFilesLastModif = new ArrayList<>();
		int result=0;

		//fill last modified information of all combined csv files
		for (int i = 0; i < combinedCSVFileList.size(); i++) 
			combCSVFilesLastModif.add(new File(combinedCSVFileList.get(i)).lastModified());

		while(!filesThread.isInterrupted()) {
			filesThread.sleep(500);
			//loop over all files
			for (int i = 0; i < combinedCSVFileList.size(); i++) {
				File currentFile = new File(combinedCSVFileList.get(i));
				long currentFileLastModif = currentFile.lastModified();
				if(currentFileLastModif!=combCSVFilesLastModif.get(i)) {//a change has been detected

					if(currentFileLastModif==0) { //file has been deleted
						combinedCSVFileList.remove(i);
						combCSVFilesLastModif.remove(i);
						result = JOptionPane.showOptionDialog(frame, "A Combined CSV has been deleted. Would you want to update?", "Update", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,null,null);
						if(result == JOptionPane.OK_OPTION) {//accepted an update
							_programCore.loadRecordsFromWiggleDir(wiggleDir);
							for (String comCsvPath : combinedCSVFileList) 
								_programCore.addCombinedCSV(comCsvPath);
							updateInfo();
						}
					}

					else { //file has been edited
						combCSVFilesLastModif.set(i, currentFileLastModif);
						result = JOptionPane.showOptionDialog(frame, "A Combined CSV has been deleted. Would you want to update?", "Update", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,null,null);
						if(result == JOptionPane.OK_OPTION) {//accepted an update
							_programCore.loadRecordsFromWiggleDir(wiggleDir);
							for (String comCsvPath : combinedCSVFileList) 
								_programCore.addCombinedCSV(comCsvPath);
							updateInfo();
						}
					}
				}
			}
		}
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 13));
		frame.setBounds(100, 100, 1030, 685);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel general=new JPanel();
		JPanel filterOptions=new JPanel();  
		JPanel algorithms=new JPanel(); 
		JTabbedPane tabs=new JTabbedPane();
		tabs.setBounds(new Rectangle(0, 0, 1012, 638));



		//----------------------------------------------------------GENERAL TAB----------------------------------------------------//

		tabs.add("General",general); 
		tabs.setFont(new Font("Tahoma", Font.PLAIN, 20));

		revertBtn = new JButton("Revert back(Cancel filters)");
		revertBtn.setBounds(85, 16, 252, 31);
		revertBtn.setEnabled(false);
		revertBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_programCore.switchRecords();
				_programCore.cleanFilteredRecords();
				_filters.cleanFilter();
				updateFilterInfo();
				updateInfo();
				revertBtn.setEnabled(false);
				btnClearFilter.setEnabled(false);
				btnApplyFilter.setEnabled(false);
				saveFilterBtn.setEnabled(false);
			}
		});
		general.setLayout(null);
		general.add(revertBtn);
		revertBtn.setFont(new Font("Tahoma", Font.PLAIN, 18));

		//create csv output button
		JButton createOutputCsvButton = new JButton(".csv");
		createOutputCsvButton.setBounds(835, 541, 160, 34);
		general.add(createOutputCsvButton);

		JButton createOutputKmlBtn = new JButton(".kml");

		//clear button
		JButton clearButton = new JButton("Clear");
		clearButton.setBounds(776, 255, 195, 38);
		general.add(clearButton);
		clearButton.setFont(new Font("Tahoma", Font.PLAIN, 18));

		//load wiggle dir button
		JButton btnLoadWigglewifiDirectory = new JButton("Load WiggleWifi Directory");
		btnLoadWigglewifiDirectory.setBounds(743, 14, 252, 34);
		general.add(btnLoadWigglewifiDirectory);
		btnLoadWigglewifiDirectory.setFont(new Font("Tahoma", Font.PLAIN, 18));

		//dir loaded text
		txtDirLoaded = new JTextField();
		txtDirLoaded.setBounds(839, 46, 89, 22);
		general.add(txtDirLoaded);
		txtDirLoaded.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtDirLoaded.setEditable(false);
		txtDirLoaded.setText("dir loaded!");
		txtDirLoaded.setColumns(10);

		JButton AddCombCsvBtn = new JButton("Add combined .csv");
		AddCombCsvBtn.setEnabled(false);
		AddCombCsvBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser fl = new fileChooser();
				try {
					String combinedCsv = fl.run("Choose combined .csv file:");
					if(!combinedCSVFileList.contains(combinedCsv)) {
						try {
							_programCore.addCombinedCSV(combinedCsv);
						}
						catch(NullPointerException ex) {
							_programCore = new programCoreV2();
							_programCore.addCombinedCSV(combinedCsv);
						}
						txtFileAddedSuccesfully.setVisible(true);
						createOutputCsvButton.setEnabled(true);
						createOutputKmlBtn.setEnabled(true);
						clearButton.setEnabled(true);
						updateInfo();
						//TODO:here

						combinedCSVFileList.add(combinedCsv);
						filesThread = new Thread(()->{
							try {
								runFilesWatch();
							} catch (InterruptedException e1) {
								//do nothing
							}
						});

						filesThread.start();
					}//end if
					else {
						JOptionPane.showMessageDialog(frame, "File already exists!", "File not added", JOptionPane.WARNING_MESSAGE, null);
					}
				}
				catch (NullPointerException e2) {
					// do nothing
				}
			}

		});
		AddCombCsvBtn.setBounds(776, 120, 195, 38);
		general.add(AddCombCsvBtn);
		AddCombCsvBtn.setFont(new Font("Tahoma", Font.PLAIN, 18));




		txtSaveDataAs = new JTextField();
		txtSaveDataAs.setBounds(743, 502, 146, 26);
		general.add(txtSaveDataAs);
		txtSaveDataAs.setEditable(false);
		txtSaveDataAs.setFont(new Font("Tahoma", Font.PLAIN, 18));
		txtSaveDataAs.setForeground(Color.BLACK);
		txtSaveDataAs.setText("   Save data as:");
		txtSaveDataAs.setColumns(10);



		createOutputKmlBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dirChooser dir = new dirChooser();
				try {
					outputDir = dir.run("Choose path to save output kml:");
					_programCore.createKMLfromRecords(outputDir);
					txtOutputCsvCreated.setText("output kml created at: "+ outputDir);
					txtOutputCsvCreated.setVisible(true);
				}
				catch (NullPointerException e) {
					//do nothing
				}
			}
		});
		createOutputKmlBtn.setBounds(634, 541, 160, 34);
		general.add(createOutputKmlBtn);




		//created csv path text
		txtOutputCsvCreated = new JTextField();
		txtOutputCsvCreated.setBounds(598, 573, 397, 22);
		general.add(txtOutputCsvCreated);
		txtOutputCsvCreated.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtOutputCsvCreated.setEditable(false);
		txtOutputCsvCreated.setForeground(SystemColor.desktop);
		txtOutputCsvCreated.setColumns(10);
		txtOutputCsvCreated.setVisible(false);
		createOutputCsvButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChooser dir = new dirChooser();
				try {
					outputDir = dir.run("Choose path to save output csv: ");
					_programCore.createCSVfromRecords(outputDir);
					txtOutputCsvCreated.setText("output csv created at: "+ outputDir);
					txtOutputCsvCreated.setVisible(true);
				}
				catch (NullPointerException e3) {
					// do nothing
				}
			}		
		});
		clearButton.setEnabled(false);

		txtFileAddedSuccesfully = new JTextField();
		txtFileAddedSuccesfully.setFont(new Font("Tahoma", Font.BOLD, 13));
		txtFileAddedSuccesfully.setEditable(false);
		txtFileAddedSuccesfully.setText("File added succesfully!");
		txtFileAddedSuccesfully.setBounds(786, 155, 185, 22);
		general.add(txtFileAddedSuccesfully);
		txtFileAddedSuccesfully.setColumns(10);

		txtNumberOfScans = new JTextField();
		txtNumberOfScans.setFont(new Font("Tahoma", Font.PLAIN, 18));
		txtNumberOfScans.setText("Number of scans: ");
		txtNumberOfScans.setEditable(false);
		txtNumberOfScans.setBounds(15, 348, 156, 26);
		general.add(txtNumberOfScans);
		txtNumberOfScans.setColumns(10);

		numOfScans = new JTextField();
		numOfScans.setFont(new Font("Tahoma", Font.BOLD, 18));
		numOfScans.setEditable(false);
		numOfScans.setBounds(172, 349, 65, 25);
		general.add(numOfScans);
		numOfScans.setColumns(10);
		numOfScans.setText("N/A");

		txtNumberOfDiffrent = new JTextField();
		txtNumberOfDiffrent.setText("Number of diffrent routers: ");
		txtNumberOfDiffrent.setFont(new Font("Tahoma", Font.PLAIN, 18));
		txtNumberOfDiffrent.setEditable(false);
		txtNumberOfDiffrent.setColumns(10);
		txtNumberOfDiffrent.setBounds(15, 390, 233, 26);
		general.add(txtNumberOfDiffrent);

		numOfRouters = new JTextField();
		numOfRouters.setEditable(false);
		numOfRouters.setFont(new Font("Tahoma", Font.BOLD, 18));
		numOfRouters.setColumns(10);
		numOfRouters.setBounds(247, 391, 65, 25);
		general.add(numOfRouters);

		txtFilterInformation = new JTextField();
		txtFilterInformation.setEditable(false);
		txtFilterInformation.setFont(new Font("Tahoma", Font.PLAIN, 18));
		txtFilterInformation.setText("Filter Information:");
		txtFilterInformation.setBounds(15, 432, 160, 26);
		general.add(txtFilterInformation);
		txtFilterInformation.setColumns(10);

		txtFilterInfoGeneral = new JTextPane();
		txtFilterInfoGeneral.setText("No filter selected");
		txtFilterInfoGeneral.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 15));
		txtFilterInfoGeneral.setEditable(false);
		txtFilterInfoGeneral.setBackground(Color.LIGHT_GRAY);
		txtFilterInfoGeneral.setBounds(180, 430, 266, 116);
		general.add(txtFilterInfoGeneral);
		txtFileAddedSuccesfully.setVisible(false);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_programCore.cleanRecordsData();
				_programCore.switchRecords();
				_programCore.cleanRecordsData();
				_programCore.switchRecords();
				clearButton.setEnabled(false);
				createOutputCsvButton.setEnabled(false);
				createOutputKmlBtn.setEnabled(false);
				btnLoadWigglewifiDirectory.setEnabled(true);
				AddCombCsvBtn.setEnabled(false);
				txtDirLoaded.setVisible(false);
				txtOutputCsvCreated.setVisible(false);
				txtFileAddedSuccesfully.setVisible(false);
				numOfRouters.setText("N/A");
				numOfScans.setText("N/A");

				dirThread.interrupt();
				if(!combinedCSVFileList.isEmpty())
					filesThread.interrupt();

				combinedCSVFileList.clear();

			}
		});
		txtDirLoaded.setVisible(false);
		btnLoadWigglewifiDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dirChooser dir = new dirChooser();
				try {
					wiggleDir = dir.run("Choose WiggleWifi path: ");
					_programCore = new programCoreV2();
					_programCore.loadRecordsFromWiggleDir(wiggleDir);
					txtDirLoaded.setVisible(true);
					txtOutputCsvCreated.setVisible(false);
					if(!_programCore.get_records().isEmpty()) {
						createOutputCsvButton.setEnabled(true);
						createOutputKmlBtn.setEnabled(true);
						clearButton.setEnabled(true);
						AddCombCsvBtn.setEnabled(true);
						btnLoadWigglewifiDirectory.setEnabled(false);
					}
					updateInfo();
					dirThread = new Thread(()->{
						runDirWatch();
					});

					dirThread.start();
				}
				catch (NullPointerException e) {
					//do nothing
				}
			}
		});
		frame.getContentPane().setLayout(null);

		//----------------------------------------------------------FILTER OPTIONS TAB----------------------------------------------------//

		tabs.add("filterOptions",filterOptions);
		filterOptions.setLayout(null);

		saveFilterBtn = new JButton("Save current filter");
		saveFilterBtn.setEnabled(false);
		saveFilterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChooser dir = new dirChooser();
				try {
					String path = dir.run("Choose path to save filter:");
					try {
						_filters.saveFilterToDisk(path);
					} catch (Exception e2) {
						System.out.println("Error saving filter \n"+e2);
					}
				}
				catch (NullPointerException e4) {
					//do nothing
				}

			}
		});
		saveFilterBtn.setBounds(801, 180, 156, 33);
		filterOptions.add(saveFilterBtn);

		JButton loadExternalFilterBtn = new JButton("Load external filter");
		loadExternalFilterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser f = new fileChooser();
				try {
					String path = f.run("Choose external filter");
					_filters = new FilterForRecords(path);
					updateFilterInfo();
					btnApplyFilter.setEnabled(true);
					btnClearFilter.setEnabled(true);
					saveFilterBtn.setEnabled(true);
				}
				catch (NullPointerException e3) {
					//do nothing
				}
			}
		});
		loadExternalFilterBtn.setBounds(801, 13, 156, 33);
		filterOptions.add(loadExternalFilterBtn);

		JButton addFilterBtn = new JButton("Add filter +");
		addFilterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String relation = "";
				if(_filters.getNumOfFilters()>=1) {
					final JPanel panel = new JPanel();
					final JRadioButton andRelation = new JRadioButton("And");
					final JRadioButton orRelation = new JRadioButton("Or");
					ButtonGroup gr = new ButtonGroup();
					andRelation.setSelected(true);
					gr.add(andRelation);
					gr.add(orRelation);
					panel.add(andRelation);
					panel.add(orRelation);       
					JOptionPane.showMessageDialog(frame, panel,"Choose relation for this filter:",1);

					if(andRelation.isSelected())
						relation="&&";
					else
						relation="||";

				}
				filterPicker filterChoose = new filterPicker(_filters, relation);
				filterChoose.run();
				updateFilterInfo();
				if(_filters.getNumOfFilters()>0) {
					btnApplyFilter.setEnabled(true);
					btnClearFilter.setEnabled(true);
					saveFilterBtn.setEnabled(true);
				}
				if(_filters.getNumOfFilters()==2)
					addFilterBtn.setEnabled(false);
			}
		});
		addFilterBtn.setBounds(801, 93, 156, 33);
		filterOptions.add(addFilterBtn);

		txtpncurrentFilterInformation = new JTextPane();
		txtpncurrentFilterInformation.setBackground(Color.LIGHT_GRAY);
		txtpncurrentFilterInformation.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 15));
		txtpncurrentFilterInformation.setEditable(false);
		txtpncurrentFilterInformation.setText("No filter selected");
		txtpncurrentFilterInformation.setBounds(45, 426, 266, 116);
		filterOptions.add(txtpncurrentFilterInformation);

		btnApplyFilter = new JButton("Apply filter");
		btnApplyFilter.setEnabled(false);
		btnApplyFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(_programCore.isRecordsEmpty())
					JOptionPane.showMessageDialog(frame, "The data base is empty! Filter cannot be done.", "Data base empty", JOptionPane.WARNING_MESSAGE, null);
				else{
				_programCore.filter(_filters);
				_programCore.switchRecords();
				updateInfo();
				btnApplyFilter.setEnabled(false);
				revertBtn.setEnabled(true);
			}
			}
		});
		btnApplyFilter.setBounds(801, 534, 156, 33);
		filterOptions.add(btnApplyFilter);

		btnClearFilter = new JButton("Clear filter");
		btnClearFilter.setEnabled(false);
		btnClearFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_filters = new FilterForRecords();
				updateFilterInfo();
				btnClearFilter.setEnabled(false);
				btnApplyFilter.setEnabled(false);
				saveFilterBtn.setEnabled(false);
				addFilterBtn.setEnabled(true);
			}
		});
		btnClearFilter.setBounds(801, 488, 156, 33);
		filterOptions.add(btnClearFilter);

		//----------------------------------------------------------Algorithms-----------------------------------------------------------//

		tabs.add("Algorithms",algorithms);
		algorithms.setLayout(null);

		txtFindRouter = new JTextField();
		txtFindRouter.setEditable(false);
		txtFindRouter.setFont(new Font("Tahoma", Font.PLAIN, 19));
		txtFindRouter.setText("              Find Router");
		txtFindRouter.setBounds(328, 0, 255, 22);
		algorithms.add(txtFindRouter);
		txtFindRouter.setColumns(10);

		txtEnterMac = new JTextField();
		txtEnterMac.setEditable(false);
		txtEnterMac.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtEnterMac.setText("Enter MAC: ");
		txtEnterMac.setBounds(12, 59, 101, 42);
		algorithms.add(txtEnterMac);
		txtEnterMac.setColumns(10);

		JTextArea textAreaMacAlgo1 = new JTextArea();
		textAreaMacAlgo1.setFont(new Font("Monospaced", Font.PLAIN, 16));
		textAreaMacAlgo1.setBounds(113, 67, 203, 31);
		algorithms.add(textAreaMacAlgo1);

		JButton btnNewButton_1 = new JButton("Calculate");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(_programCore.isRecordsEmpty())
					JOptionPane.showMessageDialog(frame, "The data base is empty! Please load it with data", "Data base empty", JOptionPane.WARNING_MESSAGE, null);
				else{
					String mac = textAreaMacAlgo1.getText();
					if(_programCore.checkIfMacExistsInRecords(mac)) {
						Map<String, Double> answer = _programCore.locateRouter(mac);
						Double lat = answer.get("lat");
						Double lon = answer.get("lon");
						Double alt = answer.get("alt");

						latAnswerAlgo1.setText(Double.toString(lat));
						lonAnswerAlgo1.setText(Double.toString(lon));
						altAnswerAlgo1.setText(Double.toString(alt));
					}
					else {
						JOptionPane.showMessageDialog(frame, "The MAC you entered does not exist in data base!", "mac dosen't exist", JOptionPane.WARNING_MESSAGE, null);
					}
				}
			}
		});
		btnNewButton_1.setBounds(328, 69, 148, 32);
		algorithms.add(btnNewButton_1);

		txtLon = new JTextField();
		txtLon.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtLon.setEditable(false);
		txtLon.setText("Lon:");
		txtLon.setBounds(12, 158, 42, 22);
		algorithms.add(txtLon);
		txtLon.setColumns(10);

		latAnswerAlgo1 = new JTextField();
		latAnswerAlgo1.setEditable(false);
		latAnswerAlgo1.setBounds(58, 131, 184, 22);
		algorithms.add(latAnswerAlgo1);
		latAnswerAlgo1.setColumns(10);

		txtLat = new JTextField();
		txtLat.setText("Lat:");
		txtLat.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtLat.setEditable(false);
		txtLat.setColumns(10);
		txtLat.setBounds(12, 130, 42, 22);
		algorithms.add(txtLat);

		lonAnswerAlgo1 = new JTextField();
		lonAnswerAlgo1.setEditable(false);
		lonAnswerAlgo1.setColumns(10);
		lonAnswerAlgo1.setBounds(58, 159, 184, 22);
		algorithms.add(lonAnswerAlgo1);

		txtAlt = new JTextField();
		txtAlt.setText("Alt:");
		txtAlt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtAlt.setEditable(false);
		txtAlt.setColumns(10);
		txtAlt.setBounds(12, 186, 42, 22);
		algorithms.add(txtAlt);

		altAnswerAlgo1 = new JTextField();
		altAnswerAlgo1.setEditable(false);
		altAnswerAlgo1.setColumns(10);
		altAnswerAlgo1.setBounds(58, 187, 184, 22);
		algorithms.add(altAnswerAlgo1);

		JButton btnNewButton_2 = new JButton("Clear");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				latAnswerAlgo1.setText("");
				lonAnswerAlgo1.setText("");
				altAnswerAlgo1.setText("");
			}
		});
		btnNewButton_2.setBounds(328, 158, 148, 32);
		algorithms.add(btnNewButton_2);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 221, 891, 16);
		algorithms.add(separator);

		txtFindUser = new JTextField();
		txtFindUser.setText("              Find User");
		txtFindUser.setFont(new Font("Tahoma", Font.PLAIN, 19));
		txtFindUser.setEditable(false);
		txtFindUser.setColumns(10);
		txtFindUser.setBounds(328, 231, 255, 22);
		algorithms.add(txtFindUser);

		txtEnterMac_1 = new JTextField();
		txtEnterMac_1.setText("Enter MAC1: ");
		txtEnterMac_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtEnterMac_1.setEditable(false);
		txtEnterMac_1.setColumns(10);
		txtEnterMac_1.setBounds(12, 271, 101, 42);
		algorithms.add(txtEnterMac_1);

		JTextArea mac1InputAlgo2 = new JTextArea();
		mac1InputAlgo2.setFont(new Font("Monospaced", Font.PLAIN, 16));
		mac1InputAlgo2.setBounds(113, 282, 148, 31);
		algorithms.add(mac1InputAlgo2);

		txtEnterMac_2 = new JTextField();
		txtEnterMac_2.setText("Enter MAC2: ");
		txtEnterMac_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtEnterMac_2.setEditable(false);
		txtEnterMac_2.setColumns(10);
		txtEnterMac_2.setBounds(12, 326, 101, 42);
		algorithms.add(txtEnterMac_2);

		JTextArea mac2InputAlgo2 = new JTextArea();
		mac2InputAlgo2.setFont(new Font("Monospaced", Font.PLAIN, 16));
		mac2InputAlgo2.setBounds(113, 337, 148, 31);
		algorithms.add(mac2InputAlgo2);

		txtEnterMac_3 = new JTextField();
		txtEnterMac_3.setText("Enter MAC3: ");
		txtEnterMac_3.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtEnterMac_3.setEditable(false);
		txtEnterMac_3.setColumns(10);
		txtEnterMac_3.setBounds(12, 380, 101, 42);
		algorithms.add(txtEnterMac_3);

		JTextArea mac3InputAlgo2 = new JTextArea();
		mac3InputAlgo2.setFont(new Font("Monospaced", Font.PLAIN, 16));
		mac3InputAlgo2.setBounds(113, 391, 148, 31);
		algorithms.add(mac3InputAlgo2);

		txtEnterSignal = new JTextField();
		txtEnterSignal.setText("Enter SIGNAL1: ");
		txtEnterSignal.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtEnterSignal.setEditable(false);
		txtEnterSignal.setColumns(10);
		txtEnterSignal.setBounds(375, 271, 126, 42);
		algorithms.add(txtEnterSignal);

		JTextArea signal1InputAlgo2 = new JTextArea();
		signal1InputAlgo2.setFont(new Font("Monospaced", Font.PLAIN, 16));
		signal1InputAlgo2.setBounds(501, 279, 148, 31);
		algorithms.add(signal1InputAlgo2);

		JTextArea signal2InputAlgo2 = new JTextArea();
		signal2InputAlgo2.setFont(new Font("Monospaced", Font.PLAIN, 16));
		signal2InputAlgo2.setBounds(501, 334, 148, 31);
		algorithms.add(signal2InputAlgo2);

		JTextArea signal3InputAlgo2 = new JTextArea();
		signal3InputAlgo2.setFont(new Font("Monospaced", Font.PLAIN, 16));
		signal3InputAlgo2.setBounds(501, 388, 148, 31);
		algorithms.add(signal3InputAlgo2);

		txtEnterSignal_1 = new JTextField();
		txtEnterSignal_1.setText("Enter SIGNAL2: ");
		txtEnterSignal_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtEnterSignal_1.setEditable(false);
		txtEnterSignal_1.setColumns(10);
		txtEnterSignal_1.setBounds(375, 326, 126, 42);
		algorithms.add(txtEnterSignal_1);

		txtEnterSignal_2 = new JTextField();
		txtEnterSignal_2.setText("Enter SIGNAL3: ");
		txtEnterSignal_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtEnterSignal_2.setEditable(false);
		txtEnterSignal_2.setColumns(10);
		txtEnterSignal_2.setBounds(375, 380, 126, 42);
		algorithms.add(txtEnterSignal_2);

		JButton button = new JButton("Calculate");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(_programCore.isRecordsEmpty())
					JOptionPane.showMessageDialog(frame, "The data base is empty! Please load it with data", "Data base empty", JOptionPane.WARNING_MESSAGE, null);
				else {
					try {
					String mac1 = mac1InputAlgo2.getText();
					String mac2 = mac2InputAlgo2.getText();
					String mac3 = mac3InputAlgo2.getText();
					int signal1 = Integer.parseInt(signal1InputAlgo2.getText());
					int signal2 = Integer.parseInt(signal2InputAlgo2.getText());
					int signal3 = Integer.parseInt(signal3InputAlgo2.getText());
					Map<String, Double> answer;
					try { answer = _programCore.locateUser(mac1, signal1, mac2, signal2, mac3, signal3); }		
					catch (Exception e1) { 
						answer = new HashMap<>();
						e1.printStackTrace(); 
					}
					Double lat = answer.get("lat");
					Double lon = answer.get("lon");
					Double alt = answer.get("alt");

					latAnswerAlgo2.setText(Double.toString(lat));
					lonAnswerAlgo2.setText(Double.toString(lon));
					altAnswerAlgo2.setText(Double.toString(alt));
					}
					catch (Exception ex) {
						JOptionPane.showMessageDialog(frame, "One of the MAC's you entered do not exist in the data base!", "MAC not existing", JOptionPane.WARNING_MESSAGE, null);
					}
				}
			}
		});
		button.setBounds(725, 336, 148, 32);
		algorithms.add(button);

		textField_1 = new JTextField();
		textField_1.setText("Lon:");
		textField_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		textField_1.setBounds(12, 510, 42, 22);
		algorithms.add(textField_1);

		textField_4 = new JTextField();
		textField_4.setText("Lat:");
		textField_4.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textField_4.setEditable(false);
		textField_4.setColumns(10);
		textField_4.setBounds(12, 480, 42, 22);
		algorithms.add(textField_4);

		textField_5 = new JTextField();
		textField_5.setText("Alt:");
		textField_5.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textField_5.setEditable(false);
		textField_5.setColumns(10);
		textField_5.setBounds(12, 539, 42, 22);
		algorithms.add(textField_5);

		latAnswerAlgo2 = new JTextField();
		latAnswerAlgo2.setEditable(false);
		latAnswerAlgo2.setColumns(10);
		latAnswerAlgo2.setBounds(58, 481, 160, 22);
		algorithms.add(latAnswerAlgo2);

		lonAnswerAlgo2 = new JTextField();
		lonAnswerAlgo2.setEditable(false);
		lonAnswerAlgo2.setColumns(10);
		lonAnswerAlgo2.setBounds(58, 511, 160, 22);
		algorithms.add(lonAnswerAlgo2);

		altAnswerAlgo2 = new JTextField();
		altAnswerAlgo2.setEditable(false);
		altAnswerAlgo2.setColumns(10);
		altAnswerAlgo2.setBounds(58, 540, 160, 22);
		algorithms.add(altAnswerAlgo2);

		JButton button_1 = new JButton("Clear");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				latAnswerAlgo2.setText("");
				lonAnswerAlgo2.setText("");
				altAnswerAlgo2.setText("");
			}
		});
		button_1.setBounds(271, 506, 148, 32);
		algorithms.add(button_1);

		frame.getContentPane().add(tabs);

		_programCore = new programCoreV2();
		_filters = new FilterForRecords();
		if (_programCore.isRecordsEmpty()) {
			createOutputCsvButton.setEnabled(false);
			createOutputKmlBtn.setEnabled(false);
			clearButton.setEnabled(false);
		}

	}
}
