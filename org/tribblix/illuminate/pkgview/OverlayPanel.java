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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * A panel showing overlays.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public final class OverlayPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * The tree of overlays.
     */
    final OverlayTree tree;
    /**
     * A panel to show the package information.
     */
    private PackageInformationPanel pip;
    private transient SVR4Package currentPackage;

    /**
     * Create a new OverlayPanel. The panel contains a tree view of overlays
     * on the left, with a main information panel.
     *
     * @param pkghdl a PackageHandler to query for information
     */
    public OverlayPanel(final PackageHandler pkghdl) {
	setLayout(new BorderLayout());
	OverlayList ovlist = pkghdl.getOverlayList();

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
	    @Override
	    public void valueChanged(final TreeSelectionEvent e) {
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
	pip = new PackageInformationPanel(pkghdl);

	// split pane to hold the lot
	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		new JScrollPane(tree), pip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(180);
	add(psplit);
    }

    void showOverlay(final Overlay ovl) {
	currentPackage = null;
	pip.showOverlay(ovl);
    }

    void showPkg(final SVR4Package pkg) {
	pip.showPkg(pkg);
	if (pkg.isInstalled()) {
	    currentPackage = pkg;
	}
    }

    /**
     * Cause the reverse dependency tab to be shown.
     */
    public void showRevDependencies() {
	pip.showRevDependencies(currentPackage);
    }

    /**
     * Cause the detailed view of contents to be shown.
     */
    public void showDetailedView() {
	pip.showDetailedView();
    }
}
