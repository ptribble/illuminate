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
    private DefaultPieDataset<String> totalCacheHitsDataset;
    private DefaultPieDataset<String> currentCacheHitsDataset;
    private DefaultPieDataset<String> totalCacheHitsByTypeDataset;
    private DefaultPieDataset<String> currentCacheHitsByTypeDataset;
    private DefaultPieDataset<String> totalCacheMissesByTypeDataset;
    private DefaultPieDataset<String> currentCacheMissesByTypeDataset;

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
	totalCacheHitsDataset = new DefaultPieDataset<String>();
	currentCacheHitsDataset = new DefaultPieDataset<String>();
	totalCacheHitsByTypeDataset = new DefaultPieDataset<String>();
	currentCacheHitsByTypeDataset = new DefaultPieDataset<String>();
	totalCacheMissesByTypeDataset = new DefaultPieDataset<String>();
	currentCacheMissesByTypeDataset = new DefaultPieDataset<String>();

	// initialise the pie charts
	// args: title, dataset, legend?, tooltips?, urls?
	JFreeChart totalCacheChart = ChartFactory.createPieChart(
				"Total Cache Hits By List",
				totalCacheHitsDataset,
				false,
				true,
				false);
	JFreeChart currentCacheChart = ChartFactory.createPieChart(
				"Current Cache Hits By List",
				currentCacheHitsDataset,
				false,
				true,
				false);
	JFreeChart totalCacheHitsByTypeChart = ChartFactory.createPieChart(
				"Total Cache Hits By Type",
				totalCacheHitsByTypeDataset,
				false,
				true,
				false);
	JFreeChart currentCacheHitsByTypeChart = ChartFactory.createPieChart(
				"Current Cache Hits By Type",
				currentCacheHitsByTypeDataset,
				false,
				true,
				false);
	JFreeChart totalCacheMissesByTypeChart = ChartFactory.createPieChart(
				"Total Cache Misses By Type",
				totalCacheMissesByTypeDataset,
				false,
				true,
				false);
	JFreeChart currentCacheMissesByTypeChart = ChartFactory.createPieChart(
				"Current Cache Misses By Type",
				currentCacheMissesByTypeDataset,
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
	JPanel cacheHitByTypePanel = new JPanel(new GridLayout(1, 2));
	JPanel cacheMissByTypePanel = new JPanel(new GridLayout(1, 2));

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
	ChartPanel cp1a = new ChartPanel(totalCacheChart);
	cp1a.setPreferredSize(dchart);
	ChartPanel cp1b = new ChartPanel(currentCacheChart);
	cp1b.setPreferredSize(dchart);
	ChartPanel cp2a = new ChartPanel(totalCacheHitsByTypeChart);
	cp2a.setPreferredSize(dchart);
	ChartPanel cp2b = new ChartPanel(currentCacheHitsByTypeChart);
	cp2b.setPreferredSize(dchart);
	ChartPanel cp3a = new ChartPanel(totalCacheMissesByTypeChart);
	cp3a.setPreferredSize(dchart);
	ChartPanel cp3b = new ChartPanel(currentCacheMissesByTypeChart);
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
	cacheHitByTypePanel.add(cp2a);
	cacheHitByTypePanel.add(cp2b);
	cacheMissByTypePanel.add(cp3a);
	cacheMissByTypePanel.add(cp3b);

	add(infoPanel);
	add(hitPanel);
	add(cacheByListPanel);
	add(cacheHitByTypePanel);
	add(cacheMissByTypePanel);

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

	long n_arcHits = ks.longData("hits");
	long n_arcMisses = ks.longData("misses");
	long n_mfuHits = ks.longData("mfuHits");
	long n_mruHits = ks.longData("mruHits");
	long n_mfuGhostHits = ks.longData("mfuGhostHits");
	long n_mruGhostHits = ks.longData("mruGhostHits");
	long n_anonHits = n_arcHits - (n_mfuHits + n_mruHits
				    + n_mfuGhostHits + n_mruGhostHits);
	long n_demandDataHits = ks.longData("demandDataHits");
	long n_demandMdHits = ks.longData("demand_metadata_hits");
	long n_pfDataHits = ks.longData("prefetch_data_hits");
	long n_pfMdHits = ks.longData("prefetch_metadata_hits");
	long n_demandDataMisses = ks.longData("demandDataMisses");
	long n_demandMdMisses = ks.longData("demand_metadata_misses");
	long n_pfDataMisses = ks.longData("prefetch_data_misses");
	long n_pfMdMisses = ks.longData("prefetch_metadata_misses");
	// sizes
	long arc_size = ks.longData("size");
	long target_size = ks.longData("c");
	long arc_min_size = ks.longData("c_min");
	long arc_max_size = ks.longData("c_max");
	long mru_size = ks.longData("p");
	long mfu_size = target_size - mru_size;

	// summary labels
	arcSizeLabel.setText(mbstring(arc_size));
	targetSizeLabel.setText(mbstring(target_size));
	arcMinSizeLabel.setText(mbstring(arc_min_size));
	arcMaxSizeLabel.setText(mbstring(arc_max_size));
	mruSizeLabel.setText(mbstring(mru_size));
	mfuSizeLabel.setText(mbstring(mfu_size));

	// create deltas
	long d_arcHits = n_arcHits - arcHits;
	long d_arcMisses = n_arcMisses - arcMisses;
	long d_mfuHits = n_mfuHits - mfuHits;
	long d_mruHits = n_mruHits - mruHits;
	long d_mfuGhostHits = n_mfuGhostHits - mfuGhostHits;
	long d_mruGhostHits = n_mruGhostHits - mruGhostHits;
	long d_anonHits = n_anonHits - anonHits;
	long d_demandDataHits = n_demandDataHits - demandDataHits;
	long d_demandMdHits = n_demandMdHits - demandMdHits;
	long d_pfDataHits = n_pfDataHits - pfDataHits;
	long d_pfMdHits = n_pfMdHits - pfMdHits;
	long d_demandDataMisses = n_demandDataMisses - demandDataMisses;
	long d_demandMdMisses = n_demandMdMisses - demandMdMisses;
	long d_pfDataMisses = n_pfDataMisses - pfDataMisses;
	long d_pfMdMisses = n_pfMdMisses - pfMdMisses;

	// save values
	arcHits = n_arcHits;
	arcMisses = n_arcMisses;
	mfuHits = n_mfuHits;
	mruHits = n_mruHits;
	mfuGhostHits = n_mfuGhostHits;
	mruGhostHits = n_mruGhostHits;
	anonHits = n_anonHits;
	demandDataHits = n_demandDataHits;
	demandMdHits = n_demandMdHits;
	pfDataHits = n_pfDataHits;
	pfMdHits = n_pfMdHits;
	demandDataMisses = n_demandDataMisses;
	demandMdMisses = n_demandMdMisses;
	pfDataMisses = n_pfDataMisses;
	pfMdMisses = n_pfMdMisses;

	arcHitBar.setValue(hitrate(d_arcHits, d_arcMisses));
	demandHitBar.setValue(hitrate(d_demandDataHits,
					d_demandDataMisses));
	pfHitBar.setValue(hitrate(d_pfDataHits, d_pfDataMisses));
	mdemandHitBar.setValue(hitrate(d_demandMdHits, d_demandMdMisses));
	mpfHitBar.setValue(hitrate(d_pfMdHits, d_pfMdMisses));

	// hits by list
	totalCacheHitsDataset.setValue("Anon",
					anonHits);
	totalCacheHitsDataset.setValue("Recently Used",
					mruHits);
	totalCacheHitsDataset.setValue("Frequently Used",
					mfuHits);
	totalCacheHitsDataset.setValue("Recently Used Ghost",
					mruGhostHits);
	totalCacheHitsDataset.setValue("Frequently Used Ghost",
					mfuGhostHits);
	currentCacheHitsDataset.setValue("Anon",
					d_anonHits);
	currentCacheHitsDataset.setValue("Recently Used",
					d_mruHits);
	currentCacheHitsDataset.setValue("Frequently Used",
					d_mfuHits);
	currentCacheHitsDataset.setValue("Recently Used Ghost",
					d_mruGhostHits);
	currentCacheHitsDataset.setValue("Frequently Used Ghost",
					d_mfuGhostHits);

	// totals by type
	totalCacheHitsByTypeDataset.setValue("Demand Data Hits",
					demandDataHits);
	totalCacheHitsByTypeDataset.setValue("Demand Metadata Hits",
					demandMdHits);
	totalCacheHitsByTypeDataset.setValue("Prefetch Data Hits",
					pfDataHits);
	totalCacheHitsByTypeDataset.setValue("Prefetch Metadata Hits",
					pfMdHits);
	totalCacheMissesByTypeDataset.setValue("Demand Data Misses",
					demandDataMisses);
	totalCacheMissesByTypeDataset.setValue("Demand Metadata Misses",
					demandMdMisses);
	totalCacheMissesByTypeDataset.setValue("Prefetch Data Misses",
					pfDataMisses);
	totalCacheMissesByTypeDataset.setValue("Prefetch Metadata Misses",
					pfMdMisses);

	// current activity by type
	currentCacheHitsByTypeDataset.setValue("Demand Data Hits",
					d_demandDataHits);
	currentCacheHitsByTypeDataset.setValue("Demand Metadata Hits",
					d_demandMdHits);
	currentCacheHitsByTypeDataset.setValue("Prefetch Data Hits",
					d_pfDataHits);
	currentCacheHitsByTypeDataset.setValue("Prefetch Metadata Hits",
					d_pfMdHits);
	currentCacheMissesByTypeDataset.setValue("Demand Data Misses",
					d_demandDataMisses);
	currentCacheMissesByTypeDataset.setValue("Demand Metadata Misses",
					d_demandMdMisses);
	currentCacheMissesByTypeDataset.setValue("Prefetch Data Misses",
					d_pfDataMisses);
	currentCacheMissesByTypeDataset.setValue("Prefetch Metadata Misses",
					d_pfMdMisses);
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
