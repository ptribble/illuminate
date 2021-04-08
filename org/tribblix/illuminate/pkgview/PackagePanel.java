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

package org.tribblix.illuminate.pkgview;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.BorderLayout;

/**
 * Show a table inside a Scroll pane, displaying the packages installed.
 * The columns can be sorted by clicking the column headers. Selecting
 * a row causes information to appear in a tabbed area below the table.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class PackagePanel extends JPanel {

    private PackageInformationPanel pip;
    private SVR4Package currentPackage;
    final JTable ptable;

    public PackagePanel(String altroot, PkgList plist, OverlayList ovlist,
			ZapConfig zc) {

	setLayout(new BorderLayout());

	JPanel jpp = new JPanel(new BorderLayout());
	final PackageTableModel ptm = new PackageTableModel(plist);
	final TableSorter sortedModel = new TableSorter(ptm);
	ptable = new JTable(sortedModel);
	jpp.add(new JScrollPane(ptable));
	sortedModel.setTableHeader(ptable.getTableHeader());

	pip = new PackageInformationPanel(altroot, ovlist, zc);

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

    public void showRevDependencies() {
	pip.showRevDependencies(currentPackage);
    }

    public void showDetailedView() {
	pip.showDetailedView();
    }
}
