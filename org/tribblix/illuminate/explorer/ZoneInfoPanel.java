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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.tribblix.illuminate.InfoCommand;
import org.tribblix.illuminate.helpers.ManFrame;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.gui.AccessoryNetPanel;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import uk.co.petertribble.jkstat.gui.KstatAccessorySet;
import uk.co.petertribble.jkstat.gui.SparkRateAccessory;
import uk.co.petertribble.jkstat.gui.KstatTable;

/**
 * ZoneInfoPanel - shows Zone information.
 * @author Peter Tribble
 * @version 1.0
 */
public final class ZoneInfoPanel extends InfoPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private transient JKstat jkstat;
    private transient KstatAccessorySet kas;
    /**
     * A table for kstat data.
     */
    private KstatTable kt;
    private transient List<KstatAccessoryPanel> kaplist;
    /**
     * A button to show the zone brand manual page.
     */
    private JButton jmb;

    /**
     * Display a zone information panel.
     *
     * @param hi The item to display
     * @param njkstat A JKstat object
     */
    public ZoneInfoPanel(final SysItem hi, final JKstat njkstat) {
	super(hi);
	jkstat = njkstat;
	kaplist = new ArrayList<>();

	if (hi.getType() == SysItem.ZONE_CONTAINER) {
	    displaySummary();
	} else if (hi.getType() == SysItem.ZONE_ZONE) {
	    displayZone();
	} else if (hi.getType() == SysItem.ZONE_PROC) {
	    displayZoneProc();
	} else if (hi.getType() == SysItem.ZONE_NET) {
	    displayZoneNet();
	} else if (hi.getType() == SysItem.ZONE_KSTAT) {
	    displayZoneKstat();
	} else if (hi.getType() == SysItem.ZONE_USAGE) {
	    displayZoneProc();
	}

	validate();
	kas = new KstatAccessorySet(kaplist, 1);
    }

    @Override
    public void stopLoop() {
	kas.stopLoop();
	if (kt != null) {
	    kt.stopLoop();
	}
    }

    /*
     * Top level summary. List of Zones.
     */
    private void displaySummary() {
	addLabel("Zone Summary");
	addText(new CommandTableModel(new InfoCommand("za", "/usr/sbin/zoneadm",
					"list -icv")));
    }

    /*
     * Describe a Zone.
     */
    private void displayZone() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	JToolBar jtb = new JToolBar();
	jtb.setFloatable(false);
	jtb.setRollover(true);
	jtb.setLayout(new BorderLayout());
	jtb.add(new JLabel(
		"Details of " + ze.getState() + " zone " + ze.getName()),
		BorderLayout.LINE_START);
	jtb.addSeparator();
	if (new File("/usr/share/man/man7/" + ze.getBrand() + ".7").exists()) {
	    jmb = new JButton("About " + ze.getBrand() + " zones");
	    jmb.setEnabled(true);
	    jmb.setName(ze.getBrand() + ".7");
	    jmb.addActionListener(this);
	    jtb.add(jmb, BorderLayout.LINE_END);
	}

	addComponent(jtb);
	addText(ze.getConfig());
    }

    /*
     * Show Zone processes
     */
    private void displayZoneProc() {
	addComponent(new ProcessInfoPanel(hi));
    }

    /*
     * Show Zone kstats
     */
    private void displayZoneKstat() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	addLabel("Zone " + ze.getName() + " kstats");
	kt = new KstatTable(hi.getKstat(), 1, jkstat);
	addScrollPane(kt);
    }

    /*
     * Show Zone network
     */
    private void displayZoneNet() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	@SuppressWarnings("unchecked")
	Map<String, Kstat> netMap =
	    (Map<String, Kstat>) hi.getAttribute("netmap");
	for (ZoneNet znet : ze.getNetworks()) {
	    Kstat ks = netMap.get(znet.getPhysical());
	    if (ks == null) {
		addText("Interface " + znet.getPhysical());
	    } else {
		// outer panel
		JPanel opanel = new JPanel();
		opanel.setLayout(new BoxLayout(opanel, BoxLayout.PAGE_AXIS));

		// regular network accessory
		AccessoryNetPanel acp = new AccessoryNetPanel(ks, 5, jkstat);
		kaplist.add(acp);
		opanel.add(acp);

		// panel for row of sparkcharts
		JPanel npanel = new JPanel();
		npanel.add(new JLabel("In: "));
		SparkRateAccessory kap =
		    new SparkRateAccessory(ks, -1, jkstat, "rbytes64");
		kap.enableTips("Current kb/s in:", 1.0 / 1024.0);
		npanel.add(kap);
		kaplist.add(kap);
		npanel.add(new JLabel("  Out: "));
		kap = new SparkRateAccessory(ks, -1, jkstat, "obytes64");
		kap.enableTips("Current kb/s out:", 1.0 / 1024.0);
		npanel.add(kap);
		kaplist.add(kap);
		opanel.add(npanel);
		opanel.setBorder(BorderFactory.createTitledBorder(
				 "Interface " + ks.getName()));
		addComponent(opanel);
	    }
	}
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	if (e.getSource() == jmb) {
	    new ManFrame(jmb.getName());
	}
    }
}
