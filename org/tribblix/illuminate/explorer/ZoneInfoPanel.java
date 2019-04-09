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

import javax.swing.JLabel;
import org.tribblix.illuminate.InfoCommand;

/**
 * ZoneInfoPanel - shows Zone information.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZoneInfoPanel extends InfoPanel {

    /**
     * Display a zone information panel.
     *
     * @param hi The item to display
     */
    public ZoneInfoPanel(SysItem hi) {
	super(hi);

	if (hi.getType() == SysItem.ZONE_CONTAINER) {
	    displaySummary();
	} else if (hi.getType() == SysItem.ZONE_ZONE) {
	    displayZone();
	} else if (hi.getType() == SysItem.ZONE_PROC) {
	    displayZoneProc();
	} else if (hi.getType() == SysItem.ZONE_NET) {
	    displayZoneNet();
	}

	validate();
    }

    /*
     * Top level summary. List of Zones.
     */
    private void displaySummary() {
	jvp.add(new JLabel("Zone Summary"));
	addText(new CommandTableModel(new InfoCommand("za", "/usr/sbin/zoneadm",
					"list -icv")));
    }

    /*
     * Describe a Zone.
     */
    private void displayZone() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	jvp.add(new JLabel("Details of "+ze.getState()+" zone "+ze.getName()));
	addText(ze.getConfig());
    }

    /*
     * Show Zone processes
     */
    private void displayZoneProc() {
	jvp.add(new ProcessInfoPanel(hi));
    }

    /*
     * Show Zone network
     */
    private void displayZoneNet() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	jvp.add(new JLabel("Zone Network"));
	for (ZoneNet znet : ze.getNetworks()) {
	    addText(znet.getPhysical());
	}
    }
}
