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

package org.tribblix.illuminate;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * An smf informational panel, containing a list of smf services for which
 * information can be shown on the left and a panel showing the information
 * about those services on the right.
 */
public class SmfPanel extends JPanel {

    /**
     * The panel showing the information for services.
     */
    private final SmfInfoPanel sip;

    /**
     * Display an smf information panel.
     */
    public SmfPanel() {
	setLayout(new BorderLayout());

	SmfServiceList sslist = new SmfServiceList();
	SmfList plist = new SmfList(sslist);
	plist.addMouseListener(mouseListener);
	plist.addKeyListener(keyListener);

	final SmfTree ptree = new SmfTree(sslist);
	ToolTipManager.sharedInstance().registerComponent(ptree);
	ptree.addTreeSelectionListener(new TreeSelectionListener() {
	    @Override
	    public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		    ptree.getLastSelectedPathComponent();

		if (node != null && node.isLeaf()) {
		    setInfo((SmfService) node.getUserObject());
		}
	    }
	});

	JTabbedPane jtp = new JTabbedPane();

	jtp.add(IlluminateResources.getString("SMF.LIST"),
		new JScrollPane(plist));
	jtp.add(IlluminateResources.getString("SMF.TREE"),
		new JScrollPane(ptree));

	sip = new SmfInfoPanel();

	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		jtp, sip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(180);
	add(psplit);
    }

    void setInfo(SmfService svc) {
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	sip.setInfo(svc);
	setCursor(c);
    }

    /**
     * A MouseListener so that clicking on a service in the menu will show its
     * information.
     */
    MouseListener mouseListener = new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	    SmfList source = (SmfList) e.getSource();
	    setInfo(source.getSelectedValue());
	}
    };

    /**
     * A KeyListener so that selecting a service in the menu will show its
     * information.
     */
    KeyListener keyListener = new KeyAdapter() {
	@Override
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		SmfList source = (SmfList) e.getSource();
		setInfo(source.getSelectedValue());
	    }
	}
    };
}
