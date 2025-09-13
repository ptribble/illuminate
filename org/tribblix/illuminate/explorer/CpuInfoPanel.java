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
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatAggregate;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import uk.co.petertribble.jkstat.gui.AccessoryCpuPanel;
import uk.co.petertribble.jkstat.gui.KstatBaseChart;
import uk.co.petertribble.jkstat.gui.KstatAreaChart;
import uk.co.petertribble.jkstat.gui.KstatAggregateAreaChart;
import uk.co.petertribble.jkstat.gui.MPstatTable;
import uk.co.petertribble.jkstat.demo.ProcessorChip;
import uk.co.petertribble.jkstat.demo.ProcessorCore;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import org.jfree.chart.ChartPanel;

/**
 * CpuInfoPanel - shows Cpu status.
 * @author Peter Tribble
 * @version 1.0
 */
public final class CpuInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    private transient ProcessorTree proctree;
    /**
     * A table to show mpstat data.
     */
    private MPstatTable mptable;
    /**
     * An accessory for this cpu.
     */
    private KstatAccessoryPanel kap;
    private transient KstatBaseChart kbc;
    private transient JKstat jkstat;
    private static final List<String> MYSTATS =
		Arrays.asList("kernel", "user", "idle");
    private static final List<Color> MYCOLORS =
		Arrays.asList(Color.YELLOW, Color.GREEN, Color.BLUE);

    /**
     * Display a Cpu information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public CpuInfoPanel(final SysItem hi, final JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;

	proctree = (ProcessorTree) hi.getAttribute("ptree");

	switch (hi.getType()) {
	    case SysItem.CPU:
		displayChip();
		break;
	    case SysItem.CPU_CORE:
		displayCore();
		break;
	    case SysItem.CPU_THREAD:
		displayThread();
		break;
	    case SysItem.CPU_CONTAINER:
		displaySummary();
		break;
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (kap != null) {
	    kap.stopLoop();
	}
	if (mptable != null) {
	    mptable.stopLoop();
	}
	if (kbc != null) {
	    kbc.stopLoop();
	}
    }

    /*
     * Top level summary. This ought to be a variant on psrinfo.
     */
    private void displaySummary() {
        addLabel((proctree.numChips() == 1)
		? "System contains 1 chip"
		: "System contains " + proctree.numChips() + " chips");

	addText(chipDetails());
	mptable = new MPstatTable(jkstat, 5);
	addScrollPane(mptable);
    }

    /*
     * Note on types: See SysTree.java, where the "core" and "chip"
     * attributes are the respective classes, whereas the "thread" is an int.
     */

    /*
     * A processor chip.
     */
    private void displayChip() {
	ProcessorChip chip = (ProcessorChip) hi.getAttribute("chip");
	StringBuilder sb = new StringBuilder(40);
	sb.append("Processor ").append(chip.getChipid());
	if (proctree.isMulticore()) {
	    sb.append(" (").append(chip.numCores()).append(" cores");
	    if (proctree.isThreaded()) {
		sb.append(", ").append(chip.numThreads())
		    .append(" threads)");
	    } else {
		sb.append(')');
	    }
	} else {
	    if (proctree.isThreaded()) {
		sb.append(" (").append(chip.numThreads())
		    .append(" threads)");
	    }
	}
	addLabel(sb.toString());
	addLabel(chip.getBrand());
	if (proctree.isMulticore()) {
	    addChipAccessory();
	} else {
	    addAccessory();
	}
    }

    /*
     * A processor core.
     */
    private void displayCore() {
	ProcessorChip chip = (ProcessorChip) hi.getAttribute("chip");
	ProcessorCore core = (ProcessorCore) hi.getAttribute("core");
	Long lcore = core.getCoreid();
	Long lchip = chip.getChipid();
	if (proctree.isThreaded()) {
	    addLabel("Core " + lcore
		     + " (" + core.numThreads()
		     + " threads) on processor " + lchip);
	    addCoreAccessory();
	} else {
	    addLabel("Core " + lcore
		     + " on processor " + lchip);
	    addAccessory();
	}
    }

    /*
     * A processor thread.
     */
    private void displayThread() {
	ProcessorChip chip = (ProcessorChip) hi.getAttribute("chip");
	ProcessorCore core = (ProcessorCore) hi.getAttribute("core");
	Long lcore = core.getCoreid();
	Long lchip = chip.getChipid();
	if (proctree.isMulticore()) {
	    addLabel("Thread " + hi.getAttribute("thread")
		+ " of core " + lcore
		+ " on processor " + lchip);
	} else {
	    addLabel("Thread " + hi.getAttribute("thread")
		+ " on processor " + lchip);
	}
	addAccessory();
    }

    /*
     * Add an accessory if we can.
     */
    private void addAccessory() {
	Kstat ks = hi.getKstat();
	if (ks != null) {
	    Kstat ksc = ProcessorTree.makeCpuKstat(ks);
	    kap = new AccessoryCpuPanel(ksc, 1, jkstat);
	    JPanel jp = new JPanel(new BorderLayout());
	    jp.add(kap);
	    jp.setBorder(BorderFactory.createTitledBorder("CPU activity"));
	    addComponent(jp);
	    kbc = new KstatAreaChart(jkstat, ksc, MYSTATS, true);
	    kbc.setColors(MYCOLORS);
	    addComponent(new ChartPanel(kbc.getChart()));
	}
    }

    /*
     * Add an accessory aggregated over threads if we can.
     */
    private void addCoreAccessory() {
	ProcessorCore core = (ProcessorCore) hi.getAttribute("core");
	Set<Kstat> kss = ProcessorTree.makeCpuKstats(core.infoStats());
	if (!kss.isEmpty()) {
	    addAggregateAccessory(new KstatAggregate(jkstat, kss,
					    "core " + core.getCoreid()));
	}
    }

    /*
     * Add an accessory aggregated over cores if we can.
     */
    private void addChipAccessory() {
	ProcessorChip chip = (ProcessorChip) hi.getAttribute("chip");
	Set<Kstat> kss = ProcessorTree.makeCpuKstats(chip.infoStats());
	if (!kss.isEmpty()) {
	    addAggregateAccessory(new KstatAggregate(jkstat, kss,
					    "chip " + chip.getChipid()));
	}
    }

    /*
     * Common aggregate accessory.
     */
    private void addAggregateAccessory(final KstatAggregate ksa) {
	kap = new AggregateCpuPanel(ksa, 1, jkstat);
	JPanel jp = new JPanel(new BorderLayout());
	jp.add(kap);
	jp.setBorder(BorderFactory.createTitledBorder(
						"Aggregate CPU activity"));
	addComponent(jp);
	kbc = new KstatAggregateAreaChart(jkstat, ksa, MYSTATS, true);
	kbc.setColors(MYCOLORS);
	addComponent(new ChartPanel(kbc.getChart()));
    }

    /*
     * How many threads per core? If it's the same for all cores in a chip,
     * return that, otherwise -1. This for one chip.
     */
    private int threadsPerCore(final ProcessorChip chip) {
	int imin = 0;
	int imax = Integer.MAX_VALUE;
	for (ProcessorCore core : chip.getCores()) {
	    int i = core.numThreads();
	    if (i > imin) {
		imin = i;
	    }
	    if (i < imax) {
		imax = i;
	    }
	}
	return imin == imax ? imax : -1;
    }

    /*
     * A prettier version of chipDetails in ProcessorTree, handling the
     * case where all cores have the same thread count more elegantly.
     */
    private String chipDetails() {
	StringBuilder sb = new StringBuilder();
	String brand = "";
	for (ProcessorChip chip : proctree.getProcessorChips()) {
	    sb.append(chipDetails(chip));
	    brand = chip.getBrand();
	}
	sb.append("    ").append(brand);
	return sb.toString();
    }

    private String chipDetails(final ProcessorChip chip) {
	if (threadsPerCore(chip) > 1) {
	    StringBuilder sb = new StringBuilder(64);
	    sb.append("Physical processor ").append(chip.getChipid())
		.append(" has ");
	    if (chip.numCores() == 1) {
		sb.append("1 core with ");
	    } else {
		sb.append(chip.numCores()).append(" cores with ");
	    }
	    sb.append(threadsPerCore(chip)).append(" threads per core\n");
	    return sb.toString();
	} else {
	    return proctree.chipDetails(chip);
	}
    }
}
