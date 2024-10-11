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

import uk.co.petertribble.jkstat.api.*;
import uk.co.petertribble.jkstat.demo.JKmemPanel;
import org.tribblix.illuminate.InfoCommand;

/**
 * MemoryInfoPanel - shows Memory status.
 * @author Peter Tribble
 * @version 1.0
 *
 */
public class MemoryInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    private JKstat jkstat;
    private JKmemPanel kmPanel;
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
