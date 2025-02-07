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

package org.tribblix.illuminate.pkgview;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import uk.co.petertribble.jingle.TableSorter;

/**
 * Show a table inside a Scroll pane, displaying the packages installed.
 * The columns can be sorted by clicking the column headers. Selecting
 * a row causes information to appear in a tabbed area below the table.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public final class PackagePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private PackageInformationPanel pip;
    private transient SVR4Package currentPackage;
    final JTable ptable;

    /**
     * Create a new PackagePanel allowing the details of any of a list
     * of packages to be displayed.
     *
     * @param pkghdl a PackageHandler to query for information
     */
    public PackagePanel(PackageHandler pkghdl) {

	setLayout(new BorderLayout());

	PkgList plist = pkghdl.getPkgList();

	JPanel jpp = new JPanel(new BorderLayout());
	final PackageTableModel ptm = new PackageTableModel(plist);
	final TableSorter sortedModel = new TableSorter(ptm);
	ptable = new JTable(sortedModel);
	jpp.add(new JScrollPane(ptable));
	sortedModel.setTableHeader(ptable.getTableHeader());

	pip = new PackageInformationPanel(pkghdl);

	JSplitPane psplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
		jpp, pip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(180);
	add(psplit);

	ptable.getSelectionModel().addListSelectionListener(
		new ListSelectionListener() {
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() instanceof DefaultListSelectionModel
			&& !e.getValueIsAdjusting()) {
		    int irow = ptable.getSelectedRow();
		    if (irow >= 0) {
			// sorted table, need to convert the index
			int imod = sortedModel.modelIndex(irow);
			showPkg(ptm.getPackageAtRow(imod));
		    }
		}
	    }
	});
    }

    void showPkg(SVR4Package pkg) {
	pip.showPkg(pkg);
	if (pkg.isInstalled()) {
	    currentPackage = pkg;
	}
    }

    /**
     * Show the reverse dependency tab.
     */
    public void showRevDependencies() {
	pip.showRevDependencies(currentPackage);
    }

    /**
     * Cause the detailed view of package contents to be shown.
     */
    public void showDetailedView() {
	pip.showDetailedView();
    }
}
