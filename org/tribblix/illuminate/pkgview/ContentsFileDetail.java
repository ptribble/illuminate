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
import java.util.StringTokenizer;

/**
 * Parse a line of the SVR4 packaging contents file.
 */
public class ContentsFileDetail implements Comparable <ContentsFileDetail> {

    private String altroot;

    private String filename;
    private String ftype;
    // private String pclass;
    private String owner;
    private String group;
    private String mode;
    // stored as String, let consumers do the conversion work
    private String size;
    // private String cksum;
    private String modtime;
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
	pkglist = new ArrayList <String> (4);
	parseNewStyle(s);
    }

    /**
     * Parse the line. We pick the name and the type, then parse the
     * rest according to the type. From contents(4)
     *
     * ftype s: path=rpath s class package
     * ftype l: path l class package
     * ftype d: path d class mode owner group package(s)
     * ftype b: path b class major minor mode owner group package
     * ftype c: path c class major minor mode owner group package
     * ftype f: path f class mode owner group size cksum modtime package
     * ftype x: path x class mode owner group package
     * ftype v: path v class mode owner group size cksum modtime package
     * ftype e: path e class mode owner group size cksum modtime package
     */
    private void parseNewStyle(String s) {
	StringTokenizer st = new StringTokenizer(s, " ");
	filename = st.nextToken();
	ftype = st.nextToken();
	// skip pclass
	st.nextToken();
	// deal with links first
	if (isLink()) {
	    // split the filename into name and link target
	    String[] ds = filename.split("=", 2);
	    filename = ds[0];
	    target = ds[1];
	    while (st.hasMoreTokens()) {
		pkglist.add(st.nextToken());
	    }
	    return;
	}
	if (isDevice()) {
	    // skip major and minor device numbers
	    st.nextToken();
	    st.nextToken();
	}
	mode = st.nextToken();
	owner = st.nextToken();
	group = st.nextToken();
	if (isRegular()) {
	    size = st.nextToken();
	    // skip cksum and modtime
	    st.nextToken();
	    modtime = st.nextToken();
	}
	// anything left is a package
	while (st.hasMoreTokens()) {
	    pkglist.add(st.nextToken());
	}
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
    public String getSize() {
	return size;
    }

    /**
     * Return the last modified time of a file, in seconds since the epoch
     *
     * @return the time the file was last modified
     */
    public long lastModified() {
	return Long.parseLong(modtime);
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
	List <SVR4Package> lp = new ArrayList <SVR4Package> ();
	for (String s : pkglist) {
	    lp.add(new SVR4Package(altroot, s));
	}
	return lp;
    }

    /**
     * Return whether this entry is shared amongst packages. This is determined
     * by whether the number of packages that own this entry is one
     * or more than one.
     */
    public boolean isShared() {
	return (pkglist.size() != 1);
    }

    /**
     * Return whether this entry is a directory. Which means that it
     * would be of type d or x (x denotes a directory that is exclusive
     * to a package).
     */
    public boolean isDirectory() {
	return ("d".equals(ftype) || "x".equals(ftype));
    }

    /**
     * Return whether this entry is a regular file, which means that it
     * would be of type f or v or e. As a result, it will have a size,
     * checksum, and modification time
     */
    public boolean isRegular() {
	return ("e".equals(ftype) || "f".equals(ftype) || "v".equals(ftype));
    }

    /**
     * Return whether this entry is editable, which means that it would be of
     * type v or e. As a result, size, checksum, and modification may differ
     * from the installed values.
     */
    public boolean isEditable() {
	return ("e".equals(ftype) || "v".equals(ftype));
    }

    /**
     * Return whether this entry is a hard link to another file.
     * This is denoted by it being of type l.
     */
    public boolean isHardLink() {
	return "l".equals(ftype);
    }

    /**
     * Return whether this entry is a soft link to another file.
     * This is denoted by it being of type s.
     */
    public boolean isSymLink() {
	return "s".equals(ftype);
    }

    /**
     * Return whether this entry is a link to another file.
     * This is denoted by it being of type l (hard link) or
     * type s (symbolic link).
     */
    public boolean isLink() {
	return ("l".equals(ftype) || "s".equals(ftype));
    }

    /**
     * Return whether this entry is a device file.
     * This is denoted by it being of type b (block device) or
     * type c (character device).
     */
    public boolean isDevice() {
	return ("b".equals(ftype) || "c".equals(ftype));
    }

    /**
     * Return the basic type of this entry as a descriptive String.
     */
    public String getBasicType() {
	String t;
	if (isRegular()) {
	    t = "file";
	} else if (isDirectory()) {
	    t = "directory";
	} else if (isDevice()) {
	    t = "device file";
	} else if ("s".equals(ftype)) {
	    t = "symbolic link";
	} else if ("l".equals(ftype)) {
	    t = "hard link";
	} else if ("p".equals(ftype)) {
	    t = "named pipe";
	} else {
	    t = "file";
	}
	return t;
    }

    /**
     * Return the type of this entry as a descriptive String.
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
	if ("f".equals(ftype)) {
	    t = "Regular file.";
	} else if ("d".equals(ftype)) {
	    t = "Directory.";
	} else if ("s".equals(ftype)) {
	    t = "Symbolic link.";
	} else if ("l".equals(ftype)) {
	    t = "Hard linked file.";
	} else if ("e".equals(ftype)) {
	    t = "Editable file.";
	} else if ("v".equals(ftype)) {
	    t = "Volatile file.";
	} else if ("b".equals(ftype)) {
	    t = "Block special device.";
	} else if ("c".equals(ftype)) {
	    t = "Character special device.";
	} else if ("i".equals(ftype)) {
	    t = "Information file.";
	} else if ("p".equals(ftype)) {
	    t = "Named pipe.";
	} else if ("x".equals(ftype)) {
	    t = "Directory exclusive to this package.";
	} else {
	    t = "Unknown.";
	}
	return t;
    }

    /**
     * For Comparable.
     */
    public int compareTo(ContentsFileDetail cfd) {
	return filename.compareTo(cfd.getName());
    }

}
