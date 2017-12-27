package GUI;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class fileChooser {

	/**
	 * @wbp.parser.entryPoint
	 */
	public static File run() {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Choose combined CSV: ");

		int returnValue = jfc.showDialog(null,"Choose");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV FILES", "csv", "csv");
		jfc.setFileFilter(filter);
		jfc.addChoosableFileFilter(filter);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			if (jfc.getSelectedFile().isFile()) {
				return jfc.getSelectedFile();
			}
		}
		return null;

	}

}
