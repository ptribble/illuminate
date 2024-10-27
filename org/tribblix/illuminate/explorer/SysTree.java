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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import uk.co.petertribble.jkstat.api.*;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import org.tribblix.illuminate.IlluminateResources;

/**
 * SysTree - shows a hierarchical hardware view.
 * @author Peter Tribble
 * @version 1.0
 */
public class SysTree extends JTree {

    private static final long serialVersionUID = 1L;

    private Map <String, Kstat> netMap = new HashMap<>();
    private JKstat jkstat;

    /**
     * Display a tree of hardware items.
     *
     * @param title the name of the tree
     */
    public SysTree(String title) {
	this(new NativeJKstat(), title);
    }

    /**
     * Display a tree of hardware items.
     *
     * @param title the name of the tree
     * @param jkstat a JKstat object
     */
    public SysTree(JKstat jkstat, String title) {
	this.jkstat = jkstat;

	DefaultMutableTreeNode root = new SysTreeNode(
					new SysItem(SysItem.HOST), title);
	buildTree(root);
	setModel(new DefaultTreeModel(root));
    }

    private void buildTree(DefaultMutableTreeNode root) {
	addProcessors(root);
	addDisks(root);
	addNetworks(root);
	ZFSconfig zconfig = ZFSconfig.getInstance();
	addMemory(root, zconfig);
	addFilesystems(root, zconfig);
	addProcesses(root);
	addZones(root);
    }

    private void addProcessors(DefaultMutableTreeNode root) {
	/*
	 * Add a node for each processor.
	 */
	ProcessorTree proctree = new ProcessorTree(jkstat);
	SysItem hi = new SysItem(SysItem.CPU_CONTAINER);
	hi.addAttribute("ptree", proctree);
	SysTreeNode htn = new SysTreeNode(hi,
				IlluminateResources.getString("HARD.CPU"));
	root.add(htn);

	// loop over chips
	for (Long l : proctree.getChips()) {
	    SysItem hi2 = new SysItem(SysItem.CPU);
	    hi2.addAttribute("ptree", proctree);
	    hi2.addAttribute("chip", l);
	    SysTreeNode htnchip = new SysTreeNode(hi2, "CPU " + l.toString());
	    if (proctree.isMulticore()) {
		// multicore, loop over cores
		for (Long ll : proctree.getCores(l)) {
		    SysItem hi3 = new SysItem(SysItem.CPU_CORE);
		    SysTreeNode htncore = new SysTreeNode(hi3,
						    "Core " + ll.toString());
		    hi3.addAttribute("ptree", proctree);
		    hi3.addAttribute("chip", l);
		    hi3.addAttribute("core", ll);
		    if (proctree.isThreaded()) {
			// multithreaded, loop over threads
			for (Kstat ks : proctree.coreInfoStats(l, ll)) {
			    SysItem hi4 = new SysItem(SysItem.CPU_THREAD);
			    hi4.setKstat(ks);
			    hi4.addAttribute("ptree", proctree);
			    hi4.addAttribute("thread", ks.getInst());
			    hi4.addAttribute("core", ll);
			    hi4.addAttribute("chip", l);
			    htncore.add(new SysTreeNode(hi4,
						"Thread " + ks.getInstance()));
			}
		    } else {
			// single thread
			// we should go round the loop exactly once
			for (Kstat ks : proctree.coreInfoStats(l, ll)) {
			    hi3.setKstat(ks);
			}
		    }
		    htnchip.add(htncore);
		}
	    } else {
		// single core
		if (proctree.isThreaded()) {
		    // multithreaded, loop over threads
		    for (Kstat ks : proctree.chipInfoStats(l)) {
			SysItem hi3 = new SysItem(SysItem.CPU_THREAD);
			hi3.setKstat(ks);
			hi3.addAttribute("ptree", proctree);
			hi3.addAttribute("thread", ks.getInst());
			hi3.addAttribute("chip", l);
			htnchip.add(new SysTreeNode(hi3,
						ks.getInstance()));
		    }
		} else {
		    // single thread
		    for (Kstat ks : proctree.chipInfoStats(l)) {
			hi2.setKstat(ks);
		    }
		}
	    }
	    htn.add(htnchip);
	}
    }

