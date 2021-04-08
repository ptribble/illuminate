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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;

/**
 * SmfListCellRenderer - custom cell renderer for SMF services.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfListCellRenderer extends DefaultListCellRenderer {

    private static final Color lcolor = new Color(127, 255, 255);
    private static final Color mcolor = new Color(255, 127, 127);
    private static final Color dcolor = new Color(127, 127, 255);
    private static final Color oncolor = new Color(127, 255, 127);
    private static final Color offcolor = Color.ORANGE;

    @Override
    public Component getListCellRendererComponent(JList list,
	    Object value, int index, boolean isSelected, boolean cellHasFocus) {

	super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
	if (value instanceof SmfService) {
	    SmfService svc = (SmfService) value;
	    setText(svc.getFMRI());
	    Color color = getStatusColor(svc);
	    if (color != null) {
		setBackground(isSelected ? color : color.brighter());
	    }
	}
	return this;
    }

    private Color getStatusColor(SmfService svc) {
	if ("online".equals(svc.getStatus())) {
	    return oncolor;
	} else if ("offline".equals(svc.getStatus())) {
	    return offcolor;
	} else if ("legacy_run".equals(svc.getStatus())) {
	    return lcolor;
	} else if ("disabled".equals(svc.getStatus())) {
	    return dcolor;
	} else if ("maintenance".equals(svc.getStatus())) {
	    return mcolor;
	}
	return null;
    }
}
