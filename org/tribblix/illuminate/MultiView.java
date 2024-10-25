package org.tribblix.illuminate;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

    private static final long serialVersionUID = 1L;

    /**
     * A JMenuItem to exit the application.
     */
    private final JMenuItem exitItem;
    /**
     * A JMenuItem to show help.
     */
    private final JMenuItem helpItem;
    /**
     * A JMenuItem to show the license.
     */
    private final JMenuItem licenseItem;
    /**
     * The name of the help file.
     */
    private final String helpfile;

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

	addWindowListener(new WinExit());
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

    class WinExit extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent we) {
	    System.exit(0);
	}
    }

    @Override
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
    public static void main(String[] args) {
	if ("information".equals(args[0])) {
	    new MultiView("ILLUMINATE.INFO.TEXT", "HELP.ABOUT.INFO",
			"help/infoview.html", "pixmaps/solinfo.png",
			new InfoCommandPanel());
	} else if ("explorer".equals(args[0])) {
	    new MultiView("ILLUMINATE.EXPL.TEXT", "HELP.ABOUT.EXPL",
			"help/explorer.html", "pixmaps/solexpl.png",
			new SysPanel());
	} else if ("services".equals(args[0])) {
	    new MultiView("ILLUMINATE.SERV.TEXT", "HELP.ABOUT.SERV",
			"help/serviceview.html", "pixmaps/smfview.png",
			new SmfPanel());
	} else if ("software".equals(args[0])) {
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
