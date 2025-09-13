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
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.BorderLayout;
import java.awt.Cursor;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.NativeJKstat;
import org.tribblix.illuminate.InfoCommand;

/**
 * SysPanel - shows hardware in the system.
 * @author Peter Tribble
 * @version 1.0
 */
public final class SysPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * The main display panel.
     */
    private SysInfoPanel hip;

    /**
     * Display a Sys information panel.
     */
    public SysPanel() {
	this(new NativeJKstat());
    }

    /**
     * Display a Sys information panel.
     *
     * @param jkstat A JKstat object
     */
    public SysPanel(final JKstat jkstat) {
	/*
	 * I'm not sure what this will eventually look like. I'm planning
	 * on something that just has big buttons "cpu" "memory" "storage"
	 * "network", but as the underlying data model is a hierarchical
	 * view the initial display is as a tree.
	 */
	setLayout(new BorderLayout());

	InfoCommand ic = new InfoCommand("hn", "/usr/bin/hostname");
	final SysTree ptree = new SysTree(ic.getOutput());
	// should this be set inside the SysTree itself?
	ptree.setCellRenderer(new SysTreeCellRenderer());
	ptree.addTreeSelectionListener(new TreeSelectionListener() {
	    @Override
	    public void valueChanged(final TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		    ptree.getLastSelectedPathComponent();

		if (node != null) {
		    setInfo(node.getUserObject());
		}
	    }
	});
	// end of tree listener


	hip = new SysInfoPanel(jkstat);

	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		new JScrollPane(ptree), hip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(160);
	add(psplit);
    }

    void setInfo(final Object o) {
	if (o instanceof SysItem) {
	    Cursor c = getCursor();
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    hip.display((SysItem) o);
	    setCursor(c);
	}
    }
}
