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

import java.util.List;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * A TableModel describing SVR4 packages.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class PackageTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private List<SVR4Package> pkgs;

    /*
     * Columns to show
     */
    private String[] columnNames = { "Name", "Version", "Description" };

    /**
     * Create a new PackageTableModel.
     *
     * @param plist a PkgList
     */
    public PackageTableModel(PkgList plist) {
	pkgs = new ArrayList<>(plist);
    }

    @Override
    public int getColumnCount() {
	return columnNames.length;
    }

    @Override
    public int getRowCount() {
	return pkgs.size();
    }

    @Override
    public String getColumnName(int col) {
	return columnNames[col];
    }

    /**
     * Return the appropriate data.
     *
     * @see #setValueAt
     *
     * @param row the int row of the selected cell
     * @param col the int column of the selected cell
     *
     * @return the Object at the selected cell
     */
    @Override
    public Object getValueAt(int row, int col) {
	SVR4Package pkg = pkgs.get(row);
	if (col == 0) {
	    return pkg.getName();
	} else if (col == 1) {
	    return pkg.getVersion();
	} else {
	    return pkg.getDescription();
	}
    }

    @Override
    public Class<?> getColumnClass(int col) {
	return String.class;
    }

    /**
     * Return the package at the selected row.
     *
     * @param row the row to select
     *
     * @return the SVR4Package on the given row
     */
    public SVR4Package getPackageAtRow(int row) {
	return pkgs.get(row);
    }
}
