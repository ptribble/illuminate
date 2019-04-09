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
public class ContentsFileTreeNode extends DefaultMutableTreeNode {

    private boolean explored;
    private File file;

    public ContentsFileTreeNode(File file) {
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
	    } catch (Exception e) {}

	    explored = true;
	}
    }
}
