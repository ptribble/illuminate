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
    private Map <SVR4Package, NodeSet> pkgmap = new HashMap<>();
    private Map <Overlay, NodeSet> ovmap = new HashMap<>();

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

    public void nodeChanged(SVR4Package pkg) {
	NodeSet ns = pkgmap.get(pkg);
	if (ns != null) {
	    for (DefaultMutableTreeNode node : ns.getNodes()) {
		model.nodeChanged(node);
	    }
	}
    }

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
	private Set <DefaultMutableTreeNode> nodes = new HashSet<>();
	public void add(DefaultMutableTreeNode node) {
	    nodes.add(node);
	}
	public Set <DefaultMutableTreeNode> getNodes() {
	    return nodes;
	}
    }
}
