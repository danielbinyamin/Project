package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;

import java.awt.event.ActionListener;
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

	private final JPanel contentPanel = new JPanel();
	private JTextField txtStartDate;
	private JTextField txtEndDate;
	private JTextField txtLat;
	private JTextField txtLon;
	private JTextField txtRadius;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;

	/**
	 * Launch the application.
	 */
	public static void run() {
		try {
			filterPicker dialog = new filterPicker();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public filterPicker() {
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
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Date: ");
		rdbtnNewRadioButton.setBounds(32, 58, 63, 25);
		contentPanel.add(rdbtnNewRadioButton);
		
		txtStartDate = new JTextField();
		txtStartDate.setEditable(false);
		txtStartDate.setText("Start date: ");
		txtStartDate.setBounds(88, 92, 116, 22);
		contentPanel.add(txtStartDate);
		txtStartDate.setColumns(10);
		
		//start date
		DatePicker datePickerStart = new DatePicker();
		datePickerStart.getComponentToggleCalendarButton().setBounds(131, 12, 23, 23);
		datePickerStart.getComponentDateTextField().setBounds(0, 12, 131, 23);
        datePickerStart.setBounds(212, 82, 160, 43);
        datePickerStart.setVisible(true);
        contentPanel.add(datePickerStart);
        datePickerStart.setLayout(null);
        //start time
        TimePicker timePickerStart = new TimePicker();
        timePickerStart.getComponentToggleTimeMenuButton().setBounds(92, 0, 21, 22);
        timePickerStart.getComponentTimeTextField().setBounds(12, 0, 81, 22);
        timePickerStart.setBounds(385,93,125,22);
        timePickerStart.setVisible(true);
        contentPanel.add(timePickerStart);
        timePickerStart.setLayout(null);
        
        //end date
        DatePicker datePickerEnd = new DatePicker();
		datePickerEnd.getComponentToggleCalendarButton().setBounds(131, 12, 23, 23);
		datePickerEnd.getComponentDateTextField().setBounds(0, 12, 131, 23);
        datePickerEnd.setBounds(212,139, 166, 43);
        datePickerEnd.setVisible(true);
        contentPanel.add(datePickerEnd);
        datePickerEnd.setLayout(null);
        //end Time
        TimePicker timePickerEnd = new TimePicker();
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
		
		JRadioButton rdbtnLocation = new JRadioButton("Location:");
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
		
		textField = new JTextField();
		textField.setBounds(212, 237, 160, 22);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(212, 262, 160, 22);
		contentPanel.add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(212, 287, 160, 22);
		contentPanel.add(textField_2);
		
		JRadioButton rdbtnId = new JRadioButton("ID:");
		rdbtnId.setBounds(32, 354, 45, 25);
		contentPanel.add(rdbtnId);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(88, 355, 160, 22);
		contentPanel.add(textField_3);
		
		JCheckBox chckbxNot = new JCheckBox("NOT");
		chckbxNot.setBounds(375, 58, 113, 25);
		contentPanel.add(chckbxNot);
		
		JCheckBox checkBox = new JCheckBox("NOT");
		checkBox.setBounds(375, 203, 113, 25);
		contentPanel.add(checkBox);
		
		JCheckBox checkBox_1 = new JCheckBox("NOT");
		checkBox_1.setBounds(375, 354, 113, 25);
		contentPanel.add(checkBox_1);
		
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
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
