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
	public static String run(String title) {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle(title);

		int returnValue = jfc.showDialog(null,"Choose");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			if (jfc.getSelectedFile().isFile()) {
				return jfc.getSelectedFile().getPath();
			}
		}
		return null;

	}

}
