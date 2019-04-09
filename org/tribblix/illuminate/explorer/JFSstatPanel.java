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

import javax.swing.*;
import java.awt.BorderLayout;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jingle.TableSorter;
import java.util.Set;

/**
 * Display fsstat tabular output in a panel.
 * @author Peter Tribble
 * @version 1.0
 */
public class JFSstatPanel extends JPanel {

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
     * @param b true if the aggreagtes by filesystem type should be shown.
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
    public Set <String> titles() {
	return ftm.titles();
    }
}
