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
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;
import javax.swing.event.*;

/**
 * A panel showing overlays.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class OverlayPanel extends JPanel {

    final OverlayTree tree;
    private PackageInformationPanel pip;
    private SVR4Package currentPackage;

    public OverlayPanel(String altroot, OverlayList ovlist, ZapConfig zc) {
	setLayout(new BorderLayout());

	tree = new OverlayTree(ovlist);
	tree.expandRow(0);

	/*
	 * Set a renderer to do tooltips and custom icons.
	 * Show the installed status.
	 */
	tree.setCellRenderer(new OverlayTreeCellRenderer());
	ToolTipManager.sharedInstance().registerComponent(tree);

	// Listen for when the selection changes.
	tree.addTreeSelectionListener(new TreeSelectionListener() {
	    public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		    tree.getLastSelectedPathComponent();

		if (node != null) {
		    Object o = node.getUserObject();
		    if (o instanceof SVR4Package) {
			showPkg((SVR4Package) o);
		    } else if (o instanceof Overlay) {
			showOverlay((Overlay) o);
		    }
		}
	    }
	});
	// end of tree listener

	// tabbed info pane on right
	pip = new PackageInformationPanel(altroot, ovlist, zc);

	// split pane to hold the lot
	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		new JScrollPane(tree), pip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(180);
	add(psplit);
    }

    void showOverlay(Overlay ovl) {
	currentPackage = null;
	pip.showOverlay(ovl);
    }

    void showPkg(SVR4Package pkg) {
	pip.showPkg(pkg);
	if (pkg.isInstalled()) {
	    currentPackage = pkg;
	}
    }

    public void showRevDependencies() {
	pip.showRevDependencies(currentPackage);
    }

    public void showDetailedView() {
	pip.showDetailedView();
    }
}
