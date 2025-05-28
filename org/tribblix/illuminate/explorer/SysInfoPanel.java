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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import uk.co.petertribble.jkstat.api.JKstat;

/**
 * SysInfoPanel - shows System status.
 * @author Peter Tribble
 * @version 1.0
 *
 */
public final class SysInfoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private InfoPanel ip;
    private transient JKstat jkstat;

    /**
     * Display an Sys information panel.
     *
     * @param jkstat A JKstat object
     */
    public SysInfoPanel(JKstat jkstat) {
	this.jkstat = jkstat;
	setLayout(new BorderLayout());

	ip = new SummaryPanel(new SysItem(SysItem.HOST), jkstat);
	add(ip);
    }

    /**
     * Display an information panel appropriate to the hardware item that is
     * selected. Stops and removes any previous panel that may have been
     * displayed.
     *
     * @param hi The item to display
     */
    public void display(SysItem hi) {
	if (ip != null) {
	    ip.stopLoop();
	}
	removeAll();
	validate();

	switch (hi.getType()) {
	    case SysItem.HOST:
		ip = new SummaryPanel(hi, jkstat);
		add(ip);
		break;
	    case SysItem.CPU_CONTAINER:
	    case SysItem.CPU:
	    case SysItem.CPU_CORE:
	    case SysItem.CPU_THREAD:
		ip = new CpuInfoPanel(hi, jkstat);
		add(ip);
		break;
	    case SysItem.DISK_CONTAINER:
	    case SysItem.DISK:
	    case SysItem.DISK_IO:
	    case SysItem.DISK_PARTITION:
		ip = new DiskInfoPanel(hi, jkstat);
		add(ip);
		break;
	    case SysItem.NET_CONTAINER:
	    case SysItem.NET_INTERFACE:
	    case SysItem.NET_PROTOCOL:
	    case SysItem.NET_PROTO_IP:
	    case SysItem.NET_PROTO_TCP:
	    case SysItem.NET_PROTO_UDP:
	    case SysItem.NET_PROTO_SCTP:
	    case SysItem.NET_STAT:
	    case SysItem.NET_DLADM:
	    case SysItem.NET_DL_PHYS:
	    case SysItem.NET_DL_LINK:
	    case SysItem.NET_DL_LINKPROP:
	    case SysItem.NET_DL_VNIC:
	    case SysItem.NET_DL_ETHERSTUB:
	    case SysItem.NET_DL_AGGR:
	    case SysItem.NET_IPADM:
	    case SysItem.NET_IP_IF:
	    case SysItem.NET_IP_IFPROP:
	    case SysItem.NET_IP_ADDR:
	    case SysItem.NET_IP_ADDRPROP:
	    case SysItem.NET_IP_PROP:
	    case SysItem.NET_ROUTE_TABLE:
	    case SysItem.NET_ROUTE_ADM:
		ip = new NetInfoPanel(hi, jkstat);
		add(ip);
		break;
	    case SysItem.MEM_CONTAINER:
	    case SysItem.MEM_KMEM:
	    case SysItem.MEM_ARCSTAT:
		ip = new MemoryInfoPanel(hi, jkstat);
		add(ip);
		break;
	    case SysItem.FS_FSSTAT:
	    case SysItem.FS_CONTAINER:
	    case SysItem.ZFS_CONTAINER:
	    case SysItem.ZFS_FS:
	    case SysItem.ZFS_POOL:
	    case SysItem.ZFS_VOLUME:
		ip = new FsInfoPanel(hi, jkstat);
		add(ip);
		break;
	    case SysItem.ZONE_CONTAINER:
	    case SysItem.ZONE_ZONE:
	    case SysItem.ZONE_PROC:
	    case SysItem.ZONE_NET:
	    case SysItem.ZONE_KSTAT:
	    case SysItem.ZONE_USAGE:
		ip = new ZoneInfoPanel(hi, jkstat);
		add(ip);
		break;
	    case SysItem.PROCESS_CONTAINER:
		ip = new ProcessInfoPanel(hi);
		add(ip);
		break;
	}
	validate();
    }
}
