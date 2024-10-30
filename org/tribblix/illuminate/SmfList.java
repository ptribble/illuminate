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

package org.tribblix.illuminate;

import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 * SmfList - shows SMF services in a JList.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfList extends JList<SmfService> {

    private static final long serialVersionUID = 1L;

    /**
     * Display a list of SMF services.
     *
     * @param sslist An SmfServiceList
     */
    public SmfList(SmfServiceList sslist) {
	super(sslist);
	setCellRenderer(new SmfListCellRenderer());
    }

    /**
     * Return a tooltip containing the command that will be run.
     *
     * @param me the MouseEvent that generates the tooltip
     *
     * @return the tooltip for the command
     */
    @Override
    public String getToolTipText(MouseEvent me) {
	Object o = this.getModel().getElementAt(locationToIndex(me.getPoint()));
	if (o instanceof SmfService) {
	    return ((SmfService) o).getName();
	}
	return null;
    }
}
