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
    /**
     * The displayed name of this node.
     */
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

    /**
     * For Comparable.
     *
     * @param o the SysTreeNode to check for equality with this
     * SysTreeNode
     *
     * @return whether the given object is equal to this SysTreeNode
     */
    @Override
    public boolean equals(final Object o) {
	if (o instanceof SysTreeNode) {
	    SysTreeNode othernode = (SysTreeNode) o;
	    return node.equals(othernode.toString());
        }
        return false;
    }

    /**
     * For Comparable. As the unique property of an SysTreeNode is its
     * name, use the hashCode of the underlying node name.
     *
     * @return a unique hashcode for this SysTreeNode
     */
    @Override
    public int hashCode() {
	return node.hashCode();
    }
}
