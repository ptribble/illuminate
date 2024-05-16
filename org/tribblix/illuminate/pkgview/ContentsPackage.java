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

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Represent the files contained in an SVR4 package.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class ContentsPackage {

    private Set <ContentsFileDetail> fileset = new HashSet<>();

    /**
     * Create a new empty ContentsPackage.
     */
    public ContentsPackage() {
    }

    /**
     * Create a new ContentsPackage containing the files from the
     * given overlay.
     *
     * @param ovl the overlay whose files will populate this ContentsPackage
     * @param cp a ContentsParser
     */
    public ContentsPackage(Overlay ovl, ContentsParser cp) {
	addPkgFiles(ovl, cp);
    }

    /**
     * Add a file.
     *
     * @param cfd the ContentsFileDetail to add.
     */
    public void addFile(ContentsFileDetail cfd) {
	fileset.add(cfd);
    }

    private void addPkgFiles(Overlay ovl, ContentsParser cp) {
	for (SVR4Package pkg : ovl.getPackages()) {
	    addPkgFiles(pkg, cp);
	}
    }

    private void addPkgFiles(SVR4Package pkg, ContentsParser cp) {
	ContentsPackage cpp = cp.getPackage(pkg.getName());
	if (cpp != null) {
	    fileset.addAll(cpp.getDetails());
	}
    }

    /**
     * Returns the number of entries in this ContentsPackage.
     *
     * @return the number of entries
     */
    public int numEntries() {
	return fileset.size();
    }

    /**
     * Returns the number of files in this ContentsPackage.
     *
     * @return the number of files
     */
    public int numFiles() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isRegular()) {
		i++;
	    }
	}
	return i;
    }

    /**
     * Returns the number of directories in this ContentsPackage.
     *
     * @return the number of directories
     */
    public int numDirectories() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isDirectory()) {
		i++;
	    }
	}
	return i;
    }

    /**
     * Returns the number of hard links in this ContentsPackage. This is what
     * pkginfo -l refers to as linked files.
     *
     * @return the number of hard links
     */
    public int numHardLinks() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isHardLink()) {
		i++;
	    }
	}
	return i;
    }

    /**
     * Returns the number of symbolic links in this ContentsPackage.
     *
     * @return the number of symbolic links
     */
    public int numSymLinks() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isSymLink()) {
		i++;
	    }
	}
	return i;
    }

    /**
     * Returns the number of device files in this ContentsPackage.
     *
     * @return the number of devices
     */
    public int numDevices() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isDevice()) {
		i++;
	    }
	}
	return i;
    }

    /**
     * Returns the number of shared files in this ContentsPackage, that is,
     * files contained in more than one packegs.
     *
     * @return the number of shared files
     */
    public int numShared() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isShared()) {
		i++;
	    }
	}
	return i;
    }

    /**
     * Return the space used by the files in this ContentsPackage
     *
     * @return the space used by the files in this ContentsPackage
     */
    public long spaceUsed() {
	long l = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isRegular()) {
		l += cfd.getSize();
	    }
	}
	return l;
    }

    /**
     * Return a Set of ContentsFileDetail objects, sorted by path name.
     *
     * @return a Set of ContentsFileDetail objects, sorted by path name
     */
    public Set <ContentsFileDetail> getDetails() {
	return new TreeSet <ContentsFileDetail> (fileset);
    }
}
