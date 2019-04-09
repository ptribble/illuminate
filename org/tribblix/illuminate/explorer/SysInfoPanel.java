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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import uk.co.petertribble.jkstat.api.JKstat;

/**
 * SysInfoPanel - shows System status.
 * @author Peter Tribble
 * @version 1.0
 *
 */
public class SysInfoPanel extends JPanel {

    private InfoPanel ip;
    private JKstat jkstat;

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
	    case SysItem.NET_STAT:
	    case SysItem.NET_DLADM:
	    case SysItem.NET_DLADM_PHYS:
	    case SysItem.NET_DLADM_LINK:
	    case SysItem.NET_DLADM_LINKPROP:
	    case SysItem.NET_DLADM_VNIC:
	    case SysItem.NET_DLADM_ETHERSTUB:
	    case SysItem.NET_DLADM_AGGR:
	    case SysItem.NET_IPADM:
	    case SysItem.NET_IPADM_IF:
	    case SysItem.NET_IPADM_IFPROP:
	    case SysItem.NET_IPADM_ADDR:
	    case SysItem.NET_IPADM_ADDRPROP:
	    case SysItem.NET_IPADM_PROP:
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
		ip = new FsInfoPanel(hi, jkstat);
		add(ip);
		break;
	    case SysItem.ZONE_CONTAINER:
	    case SysItem.ZONE_ZONE:
	    case SysItem.ZONE_PROC:
	    case SysItem.ZONE_NET:
		ip = new ZoneInfoPanel(hi);
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
