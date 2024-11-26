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

    private DefaultTreeModel model;

    /**
     * Create a JTree representing local files and directories.
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public ContentsFileTree(PackageHandler pkghdl) {
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
	    public void treeCollapsed(TreeExpansionEvent e) {
		// nothing to do
	    }
	    @Override
	    public void treeExpanded(TreeExpansionEvent e) {
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

    void explore(ContentsFileTreeNode node) {
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	node.explore();
	setCursor(c);
	model.nodeStructureChanged(node);
    }
}
