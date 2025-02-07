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
public final class SmfPanel extends JPanel {

    private static final long serialVersionUID = 1L;

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
