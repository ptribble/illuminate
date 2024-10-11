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

import uk.co.petertribble.jkstat.api.*;
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
public class NetInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    private AccessoryNetPanel acp;
    private JKstat jkstat;
    private KstatTable kt;
    private KstatBaseChart kbc;

    /**
     * Display a network information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public NetInfoPanel(SysItem hi, JKstat jkstat) {
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
	    case SysItem.NET_DLADM_PHYS:
		displayDladm("phys");
		break;
	    case SysItem.NET_DLADM_LINK:
		displayDladm("link");
		break;
	    case SysItem.NET_DLADM_LINKPROP:
		displayDladm("linkprop");
		break;
	    case SysItem.NET_DLADM_VNIC:
		displayDladm("vnic");
		break;
	    case SysItem.NET_DLADM_ETHERSTUB:
		displayDladm("etherstub");
		break;
	    case SysItem.NET_DLADM_AGGR:
		displayDladm("aggr");
		break;
	    case SysItem.NET_IPADM:
		displayManual("ipadm.8");
		break;
	    case SysItem.NET_IPADM_IF:
		displayIpadm("if");
		break;
	    case SysItem.NET_IPADM_IFPROP:
		displayIpadm("ifprop");
		break;
	    case SysItem.NET_IPADM_ADDR:
		displayIpadm("addr");
		break;
	    case SysItem.NET_IPADM_ADDRPROP:
		displayIpadm("addrprop");
		break;
	    case SysItem.NET_IPADM_PROP:
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
    private void displayDladm(String dltype) {
	addLabel("Output from dladm show-"+dltype);

	addText(new InfoCommand("IF", "/usr/sbin/dladm", "show-"+dltype));
    }

    /*
     * ipadm
     */
    private void displayIpadm(String iptype) {
	addLabel("Output from ipadm show-"+iptype);

	addText(new InfoCommand("IF", "/usr/sbin/ipadm", "show-"+iptype));
    }

    /*
     * Routing
     */
    private void displayRoute(int rtype) {
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
     */
    private void displayInterface(Kstat ks) {
	if (ks != null) {
	    addLabel("Details of Network Interface " + ks.getName());
	    addAccessory(ks);
	}
    }

    /*
     * Generic protocol handler.
     */
    private void displayProto(String proto) {
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
    private void displayManual(String manpage) {
	add(new ManPane(manpage));
    }

    /*
     * Add an accessory if we can.
     * NOTE: we've already checked for ks being non-null above
     */
    private void addAccessory(Kstat ks) {
	acp = new AccessoryNetPanel(ks, 5, jkstat);
	addComponent(acp);
	List <String> statistics = Arrays.asList("rbytes64", "obytes64");
	kbc = new KstatChart(jkstat, ks, statistics, true);
	addComponent(new ChartPanel(kbc.getChart()));
    }
}
