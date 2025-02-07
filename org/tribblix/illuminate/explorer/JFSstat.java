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

package org.tribblix.illuminate.explorer;

import java.util.Set;
import java.util.HashSet;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.NativeJKstat;
import uk.co.petertribble.jkstat.demo.JKdemo;
import org.tribblix.illuminate.IlluminateResources;

/**
 * A kstat demo emulating fsstat.
 * @author Peter Tribble
 * @version 1.0
 */
public final class JFSstat extends JKdemo implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SVERSION = "JFSstat version 1.0";
    private Set<JRadioButtonMenuItem> displayset = new HashSet<>();
    private JCheckBoxMenuItem hiddenItem;
    private JCheckBoxMenuItem aggrItem;

    private JFSstatPanel mainPanel;

    /**
     * Create a JFSstat demo.
     */
    public JFSstat() {
	this(new NativeJKstat(), true);
    }

    /**
     * Create a JFSstat demo.
     *
     * @param jkstat a JKstat object
     * @param standalone if false, indicates that this demo is being called
     * from another application
     */
    public JFSstat(JKstat jkstat, boolean standalone) {
	super("jfsstat", standalone);

	mainPanel = new JFSstatPanel(jkstat, DEFAULT_INTERVAL);

	setContentPane(mainPanel);

	addInfoPanel(mainPanel, SVERSION);

	JMenu displayMenu = new JMenu(IlluminateResources.getString(
						"FSSTAT.DISPLAY.TEXT"));
	displayMenu.setMnemonic(KeyEvent.VK_D);
	String curtitle = mainPanel.currentTitle();
	ButtonGroup displayGroup = new ButtonGroup();
	for (String title : mainPanel.titles()) {
	    JRadioButtonMenuItem jmis =
		new JRadioButtonMenuItem(title, title.equals(curtitle));
	    displayMenu.add(jmis);
	    displayGroup.add(jmis);
	    displayset.add(jmis);
	    jmis.addActionListener(this);
	}
	addMenu(displayMenu);

	JMenu showMenu = new JMenu(IlluminateResources.getString(
						"FSSTAT.SHOW.TEXT"));
	showMenu.setMnemonic(KeyEvent.VK_S);
	hiddenItem = new JCheckBoxMenuItem(IlluminateResources.getString(
					"FSSTAT.HIDDEN.TEXT"), false);
	hiddenItem.addActionListener(this);
	showMenu.add(hiddenItem);
	aggrItem = new JCheckBoxMenuItem(IlluminateResources.getString(
					"FSSTAT.FSTYPE.TEXT"), false);
	aggrItem.addActionListener(this);
	showMenu.add(aggrItem);
	addMenu(showMenu);

	setSize(620, 250);
	validate();
	setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	super.actionPerformed(e);
	JMenuItem jmi = (JMenuItem) e.getSource();
	if (displayset.contains(jmi)) {
	    mainPanel.setNames(jmi.getText());
	} else if (jmi == hiddenItem) {
	    mainPanel.showIgnored(hiddenItem.isSelected());
	} else if (jmi == aggrItem) {
	    mainPanel.showAggregates(aggrItem.isSelected());
	}
    }

    @Override
    public void stopLoop() {
	mainPanel.stopLoop();
    }

    @Override
    public void setDelay(int i) {
	mainPanel.setDelay(i);
	setLabelDelay(i);
    }

    /**
     * Create a standalone JFSstat demo application.
     *
     * @param args Command line arguments, unused
     */
    public static void main(String[] args) {
	new JFSstat();
    }
}
