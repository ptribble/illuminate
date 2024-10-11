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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.BorderLayout;
import java.awt.Cursor;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.NativeJKstat;
import org.tribblix.illuminate.InfoCommand;

/**
 * SysPanel - shows hardware in the system.
 * @author Peter Tribble
 * @version 1.0
 */
public class SysPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private SysInfoPanel hip;

    /**
     * Display a Sys information panel.
     */
    public SysPanel() {
	this(new NativeJKstat());
    }

    /**
     * Display a Sys information panel.
     *
     * @param jkstat A JKstat object
     */
    public SysPanel(JKstat jkstat) {
	/*
	 * I'm not sure what this will eventually look like. I'm planning
	 * on something that just has big buttons "cpu" "memory" "storage"
	 * "network", but as the underlying data model is a hierarchical
	 * view the initial display is as a tree.
	 */
	setLayout(new BorderLayout());

	InfoCommand ic = new InfoCommand("hn", "/usr/bin/hostname");
	final SysTree ptree = new SysTree(ic.getOutput());
	// should this be set inside the SysTree itself?
	ptree.setCellRenderer(new SysTreeCellRenderer());
	ptree.addTreeSelectionListener(new TreeSelectionListener() {
	    @Override
	    public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		    ptree.getLastSelectedPathComponent();

		if (node != null) {
		    setInfo(node.getUserObject());
		}
	    }
	});
	// end of tree listener


	hip = new SysInfoPanel(jkstat);

	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		new JScrollPane(ptree), hip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(160);
	add(psplit);
    }

    void setInfo(Object o) {
	if (o instanceof SysItem) {
	    Cursor c = getCursor();
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    hip.display((SysItem) o);
	    setCursor(c);
	}
    }
}
