import java.awt.Image;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

class ImageViewer {
	private JFrame oJFrame = new JFrame("Image");
	ImageIcon oImageIcon = new ImageIcon("images/basicProfileImage.PNG");
	JLabel oJLabel = new JLabel(oImageIcon);
	
	public ImageViewer (Image img)  {
		this.oImageIcon = new ImageIcon(img);
		oJLabel = new JLabel(oImageIcon);
		oJFrame.add(oJLabel);
		oJFrame.pack();
		oJFrame.setVisible(true);
		oJFrame.setResizable(false);
		oJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
}