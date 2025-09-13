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

import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 * InfoJList - a customized JList with tooltips.
 * @author Peter Tribble
 * @version 1.0
 */
public class InfoJList extends JList<InfoCommand> {

    private static final long serialVersionUID = 1L;

    /**
     * Display a list of commands.
     *
     * @param iclist An InfoCommandList
     */
    public InfoJList(final InfoCommandList iclist) {
	super(iclist);
    }

    /**
     * Return a tooltip containing the command that will be run.
     *
     * @param me the MouseEvent that generates the tooltip
     *
     * @return the tooltip for the command
     */
    @Override
    public String getToolTipText(final MouseEvent me) {
	Object o = this.getModel().getElementAt(locationToIndex(me.getPoint()));
	if (o instanceof InfoCommand) {
	    return ((InfoCommand) o).getFullCmd();
	}
	return null;
    }
}