    private void addDisks(DefaultMutableTreeNode root) {
	SysItem diskItem = new SysItem(SysItem.DISK_CONTAINER);
	SysTreeNode htn = new SysTreeNode(diskItem,
				IlluminateResources.getString("HARD.DISK"));
	root.add(htn);
	htn.add(new SysTreeNode(new SysItem(SysItem.DISK_IO),
				IlluminateResources.getString("HARD.IO")));

	/*
	 * Here we enumerate disks. We build up the list of devices into
	 * the 'disks' Set, which sorts them, and run through that at the
	 * end to add all the nodes to the tree.
	 */
	Set <SysTreeNode> disks = new TreeSet<>();
	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.setFilterClass("disk");
	KstatSet kss = new KstatSet(jkstat, ksf);
	/*
	 * zfs pools also come under class "disk" which is probably an error
	 * we do want them enumerated, but don't want them in the count
	 */
	int ndisks = 0;
	for (Kstat ks : kss.getKstats()) {
	    SysItem hi = new SysItem(SysItem.DISK);
	    hi.setKstat(ks);
	    SysTreeNode htndisk = new SysTreeNode(hi, ks.getName());
	    disks.add(htndisk);
	    if (!"zfs".equals(ks.getModule())) {
		ndisks++;
	    }
	    // add partitions
	    KstatFilter ksfp = new KstatFilter(jkstat);
	    ksfp.setFilterClass("partition");
	    ksfp.addFilter(ks.getModule() + ":" + ks.getInstance() + "::");
	    KstatSet kssp = new KstatSet(jkstat, ksfp);
	    for (Kstat ksp : new TreeSet <Kstat>(kssp.getKstats())) {
		SysItem hi3 = new SysItem(SysItem.DISK_PARTITION);
		hi3.setKstat(ksp);
		htndisk.add(new SysTreeNode(hi3, ksp.getName()));
	    }
	}

	diskItem.addAttribute("ndisks", ndisks);

	// now add the entries to the tree, so they end up sorted
	for (SysTreeNode stn : disks) {
	    htn.add(stn);
	}
    }

