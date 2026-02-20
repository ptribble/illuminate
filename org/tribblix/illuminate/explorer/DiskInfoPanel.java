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
 * Copyright 2026 Peter Tribble
 *
 */

package org.tribblix.illuminate.explorer;

import java.util.Set;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatFilter;
import uk.co.petertribble.jkstat.api.KstatSet;
import uk.co.petertribble.jkstat.api.KstatType;
import uk.co.petertribble.jkstat.gui.AccessoryIOPanel;
import uk.co.petertribble.jkstat.gui.KstatTable;
import uk.co.petertribble.jkstat.gui.IOstatTable;
import org.tribblix.illuminate.InfoCommand;

/**
 * DiskInfoPanel - shows Disk status.
 * @author Peter Tribble
 * @version 1.0
 */
public final class DiskInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    /**
     * A panel for an IO accessory.
     */
    private AccessoryIOPanel acp;
    private transient JKstat jkstat;
    /**
     * This panel's kstat.
     */
    private Kstat ks;
    /**
     * A table to display the current kstat.
     */
    private KstatTable kt;
    /**
     * A table to display the current IO kstat.
     */
    private IOstatTable iot;
    private transient Mnttab mnttab = new Mnttab();

    /**
     * Display a Disk information panel.
     *
     * @param hi The item to display
     * @param njkstat A JKstat object
     */
    public DiskInfoPanel(final SysItem hi, final JKstat njkstat) {
	super(hi);
	jkstat = njkstat;
	ks = hi.getKstat();

	switch (hi.getType()) {
	    case SysItem.DISK_IO:
		displayIO();
		break;
	    case SysItem.DISK:
		displayDisk();
		break;
	    case SysItem.DISK_PARTITION:
		displayPartition();
		break;
	    case SysItem.DISK_CONTAINER:
		displaySummary();
		break;
	    default:
		System.err.println("Invalid DISK type " + hi.getType());
		break;
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (acp != null) {
	    acp.stopLoop();
	}
	if (kt != null) {
	    kt.stopLoop();
	}
	if (iot != null) {
	    iot.stopLoop();
	}
    }

    /*
     * Top level summary.
     */
    private void displaySummary() {
	addLabel("Disk Summary");
	Integer nd = (Integer) hi.getAttribute("ndisks");
	addLabel("System contains " + nd + " disks");
	addText(new InfoCommand("IO", "/usr/bin/iostat", "-En"));
    }

    /*
     * IO table
     */
    private void displayIO() {
	addLabel("I/O statistics");
	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.setFilterType(KstatType.KSTAT_TYPE_IO);
	// ignore usba statistics
	ksf.addNegativeFilter("usba:::");
	iot = new IOstatTable(new KstatSet(jkstat, ksf), 5, jkstat);
	addScrollPane(iot);
    }

    /*
     * A disk device
     */
    private void displayDisk() {
	if (ks != null) {
	    String dname = ks.getName();
	    if (isPool(dname)) {
		addLabel("More on pool " + dname);
	    } else {
		addLabel("Details of device " + dname);
	    }
	    displayAka();
	    if (isPool(dname)) {
		addText(new CommandTableModel(
		    new InfoCommand("zp", "/usr/sbin/zpool",
		    "list -o name,size,alloc,free,cap,dedup,health " + dname)));
	    }
	    addDiskInfo();
	    addAccessory();
	}
    }

    /*
     * Is this a zfs pool?
     */
    private boolean isPool(final String s) {
	for (Zpool pool : ZFSconfig.getInstance().pools()) {
	    if (pool.getName().equals(s)) {
		return true;
	    }
	}
	return false;
    }

    /*
     * A disk partition (or is that slice?)
     */
    private void displayPartition() {
	if (ks != null) {
	    String[] ds = ks.getName().split(",", 2);
	    addLabel("Details of partition " + ds[1] + " on disk " + ds[0]);
	    displayAka();
	    addAccessory();
	}
    }

    private void displayAka() {
	ZFSconfig zconfig = ZFSconfig.getInstance();
	DevicePath devpath = DevicePath.getInstance();
	/*
	 * Check for devices matching this name. There may be multiple or only
	 * one.
	 */
	Set<String> salt = devpath.getDiskNames(ks.getName());
	if (salt == null) {
	    String devname = devpath.getDiskName(ks.getName());
	    if (devname != null) {
		addLabel("Also known as " + devname);
		showAka(devname, zconfig);
	    }
	} else {
	    addLabel("Also known as " + salt);
	    for (String devname : salt) {
		showAka(devname, zconfig);
	    }
	}
    }

    private void showAka(final String devname, final ZFSconfig zconfig) {
	for (Zpool pool : zconfig.pools()) {
	    if (pool.contains(devname)) {
		addLabel("Part of ZFS pool " + pool.getName());
	    }
	}
	String fs = mnttab.getFs("/dev/dsk/" + devname);
	if (fs != null) {
	    addLabel("Mounted as a " + mnttab.getFsType(fs)
			+ " file system at " + fs);
	}
	// FIXME check swap devices
    }

    /*
     * Display information like iostat -E.
     * NOTE: we've already checked for ks being non-null above
     */
    private void addDiskInfo() {
	Kstat ksd = jkstat.getKstat(ks.getModule() + "err", ks.getInst(),
				    ks.getName() + ",err");
	if (ksd != null) {
	    kt = new KstatTable(ks.getModule() + "err", ks.getInstance(),
				ks.getName() + ",err", -1, jkstat);
	    addScrollPane(kt);
	}
	// wouldn't life be simple if names were consistent?
	ksd = jkstat.getKstat(ks.getModule() + "error", ks.getInst(),
				    ks.getName() + ",error");
	if (ksd != null) {
	    kt = new KstatTable(ks.getModule() + "error", ks.getInstance(),
				ks.getName() + ",error", -1, jkstat);
	    addScrollPane(kt);
	}
    }

    /*
     * Add an accessory if we can.
     * NOTE: we've already checked for ks being non-null above
     */
    private void addAccessory() {
	acp = new AccessoryIOPanel(ks, 5, jkstat);
	addComponent(acp);
    }
}
