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

package org.tribblix.illuminate.explorer;

import java.util.Set;
import uk.co.petertribble.jkstat.api.*;
import uk.co.petertribble.jkstat.gui.AccessoryIOPanel;
import uk.co.petertribble.jkstat.gui.KstatTable;
import uk.co.petertribble.jkstat.gui.IOstatTable;
import org.tribblix.illuminate.InfoCommand;

/**
 * DiskInfoPanel - shows Disk status.
 * @author Peter Tribble
 * @version 1.0
 */
public class DiskInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    private AccessoryIOPanel acp;
    private JKstat jkstat;
    private Kstat ks;
    private KstatTable kt;
    private IOstatTable iot;
    private transient Mnttab mnttab = new Mnttab();

    /**
     * Display a Disk information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public DiskInfoPanel(SysItem hi, JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;
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
    private boolean isPool(String s) {
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
	Set <String> salt = devpath.getDiskNames(ks.getName());
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

    private void showAka(String devname, ZFSconfig zconfig) {
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
