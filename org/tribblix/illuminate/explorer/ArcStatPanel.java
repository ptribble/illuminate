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

import org.jfree.chart.*;
import org.jfree.data.general.*;

/**
 * Display ZFS ARC statistics in a Panel.
 * @author Peter Tribble
 * @version 1.0
 */
public class ArcStatPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final long MB = 1024*1024;

    private JKstat jkstat;

    // labels for sizes
    private JLabel arc_size_label;
    private JLabel target_size_label;
    private JLabel arc_min_size_label;
    private JLabel arc_max_size_label;
    private JLabel mru_size_label;
    private JLabel mfu_size_label;

    // bars for current hit rates
    private JProgressBar arc_hit_bar;
    private JProgressBar demand_hit_bar;
    private JProgressBar pf_hit_bar;
    private JProgressBar mdemand_hit_bar;
    private JProgressBar mpf_hit_bar;

    // cache hit/miss data
    private DefaultPieDataset <String> totalCacheHitsDataset;
    private DefaultPieDataset <String> currentCacheHitsDataset;
    private DefaultPieDataset <String> totalCacheHitsByTypeDataset;
    private DefaultPieDataset <String> currentCacheHitsByTypeDataset;
    private DefaultPieDataset <String> totalCacheMissesByTypeDataset;
    private DefaultPieDataset <String> currentCacheMissesByTypeDataset;

    // saved statistics
    // naming: pf = prefetch; md = metadata
    private long arc_hits;
    private long arc_misses;
    private long mfu_hits;
    private long mru_hits;
    private long mfu_ghost_hits;
    private long mru_ghost_hits;
    private long anon_hits;
    private long demand_data_hits;
    private long demand_md_hits;
    private long pf_data_hits;
    private long pf_md_hits;
    private long demand_data_misses;
    private long demand_md_misses;
    private long pf_data_misses;
    private long pf_md_misses;

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
	totalCacheHitsDataset = new DefaultPieDataset <String> ();
	currentCacheHitsDataset = new DefaultPieDataset <String> ();
	totalCacheHitsByTypeDataset = new DefaultPieDataset <String> ();
	currentCacheHitsByTypeDataset = new DefaultPieDataset <String> ();
	totalCacheMissesByTypeDataset = new DefaultPieDataset <String> ();
	currentCacheMissesByTypeDataset = new DefaultPieDataset <String> ();

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

	arc_hit_bar = new JProgressBar(0, 100);
	arc_hit_bar.setStringPainted(true);
	demand_hit_bar = new JProgressBar(0, 100);
	demand_hit_bar.setStringPainted(true);
	pf_hit_bar = new JProgressBar(0, 100);
	pf_hit_bar.setStringPainted(true);
	mdemand_hit_bar = new JProgressBar(0, 100);
	mdemand_hit_bar.setStringPainted(true);
	mpf_hit_bar = new JProgressBar(0, 100);
	mpf_hit_bar.setStringPainted(true);
	hitPanel.add(new JLabel("ARC hit rate"));
	hitPanel.add(arc_hit_bar);
	hitPanel.add(new JLabel("Demand data hit rate"));
	hitPanel.add(demand_hit_bar);
	hitPanel.add(new JLabel("Prefetch data hit rate"));
	hitPanel.add(pf_hit_bar);
	hitPanel.add(new JLabel("Demand metadata hit rate"));
	hitPanel.add(mdemand_hit_bar);
	hitPanel.add(new JLabel("Prefetch metadata hit rate"));
	hitPanel.add(mpf_hit_bar);
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
	arc_size_label = new JLabel();
	infoPanel.add(arc_size_label);
	infoPanel.add(new JLabel("Target size"));
	target_size_label = new JLabel();
	infoPanel.add(target_size_label);
	infoPanel.add(new JLabel("Min size"));
	arc_min_size_label = new JLabel();
	infoPanel.add(arc_min_size_label);
	infoPanel.add(new JLabel("Max size"));
	arc_max_size_label = new JLabel();
	infoPanel.add(arc_max_size_label);
	infoPanel.add(new JLabel("MRU size"));
	mru_size_label = new JLabel();
	infoPanel.add(mru_size_label);
	infoPanel.add(new JLabel("MFU size"));
	mfu_size_label = new JLabel();
	infoPanel.add(mfu_size_label);
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

	long n_arc_hits = ks.longData("hits");
	long n_arc_misses = ks.longData("misses");
	long n_mfu_hits = ks.longData("mfu_hits");
	long n_mru_hits = ks.longData("mru_hits");
	long n_mfu_ghost_hits = ks.longData("mfu_ghost_hits");
	long n_mru_ghost_hits = ks.longData("mru_ghost_hits");
	long n_anon_hits = n_arc_hits - (n_mfu_hits + n_mru_hits
				    + n_mfu_ghost_hits + n_mru_ghost_hits);
	long n_demand_data_hits = ks.longData("demand_data_hits");
	long n_demand_md_hits = ks.longData("demand_metadata_hits");
	long n_pf_data_hits = ks.longData("prefetch_data_hits");
	long n_pf_md_hits = ks.longData("prefetch_metadata_hits");
	long n_demand_data_misses = ks.longData("demand_data_misses");
	long n_demand_md_misses = ks.longData("demand_metadata_misses");
	long n_pf_data_misses = ks.longData("prefetch_data_misses");
	long n_pf_md_misses = ks.longData("prefetch_metadata_misses");
	// sizes
	long arc_size = ks.longData("size");
	long target_size = ks.longData("c");
	long arc_min_size = ks.longData("c_min");
	long arc_max_size = ks.longData("c_max");
	long mru_size = ks.longData("p");
	long mfu_size = target_size - mru_size;

	// summary labels
	arc_size_label.setText(mbstring(arc_size));
	target_size_label.setText(mbstring(target_size));
	arc_min_size_label.setText(mbstring(arc_min_size));
	arc_max_size_label.setText(mbstring(arc_max_size));
	mru_size_label.setText(mbstring(mru_size));
	mfu_size_label.setText(mbstring(mfu_size));

	// create deltas
	long d_arc_hits = n_arc_hits - arc_hits;
	long d_arc_misses = n_arc_misses - arc_misses;
	long d_mfu_hits = n_mfu_hits - mfu_hits;
	long d_mru_hits = n_mru_hits - mru_hits;
	long d_mfu_ghost_hits = n_mfu_ghost_hits - mfu_ghost_hits;
	long d_mru_ghost_hits = n_mru_ghost_hits - mru_ghost_hits;
	long d_anon_hits = n_anon_hits - anon_hits;
	long d_demand_data_hits = n_demand_data_hits - demand_data_hits;
	long d_demand_md_hits = n_demand_md_hits - demand_md_hits;
	long d_pf_data_hits = n_pf_data_hits - pf_data_hits;
	long d_pf_md_hits = n_pf_md_hits - pf_md_hits;
	long d_demand_data_misses = n_demand_data_misses - demand_data_misses;
	long d_demand_md_misses = n_demand_md_misses - demand_md_misses;
	long d_pf_data_misses = n_pf_data_misses - pf_data_misses;
	long d_pf_md_misses = n_pf_md_misses - pf_md_misses;

	// save values
	arc_hits = n_arc_hits;
	arc_misses = n_arc_misses;
	mfu_hits = n_mfu_hits;
	mru_hits = n_mru_hits;
	mfu_ghost_hits = n_mfu_ghost_hits;
	mru_ghost_hits = n_mru_ghost_hits;
	anon_hits = n_anon_hits;
	demand_data_hits = n_demand_data_hits;
	demand_md_hits = n_demand_md_hits;
	pf_data_hits = n_pf_data_hits;
	pf_md_hits = n_pf_md_hits;
	demand_data_misses = n_demand_data_misses;
	demand_md_misses = n_demand_md_misses;
	pf_data_misses = n_pf_data_misses;
	pf_md_misses = n_pf_md_misses;

	arc_hit_bar.setValue(hitrate(d_arc_hits, d_arc_misses));
	demand_hit_bar.setValue(hitrate(d_demand_data_hits, d_demand_data_misses));
	pf_hit_bar.setValue(hitrate(d_pf_data_hits, d_pf_data_misses));
	mdemand_hit_bar.setValue(hitrate(d_demand_md_hits, d_demand_md_misses));
	mpf_hit_bar.setValue(hitrate(d_pf_md_hits, d_pf_md_misses));

	// hits by list
	totalCacheHitsDataset.setValue("Anon",
					anon_hits);
	totalCacheHitsDataset.setValue("Recently Used",
					mru_hits);
	totalCacheHitsDataset.setValue("Frequently Used",
					mfu_hits);
	totalCacheHitsDataset.setValue("Recently Used Ghost",
					mru_ghost_hits);
	totalCacheHitsDataset.setValue("Frequently Used Ghost",
					mfu_ghost_hits);
	currentCacheHitsDataset.setValue("Anon",
					d_anon_hits);
	currentCacheHitsDataset.setValue("Recently Used",
					d_mru_hits);
	currentCacheHitsDataset.setValue("Frequently Used",
					d_mfu_hits);
	currentCacheHitsDataset.setValue("Recently Used Ghost",
					d_mru_ghost_hits);
	currentCacheHitsDataset.setValue("Frequently Used Ghost",
					d_mfu_ghost_hits);

	// totals by type
	totalCacheHitsByTypeDataset.setValue("Demand Data Hits",
					demand_data_hits);
	totalCacheHitsByTypeDataset.setValue("Demand Metadata Hits",
					demand_md_hits);
	totalCacheHitsByTypeDataset.setValue("Prefetch Data Hits",
					pf_data_hits);
	totalCacheHitsByTypeDataset.setValue("Prefetch Metadata Hits",
					pf_md_hits);
	totalCacheMissesByTypeDataset.setValue("Demand Data Misses",
					demand_data_misses);
	totalCacheMissesByTypeDataset.setValue("Demand Metadata Misses",
					demand_md_misses);
	totalCacheMissesByTypeDataset.setValue("Prefetch Data Misses",
					pf_data_misses);
	totalCacheMissesByTypeDataset.setValue("Prefetch Metadata Misses",
					pf_md_misses);

	// current activity by type
	currentCacheHitsByTypeDataset.setValue("Demand Data Hits",
					d_demand_data_hits);
	currentCacheHitsByTypeDataset.setValue("Demand Metadata Hits",
					d_demand_md_hits);
	currentCacheHitsByTypeDataset.setValue("Prefetch Data Hits",
					d_pf_data_hits);
	currentCacheHitsByTypeDataset.setValue("Prefetch Metadata Hits",
					d_pf_md_hits);
	currentCacheMissesByTypeDataset.setValue("Demand Data Misses",
					d_demand_data_misses);
	currentCacheMissesByTypeDataset.setValue("Demand Metadata Misses",
					d_demand_md_misses);
	currentCacheMissesByTypeDataset.setValue("Prefetch Data Misses",
					d_pf_data_misses);
	currentCacheMissesByTypeDataset.setValue("Prefetch Metadata Misses",
					d_pf_md_misses);
    }

    private String mbstring(long lval) {
	return Long.toString(lval/MB) + " MB";
    }

    private int hitrate(long hits, long misses) {
	if ((hits+misses) > 0) {
	    return (int) (100L*hits / (hits+misses));
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
	delay = interval*1000;
	if (timer != null) {
	    timer.setDelay(delay);
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	update();
    }
}