    private void addNetworks(DefaultMutableTreeNode root) {
	SysTreeNode htn = new SysTreeNode(new SysItem(SysItem.NET_CONTAINER),
				IlluminateResources.getString("HARD.NETWORK"));
	root.add(htn);

	// enumerate networks
	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.setFilterClass("net");
	ksf.addFilter(":::rbytes64");
	ksf.addNegativeFilter("::mac");
	for (Kstat ks : new TreeSet <Kstat>(ksf.getKstats())) {
	    SysItem hi = new SysItem(SysItem.NET_INTERFACE);
	    hi.setKstat(ks);
	    htn.add(new SysTreeNode(hi, ks.getName()));
	    netMap.put(ks.getName(), ks);
	}

	// add network protocols
	SysTreeNode htnp = new SysTreeNode(new SysItem(SysItem.NET_PROTOCOL),
				IlluminateResources.getString("HARD.NETPROTO"));
	htn.add(htnp);

	SysTreeNode htnpx = new SysTreeNode(
				new SysItem(SysItem.NET_PROTO_IP), "ip");
	htnpx.add(new SysTreeNode(
		   new SysItem(SysItem.NET_PROTO_IP, "icmp"), "icmp"));
	htnpx.add(new SysTreeNode(
		   new SysItem(SysItem.NET_PROTO_IP, "stats"), "ipstats"));
	htnpx.add(new SysTreeNode(
		    new SysItem(SysItem.NET_PROTO_IP, "6stats"), "ip6stats"));
	htnpx.add(new SysTreeNode(
			new SysItem(SysItem.NET_PROTO_IP, "man"), "manual"));
	htnp.add(htnpx);
	htnpx = new SysTreeNode(new SysItem(SysItem.NET_PROTO_TCP), "tcp");
	htnpx.add(new SysTreeNode(
		    new SysItem(SysItem.NET_PROTO_TCP, "stats"), "tcpstats"));
	htnpx.add(new SysTreeNode(
		    new SysItem(SysItem.NET_PROTO_TCP, "man"), "manual"));
	htnp.add(htnpx);
	htnpx = new SysTreeNode(new SysItem(SysItem.NET_PROTO_UDP), "udp");
	htnpx.add(new SysTreeNode(
		    new SysItem(SysItem.NET_PROTO_UDP, "stats"), "udpstats"));
	htnpx.add(new SysTreeNode(
		    new SysItem(SysItem.NET_PROTO_UDP, "man"), "manual"));
	htnp.add(htnpx);
	htnpx = new SysTreeNode(new SysItem(SysItem.NET_PROTO_SCTP), "sctp");
	htnpx.add(new SysTreeNode(
		    new SysItem(SysItem.NET_PROTO_SCTP, "stats"), "sctpstats"));
	htnpx.add(new SysTreeNode(
		    new SysItem(SysItem.NET_PROTO_SCTP, "man"), "manual"));
	htnp.add(htnpx);

	// dladm output
	SysTreeNode htnd = new SysTreeNode(new SysItem(SysItem.NET_DLADM),
				"dladm");
	htn.add(htnd);
	htnd.add(new SysTreeNode(new SysItem(SysItem.NET_DLADM_PHYS), "phys"));
	htnd.add(new SysTreeNode(new SysItem(SysItem.NET_DLADM_LINK), "link"));
	htnd.add(new SysTreeNode(new SysItem(SysItem.NET_DLADM_LINKPROP),
				"linkprop"));
	htnd.add(new SysTreeNode(new SysItem(SysItem.NET_DLADM_VNIC), "vnic"));
	htnd.add(new SysTreeNode(new SysItem(SysItem.NET_DLADM_ETHERSTUB),
				"etherstub"));
	htnd.add(new SysTreeNode(new SysItem(SysItem.NET_DLADM_AGGR), "aggr"));

	// dladm output
	SysTreeNode htni = new SysTreeNode(new SysItem(SysItem.NET_IPADM),
				"ipadm");
	htn.add(htni);
	htni.add(new SysTreeNode(new SysItem(SysItem.NET_IPADM_IF), "if"));
	htni.add(new SysTreeNode(new SysItem(SysItem.NET_IPADM_IFPROP),
				"ifprop"));
	htni.add(new SysTreeNode(new SysItem(SysItem.NET_IPADM_ADDR), "addr"));
	htni.add(new SysTreeNode(new SysItem(SysItem.NET_IPADM_ADDRPROP),
				"addrprop"));
	htni.add(new SysTreeNode(new SysItem(SysItem.NET_IPADM_PROP), "prop"));

	// Routing
	SysTreeNode htnr = new SysTreeNode(new SysItem(SysItem.NET_ROUTE),
				IlluminateResources.getString("HARD.ROUTING"));
	htn.add(htnr);
	htnr.add(new SysTreeNode(
			     new SysItem(SysItem.NET_ROUTE_TABLE), "Table"));
	htnr.add(new SysTreeNode(
			     new SysItem(SysItem.NET_ROUTE_ADM), "Services"));

	// netstat -an
	htn.add(new SysTreeNode(new SysItem(SysItem.NET_STAT), "netstat"));
    }

    private void addMemory(DefaultMutableTreeNode root, ZFSconfig zconfig) {
	SysTreeNode htn = new SysTreeNode(new SysItem(SysItem.MEM_CONTAINER),
				IlluminateResources.getString("HARD.MEMORY"));
	root.add(htn);

	htn.add(new SysTreeNode(new SysItem(SysItem.MEM_KMEM),
				IlluminateResources.getString("HARD.KMEM")));

	if (!zconfig.pools().isEmpty()) {
	    htn.add(new SysTreeNode(new SysItem(SysItem.MEM_ARCSTAT),
				IlluminateResources.getString("HARD.ZMEM")));
	}
    }

    private void addFilesystems(DefaultMutableTreeNode root,
				ZFSconfig zconfig) {
	SysTreeNode htn = new SysTreeNode(new SysItem(SysItem.FS_CONTAINER),
				IlluminateResources.getString("HARD.FS"));
	root.add(htn);

	htn.add(new SysTreeNode(new SysItem(SysItem.FS_FSSTAT),
				IlluminateResources.getString("HARD.FSSTAT")));

	if (!zconfig.pools().isEmpty()) {
	    SysTreeNode zn = new SysTreeNode(new SysItem(SysItem.ZFS_CONTAINER),
				IlluminateResources.getString("HARD.ZFS"));
	    htn.add(zn);
	    for (Zpool zpool : zconfig.pools()) {
		addPool(zn, zpool);
	    }
	}
    }

