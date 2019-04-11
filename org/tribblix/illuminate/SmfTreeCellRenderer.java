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
public class SmfTreeCellRenderer extends DefaultTreeCellRenderer {

    private static ImageIcon failIcon;
    private static ImageIcon okIcon;

    /**
     * Create an SmfTreeCellRenderer.
     */
    public SmfTreeCellRenderer() {
	initIcons();
    }

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
	    if (ss != null) {
		setToolTipText(ss.getName());
	    } else {
		setToolTipText(null);
	    }
	} else {
	    setToolTipText(null);
	}
	return this;
    }

    /*
     * Absolute paths, otherwise they get resolved relative to this
     * class itself which is deep down in the hierarchy.
     */
    private void initIcons() {
	failIcon = createImageIcon("/images/fail.png");
	okIcon = createImageIcon("/images/ok.png");
    }

    /*
     * Based on the Java Swing tutorial examples.
     */
    private ImageIcon createImageIcon(String s) {
	URL imgURL = getClass().getResource(s);
	return (imgURL == null) ? null : new ImageIcon(imgURL);
    }
}
