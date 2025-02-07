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

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * A tree structure representing the hierarchical structure of overlays
 * and packages.
 * @author Peter Tribble
 * @version 1.0
 */
public class OverlayTree extends JTree {

    private static final long serialVersionUID = 1L;

    private DefaultTreeModel model;
    private Map<SVR4Package, NodeSet> pkgmap = new HashMap<>();
    private Map<Overlay, NodeSet> ovmap = new HashMap<>();

    /**
     * Create a tree from an overlay list and add it to the parent node.
     *
     * @param ovlist A list of overlays
     */
    public OverlayTree(OverlayList ovlist) {
	DefaultMutableTreeNode topmenu = new DefaultMutableTreeNode("Overlays");
	model = new DefaultTreeModel(topmenu);
	setModel(model);
	for (Overlay ovl : ovlist.getOverlays()) {
	    DefaultMutableTreeNode mitem = new DefaultMutableTreeNode(ovl);
	    topmenu.add(mitem);
	    addMC(mitem, ovl);
	}
    }

    /**
     * Notification of a package change, propagated to the model.
     *
     * @param pkg the package that's changed
     */
    public void nodeChanged(SVR4Package pkg) {
	NodeSet ns = pkgmap.get(pkg);
	if (ns != null) {
	    for (DefaultMutableTreeNode node : ns.getNodes()) {
		model.nodeChanged(node);
	    }
	}
    }

    /**
     * Notification of an overlay change, propagated to the model.
     *
     * @param ovl the overlay that's changed
     */
    public void nodeChanged(Overlay ovl) {
	NodeSet ns = ovmap.get(ovl);
	if (ns != null) {
	    for (DefaultMutableTreeNode node : ns.getNodes()) {
		model.nodeChanged(node);
	    }
	}
    }

    private void addMC(DefaultMutableTreeNode node, Overlay ovl) {
	for (Overlay ov : ovl.getOverlays()) {
	    node.add(addNode(ov));
	}
	for (SVR4Package pkg : ovl.getPackages()) {
	    node.add(addNode(pkg));
	}
    }

    private DefaultMutableTreeNode addNode(Overlay ovl) {
	DefaultMutableTreeNode mitem = new DefaultMutableTreeNode(ovl);
	for (Overlay ov : ovl.getOverlays()) {
	    mitem.add(addNode(ov));
	}
	for (SVR4Package pkg : ovl.getPackages()) {
	    mitem.add(addNode(pkg));
	}
	NodeSet ns = ovmap.get(ovl);
	if (ns == null) {
	    ns = new NodeSet();
	    ovmap.put(ovl, ns);
	}
	ns.add(mitem);
	return mitem;
    }

    private DefaultMutableTreeNode addNode(SVR4Package pkg) {
	DefaultMutableTreeNode node = new DefaultMutableTreeNode(pkg);
	NodeSet ns = pkgmap.get(pkg);
	if (ns == null) {
	    ns = new NodeSet();
	    pkgmap.put(pkg, ns);
	}
	ns.add(node);
	return node;
    }

    class NodeSet {
	private Set<DefaultMutableTreeNode> nodes = new HashSet<>();
	public void add(DefaultMutableTreeNode node) {
	    nodes.add(node);
	}
	public Set<DefaultMutableTreeNode> getNodes() {
	    return nodes;
	}
    }
}
