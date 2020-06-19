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

    private Set <ContentsFileDetail> fileset;
    private ContentsParser cp;

    public ContentsPackage() {
	fileset = new HashSet <ContentsFileDetail> ();
    }

    public ContentsPackage(Overlay ovl) {
	fileset = new HashSet <ContentsFileDetail> ();
	cp = ContentsParser.getInstance();
	addPkgFiles(ovl);
    }

    public void addFile(ContentsFileDetail cfd) {
	fileset.add(cfd);
    }

    private void addPkgFiles(Overlay ovl) {
	for (SVR4Package pkg : ovl.getPackages()) {
	    addPkgFiles(pkg);
	}
    }

    private void addPkgFiles(SVR4Package pkg) {
	ContentsPackage cpp = cp.getPackage(pkg.getName());
	if (cpp != null) {
	    fileset.addAll(cpp.getDetails());
	}
    }

    public int numEntries() {
	return fileset.size();
    }

    public int numFiles() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isRegular()) {
		i++;
	    }
	}
	return i;
    }

    public int numDirectories() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isDirectory()) {
		i++;
	    }
	}
	return i;
    }

    /*
     * pkginfo -l reports "linked files" just for hard links
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

    public int numSymLinks() {
	int i = 0;
	for (ContentsFileDetail cfd : fileset) {
	    if (cfd.isSymLink()) {
		i++;
	    }
	}
	return i;
    }

    /*
     * Devices
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

    /*
     * Shared files - contained in more than one package
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

    /*
     * Add up the space used
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
