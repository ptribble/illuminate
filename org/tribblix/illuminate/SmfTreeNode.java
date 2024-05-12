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

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;

/**
 * SmfTreeNode - represent SMF services as nodes in a tree.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfTreeNode extends DefaultMutableTreeNode {

    /**
     * The service underlying this SmfTreeNode
     */
    private SmfService svc;
    /**
     * The display name of this SmfTreeNode
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
	Set <String> statusses = new HashSet<>();
	for (Enumeration e = children(); e.hasMoreElements(); ) {
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
}
