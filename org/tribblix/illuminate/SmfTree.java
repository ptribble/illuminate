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

package org.tribblix.illuminate;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Map;
import java.util.HashMap;

/**
 * SmfTree - shows SMF services in a tree.
 * @author Peter Tribble
 * @version 1.0
 */
public final class SmfTree extends JTree {

    private static final long serialVersionUID = 1L;

    /**
     * A Map to track the services so we can find relationships when
     * building the tree.
     */
    private final transient Map<String, SmfTreeNode> treeMap = new HashMap<>();

    /**
     * Display a tree of SMF services.
     *
     * @param sslist An SmfServiceList
     */
    public SmfTree(final SmfServiceList sslist) {
	DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				IlluminateResources.getString("SMF.SERVICES"));

	// build up the tree
	for (SmfService svc : sslist) {
	    addService(svc, root);
	}

	setModel(new DefaultTreeModel(root));
	setCellRenderer(new SmfTreeCellRenderer());
    }

    private void addService(final SmfService svc,
			    final DefaultMutableTreeNode root) {
	/*
	 * We first break up the String into its components and then walk
	 * through them. The last component is the leaf node.
	 *
	 * We store the node for each path in a Hash, and check for that.
	 */
	String[] parts = svc.getFMRI().split("[:/]+");
	StringBuilder sb = new StringBuilder();
	// first one is special
	sb.append(parts[0]).append(':');
	SmfTreeNode stn = treeMap.get(sb.toString());
	if (stn == null) {
	    stn = new SmfTreeNode(parts[0]);
	    treeMap.put(sb.toString(), stn);
	    root.add(stn);
	}
	for (int i = 1; i < parts.length - 1; i++) {
	    sb.append('/').append(parts[i]);
	    SmfTreeNode stn2 = treeMap.get(sb.toString());
	    if (stn2 == null) {
		stn2 = new SmfTreeNode(parts[i]);
		treeMap.put(sb.toString(), stn2);
		stn.add(stn2);
	    }
	    stn = stn2;
	}
	// the last is special too
	stn.add(new SmfTreeNode(svc, parts[parts.length - 1]));
    }
}
