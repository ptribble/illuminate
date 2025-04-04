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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jingle.SpringUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Display ZFS ARC statistics in a Panel.
 * @author Peter Tribble
 * @version 1.0
 */
public final class ArcStatPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final long MB = 1024 * 1024;

    private JKstat jkstat;

    // labels for sizes
    private JLabel arcSizeLabel;
    private JLabel targetSizeLabel;
    private JLabel arcMinSizeLabel;
    private JLabel arcMaxSizeLabel;
    private JLabel mruSizeLabel;
    private JLabel mfuSizeLabel;

    // bars for current hit rates
    private JProgressBar arcHitBar;
    private JProgressBar demandHitBar;
    private JProgressBar pfHitBar;
    private JProgressBar mdemandHitBar;
    private JProgressBar mpfHitBar;

    // cache hit/miss data
    private DefaultPieDataset<String> totHitsDS;
    private DefaultPieDataset<String> curHitsDS;
    private DefaultPieDataset<String> totHitsByTypeDS;
    private DefaultPieDataset<String> curHitsByTypeDS;
    private DefaultPieDataset<String> totMissByTypeDS;
    private DefaultPieDataset<String> curMissByTypeDS;

    // saved statistics
    // naming: pf = prefetch; md = metadata
    private long arcHits;
    private long arcMisses;
    private long mfuHits;
    private long mruHits;
    private long mfuGhostHits;
    private long mruGhostHits;
    private long anonHits;
    private long demandDataHits;
    private long demandMdHits;
    private long pfDataHits;
    private long pfMdHits;
    private long demandDataMisses;
    private long demandMdMisses;
    private long pfDataMisses;
    private long pfMdMisses;

    private Timer timer;
    private int delay = 5000;

    /**
     * Create a new ArcStatPanel, to display ZFS ARC statistics graphically.
     *
     * @param jkstat a JKstat object
     * @param interval the desired display update interval in seconds
     */
    public ArcStatPanel(JKstat jkstat, int interval) {
	this.jkstat = jkstat;

	setDelay(interval);

	// FIXME all labels need to be localized

	// create a main panel
	setLayout(new SpringLayout());

	// initialise the datasets
	totHitsDS = new DefaultPieDataset<String>();
	curHitsDS = new DefaultPieDataset<String>();
	totHitsByTypeDS = new DefaultPieDataset<String>();
	curHitsByTypeDS = new DefaultPieDataset<String>();
	totMissByTypeDS = new DefaultPieDataset<String>();
	curMissByTypeDS = new DefaultPieDataset<String>();

	// initialise the pie charts
	// args: title, dataset, legend?, tooltips?, urls?
	JFreeChart totCacheChart = ChartFactory.createPieChart(
				"Total Cache Hits By List",
				totHitsDS,
				false,
				true,
				false);
	JFreeChart curCacheChart = ChartFactory.createPieChart(
				"Current Cache Hits By List",
				curHitsDS,
				false,
				true,
				false);
	JFreeChart totHitsTypeChart = ChartFactory.createPieChart(
				"Total Cache Hits By Type",
				totHitsByTypeDS,
				false,
				true,
				false);
	JFreeChart curHitsTypeChart = ChartFactory.createPieChart(
				"Current Cache Hits By Type",
				curHitsByTypeDS,
				false,
				true,
				false);
	JFreeChart totMissTypeChart = ChartFactory.createPieChart(
				"Total Cache Misses By Type",
				totMissByTypeDS,
				false,
				true,
				false);
	JFreeChart curMissTypeChart = ChartFactory.createPieChart(
				"Current Cache Misses By Type",
				curMissByTypeDS,
				false,
				true,
				false);

	/*
	 * The overall layout is simple: at the top are the overall size
	 * statistics. Then the percentages for hits/misses/types for the
	 * cache are shown below.
	 */
	JPanel infoPanel = new JPanel(new GridLayout(0, 2));
	JPanel hitPanel = new JPanel(new GridLayout(0, 2));
	JPanel cacheByListPanel = new JPanel(new GridLayout(1, 2));
	JPanel hitByTypePanel = new JPanel(new GridLayout(1, 2));
	JPanel missByTypePanel = new JPanel(new GridLayout(1, 2));

	// cache hit rates, in a separate panel with bars.

	arcHitBar = new JProgressBar(0, 100);
	arcHitBar.setStringPainted(true);
	demandHitBar = new JProgressBar(0, 100);
	demandHitBar.setStringPainted(true);
	pfHitBar = new JProgressBar(0, 100);
	pfHitBar.setStringPainted(true);
	mdemandHitBar = new JProgressBar(0, 100);
	mdemandHitBar.setStringPainted(true);
	mpfHitBar = new JProgressBar(0, 100);
	mpfHitBar.setStringPainted(true);
	hitPanel.add(new JLabel("ARC hit rate"));
	hitPanel.add(arcHitBar);
	hitPanel.add(new JLabel("Demand data hit rate"));
	hitPanel.add(demandHitBar);
	hitPanel.add(new JLabel("Prefetch data hit rate"));
	hitPanel.add(pfHitBar);
	hitPanel.add(new JLabel("Demand metadata hit rate"));
	hitPanel.add(mdemandHitBar);
	hitPanel.add(new JLabel("Prefetch metadata hit rate"));
	hitPanel.add(mpfHitBar);
	hitPanel.setBorder(BorderFactory.createTitledBorder("Cache Hit Rates"));

	Dimension dchart = new Dimension(320, 240);
	ChartPanel cp1a = new ChartPanel(totCacheChart);
	cp1a.setPreferredSize(dchart);
	ChartPanel cp1b = new ChartPanel(curCacheChart);
	cp1b.setPreferredSize(dchart);
	ChartPanel cp2a = new ChartPanel(totHitsTypeChart);
	cp2a.setPreferredSize(dchart);
	ChartPanel cp2b = new ChartPanel(curHitsTypeChart);
	cp2b.setPreferredSize(dchart);
	ChartPanel cp3a = new ChartPanel(totMissTypeChart);
	cp3a.setPreferredSize(dchart);
	ChartPanel cp3b = new ChartPanel(curMissTypeChart);
	cp3b.setPreferredSize(dchart);

	infoPanel.add(new JLabel("Current size"));
	arcSizeLabel = new JLabel();
	infoPanel.add(arcSizeLabel);
	infoPanel.add(new JLabel("Target size"));
	targetSizeLabel = new JLabel();
	infoPanel.add(targetSizeLabel);
	infoPanel.add(new JLabel("Min size"));
	arcMinSizeLabel = new JLabel();
	infoPanel.add(arcMinSizeLabel);
	infoPanel.add(new JLabel("Max size"));
	arcMaxSizeLabel = new JLabel();
	infoPanel.add(arcMaxSizeLabel);
	infoPanel.add(new JLabel("MRU size"));
	mruSizeLabel = new JLabel();
	infoPanel.add(mruSizeLabel);
	infoPanel.add(new JLabel("MFU size"));
	mfuSizeLabel = new JLabel();
	infoPanel.add(mfuSizeLabel);
	infoPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

	cacheByListPanel.add(cp1a);
	cacheByListPanel.add(cp1b);
	hitByTypePanel.add(cp2a);
	hitByTypePanel.add(cp2b);
	missByTypePanel.add(cp3a);
	missByTypePanel.add(cp3b);

	add(infoPanel);
	add(hitPanel);
	add(cacheByListPanel);
	add(hitByTypePanel);
	add(missByTypePanel);

	SpringUtilities.makeCompactGrid(this, 5, 1, 3, 3, 3, 3);

	update();
	startLoop();
    }

    /**
     * Update the statistics and the display.
     */
    private void update() {
	Kstat ks = jkstat.getKstat("zfs", 0, "arcstats");
	if (ks == null) {
	    return;
	}

	long nArcHits = ks.longData("hits");
	long nArcMisses = ks.longData("misses");
	long nMfuHits = ks.longData("mfu_hits");
	long nMruHits = ks.longData("mru_hits");
	long nMfuGhostHits = ks.longData("mfu_ghost_hits");
	long nMruGhostHits = ks.longData("mru_ghost_hits");
	long nAnonHits = nArcHits - (nMfuHits + nMruHits
				    + nMfuGhostHits + nMruGhostHits);
	long nDemandDataHits = ks.longData("demand_data_hits");
	long nDemandMdHits = ks.longData("demand_metadata_hits");
	long nPfDataHits = ks.longData("prefetch_data_hits");
	long nPfMdHits = ks.longData("prefetch_metadata_hits");
	long nDemandDataMisses = ks.longData("demand_data_misses");
	long nDemandMdMisses = ks.longData("demand_metadata_misses");
	long nPfDataMisses = ks.longData("prefetch_data_misses");
	long nPfMdMisses = ks.longData("prefetch_metadata_misses");
	// sizes
	long arcSize = ks.longData("size");
	long targetSize = ks.longData("c");
	long arcMinSize = ks.longData("c_min");
	long arcMaxSize = ks.longData("c_max");
	long mruSize = ks.longData("p");
	long mfuSize = targetSize - mruSize;

	// summary labels
	arcSizeLabel.setText(mbstring(arcSize));
	targetSizeLabel.setText(mbstring(targetSize));
	arcMinSizeLabel.setText(mbstring(arcMinSize));
	arcMaxSizeLabel.setText(mbstring(arcMaxSize));
	mruSizeLabel.setText(mbstring(mruSize));
	mfuSizeLabel.setText(mbstring(mfuSize));

	// create deltas
	long dArcHits = nArcHits - arcHits;
	long dArcMisses = nArcMisses - arcMisses;
	long dMfuHits = nMfuHits - mfuHits;
	long dMruHits = nMruHits - mruHits;
	long dMfuGhostHits = nMfuGhostHits - mfuGhostHits;
	long dMruGhostHits = nMruGhostHits - mruGhostHits;
	long dAnonHits = nAnonHits - anonHits;
	long dDemandDataHits = nDemandDataHits - demandDataHits;
	long dDemandMdHits = nDemandMdHits - demandMdHits;
	long dPfDataHits = nPfDataHits - pfDataHits;
	long dPfMdHits = nPfMdHits - pfMdHits;
	long dDemandDataMisses = nDemandDataMisses - demandDataMisses;
	long dDemandMdMisses = nDemandMdMisses - demandMdMisses;
	long dPfDataMisses = nPfDataMisses - pfDataMisses;
	long dPfMdMisses = nPfMdMisses - pfMdMisses;

	// save values
	arcHits = nArcHits;
	arcMisses = nArcMisses;
	mfuHits = nMfuHits;
	mruHits = nMruHits;
	mfuGhostHits = nMfuGhostHits;
	mruGhostHits = nMruGhostHits;
	anonHits = nAnonHits;
	demandDataHits = nDemandDataHits;
	demandMdHits = nDemandMdHits;
	pfDataHits = nPfDataHits;
	pfMdHits = nPfMdHits;
	demandDataMisses = nDemandDataMisses;
	demandMdMisses = nDemandMdMisses;
	pfDataMisses = nPfDataMisses;
	pfMdMisses = nPfMdMisses;

	arcHitBar.setValue(hitrate(dArcHits, dArcMisses));
	demandHitBar.setValue(hitrate(dDemandDataHits,
					dDemandDataMisses));
	pfHitBar.setValue(hitrate(dPfDataHits, dPfDataMisses));
	mdemandHitBar.setValue(hitrate(dDemandMdHits, dDemandMdMisses));
	mpfHitBar.setValue(hitrate(dPfMdHits, dPfMdMisses));

	// hits by list
	totHitsDS.setValue("Anon",
					anonHits);
	totHitsDS.setValue("Recently Used",
					mruHits);
	totHitsDS.setValue("Frequently Used",
					mfuHits);
	totHitsDS.setValue("Recently Used Ghost",
					mruGhostHits);
	totHitsDS.setValue("Frequently Used Ghost",
					mfuGhostHits);
	curHitsDS.setValue("Anon",
					dAnonHits);
	curHitsDS.setValue("Recently Used",
					dMruHits);
	curHitsDS.setValue("Frequently Used",
					dMfuHits);
	curHitsDS.setValue("Recently Used Ghost",
					dMruGhostHits);
	curHitsDS.setValue("Frequently Used Ghost",
					dMfuGhostHits);

	// totals by type
	totHitsByTypeDS.setValue("Demand Data Hits",
					demandDataHits);
	totHitsByTypeDS.setValue("Demand Metadata Hits",
					demandMdHits);
	totHitsByTypeDS.setValue("Prefetch Data Hits",
					pfDataHits);
	totHitsByTypeDS.setValue("Prefetch Metadata Hits",
					pfMdHits);
	totMissByTypeDS.setValue("Demand Data Misses",
					demandDataMisses);
	totMissByTypeDS.setValue("Demand Metadata Misses",
					demandMdMisses);
	totMissByTypeDS.setValue("Prefetch Data Misses",
					pfDataMisses);
	totMissByTypeDS.setValue("Prefetch Metadata Misses",
					pfMdMisses);

	// current activity by type
	curHitsByTypeDS.setValue("Demand Data Hits",
					dDemandDataHits);
	curHitsByTypeDS.setValue("Demand Metadata Hits",
					dDemandMdHits);
	curHitsByTypeDS.setValue("Prefetch Data Hits",
					dPfDataHits);
	curHitsByTypeDS.setValue("Prefetch Metadata Hits",
					dPfMdHits);
	curMissByTypeDS.setValue("Demand Data Misses",
					dDemandDataMisses);
	curMissByTypeDS.setValue("Demand Metadata Misses",
					dDemandMdMisses);
	curMissByTypeDS.setValue("Prefetch Data Misses",
					dPfDataMisses);
	curMissByTypeDS.setValue("Prefetch Metadata Misses",
					dPfMdMisses);
    }

    private String mbstring(long lval) {
	return Long.toString(lval / MB) + " MB";
    }

    private int hitrate(long hits, long misses) {
	if ((hits + misses) > 0) {
	    return (int) (100L * hits / (hits + misses));
	} else {
	    return 0;
	}
    }

    /**
     * Start the timer loop, so that the accessory updates itself.
     */
    public void startLoop() {
	if (timer == null) {
	    timer = new Timer(delay, this);
	}
	timer.start();
    }

    /**
     * Stop the timer loop, so that the display no longer updates.
     */
    public void stopLoop() {
	if (timer != null) {
	    timer.stop();
	}
    }

    /**
     * Set the update delay.
     *
     * @param interval the desired update interval, in seconds
     */
    public void setDelay(int interval) {
	delay = interval * 1000;
	if (timer != null) {
	    timer.setDelay(delay);
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	update();
    }
}
