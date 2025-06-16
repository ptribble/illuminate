/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

package org.tribblix.illuminate.helpers;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import uk.co.petertribble.jingle.JingleUtils;

/**
 * A Scrollable panel displaying a man page, with minimal navigation.
 *
 * @author Peter C. Tribble (peter.tribble@gmail.com)
 */
public final class ManPane extends JEditorPane
		implements ActionListener, HyperlinkListener {

    private static final long serialVersionUID = 1L;

    /**
     * A JLabel with the name of the manual page being displayed.
     */
    private JLabel manLabel;
    /**
     * A JButton to go back to the previous page.
     */
    private JButton backButton;
    /**
     * The JEditorPane with the man page content.
     */
    private JEditorPane jep;

    /**
     * list of opened pages for history.
     */
    private transient List<File> historyList = new ArrayList<>();

    /*
     * This is hard-coded, unfortunately
     */
    private static final String[] MANPATH = {"/usr/share/man",
					     "/usr/gnu/share/man"};

    /**
     * Create a Scrollable panel containing the text of a man page.
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

	showMan(m);
    }

    /**
     * Display a new manual page in place of the current one.
     *
     * @param m the name of the new manual page to be displayed.
     */
    public void showMan(String m) {
	showMan(findManPage(m));
    }

    private void showMan(File f) {
	if (f != null) {
	    String m = f.toString();
	    String[] fullcmd =
		{"/usr/bin/mandoc", "-T", "html","-O", "man='%N.%S'", m};
	    RunCommand rc = new RunCommand(fullcmd);
	    jep.setText(rc.getOut());
	    jep.setMargin(JingleUtils.defInsets());
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
	    historyList.remove(s - 1);
	    File f = historyList.get(s - 2);
	    historyList.remove(s - 2);
	    showMan(f);
	}
    }

    /*
     * given a manpage in the form name.section, attempt to find the file
     * that corresponds to that man page
     *
     * return null if we can't find anything
     */
    private File findManPage(String mpage) {
	/*
	 * The Hyperlink event is quoted, so dequote
	 */
	String m = mpage.replaceAll("'", "");
	/*
	 * First split off the section
	 */
	int i = m.lastIndexOf('.');
	// man pages must have something before the dot
	if (i < 1) {
	    return null;
	}
	/*
	 * Cross references invariably use the uppercase form of the section
	 * ie 1M rather than 1m, so lowercase it
	 */
	String ext = m.substring(i + 1).toLowerCase(Locale.ENGLISH);
	String name = m.substring(0, i);
	for (String dir : MANPATH) {
	    File f = new File(dir + "/man" + ext + "/" + name + "." + ext);
	    if (f.exists()) {
		return f;
	    }
	}
	return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	goBack();
    }

    @Override
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
		    new RunBrowser(ev.getURL());
		}
	    }
	}
    }
}
