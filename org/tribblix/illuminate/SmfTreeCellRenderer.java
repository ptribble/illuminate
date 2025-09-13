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

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.net.URL;

/**
 * smfTreeCellRenderer - adds tooltips and custom icons to an SMF tree.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public final class SmfTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    private static ImageIcon failIcon;
    private static ImageIcon okIcon;

    /**
     * Create an SmfTreeCellRenderer.
     */
    public SmfTreeCellRenderer() {
	failIcon = createImageIcon("/images/fail.png");
	okIcon = createImageIcon("/images/ok.png");
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree,
						  final Object value,
						  final boolean sel,
						  final boolean expanded,
						  final boolean leaf,
						  final int row,
						  final boolean hasFocus) {

	super.getTreeCellRendererComponent(tree, value, sel,
					   expanded, leaf, row,
					   hasFocus);
	Object o = (DefaultMutableTreeNode) value;
	if (o instanceof SmfTreeNode) {
	    SmfTreeNode stn = (SmfTreeNode) o;
	    if ("online".equals(stn.getStatus())) {
		setIcon(okIcon);
	    }
	    if ("maintenance".equals(stn.getStatus())) {
		setIcon(failIcon);
	    }
	    SmfService ss = (SmfService) stn.getUserObject();
	    setToolTipText(ss == null ? null : ss.getName());
	} else {
	    setToolTipText(null);
	}
	return this;
    }

    /*
     * Based on the Java Swing tutorial examples.
     */
    private ImageIcon createImageIcon(final String s) {
	URL imgURL = getClass().getResource(s);
	return (imgURL == null) ? null : new ImageIcon(imgURL);
    }
}
