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

import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.gui.AccessoryNetPanel;
import uk.co.petertribble.jkstat.gui.KstatTable;
import uk.co.petertribble.jkstat.gui.KstatBaseChart;
import uk.co.petertribble.jkstat.gui.KstatChart;
import org.tribblix.illuminate.InfoCommand;
import org.tribblix.illuminate.helpers.ManPane;
import java.util.List;
import java.util.Arrays;
import org.jfree.chart.ChartPanel;

/**
 * NetInfoPanel - shows Network status and information.
 * @author Peter Tribble
 * @version 1.0
 *
 */
public final class NetInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    /**
     * A network accessory gadget.
     */
    private AccessoryNetPanel acp;
    private transient JKstat jkstat;
    /**
     * A table for network kstats.
     */
    private KstatTable kt;
    private transient KstatBaseChart kbc;

    /**
     * Display a network information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public NetInfoPanel(final SysItem hi, final JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;

	switch (hi.getType()) {
	    case SysItem.NET_INTERFACE:
		displayInterface(hi.getKstat());
		break;
	    case SysItem.NET_PROTO_IP:
		displayProto("ip");
		break;
	    case SysItem.NET_PROTO_TCP:
		displayProto("tcp");
		break;
	    case SysItem.NET_PROTO_UDP:
		displayProto("udp");
		break;
	    case SysItem.NET_PROTO_SCTP:
		displayProto("sctp");
		break;
	    case SysItem.NET_STAT:
		displayNetstat();
		break;
	    case SysItem.NET_CONTAINER:
		displaySummary();
		break;
	    case SysItem.NET_DLADM:
		displayManual("dladm.8");
		break;
	    case SysItem.NET_DL_PHYS:
		displayDladm("phys");
		break;
	    case SysItem.NET_DL_LINK:
		displayDladm("link");
		break;
	    case SysItem.NET_DL_LINKPROP:
		displayDladm("linkprop");
		break;
	    case SysItem.NET_DL_VNIC:
		displayDladm("vnic");
		break;
	    case SysItem.NET_DL_ETHERSTUB:
		displayDladm("etherstub");
		break;
	    case SysItem.NET_DL_AGGR:
		displayDladm("aggr");
		break;
	    case SysItem.NET_IPADM:
		displayManual("ipadm.8");
		break;
	    case SysItem.NET_IP_IF:
		displayIpadm("if");
		break;
	    case SysItem.NET_IP_IFPROP:
		displayIpadm("ifprop");
		break;
	    case SysItem.NET_IP_ADDR:
		displayIpadm("addr");
		break;
	    case SysItem.NET_IP_ADDRPROP:
		displayIpadm("addrprop");
		break;
	    case SysItem.NET_IP_PROP:
		displayIpadm("prop");
		break;
	    case SysItem.NET_ROUTE_TABLE:
		displayRoute(SysItem.NET_ROUTE_TABLE);
		break;
	    case SysItem.NET_ROUTE_ADM:
		displayRoute(SysItem.NET_ROUTE_ADM);
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
	if (kbc != null) {
	    kbc.stopLoop();
	}
    }

    /*
     * Top level summary.
     */
    private void displaySummary() {
	addLabel("Interfaces: Output from ifconfig -a");

	addText(new InfoCommand("IF", "/usr/sbin/ifconfig", "-a"));
    }

    /*
     * netstat
     */
    private void displayNetstat() {
	addLabel("Output from netstat -an");

	addText(new InfoCommand("IF", "/usr/bin/netstat", "-an"));
    }

    /*
     * dladm
     */
    private void displayDladm(final String dltype) {
	addLabel("Output from dladm show-" + dltype);

	addText(new InfoCommand("IF", "/usr/sbin/dladm", "show-" + dltype));
    }

    /*
     * ipadm
     */
    private void displayIpadm(final String iptype) {
	addLabel("Output from ipadm show-" + iptype);

	addText(new InfoCommand("IF", "/usr/sbin/ipadm", "show-" + iptype));
    }

    /*
     * Routing
     */
    private void displayRoute(final int rtype) {
	if (rtype == SysItem.NET_ROUTE_TABLE) {
	    addLabel("Output from netstat -nr");
	    addText(new InfoCommand("RT", "/usr/bin/netstat", "-nr"));
	} else if (rtype == SysItem.NET_ROUTE_ADM) {
	    addLabel("Output from routeadm");
	    addText(new InfoCommand("RA", "/usr/sbin/routeadm"));
	}
    }

    /*
     * A network interface
     *
     * If the "over" property is set, then we have a vnic over that link
     */
    private void displayInterface(final Kstat ks) {
	if (ks != null) {
	    String overName = (String) hi.getAttribute("over");
	    if (overName == null) {
		addLabel("Details of Network Interface " + ks.getName());
	    } else {
		addLabel("Details of VNIC " + ks.getName() + " over "
			 + overName);
	    }
	    addAccessory(ks);
	}
    }

    /*
     * Generic protocol handler.
     */
    private void displayProto(final String proto) {
	String sflag = (String) hi.getAttribute("flag");
	if ("man".equals(sflag)) {
	    displayManual(proto + ".4p");
	} else if ("stats".equals(sflag)) {
	    addLabel("Network protocol stats: " + proto);
	    kt = new KstatTable(proto, "0", proto + "stat", 5, jkstat);
	    addScrollPane(kt);
	} else if ("6stats".equals(sflag)) {
	    addLabel("Network protocol v6 stats: " + proto);
	    kt = new KstatTable(proto, "0", proto + "6stat", 5, jkstat);
	    addScrollPane(kt);
	} else if ("icmp".equals(sflag)) {
	    addLabel("Network icmp stats: " + proto);
	    kt = new KstatTable(proto, "0", "icmp", 5, jkstat);
	    addScrollPane(kt);
	} else {
	    addLabel("Network protocol: " + proto);
	    kt = new KstatTable(proto, "0", proto, 5, jkstat);
	    addScrollPane(kt);
	}
    }

    /*
     * Display a man page
     */
    private void displayManual(final String manpage) {
	add(new ManPane(manpage));
    }

    /*
     * Add an accessory if we can.
     * NOTE: we've already checked for ks being non-null above
     */
    private void addAccessory(final Kstat ks) {
	acp = new AccessoryNetPanel(ks, 5, jkstat);
	addComponent(acp);
	List<String> statistics = Arrays.asList("rbytes64", "obytes64");
	kbc = new KstatChart(jkstat, ks, statistics, true);
	addComponent(new ChartPanel(kbc.getChart()));
    }
}
