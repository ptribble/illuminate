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

import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;

/**
 * Represents a Node in a local file tree.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public final class ContentsFileTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;

    /**
     * Whether this node has been visited.
     */
    private boolean explored;
    /**
     * The underlying File represented by this node.
     */
    private File file;

    /**
     * Create a node for the given pathname.
     *
     * @param file The File representing the current pathname.
     */
    public ContentsFileTreeNode(final File file) {
	this.file = file;
	setUserObject(file);
    }

    @Override
    public boolean getAllowsChildren() {
	return !isLeaf();
    }

    /**
     * Symlinks are also leaf nodes, from the point of view of packaging.
     */
    @Override
    public boolean isLeaf() {
	return isLink() || !file.isDirectory();
    }

    /**
     * Track whether we've already descended below this point.
     *
     * @return true if this file has been explored.
     */
    public boolean isExplored() {
	return explored;
    }

    @Override
    public String toString() {
	return file.getName();
    }

    /*
     * Detect symbolic links. We want to stop when we find these. We
     * return true on error as well, to force traversal to stop.
     */
    private boolean isLink() {
	try {
	    return !file.getAbsolutePath().equals(file.getCanonicalPath());
	} catch (IOException ioe) {
	    return true;
	}
    }

    /**
     * Traverse the directory tree from this point. Stop if any problem
     * is encountered.
     */
    public void explore() {
	if (file.isDirectory() && !explored) {
	    /*
	     * If there's a problem, just don't traverse. This includes
	     * permission denied.
	     */
	    try {
		// no filter any more, as packaging includes dot-files
		File[] children = file.listFiles();

		// sort into alphabetic order
		Arrays.sort(children);

		for (File child : children) {
		    add(new ContentsFileTreeNode(child));
		}
	    } catch (Exception e) {
	    }

	    explored = true;
	}
    }
}
