package GUI;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class dirChooserToLoad {

	/**
	 * @wbp.parser.entryPoint
	 */
	public static String run() {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Choose a directory of WiggleWifi files: ");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnValue = jfc.showDialog(null,"Choose");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			if (jfc.getSelectedFile().isDirectory()) {
				return jfc.getSelectedFile().getPath();
			}
		}
		return null;

	}

}
