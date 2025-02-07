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

package org.tribblix.illuminate.pkgview;

import org.tribblix.illuminate.InfoCommand;
import org.tribblix.illuminate.explorer.InfoPanel;
import org.tribblix.illuminate.explorer.SysItem;
import org.tribblix.illuminate.explorer.CommandTableModel;

/**
 * A simple panel to display IPS information.
 * @author Peter Tribble
 * @version 1.0
 */
public class IPSSoftwarePanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Create a panel showing basic IPS packaging information.
     */
    public IPSSoftwarePanel() {
	super(new SysItem(0));
	addLabel("Installed Packages");
	addText(new CommandTableModel(new InfoCommand("pkg", "/usr/bin/pkg",
					"list"), "NAME VERSION IFO", 3));
    }
}

