package org.tribblix.illuminate;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import org.tribblix.illuminate.explorer.SysPanel;
import org.tribblix.illuminate.pkgview.InstalledSoftwarePanel;
import uk.co.petertribble.jingle.JingleInfoFrame;

/**
 * MultiView - shows system information.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class MultiView extends JFrame implements ActionListener {

    private JMenuItem exitItem;
    private JMenuItem helpItem;
    private JMenuItem licenseItem;
    private String helpfile;

    /**
     * Create a new MultiView.
     *
     * @param stitle a String to use as the window title
     * @param hlabel a String to use for the help menu button
     * @param helpfile a String naming the help file to use
     * @param mypixmap a String naming the application icon to use
     * @param panel The panel to display
     */
    public MultiView(String stitle, String hlabel, String helpfile,
		String mypixmap, JComponent panel) {
	super(IlluminateResources.getString(stitle));
	this.helpfile = helpfile;

	addWindowListener(new winExit());
	getContentPane().add(panel, BorderLayout.CENTER);

	JMenuBar jm = new JMenuBar();

	JMenu jme = new JMenu(IlluminateResources.getString("FILE.TEXT"));
	jme.setMnemonic(KeyEvent.VK_F);
	exitItem = new JMenuItem(IlluminateResources.getString("FILE.EXIT"),
				KeyEvent.VK_X);
	exitItem.addActionListener(this);
	jme.add(exitItem);

	jm.add(jme);

	JMenu jmh = new JMenu(IlluminateResources.getString("HELP.TEXT"));
	jmh.setMnemonic(KeyEvent.VK_H);
	helpItem = new JMenuItem(IlluminateResources.getString(hlabel),
				KeyEvent.VK_A);
	helpItem.addActionListener(this);
	jmh.add(helpItem);
	licenseItem = new JMenuItem(
			IlluminateResources.getString("HELP.LICENSE.TEXT"),
			KeyEvent.VK_L);
	licenseItem.addActionListener(this);
	jmh.add(licenseItem);

	jm.add(jmh);
	setJMenuBar(jm);

	setIconImage(new ImageIcon(this.getClass().getClassLoader()
			.getResource(mypixmap)).getImage());

	setSize(720, 600);
	setVisible(true);
    }

    class winExit extends WindowAdapter {
	public void windowClosing(WindowEvent we) {
	    System.exit(0);
	}
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == exitItem) {
	    System.exit(0);
	}
	if (e.getSource() == helpItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				helpfile, "text/html");
	}
	if (e.getSource() == licenseItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				"help/CDDL.txt", "text/plain");
	}
    }

    /**
     * Create a new view from the command line.
     *
     * @param args command line arguments, the first argument representing
     * the view to show.
     */
    public static void main(String args[]) {
	if (args[0].equals("information")) {
	    new MultiView("ILLUMINATE.INFO.TEXT", "HELP.ABOUT.INFO",
			"help/infoview.html", "pixmaps/solinfo.png",
			new InfoCommandPanel());
	} else if (args[0].equals("explorer")) {
	    new MultiView("ILLUMINATE.EXPL.TEXT", "HELP.ABOUT.EXPL",
			"help/explorer.html", "pixmaps/solexpl.png",
			new SysPanel());
	} else if (args[0].equals("services")) {
	    new MultiView("ILLUMINATE.SERV.TEXT", "HELP.ABOUT.SERV",
			"help/serviceview.html", "pixmaps/smfview.png",
			new SmfPanel());
	} else if (args[0].equals("software")) {
	    String altroot = "/";
	    if (args.length == 3 && "-R".equals(args[1])) {
		altroot = args[2];
	    }
	    new MultiView("ILLUMINATE.SOFT.TEXT", "HELP.ABOUT.SOFT",
			"help/software.html", "pixmaps/solinfo.png",
			new InstalledSoftwarePanel(altroot));
	}
    }
}
