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

package org.tribblix.illuminate.explorer;

import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.net.URL;

/**
 * SysTreeCellRenderer - adds tooltips and custom icons to items in the
 * illuminate explorer tree.
 * @author Peter Tribble
 * @version 1.0
 *
 */
public final class SysTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    private static ImageIcon failIcon;
    private static ImageIcon warnIcon;
    private static ImageIcon unknownIcon;

    /**
     * Create a new SysTreeCellRenderer, that adds an icon to each item
     * in the tree to denote its status.
     */
    public SysTreeCellRenderer() {
	failIcon = createImageIcon("/images/fail.png");
	warnIcon = createImageIcon("/images/warn.png");
	unknownIcon = createImageIcon("/images/unknown.png");
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
	Object o = ((DefaultMutableTreeNode) value).getUserObject();
	if (o instanceof SysItem) {
	    SysItem si = (SysItem) o;
	    /*
	     * We set an icon depending on the status of the individual
	     * item. If we don't have status information, leave the icon alone.
	     *
	     * It would be nice to set the normal icon to something pretty
	     * and representative of the item.
	     *
	     * Another enhancement would be for the tooltip to be set to
	     * something informative about the state of the item.
	     */
	    if (si.getStatus() == SysItem.FAIL) {
		setIcon(failIcon);
	    } else if (si.getStatus() == SysItem.WARN) {
		setIcon(warnIcon);
	    } else if (si.getStatus() == SysItem.UNKNOWN) {
		setIcon(unknownIcon);
	    }
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