    /*
     * Add a pool, then its datasets.
     */
    private void addPool(SysTreeNode stn, Zpool zp) {
	SysItem zpitem = new SysItem(SysItem.ZFS_POOL);
	zpitem.addAttribute("zpool", zp);
	SysTreeNode ptn = new SysTreeNode(zpitem, zp.getName());
	stn.add(ptn);
	addZnode(ptn, zp.getParent());
	addZvol(ptn, zp);
    }

    /*
     * Add a zfs dataset, and then call recursively to add all that
     * dataset's children
     */
    private void addZnode(SysTreeNode stn, Zfilesys zfs) {
	SysItem zitem = new SysItem(SysItem.ZFS_FS);
	zitem.addAttribute("zfs", zfs);
	SysTreeNode ztn = new SysTreeNode(zitem, zfs.getShortName());
	stn.add(ztn);
	for (Zfilesys zfs2 : zfs.getChildren()) {
	    addZnode(ztn, zfs2);
	}
    }

    /*
     * If there are any volumes in this pool, add them, just as a flat list.
     */
    private void addZvol(SysTreeNode stn, Zpool zp) {
	Set <Zvolume> svol = zp.volumes();
	if (!svol.isEmpty()) {
	    SysItem zitem = new SysItem(SysItem.ZFS_VOLUME);
	    SysTreeNode vtn = new SysTreeNode(zitem,
				IlluminateResources.getString("HARD.ZVOL"));
	    stn.add(vtn);
	    for (Zvolume zvol : svol) {
		zitem = new SysItem(SysItem.ZFS_VOLUME);
		zitem.addAttribute("zvol", zvol);
		vtn.add(new SysTreeNode(zitem, zvol.getShortName()));
	    }
	}
    }

    /*
     * Add processes
     */
    private void addProcesses(DefaultMutableTreeNode root) {
	root.add(new SysTreeNode(new SysItem(SysItem.PROCESS_CONTAINER),
				IlluminateResources.getString("HARD.PS")));
    }

    /*
     * Add zones. Only display the zone tree if we're in the global zone
     * and there are non-global zones.
     */
    private void addZones(DefaultMutableTreeNode root) {
	ZoneConfig zc = ZoneConfig.getInstance();
	if (!zc.isGlobalZone() || zc.size() == 0) {
	    return;
	}
	SysTreeNode stn = new SysTreeNode(new SysItem(SysItem.ZONE_CONTAINER),
				IlluminateResources.getString("HARD.ZONES"));
	root.add(stn);
	stn.add(new SysTreeNode(new SysItem(SysItem.ZONE_USAGE),
			IlluminateResources.getString("HARD.ZONEUSAGE")));
	for (String zname : zc.names()) {
	    SysItem zitem = new SysItem(SysItem.ZONE_ZONE);
	    ZoneEntry ze = zc.getZoneEntry(zname);
	    zitem.addAttribute("zoneentry", ze);
	    SysTreeNode ztn = new SysTreeNode(zitem, zname);
	    stn.add(ztn);
	    /*
	     * Only add a process subentry if the zone is running
	     * Add kstat table if the kstats exist
	     */
	    if (ze.isRunning()) {
		zitem = new SysItem(SysItem.ZONE_PROC);
		zitem.addAttribute("zoneentry", ze);
		SysTreeNode zptn = new SysTreeNode(zitem,
				IlluminateResources.getString("HARD.PS"));
		ztn.add(zptn);
		Kstat ks =
		    jkstat.getKstat("zones", ze.getZoneid(), ze.getName());
		if (ks != null) {
		    zitem  = new SysItem(SysItem.ZONE_KSTAT);
		    zitem.addAttribute("zoneentry", ze);
		    zitem.setKstat(ks);
		    zptn = new SysTreeNode(zitem,
				IlluminateResources.getString("ZONE.KSTAT"));
		    ztn.add(zptn);
		}
	    } else {
		zitem.setStatus(SysItem.WARN);
	    }
	    /*
	     * Add a network subentry if the zone is exclusive-ip
	     */
	    if (ze.isExclusiveIP()) {
		zitem = new SysItem(SysItem.ZONE_NET);
		zitem.addAttribute("zoneentry", ze);
		zitem.addAttribute("netmap", netMap);
		SysTreeNode zntn = new SysTreeNode(zitem,
				IlluminateResources.getString("HARD.NETWORK"));
		ztn.add(zntn);
	    }
	}
    }
}
