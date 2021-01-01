/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

package org.tribblix.illuminate.helpers;

import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A Scrollable panel displaying a man page, with minimal navigation
 *
 * @author Peter C. Tribble (peter.tribble@gmail.com)
 */
public class ManPane extends JEditorPane
		implements ActionListener, HyperlinkListener {

    /*
     * navigation controls
     */
    private JLabel manLabel;
    private JButton backButton;
    private JEditorPane jep;

    /*
     * list of opened pages for history
     */
    private List <File> historyList;

    /*
     * commands to open external URLs
     */
    private static String browserExe;
    private static final String[] browsers = { "exo-open", "firefox" };

    /*
     * This is hard-coded, unfortunately
     */
    private static final String[] manpath = { "/usr/share/man",
					"/usr/gnu/share/man" };

    /**
     * Create a Scrollable panel containing the text of a man page
     *
     * @param m The name of the man page
     */
    public ManPane(String m) {
	setLayout(new BorderLayout());

	JToolBar jtb = new JToolBar();
	jtb.setFloatable(false);
	jtb.setRollover(true);
	backButton = new JButton("<");
	backButton.addActionListener(this);
	manLabel = new JLabel();
	jtb.add(backButton);
	jtb.add(manLabel);

	jep = new JEditorPane();
	jep.setContentType("text/html");
	jep.addHyperlinkListener(this);

	add(jtb, BorderLayout.PAGE_START);
	add(new JScrollPane(jep), BorderLayout.CENTER);

	historyList = new ArrayList <File> ();
	showMan(m);
    }

    public void showMan(String m) {
	showMan(findManPage(m));
    }

    private void showMan(File f) {
	if (f != null) {
	    String m = f.toString();
	    RunCommand rc =
		new RunCommand("/usr/bin/mandoc -T html -O man='%N.%S' " + m);
	    jep.setText(rc.getOut());
	    jep.setMargin(new Insets(5, 5, 5, 5));
	    jep.setCaretPosition(0);
	    jep.setEditable(false);
	    historyList.add(f);
	    manLabel.setText(m);
	    backButton.setEnabled(historyList.size() > 1);
	}
    }

    /*
     * show the previous man page in the history
     *
     * remove the current page from the history
     * save and then remove the previous page from the history
     * then call showMan() which will add it back to the history list
     */
    private void goBack() {
	int s = historyList.size();
	if (s > 1) {
	    historyList.remove(s-1);
	    File f = historyList.get(s-2);
	    historyList.remove(s-2);
	    showMan(f);
	}
    }

    /*
     * given a manpage in the form name.section, attempt to find the file
     * that corresponds to that man page
     *
     * return null if we can't find anything
     */
    private File findManPage(String m) {
	/*
	 * The Hyperlink event is quoted, so dequote
	 */
	m = m.replaceAll("'", "");
	/*
	 * First split off the section
	 */
	int i = m.lastIndexOf('.');
	// man pages must have something before the dot
	if (i < 1) {
	    return (File) null;
	}
	/*
	 * Cross references invariably use the uppercase form of the section
	 * ie 1M rather than 1m, so lowercase it
	 */
	String rawext = m.substring(i+1);
	String ext = rawext.toLowerCase(Locale.ENGLISH);
	String name = m.substring(0, i);
	for (String dir : manpath) {
	    File f = new File(dir + "/man" + ext + "/" + name + "." + ext);
	    if (f.exists()) {
		return f;
	    }
	}
	return (File) null;
    }

    public void actionPerformed(ActionEvent e) {
	goBack();
    }

    public void hyperlinkUpdate(HyperlinkEvent ev) {
	if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    File f = findManPage(ev.getDescription());
	    if (f != null) {
		showMan(f);
	    } else {
		/*
		 * if not a manpage, try as a URL
		 */
		if (ev.getURL() != null && Desktop.isDesktopSupported()) {
		    try {
			Desktop.getDesktop().browse(ev.getURL().toURI());
		    } catch (Exception e) {
			try {
			    if (browserExe == null) {
				for (String b : browsers) {
				    File f2 = new File("/usr/bin", b);
				    if (f2.exists()) {
					browserExe = "/usr/bin/" + b;
					break;
				    }
				}
			    }
			    if (browserExe != null) {
				Runtime.getRuntime().exec(new String[]
				    {browserExe, ev.getURL().toString()});
			    }
			} catch (IOException e2) {
			    System.out.println(e2);
			}
		    }
		}
	    }
	}
    }
}
