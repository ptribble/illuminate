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

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.net.URL;

/**
 * Adds tooltips and custom icons to an overlay/package tree.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public final class OverlayTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    private static ImageIcon noneIcon;
    private static ImageIcon selectedIcon;
    private static ImageIcon partIcon;
    private static ImageIcon warnIcon;

    /**
     * Create a custom tree cell renderer. It will decorate the cells
     * depending on the installed state of the overlay in each cell.
     */
    public OverlayTreeCellRenderer() {
	noneIcon = createImageIcon("/images/none.png");
	selectedIcon = createImageIcon("/images/selected.png");
	partIcon = createImageIcon("/images/part.png");
	warnIcon = createImageIcon("/images/warn.png");
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
	if (o instanceof SVR4Package) {
	    /*
	     * It would be nice if the description were more than the name.
	     */
	    SVR4Package p = (SVR4Package) o;
	    setToolTipText(p.getDescription());
	    setIcon(p.isInstalled() ? selectedIcon : noneIcon);
	} else if (o instanceof Overlay) {
	    Overlay ovl = (Overlay) o;
	    setToolTipText(ovl.getDescription());
	    if (ovl.isInstalled()) {
		setIcon(ovl.isComplete() ? selectedIcon : warnIcon);
	    } else {
		setIcon(ovl.isComplete() ? partIcon : noneIcon);
	    }
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
