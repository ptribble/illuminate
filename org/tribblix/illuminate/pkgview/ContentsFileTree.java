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
 * Copyright 2026 Peter Tribble
 *
 */

package org.tribblix.illuminate.pkgview;

import java.awt.Cursor;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import java.io.File;

/**
 * ContentsFileTree - display local files.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public final class ContentsFileTree extends JTree {

    private static final long serialVersionUID = 1L;

    /**
     * The backing TreeModel.
     */
    private final DefaultTreeModel model;

    /**
     * Create a JTree representing local files and directories.
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public ContentsFileTree(final PackageHandler pkghdl) {
	ContentsFileTreeNode rootNode = new ContentsFileTreeNode(
						new File(pkghdl.getRoot()));
	rootNode.explore();
	model = new DefaultTreeModel(rootNode);

	setModel(model);
	createTreeModelListener();
	this.getSelectionModel().setSelectionMode(
	    TreeSelectionModel.SINGLE_TREE_SELECTION);

	addTreeExpansionListener(new TreeExpansionListener() {
	    @Override
	    public void treeCollapsed(final TreeExpansionEvent e) {
		// nothing to do
	    }
	    @Override
	    public void treeExpanded(final TreeExpansionEvent e) {
		TreePath path = e.getPath();
		if (path != null) {
		    ContentsFileTreeNode node =
			(ContentsFileTreeNode) path.getLastPathComponent();

		    if (!node.isExplored()) {
			explore(node);
		    }
		}
	    }
	});

    }

    void explore(final ContentsFileTreeNode node) {
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	node.explore();
	setCursor(c);
	model.nodeStructureChanged(node);
    }
}
