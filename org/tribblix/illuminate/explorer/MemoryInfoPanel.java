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

import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.demo.JKmemPanel;
import org.tribblix.illuminate.InfoCommand;

/**
 * MemoryInfoPanel - shows Memory status.
 * @author Peter Tribble
 * @version 1.0
 *
 */
public final class MemoryInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    private transient JKstat jkstat;
    /**
     * A panel to show memory allocations.
     */
    private JKmemPanel kmPanel;
    /**
     * A panel to show ARC data.
     */
    private ArcStatPanel arcPanel;

    /**
     * Display a Memory information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public MemoryInfoPanel(SysItem hi, JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;

	switch (hi.getType()) {
	    case SysItem.MEM_ARCSTAT:
		displayArc();
		break;
	    case SysItem.MEM_KMEM:
		displayKmem();
		break;
	    case SysItem.MEM_CONTAINER:
		displaySummary();
		break;
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (kmPanel != null) {
	    kmPanel.stopLoop();
	}
	if (arcPanel != null) {
	    arcPanel.stopLoop();
	}
    }

    /*
     * Top level summary. just swap
     */
    private void displaySummary() {
	addLabel("Memory Summary");
	addText(new CommandTableModel(
			new InfoCommand("swap", "/usr/sbin/swap", "-l")));
    }

    /*
     * ZFS arc stats
     */
    private void displayArc() {
	addLabel("ZFS ARC statistics");
	arcPanel = new ArcStatPanel(jkstat, 5);
	addScrollPane(arcPanel);
    }

    /*
     * kmem allocation statistics
     */
    private void displayKmem() {
	addLabel("Kernel memory allocation");
	kmPanel = new JKmemPanel(jkstat, 5);
	addScrollPane(kmPanel);
    }
}
