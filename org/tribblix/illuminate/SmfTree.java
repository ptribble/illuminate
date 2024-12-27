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
public class SmfTree extends JTree {

    private static final long serialVersionUID = 1L;

    /**
     * A Map to track the services so we can find relationships when
     * building the tree.
     */
    private final Map<String, SmfTreeNode> treeMap = new HashMap<>();

    /**
     * Display a tree of SMF services.
     *
     * @param sslist An SmfServiceList
     */
    public SmfTree(SmfServiceList sslist) {
	DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				IlluminateResources.getString("SMF.SERVICES"));

	// build up the tree
	for (SmfService svc : sslist) {
	    addService(svc, root);
	}

	setModel(new DefaultTreeModel(root));
	setCellRenderer(new SmfTreeCellRenderer());
    }

    private void addService(SmfService svc, DefaultMutableTreeNode root) {
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
