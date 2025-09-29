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
     * @param nsvc The SmfService represented by this SmfTreeNode
     * @param nodename The display name of this SmfTreeNode
     */
    public SmfTreeNode(final SmfService nsvc, final String nodename) {
	svc = nsvc;
	node = nodename;
    }

    /**
     * An intermediate node in a tree of SMF services.
     *
     * @param nodename The display name of this SmfTreeNode
     */
    public SmfTreeNode(final String nodename) {
	node = nodename;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(final MutableTreeNode newChild) {
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
	Set<String> statuses = new HashSet<>();
	for (Enumeration e = children(); e.hasMoreElements();) {
	    SmfTreeNode stn = (SmfTreeNode) e.nextElement();
	    statuses.add(stn.getStatus());
	}
	if (statuses.contains("maintenance")) {
	    return "maintenance";
	}
	if (statuses.contains("offline")) {
	    return "offline";
	}
	// ignore disabled ones, they shouldn't make things look bad
	statuses.remove("disabled");
	statuses.remove(null);
	if (statuses.size() == 1) {
	    return statuses.stream().findFirst().get();
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
    public int compareTo(final SmfTreeNode othernode) {
	return node.compareTo(othernode.toString());
    }

    /**
     * For Comparable.
     *
     * @param o the object to check for equality with this
     * SmfTreeNode
     *
     * @return whether the given object is equal to this SmfTreeNode
     */
    @Override
    public boolean equals(final Object o) {
	if (o instanceof SmfTreeNode) {
	    SmfTreeNode othernode = (SmfTreeNode) o;
	    return node.equals(othernode.toString());
        }
        return false;
    }

    /**
     * For Comparable. As the unique property of an SmfTreeNode is its
     * name, use the hashCode of the underlying node name.
     *
     * @return a unique hashcode for this SmfTreeNode
     */
    @Override
    public int hashCode() {
	return node.hashCode();
    }
}
