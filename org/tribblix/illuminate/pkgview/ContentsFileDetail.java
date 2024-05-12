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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Parse a line of the SVR4 packaging contents file.
 */
public class ContentsFileDetail implements Comparable <ContentsFileDetail> {

    private String altroot;

    private String filename;
    private char ftype;
    // private String pclass;
    private String owner;
    private String group;
    private String mode;
    private long size;
    // private String cksum;
    private long modtime;
    private List <String> pkglist;
    // only valid for links
    private String target;
    // major + minor only valid for devices, ignore as not used

    /**
     * Create a set of details from a line of the contents file.
     *
     * @param altroot  An alternate root directory for this OS image
     * @param s  One line of the contents file
     */
    public ContentsFileDetail(String altroot, String s) {
	this.altroot = altroot;
	parseNewStyle(s);
    }

    /**
     * Parse the line. We pick the name and the type, then parse the
     * rest according to the type. From contents(4)
     *
     * ftype s: path=rpath s class package
     * ftype l: path=rpath l class package
     * ftype d: path d class mode owner group package(s)
     * ftype b: path b class major minor mode owner group package
     * ftype c: path c class major minor mode owner group package
     * ftype f: path f class mode owner group size cksum modtime package
     * ftype x: path x class mode owner group package
     * ftype v: path v class mode owner group size cksum modtime package
     * ftype e: path e class mode owner group size cksum modtime package
     */
    private void parseNewStyle(String s) {
	String[] st = s.split(" ");
	filename = st[0];
	ftype = st[1].charAt(0);
	// skip class, and start counting from here
	int i = 3;
	// deal with links first
	if (isLink()) {
	    // split the filename into name and link target
	    String[] ds = filename.split("=", 2);
	    filename = ds[0];
	    target = ds[1];
	    // anything left is a package
	    pkglist = Arrays.asList(Arrays.copyOfRange(st, i, st.length));
	    return;
	}
	if (isDevice()) {
	    // skip major and minor device numbers
	    i += 2;
	}
	mode = st[i++];
	owner = st[i++];
	group = st[i++];
	if (isRegular()) {
	    size = Long.parseLong(st[i]);
	    // increment, skip cksum
	    i += 2;
	    modtime = Long.parseLong(st[i++]);
	}
	// anything left is a package
	pkglist = Arrays.asList(Arrays.copyOfRange(st, i, st.length));
    }

    /**
     * Return the name of the file associated with this entry.
     *
     * @return the file name
     */
    public String getName() {
	return filename;
    }

    /**
     * Return the target of a link.
     *
     * @return the link target
     */
    public String getTarget() {
	return target;
    }

    /**
     * Return the owner of a file.
     *
     * @return  The file owner
     */
    public String getOwner() {
	return owner;
    }

    /**
     * Return the group owner of a file.
     *
     * @return the file group owner
     */
    public String getGroup() {
	return group;
    }

    /**
     * Return the permissions of a file.
     *
     * @return the file permissions mode
     */
    public String getMode() {
	return mode;
    }

    /**
     * Return the size of a file.
     *
     * @return the file size
     */
    public long getSize() {
	return size;
    }

    /**
     * Return the last modified time of a file, in seconds since the epoch
     *
     * @return the time the file was last modified
     */
    public long lastModified() {
	return modtime;
    }

    /**
     * Return the list of names of packages that own this entry.
     *
     * @return  A List of package names that own this entry.
     */
    public List <String> getPackageNames() {
	return pkglist;
    }

    /**
     * Return the list of packages that own this entry.
     *
     * @return  A List of packages that own this entry.
     */
    public List <SVR4Package> getPackages() {
	List <SVR4Package> lp = new ArrayList<>();
	for (String s : pkglist) {
	    lp.add(new SVR4Package(altroot, s));
	}
	return lp;
    }

    /**
     * Return whether this entry is shared amongst packages. This is determined
     * by whether the number of packages that own this entry is one
     * or more than one.
     *
     * @return true if this entry is shared by multiple packages
     */
    public boolean isShared() {
	return pkglist.size() != 1;
    }

