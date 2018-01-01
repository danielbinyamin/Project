package GUI;

import java.awt.BorderLayout;
import java.awt.CheckboxGroup;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;

import program.Filter;
import program.FilterForRecords;
import program.Records;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JTree;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;

public class filterPicker extends JDialog {

	private FilterForRecords _chosenFilter;
	private Records _records;

	private final JPanel contentPanel = new JPanel();
	private JTextField txtStartDate;
	private JTextField txtEndDate;
	private JTextField txtLat;
	private JTextField txtLon;
	private JTextField txtRadius;
	private JTextField textFieldLat;
	private JTextField textFieldLon;
	private JTextField textFieldRadius;
	private JTextField textFieldId;
	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;
	private TimePicker timePickerStart;
	private TimePicker timePickerEnd;
	private JCheckBox chckbxNotDate;
	private JCheckBox checkBoxNotLoc;
	private JCheckBox checkBoxNotId;
	private final ButtonGroup group;
	private JRadioButton rdbtnDate;
	private JRadioButton rdbtnLocation;
	private JRadioButton rdbtnId;
	//private  FilterForRecords filter;

	/**
	 * Launch the application.
	 */
	public void run() {
		try {

			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setVisible(true);		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create the dialog.
	 */
	public filterPicker(FilterForRecords filter,String relation) {
		setModalityType(ModalityType.DOCUMENT_MODAL);
		setBounds(100, 100, 567, 543);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JTextArea txtrPickFilter = new JTextArea();
			txtrPickFilter.setBounds(212, 10, 125, 29);
			txtrPickFilter.setEditable(false);
			txtrPickFilter.setFont(new Font("Monospaced", Font.PLAIN, 18));
			txtrPickFilter.setText("Pick Filter");
			contentPanel.add(txtrPickFilter);
		}

		rdbtnDate = new JRadioButton("Date:");
		rdbtnDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateDateRadio(true);
				updateLocationRadio(false);
				updateIdRadio(false);
			}
		});
		rdbtnDate.setSelected(true);
		rdbtnDate.setBounds(32, 58, 63, 25);
		contentPanel.add(rdbtnDate);

		txtStartDate = new JTextField();
		txtStartDate.setEditable(false);
		txtStartDate.setText("Start date: ");
		txtStartDate.setBounds(88, 92, 116, 22);
		contentPanel.add(txtStartDate);
		txtStartDate.setColumns(10);

		//start date
		datePickerStart = new DatePicker();
		datePickerStart.getComponentToggleCalendarButton().setBounds(131, 12, 23, 23);
		datePickerStart.getComponentDateTextField().setBounds(0, 12, 131, 23);
		datePickerStart.setBounds(212, 82, 160, 43);
		datePickerStart.setVisible(true);
		contentPanel.add(datePickerStart);
		datePickerStart.setLayout(null);
		//start time
		timePickerStart = new TimePicker();
		timePickerStart.getComponentToggleTimeMenuButton().setBounds(92, 0, 21, 22);
		timePickerStart.getComponentTimeTextField().setBounds(12, 0, 81, 22);
		timePickerStart.setBounds(385,93,125,22);
		timePickerStart.setVisible(true);
		contentPanel.add(timePickerStart);
		timePickerStart.setLayout(null);

		//end date
		datePickerEnd = new DatePicker();
		datePickerEnd.getComponentToggleCalendarButton().setBounds(131, 12, 23, 23);
		datePickerEnd.getComponentDateTextField().setBounds(0, 12, 131, 23);
		datePickerEnd.setBounds(212,139, 166, 43);
		datePickerEnd.setVisible(true);
		contentPanel.add(datePickerEnd);
		datePickerEnd.setLayout(null);
		//end Time
		timePickerEnd = new TimePicker();
		timePickerEnd.getComponentToggleTimeMenuButton().setBounds(92, 0, 21, 22);
		timePickerEnd.getComponentTimeTextField().setBounds(12, 0, 81, 22);
		timePickerEnd.setBounds(385,150,125,22);
		timePickerEnd.setVisible(true);
		contentPanel.add(timePickerEnd);
		timePickerEnd.setLayout(null);



		txtEndDate = new JTextField();
		txtEndDate.setEditable(false);
		txtEndDate.setText("End Date:");
		txtEndDate.setColumns(10);
		txtEndDate.setBounds(88, 149, 116, 22);
		contentPanel.add(txtEndDate);

		rdbtnLocation = new JRadioButton("Location:");
		rdbtnLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateLocationRadio(true);
				updateDateRadio(false);
				updateIdRadio(false);
			}
		});
		rdbtnLocation.setBounds(32, 203, 81, 25);
		contentPanel.add(rdbtnLocation);

		txtLat = new JTextField();
		txtLat.setText("Lat: ");
		txtLat.setEditable(false);
		txtLat.setColumns(10);
		txtLat.setBounds(88, 237, 116, 22);
		contentPanel.add(txtLat);

		txtLon = new JTextField();
		txtLon.setText("Lon");
		txtLon.setEditable(false);
		txtLon.setColumns(10);
		txtLon.setBounds(88, 262, 116, 22);
		contentPanel.add(txtLon);

		txtRadius = new JTextField();
		txtRadius.setText("Radius: ");
		txtRadius.setEditable(false);
		txtRadius.setColumns(10);
		txtRadius.setBounds(88, 287, 116, 22);
		contentPanel.add(txtRadius);

		textFieldLat = new JTextField();
		textFieldLat.setBounds(212, 237, 160, 22);
		contentPanel.add(textFieldLat);
		textFieldLat.setColumns(10);

		textFieldLon = new JTextField();
		textFieldLon.setColumns(10);
		textFieldLon.setBounds(212, 262, 160, 22);
		contentPanel.add(textFieldLon);

		textFieldRadius = new JTextField();
		textFieldRadius.setColumns(10);
		textFieldRadius.setBounds(212, 287, 160, 22);
		contentPanel.add(textFieldRadius);

		rdbtnId = new JRadioButton("ID:");
		rdbtnId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateIdRadio(true);
				updateDateRadio(false);
				updateLocationRadio(false);
			}
		});
		rdbtnId.setBounds(32, 354, 45, 25);
		contentPanel.add(rdbtnId);

		textFieldId = new JTextField();
		textFieldId.setColumns(10);
		textFieldId.setBounds(88, 355, 160, 22);
		contentPanel.add(textFieldId);

		chckbxNotDate = new JCheckBox("NOT");
		chckbxNotDate.setBounds(375, 58, 113, 25);
		contentPanel.add(chckbxNotDate);

		checkBoxNotLoc = new JCheckBox("NOT");
		checkBoxNotLoc.setBounds(375, 203, 113, 25);
		contentPanel.add(checkBoxNotLoc);

		checkBoxNotId = new JCheckBox("NOT");
		checkBoxNotId.setBounds(375, 354, 113, 25);
		contentPanel.add(checkBoxNotId);

		group = new ButtonGroup();
		group.add(rdbtnDate);
		group.add(rdbtnLocation);
		group.add(rdbtnId);

		updateLocationRadio(false);
		updateIdRadio(false);

		JSeparator separator = new JSeparator();
		separator.setBounds(17, 191, 493, 2);
		contentPanel.add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 322, 493, 2);
		contentPanel.add(separator_1);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//----apply filter here------//
						String textRdBtnSelected="";
						JRadioButton []rdBtnArr = {rdbtnDate,rdbtnId,rdbtnLocation};
						for (int i = 0; i < rdBtnArr.length; i++) {
							if(rdBtnArr[i].isSelected())
								textRdBtnSelected = rdBtnArr[i].getText();
						}
						boolean not=false;
						switch (textRdBtnSelected) {
						case "Date:":
							LocalDate startDate = datePickerStart.getDate();
							int year = startDate.getYear();
							int month = startDate.getMonthValue();
							int day = startDate.getDayOfMonth();

							LocalTime startTime = timePickerStart.getTime();
							int hour = startTime.getHour();
							int minute = startTime.getMinute();
							int secconds = startTime.getSecond();

							Calendar start = Calendar.getInstance();
							start.set(year, month-1, day, hour, minute, secconds);
							
							LocalDate endDate = datePickerEnd.getDate();
							int year1 = endDate.getYear();
							int month1 = endDate.getMonthValue();
							int day1 = endDate.getDayOfMonth();

							LocalTime endtTime = timePickerEnd.getTime();
							int hour1 = endtTime.getHour();
							int minute1 = endtTime.getMinute();
							int secconds1 = endtTime.getSecond();

							Calendar end = Calendar.getInstance();
							end.set(year1, month1-1, day1, hour1, minute1, secconds1);
							
							not = chckbxNotDate.isSelected();
							if(filter.getNumOfFilters()==0) { //only one filter
								filter.createDateFilter(start, end, not);
							}
							else {
								filter.addDateFilter(start, end, not, relation);
							}
							
							
							break;

						case "Location:":
							double lat = Double.parseDouble(textFieldLat.getText());
							double lon = Double.parseDouble(textFieldLon.getText());
							double radius = Double.parseDouble(textFieldRadius.getText());
							not = checkBoxNotLoc.isSelected();
							if(filter.getNumOfFilters()==0) { //only one filter
								filter.createLocationFilter(lat, lon, radius, not);
							}
							else {
								filter.addLocationFilter(lat, lon, radius, not, relation);
							}

							break;
						case "ID:":
							String id = textFieldId.getText();
							not = checkBoxNotId.isSelected();
							if(filter.getNumOfFilters()==0) { //only one filter
								filter.createIDFilter(id, not);
							}
							else {
								filter.addIDFilter(id, not, relation);
							}
							break;

						default:
							break;
						}

						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);

			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}



	}
	public void updateDateRadio(boolean b) {
		if(b) {
			txtStartDate.setEnabled(true);
			datePickerStart.getComponentDateTextField().setEnabled(true);
			datePickerStart.getComponentToggleCalendarButton().setEnabled(true);
			datePickerStart.getComponentToggleCalendarButton().setVisible(true);
			timePickerStart.getComponentTimeTextField().setEnabled(true);
			timePickerStart.getComponentToggleTimeMenuButton().setEnabled(true);
			timePickerStart.getComponentToggleTimeMenuButton().setVisible(true);
			txtEndDate.setEnabled(true);
			datePickerEnd.getComponentDateTextField().setEnabled(true);
			datePickerEnd.getComponentToggleCalendarButton().setEnabled(true);
			datePickerEnd.getComponentToggleCalendarButton().setVisible(true);
			timePickerEnd.getComponentTimeTextField().setEnabled(true);
			timePickerEnd.getComponentToggleTimeMenuButton().setEnabled(true);
			timePickerEnd.getComponentToggleTimeMenuButton().setVisible(true);
			chckbxNotDate.setEnabled(true);			 
		}
		else {
			txtStartDate.setEnabled(false);
			datePickerStart.getComponentDateTextField().setEnabled(false);
			datePickerStart.getComponentToggleCalendarButton().setEnabled(false);
			datePickerStart.getComponentToggleCalendarButton().setVisible(false);
			timePickerStart.getComponentTimeTextField().setEnabled(false);
			timePickerStart.getComponentToggleTimeMenuButton().setEnabled(false);
			timePickerStart.getComponentToggleTimeMenuButton().setVisible(false);
			txtEndDate.setEnabled(false);
			datePickerEnd.getComponentDateTextField().setEnabled(false);
			datePickerEnd.getComponentToggleCalendarButton().setEnabled(false);
			datePickerEnd.getComponentToggleCalendarButton().setVisible(false);
			timePickerEnd.getComponentTimeTextField().setEnabled(false);
			timePickerEnd.getComponentToggleTimeMenuButton().setEnabled(false);
			timePickerEnd.getComponentToggleTimeMenuButton().setVisible(false);
			chckbxNotDate.setEnabled(false); 
		}
	}

	public void updateLocationRadio(boolean b) {
		if(b) {
			txtLat.setEnabled(true);
			txtLon.setEnabled(true);
			txtRadius.setEnabled(true);
			textFieldLat.setEnabled(true);
			textFieldLon.setEnabled(true);
			textFieldRadius.setEnabled(true);
			checkBoxNotLoc.setEnabled(true);
		}
		else {
			txtLat.setEnabled(false);
			txtLon.setEnabled(false);
			txtRadius.setEnabled(false);
			textFieldLat.setEnabled(false);
			textFieldLon.setEnabled(false);
			textFieldRadius.setEnabled(false);
			checkBoxNotLoc.setEnabled(false);
		}
	}

	public void updateIdRadio(boolean b) {
		if(b) {
			textFieldId.setEnabled(true);
			checkBoxNotId.setEnabled(true);
		}
		else{
			textFieldId.setEnabled(false);
			checkBoxNotId.setEnabled(false); 
		}
	}
}
