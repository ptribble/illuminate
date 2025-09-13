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

package org.tribblix.illuminate.explorer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import uk.co.petertribble.jkstat.api.JKstat;
import org.tribblix.illuminate.InfoCommand;

/**
 * FsInfoPanel - shows Filesystem status.
 * @author Peter Tribble
 * @version 1.0
 */
public final class FsInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    private transient JKstat jkstat;
    /**
     * A panel used for fsstat statistics.
     */
    private JFSstatPanel fsPanel;
    private transient BootEnvironments beadm;

    /**
     * Display a file system information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public FsInfoPanel(final SysItem hi, final JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;

	beadm = new BootEnvironments();

	switch (hi.getType()) {
	    case SysItem.FS_FSSTAT:
		displayFS();
		break;
	    case SysItem.ZFS_POOL:
		displayZpool();
		break;
	    case SysItem.ZFS_FS:
		displayZFS();
		break;
	    case SysItem.ZFS_VOLUME:
		displayZVOL();
		break;
	    case SysItem.FS_CONTAINER:
		displaySummary();
		break;
	    case SysItem.ZFS_CONTAINER:
		displayZSummary();
		break;
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (fsPanel != null) {
	    fsPanel.stopLoop();
	}
    }

    /*
     * Top level summary. just df
     *
     * The last column header says "Mounted on" so we need to explicitly
     * specify the number of columns.
     */
    private void displaySummary() {
	addLabel("Filesystem Usage Summary");
	addText(new CommandTableModel(
			new InfoCommand("df", "/usr/sbin/df", "-h"), 6));
    }

    /*
     * Top level summary. just zpool status
     */
    private void displayZSummary() {
	addLabel("ZFS Pool Summary");
	addText(new CommandTableModel(
			new InfoCommand("zp", "/usr/sbin/zpool",
			"list -o name,size,alloc,free,cap,dedup,health")));
    }

    /*
     * Zpool details
     */
    private void displayZpool() {
	Zpool zp = (Zpool) hi.getAttribute("zpool");
	addLabel("Details of ZFS pool " + zp.getName());
	JPanel jzp = new JPanel();
	jzp.setLayout(new BoxLayout(jzp, BoxLayout.PAGE_AXIS));

	InfoCommand ic = new InfoCommand("zp", "/usr/sbin/zpool",
					"status " + zp.getName());
	jzp.add(new SysCmdPanel(ic));

	ic = new InfoCommand("zp", "/usr/sbin/zpool",
			"get -o property,value,source all " + zp.getName());
	JTable jt = new JTable(new CommandTableModel(ic, 3));
	jzp.add(jt.getTableHeader());
	jzp.add(jt);

	addScrollPane(jzp);
    }

    /*
     * fsstat
     */
    private void displayFS() {
	addLabel("Filesystem statistics");
	fsPanel = new JFSstatPanel(jkstat, 5);
	addScrollPane(fsPanel);
    }

    /*
     * ZFS filesystem properties
     */
    private void displayZFS() {
	Zfilesys zfs = (Zfilesys) hi.getAttribute("zfs");
	// this is the UUID of the corresponding boot environment
	Zproperty beprop = zfs.getProperty("org.opensolaris.libbe:uuid");
	// if not found, look for a parent
	if (beprop == null) {
	    beprop = zfs.getProperty("org.opensolaris.libbe:parentbe");
	}
	String beuuid = (beprop == null) ? "" : beprop.getValue();
	String bename = beadm.getBE(beuuid);
	String bedesc = (bename == null) ? "" : " (in BE " + bename + ")";
	addLabel("ZFS properties for " + zfs.getName() + bedesc);
	addScrollPane(new JTable(new ZfsTableModel(zfs.getProperties())));
    }

    /*
     * ZFS volume properties
     */
    private void displayZVOL() {
	Zvolume zfs = (Zvolume) hi.getAttribute("zvol");
	if (zfs == null) {
	    // comes from the plain label
	    addLabel("ZFS volumes");
	    InfoCommand ic = new InfoCommand("zf", "/usr/sbin/zfs",
		"list -t volume -o name,used,avail,refer");
	    addText(new CommandTableModel(ic, 4));
	} else {
	    addLabel("ZFS volume properties for " + zfs.getName());
	    addScrollPane(new JTable(new ZfsTableModel(zfs.getProperties())));
	}
    }
}
