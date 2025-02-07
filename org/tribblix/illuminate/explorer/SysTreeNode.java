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

package org.tribblix.illuminate.explorer;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * SysTreeNode - represent an item of hardware as a node in a tree.
 * @author Peter Tribble
 * @version 1.0
 */
public final class SysTreeNode extends DefaultMutableTreeNode
    implements Comparable<SysTreeNode> {

    private static final long serialVersionUID = 1L;

    private transient SysItem hi;
    private String node;

    /**
     * Create a Node to store information in the system tree.
     *
     * @param hi the SysItem represented by this node
     * @param node the displayed name of this node
     */
    public SysTreeNode(SysItem hi, String node) {
	this.hi = hi;
	this.node = node;
    }

    @Override
    public String toString() {
	return node;
    }

    @Override
    public Object getUserObject() {
	return hi;
    }

    /**
     * Compare the given node with this node.
     *
     * @param othernode the node to compare with
     *
     * @return an int representing the result of the comparison
     */
    @Override
    public int compareTo(SysTreeNode othernode) {
	return node.compareTo(othernode.toString());
    }
}
