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

import uk.co.petertribble.jproc.gui.JPinfoTable;
import uk.co.petertribble.jproc.gui.ZoneInfoTable;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcessSet;
import uk.co.petertribble.jproc.api.JProcessFilter;

/**
 * ProcessInfoPanel - shows Process status.
 * @author Peter Tribble
 * @version 1.0
 */
public final class ProcessInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    /**
     * A table for information by process.
     */
    private JPinfoTable jpip;
    /**
     * A table for information by zone.
     */
    private ZoneInfoTable zit;

    /**
     * Display a Process information panel.
     *
     * @param hi The item to display
     */
    public ProcessInfoPanel(SysItem hi) {
	super(hi);

	if (hi.getType() == SysItem.PROCESS_CONTAINER) {
	    displaySummary();
	}
	if (hi.getType() == SysItem.ZONE_PROC) {
	    displaySummary();
	}
	if (hi.getType() == SysItem.ZONE_USAGE) {
	    displayUsage();
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (jpip != null) {
	    jpip.stopLoop();
	}
	if (zit != null) {
	    zit.stopLoop();
	}
    }

    /*
     * Top level summary. List of Process.
     */
    private void displaySummary() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	addLabel((ze == null)
		? "Running Processes"
		: "Processes in zone " + ze.getName());
	JProc jproc = new JProc();
	jpip = new JPinfoTable(jproc, new JProcessFilter(), 5);
	jpip.removeColumn("CT");
	jpip.removeColumn("TASK");
	jpip.removeColumn("PROJ");
	jpip.removeColumn("GROUP");
	jpip.removeColumn("ppid");
	JProcessSet jps = new JProcessSet(jproc);
	if (jps.getZones().size() == 1) {
	    jpip.removeColumn("ZONE");
	}
	if (ze != null) {
	    jpip.setZone(ze.getZoneid());
	    jpip.removeColumn("ZONE");
	}
	addScrollPane(jpip);
    }

    /*
     * Top level summary. Aggregated process usage by zone.
     */
    private void displayUsage() {
	addLabel("Usage by zone");
        zit = new ZoneInfoTable(new JProc(), new JProcessFilter(), 5);
	addScrollPane(zit);
    }
}
