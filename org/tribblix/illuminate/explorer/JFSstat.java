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
public class JFSstat extends JKdemo implements ActionListener {

    private static final String sversion = "JFSstat version 1.0";
    private Set <JRadioButtonMenuItem> displayset;
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

	addInfoPanel(mainPanel, sversion);

	displayset = new HashSet <JRadioButtonMenuItem> ();
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
