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

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatFilter;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import uk.co.petertribble.jkstat.gui.KstatAccessorySet;
import uk.co.petertribble.jkstat.gui.SparkRateAccessory;
import uk.co.petertribble.jkstat.gui.SparkValueAccessory;
import uk.co.petertribble.jkstat.gui.KstatResources;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import java.util.List;
import java.util.ArrayList;

/**
 * SummaryPanel - shows overall summary of activity.
 * @author Peter Tribble
 * @version 1.0
 */
public final class SummaryPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    private final transient KstatAccessorySet kas;
    private final transient List<KstatAccessoryPanel> kaplist;
    private final transient JKstat jkstat;

    /**
     * Display a summary information panel.
     *
     * @param hi The item to display
     * @param njkstat A JKstat object
     */
    public SummaryPanel(final SysItem hi, final JKstat njkstat) {
	super(hi);
	jkstat = njkstat;
	kaplist = new ArrayList<>();

	displaySummary();

	validate();
	kas = new KstatAccessorySet(kaplist, 1);
    }

    @Override
    public void stopLoop() {
	kas.stopLoop();
    }

    /*
     * Top level summary. We should have a description, cpu summary, network
     * chart, probably process count, load average, and memory summary
     */
    private void displaySummary() {
	addCpuDescription();
	addLoadAccessory();
	addCpuAccessory();
	addNetAccessory();
    }

    /*
     * Add a cpu description panel
     */
    private void addCpuDescription() {
	ProcessorTree proctree = new ProcessorTree(jkstat);

	int nchips = proctree.numChips();
	int ncores = proctree.numCores();
	int nthreads = proctree.numThreads();
	String sbrand = proctree.getBrand();

	StringBuilder sb2 = new StringBuilder(64);
	if (nchips == 1) {
	    sb2.append("System contains 1 chip");
	} else {
	    sb2.append("System contains ").append(nchips).append(" chips");
	}
	if (nchips != ncores) {
	    sb2.append(" with ").append(ncores).append(" cores");
	}
	if (ncores != nthreads) {
	    if (nchips == ncores) {
		sb2.append(" with ");
	    } else {
		sb2.append(" and ");
	    }
	    sb2.append(nthreads).append(" threads");
	}
	/*
	 * The following appears necessary to make the label fill the width
	 * of its container.
	 */
	JPanel jp = new JPanel();
	JLabel jl = new JLabel(sb2.toString());
	Dimension dlab = jl.getMaximumSize();
	if (dlab != null) {
	    dlab.setSize(Double.MAX_VALUE, dlab.getHeight());
	    jl.setMaximumSize(dlab);
	}
	jl.setAlignmentX(CENTER_ALIGNMENT);
	jp.add(jl);
	jp.setBorder(BorderFactory.createTitledBorder(sbrand));
	addComponent(jp);
    }

    /*
     * Add a cpu activity display
     */
    private void addCpuAccessory() {
	JPanel cpupanel = new CpuStatePanel(jkstat);
	cpupanel.setBorder(
		       BorderFactory.createTitledBorder("Processor Activity"));
        addComponent(cpupanel);
    }

    /*
     * Add a network accessory
     */
    private void addNetAccessory() {
	// filter the kstats we need
	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.setFilterClass("net");
	ksf.addFilter(":::rbytes64");
	ksf.addNegativeFilter("::mac");

	String iflabel = "Network "
	    + KstatResources.getString("NETLOAD.IF.TEXT") + " ";
	for (Kstat ks : ksf.getKstats(true)) {
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
	    npanel.setBorder(BorderFactory.createTitledBorder(
				  iflabel + ks.getName()));
	    addComponent(npanel);
	}
    }

    /*
     * Add a load average accessory
     */
    private void addLoadAccessory() {
	Kstat ks = jkstat.getKstat("unix", 0, "system_misc");

	// 1 and 15 minute load averages
	JPanel lpanel = new JPanel();
	lpanel.add(new JLabel("1min: "));
	SparkValueAccessory kap =
		new SparkValueAccessory(ks, -1, jkstat, "avenrun_1min");
	kap.enableTips("Current load:", 1.0 / 256.0);
	lpanel.add(kap);
	kaplist.add(kap);
	lpanel.add(new JLabel("  15min: "));
	kap = new SparkValueAccessory(ks, -1, jkstat, "avenrun_15min");
	kap.enableTips("Current load:", 1.0 / 256.0);
	lpanel.add(kap);
	kaplist.add(kap);
	lpanel.setBorder(BorderFactory.createTitledBorder("Load Average"));
	addComponent(lpanel);

	// process count
	JPanel ppanel = new JPanel();
	ppanel.add(new JLabel("Count: "));
	kap = new SparkValueAccessory(ks, -1, jkstat, "nproc");
	kap.enableTips("Current process count:");
	ppanel.add(kap);
	kaplist.add(kap);
	ppanel.setBorder(BorderFactory.createTitledBorder("Processes"));
	addComponent(ppanel);
    }
}
