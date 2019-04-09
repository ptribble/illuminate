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
import javax.swing.event.*;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Show a tree view of the local filesystem. Selecting a file causes
 * information about the software package(s) associated with that file
 * to appear in an area to the right of the tree.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class InstalledFilesPanel extends JPanel {

    private PackageInformationPanel pip;

    /**
     * Create a new InstalledFilesPanel.
     *
     * @param ovlist A list of overlays
     */
    public InstalledFilesPanel(String altroot, OverlayList ovlist) {
	setLayout(new BorderLayout());

	JPanel jptree = new JPanel(new BorderLayout());
	pip = new PackageInformationPanel(altroot, ovlist, false);

	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		jptree, pip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(180);
	add(psplit);

	final JTree cft = new ContentsFileTree(altroot);
	cft.addTreeSelectionListener(new TreeSelectionListener() {
	    public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		    cft.getLastSelectedPathComponent();

		if (node != null) {
		    showFile((File) node.getUserObject());
		}
	    }
	});

	jptree.add(new JScrollPane(cft));
    }

    private void showFile(File f) {
	pip.showFile(f);
    }

    /**
     * Cause the detailed view of package contents to be shown.
     */
    public void showDetailedView() {
	pip.showDetailedView();
    }
}
