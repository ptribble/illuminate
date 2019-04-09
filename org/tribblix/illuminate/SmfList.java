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

import javax.swing.JList;
import javax.swing.DefaultListModel;

/**
 * SmfList - shows SMF services in a JList.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfList extends JList <SmfService> {

    /**
     * Display a list of SMF services.
     *
     * @param smfutil An SmfUtils object
     */
    public SmfList(SmfUtils smfutil) {
	DefaultListModel<SmfService> model = new DefaultListModel<SmfService>();
	for (SmfService svc : smfutil.getServices()) {
	    model.addElement(svc);
	}
	setModel(model);
	setCellRenderer(new SmfListCellRenderer());
    }
}
