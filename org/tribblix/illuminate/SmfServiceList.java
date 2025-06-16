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

package org.tribblix.illuminate;

import java.util.Vector;
import org.tribblix.illuminate.helpers.RunCommand;

/**
 * SmfServiceList - a list of all SMF services.
 * @author Peter Tribble
 * @version 1.0
 */
public final class SmfServiceList extends Vector<SmfService> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a list of SMF services for the current system.
     */
    public SmfServiceList() {
	String[] fullcmd = {"/usr/bin/svcs", "-aH"};
	RunCommand svcs = new RunCommand(fullcmd);
	// parse the svcs output to get the service name and status
	for (String s : svcs.getOut().split("\n")) {
	    String[] ds = s.split("\\s+", 3);
	    add(new SmfService(ds[2], ds[0]));
	}
    }
}
