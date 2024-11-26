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
