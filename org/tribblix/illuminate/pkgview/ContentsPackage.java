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

    private Set<ContentsFileDetail> fileset = new HashSet<>();

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
     * files contained in more than one package.
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
     * Return the space used by the files in this ContentsPackage.
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
    public Set<ContentsFileDetail> getDetails() {
	return new TreeSet<>(fileset);
    }
}
