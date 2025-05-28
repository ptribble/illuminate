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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jingle.TableSorter;
import java.util.Set;

/**
 * Display fsstat tabular output in a panel.
 * @author Peter Tribble
 * @version 1.0
 */
public final class JFSstatPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private FsstatTableModel ftm;

    /**
     * Create a new JFSstatPanel.
     *
     * @param jkstat a JKstat object
     * @param interval the desired update interval in seconds
     */
    public JFSstatPanel(JKstat jkstat, int interval) {
	setLayout(new BorderLayout());

	ftm = new FsstatTableModel(jkstat, interval);
	TableSorter sortedModel = new TableSorter(ftm);
	JTable sftmtable = new JTable(sortedModel);
	add(new JScrollPane(sftmtable));
	sortedModel.setTableHeader(sftmtable.getTableHeader());
    }

    /**
     * Set the display type.
     *
     * @param s the display to show
     */
    public void setNames(String s) {
	ftm.setNames(s);
    }

    /**
     * Stop the timer loop, so that the table will no longer be updated.
     */
    public void stopLoop() {
	ftm.stopLoop();
    }

    /**
     * Set the update delay.
     *
     * @param i  the desired update delay, in seconds
     */
    public void setDelay(int i) {
	ftm.setDelay(i);
    }

    /**
     * Determine whether ignored filesystems are shown.
     *
     * @param b true if ignored filesystems should be shown.
     */
    public void showIgnored(boolean b) {
	ftm.showIgnored(b);
    }

    /**
     * Determine whether the aggregates of filesystem types are shown.
     *
     * @param b true if the aggregates by filesystem type should be shown.
     */
    public void showAggregates(boolean b) {
	ftm.showAggregates(b);
    }

    /**
     * Return the name of the current display type.
     *
     * @return the current display type
     */
    public String currentTitle() {
	return ftm.currentTitle();
    }

    /**
     * Return the available display titles. This is a Set of the predefined
     * display types.
     *
     * @return the Set of available display names
     */
    public Set<String> titles() {
	return ftm.titles();
    }
}
