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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * SmfTreeNode - represent SMF services as nodes in a tree.
 * @author Peter Tribble
 * @version 1.0
 */
public final class SmfTreeNode extends DefaultMutableTreeNode
    implements Comparable<SmfTreeNode> {

    private static final long serialVersionUID = 1L;

    /**
     * The service underlying this SmfTreeNode.
     */
    private transient SmfService svc;

    /**
     * The display name of this SmfTreeNode.
     */
    private final String node;

    /**
     * A leaf node in a tree of SMF services.
     *
     * @param svc The SmfService represented by this SmfTreeNode
     * @param node The display name of this SmfTreeNode
     */
    public SmfTreeNode(SmfService svc, String node) {
	this.svc = svc;
	this.node = node;
    }

    /**
     * An intermediate node in a tree of SMF services.
     *
     * @param node The display name of this SmfTreeNode
     */
    public SmfTreeNode(String node) {
	this.node = node;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(MutableTreeNode newChild) {
	super.add(newChild);
	Collections.sort(this.children, SmfComparator.getInstance());
    }

    @Override
    public String toString() {
	return node;
    }

    @Override
    public Object getUserObject() {
	return svc;
    }

    /**
     * Return the status of this node. If a leaf node, then the status of the
     * service this node represents. If not a leaf node, then the consensus
     * status of all the services underneath this node. If there isn't such a
     * consensus (services have different status) then we are going to return
     * null for now, except for the special case where there's a service in
     * maintenance in which case we propagate that up.
     *
     * @return A String representing the status of the service represented
     * by this node or its children.
     */
    public String getStatus() {
	if (svc != null) {
	    return svc.getStatus();
	}
	Set<String> statusses = new HashSet<>();
	for (Enumeration e = children(); e.hasMoreElements();) {
	    SmfTreeNode stn = (SmfTreeNode) e.nextElement();
	    statusses.add(stn.getStatus());
	}
	if (statusses.contains("maintenance")) {
	    return "maintenance";
	}
	if (statusses.contains("offline")) {
	    return "offline";
	}
	// ignore disabled ones, they shouldn't make things look bad
	statusses.remove("disabled");
	statusses.remove(null);
	if (statusses.size() == 1) {
	    return statusses.stream().findFirst().get();
	}
	return null;
    }

    /**
     * For Comparable.
     *
     * @param othernode the SmfTreeNode to compare with this SmfTreeNode
     *
     * @return whether the name of the given SmfTreeNode is greater than
     * or less than the name of this SmfTreeNode
     */
    @Override
    public int compareTo(SmfTreeNode othernode) {
	return node.compareTo(othernode.toString());
    }
}
