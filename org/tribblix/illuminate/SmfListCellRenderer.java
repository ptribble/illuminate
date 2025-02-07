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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;

/**
 * SmfListCellRenderer - custom cell renderer for SMF services.
 * @author Peter Tribble
 * @version 1.0
 */
public final class SmfListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final Color LCOLOR = new Color(151, 255, 255);
    private static final Color MCOLOR = new Color(255, 151, 151);
    private static final Color DCOLOR = new Color(151, 151, 255);
    private static final Color ONCOLOR = new Color(151, 255, 151);
    private static final Color OFFCOLOR = Color.ORANGE;

    @Override
    public Component getListCellRendererComponent(JList list,
	    Object value, int index, boolean isSelected, boolean cellHasFocus) {

	super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
	if (value instanceof SmfService) {
	    SmfService svc = (SmfService) value;
	    // strip the scheme
	    String serv = svc.getFMRI();
	    int ioff = serv.startsWith("svc:") ? 2 : 1;
	    setText(serv.substring(serv.indexOf(':') + ioff));
	    Color color = getStatusColor(svc);
	    if (color != null) {
		setBackground(isSelected ? color : color.brighter());
	    }
	}
	return this;
    }

    private Color getStatusColor(SmfService svc) {
	if ("online".equals(svc.getStatus())) {
	    return ONCOLOR;
	} else if ("offline".equals(svc.getStatus())) {
	    return OFFCOLOR;
	} else if ("legacy_run".equals(svc.getStatus())) {
	    return LCOLOR;
	} else if ("disabled".equals(svc.getStatus())) {
	    return DCOLOR;
	} else if ("maintenance".equals(svc.getStatus())) {
	    return MCOLOR;
	}
	return null;
    }
}
