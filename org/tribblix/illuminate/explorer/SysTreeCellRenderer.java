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
public class SysTreeCellRenderer extends DefaultTreeCellRenderer {

    private static ImageIcon failIcon;
    private static ImageIcon warnIcon;
    private static ImageIcon unknownIcon;

    public SysTreeCellRenderer() {
	failIcon = createImageIcon("/images/fail.png");
	warnIcon = createImageIcon("/images/warn.png");
	unknownIcon = createImageIcon("/images/unknown.png");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
						  Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf,
						  int row,
						  boolean hasFocus) {

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
    private ImageIcon createImageIcon(String s) {
	URL imgURL = getClass().getResource(s);
	return (imgURL == null) ? null : new ImageIcon(imgURL);
    }
}
