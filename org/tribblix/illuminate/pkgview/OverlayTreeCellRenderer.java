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

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

/**
 * Adds tooltips and custom icons to an overlay/package tree.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class OverlayTreeCellRenderer extends DefaultTreeCellRenderer {

    private static ImageIcon noneIcon;
    private static ImageIcon selectedIcon;
    private static ImageIcon partIcon;
    private static ImageIcon warnIcon;

    public OverlayTreeCellRenderer() {
	noneIcon = createImageIcon("/images/none.png");
	selectedIcon = createImageIcon("/images/selected.png");
	partIcon = createImageIcon("/images/part.png");
	warnIcon = createImageIcon("/images/warn.png");
    }

    @Override
    public Component getTreeCellRendererComponent(
						  JTree tree,
						  Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf,
						  int row,
						  boolean hasFocus) {

	super.getTreeCellRendererComponent(
					   tree, value, sel,
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
	} else {
	    if (o instanceof Overlay) {
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
	}
	return this;
    }

    /*
     * Based on the Java Swing tutorial examples.
     */
    private ImageIcon createImageIcon(String s) {
	java.net.URL imgURL = getClass().getResource(s);
	return (imgURL == null) ? null : new ImageIcon(imgURL);
    }
}
