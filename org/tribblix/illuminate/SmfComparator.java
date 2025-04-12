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
 * Copyright 2025 Peter Tribble.
 */

package org.tribblix.illuminate;

import javax.swing.tree.TreeNode;
import java.util.Comparator;

/**
 * A Comparator for SmfTreeNodes. The children() of an SmfTreeNode are
 * typed as a Vector of TreeNode, so when sorting with Collections.sort()
 * we need to supply an explicit Comparator with the correct types.
 */
public final class SmfComparator<T extends TreeNode>
        implements Comparator<TreeNode> {
    private static final SmfComparator INSTANCE =
	new SmfComparator<>();
    private SmfComparator() {
    }
    /**
     * Get the singleton comparator instance.
     *
     * @return the singleton SmfComparator instance
     */
    public static SmfComparator getInstance() {
	return INSTANCE;
    }
    @Override
    public int compare(TreeNode t1, TreeNode t2) {
	return t1.toString().compareTo(t2.toString());
    }
}