    /**
     * Return whether this entry is a directory. Which means that it
     * would be of type d or x (x denotes a directory that is exclusive
     * to a package).
     *
     * @return true if this entry is a directory
     */
    public boolean isDirectory() {
	return 'd' == ftype || 'x' == ftype;
    }

    /**
     * Return whether this entry is a regular file, which means that it
     * would be of type f or v or e. As a result, it will have a size,
     * checksum, and modification time
     *
     * @return true if this entry is a regular file
     */
    public boolean isRegular() {
	return 'e' == ftype || 'f' == ftype || 'v' == ftype;
    }

    /**
     * Return whether this entry is editable, which means that it would be of
     * type v or e. As a result, size, checksum, and modification may differ
     * from the installed values.
     *
     * @return true if this entry is an editable file
     */
    public boolean isEditable() {
	return 'e' == ftype || 'v' == ftype;
    }

    /**
     * Return whether this entry is a hard link to another file.
     * This is denoted by it being of type l.
     *
     * @return true if this entry is a hard link
     */
    public boolean isHardLink() {
	return 'l' == ftype;
    }

    /**
     * Return whether this entry is a soft link to another file.
     * This is denoted by it being of type s.
     *
     * @return true if this entry is a soft link
     */
    public boolean isSymLink() {
	return 's' == ftype;
    }

    /**
     * Return whether this entry is a link to another file.
     * This is denoted by it being of type l (hard link) or
     * type s (symbolic link).
     *
     * @return true if this entry is a hard or soft link
     */
    public boolean isLink() {
	return 'l' == ftype || 's' == ftype;
    }

    /**
     * Return whether this entry is a device file.
     * This is denoted by it being of type b (block device) or
     * type c (character device).
     *
     * @return true if this entry is a device file
     */
    public boolean isDevice() {
	return 'b' == ftype || 'c' == ftype;
    }

    /**
     * Return the basic type of this entry as a descriptive String.
     *
     * @return the basic type of this entry as a descriptive String
     */
    public String getBasicType() {
	String t;
	if (isRegular()) {
	    t = "file";
	} else if (isDirectory()) {
	    t = "directory";
	} else if (isDevice()) {
	    t = "device file";
	} else if ('s' == ftype) {
	    t = "symbolic link";
	} else if ('l' == ftype) {
	    t = "hard link";
	} else if ('p' == ftype) {
	    t = "named pipe";
	} else {
	    t = "file";
	}
	return t;
    }

    /**
     * Return the type of this entry as a descriptive String.
     *
     * @return the type of this entry as a descriptive String
     */
    public String getDescriptiveType() {
	String t;
	/*
	 * The order here is so the most common types are found first.
	 * A sample Tribblix installation has
	 * f 305627
	 * d 30231
	 * s 24219
	 * l 3903
	 * e 182
	 */
	if ('f' == ftype) {
	    t = "Regular file.";
	} else if ('d' == ftype) {
	    t = "Directory.";
	} else if ('s' == ftype) {
	    t = "Symbolic link.";
	} else if ('l' == ftype) {
	    t = "Hard linked file.";
	} else if ('e' == ftype) {
	    t = "Editable file.";
	} else if ('v' == ftype) {
	    t = "Volatile file.";
	} else if ('b' == ftype) {
	    t = "Block special device.";
	} else if ('c' == ftype) {
	    t = "Character special device.";
	} else if ('i' == ftype) {
	    t = "Information file.";
	} else if ('p' == ftype) {
	    t = "Named pipe.";
	} else if ('x' == ftype) {
	    t = "Directory exclusive to this package.";
	} else {
	    t = "Unknown.";
	}
	return t;
    }

    /**
     * For Comparable.
     */
    @Override
    public int compareTo(ContentsFileDetail cfd) {
	return filename.compareTo(cfd.getName());
    }

}
