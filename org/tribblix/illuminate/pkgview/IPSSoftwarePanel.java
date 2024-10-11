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

