package org.tribblix.illuminate;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import uk.co.petertribble.jingle.JingleMultiFrame;
import uk.co.petertribble.jingle.JingleInfoFrame;
import org.tribblix.illuminate.explorer.SysPanel;
import org.tribblix.illuminate.pkgview.InstalledSoftwarePanel;
import org.tribblix.illuminate.pkgview.IPSSoftwarePanel;
import uk.co.petertribble.jkstat.api.NativeJKstat;
import uk.co.petertribble.jkstat.demo.KstatToolsMenu;

/**
 * Illuminate - shows system information.
 * @author Peter Tribble
 * @version 1.0
 */
public class Illuminate extends JFrame implements ActionListener {

    private final JMenuItem exitItem;
    private final JMenuItem cloneItem;
    private final JMenuItem closeItem;
    private final JMenuItem helpItem;
    private final JMenuItem licenseItem;

    public Illuminate() {
	super("Illuminate");

	addWindowListener(new WinExit());

	JTabbedPane jtp = new JTabbedPane();
	getContentPane().add(jtp, BorderLayout.CENTER);

	JMenuBar jm = new JMenuBar();

	jtp.add(IlluminateResources.getString("ILLUMINATE.EXPL.TEXT"),
		new SysPanel());
	jtp.add(IlluminateResources.getString("ILLUMINATE.INFO.TEXT"),
		new InfoCommandPanel());
	jtp.add(IlluminateResources.getString("ILLUMINATE.SERV.TEXT"),
		new SmfPanel());
	if (new File("/usr/bin/pkg").exists()) {
	    jtp.add(IlluminateResources.getString("ILLUMINATE.SOFT.TEXT"),
		new IPSSoftwarePanel());
	} else {
	    jtp.add(IlluminateResources.getString("ILLUMINATE.SOFT.TEXT"),
		new InstalledSoftwarePanel("/"));
	}

	JMenu jme = new JMenu(IlluminateResources.getString("FILE.TEXT"));
	jme.setMnemonic(KeyEvent.VK_F);
	cloneItem = new JMenuItem(
			IlluminateResources.getString("FILE.NEWBROWSER"),
			KeyEvent.VK_B);
	cloneItem.addActionListener(this);
	jme.add(cloneItem);
	closeItem = new JMenuItem(
				IlluminateResources.getString("FILE.CLOSEWIN"),
				KeyEvent.VK_W);
	closeItem.addActionListener(this);
	jme.add(closeItem);
	exitItem = new JMenuItem(IlluminateResources.getString("FILE.EXIT"),
				KeyEvent.VK_X);
	exitItem.addActionListener(this);
	jme.add(exitItem);

	JingleMultiFrame.register(this, closeItem);

	jm.add(jme);
	jm.add(new IllumosToolsMenu());

	/*
	 * Add a menu of Kstat tools
	 */
	KstatToolsMenu ktm = new KstatToolsMenu(new NativeJKstat());
	ktm.setMnemonic(KeyEvent.VK_K);
	jm.add(ktm);

	JMenu jmh = new JMenu(IlluminateResources.getString("HELP.TEXT"));
	jmh.setMnemonic(KeyEvent.VK_H);
	helpItem = new JMenuItem(
			IlluminateResources.getString("HELP.ABOUT.ILLUMINATE"),
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
			.getResource("pixmaps/illuminate.png")).getImage());

	setSize(720, 600);
	setVisible(true);
    }

    class WinExit extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent we) {
	    JingleMultiFrame.unregister(Illuminate.this);
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == cloneItem) {
	    new Illuminate();
	}
	if (e.getSource() == closeItem) {
	    JingleMultiFrame.unregister(this);
	}
	if (e.getSource() == exitItem) {
	    System.exit(0);
	}
	if (e.getSource() == helpItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				"help/index.html", "text/html");
	}
	if (e.getSource() == licenseItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				"help/CDDL.txt", "text/plain");
	}
    }

    /**
     * Create a new illuminate application from the command line.
     *
     * @param args command line arguments, ignored
     */
    public static void main(String args[]) {
	new Illuminate();
    }
}
