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

import java.io.File;
import org.tribblix.illuminate.helpers.RunCommand;

/**
 * InfoCommand - a command to show system information.
 * @author Peter Tribble
 * @version 1.0
 */
public class InfoCommand {

    private String text;
    private String cmd;
    private String fullcmd;
    private String manpage;

    /**
     * Create an informational command.
     *
     * @param text The name of the command
     * @param cmd The full path to the command
     */
    public InfoCommand(String text, String cmd) {
	this(text, cmd, (String) null);
    }

    /**
     * Create an informational command, with arguments.
     *
     * @param text The name of the command
     * @param cmd The full path to the command
     * @param args The arguments to the command
     */
    public InfoCommand(String text, String cmd, String args) {
	this.text = text;
	this.cmd = cmd;
	fullcmd = (args == null) ? cmd : cmd + " " + args;
    }

    /**
     * Set the manpage for this command.
     *
     * @param manpage the manpage, with section appended
     */
    public void setManpage(String manpage) {
	this.manpage = manpage;
    }

    /**
     * Get the manpage for this command, if any.
     *
     * @return the manpage, with section appended
     */
    public String getManpage() {
	return manpage;
    }

    /**
     * Override toString() to give the informational name.
     */
    @Override
    public String toString() {
	return text;
    }

    /**
     * Return the full command executed for this informational command,
     * including any arguments.
     *
     * @return The full command including any arguments
     */
    public String getFullCmd() {
	return fullcmd;
    }

    /**
     * Return whether the filename corresponding to the pathname exists.
     *
     * @return true if the command exists
     */
    public boolean exists() {
	return new File(cmd).exists();
    }

    /**
     * Return the textual output from executing this informational command.
     *
     * @return The output from running this command
     */
    public String getOutput() {
	return exists() ? new RunCommand(fullcmd.split("\\s+")).getOut()
	    : "Command not found";
    }

    /**
     * Return the textual output from executing this informational command,
     * broken up into lines as a String array.
     *
     * @return The output from running this command, as an array of lines
     */
    public String[] getOutputLines() {
	return getOutput().split("\n");
    }

    /**
     * Return a html String representing this command, suitable
     * for putting on a label or button.
     *
     * @return a html String representing the command
     */
    public String infoLabel() {
	StringBuilder sb = new StringBuilder(32);
	sb.append("<html>")
	    .append(IlluminateResources.getString("INFO.OUTPUT.TEXT"))
	    .append(": <b>")
	    .append(fullcmd)
	    .append("</b></html>");
	return sb.toString();
    }
}
