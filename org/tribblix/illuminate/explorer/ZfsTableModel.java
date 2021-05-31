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

import java.util.Set;
import javax.swing.table.AbstractTableModel;

/**
 * A Table Model representing zfs properties.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZfsTableModel extends AbstractTableModel {

    private final String[] columnNames = { "PROPERTY", "VALUE", "SOURCE"};
    private int nrows;
    private Zproperty[] zprops;

    /**
     * Create a ZfsTableModel from the given input.
     *
     * @param props a Set of zfs properties whose output will be converted to
     * tabular form
     */
    public ZfsTableModel(Set <Zproperty> props) {
	nrows = props.size();
	zprops = props.toArray(new Zproperty[0]);
    }

    @Override
    public int getColumnCount() {
	return columnNames.length;
    }

    @Override
    public int getRowCount() {
	return nrows;
    }

    @Override
    public Object getValueAt(int row, int col) {
	Zproperty zp = zprops[row];
	if (col == 0) {
	    return zp.getProperty();
	} else if (col == 1) {
	    return zp.getValue();
	} else if (col == 2) {
	    return zp.getSource();
	} else {
	    return "Oops!";
	}
    }

    @Override
    public String getColumnName(int col) {
	return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int c) {
	return String.class;
    }
}
