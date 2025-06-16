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
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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
public final class InstalledFilesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * A panel to show the package information.
     */
    private PackageInformationPanel pip;

    /**
     * Create a new InstalledFilesPanel.
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public InstalledFilesPanel(PackageHandler pkghdl) {
	setLayout(new BorderLayout());

	JPanel jptree = new JPanel(new BorderLayout());
	pip = new PackageInformationPanel(pkghdl, false);

	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		jptree, pip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(180);
	add(psplit);

	final JTree cft = new ContentsFileTree(pkghdl);
	cft.addTreeSelectionListener(new TreeSelectionListener() {
	    @Override
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

    void showFile(File f) {
	pip.showFile(f.toString());
    }

    /**
     * Cause the detailed view of package contents to be shown.
     */
    public void showDetailedView() {
	pip.showDetailedView();
    }
}
