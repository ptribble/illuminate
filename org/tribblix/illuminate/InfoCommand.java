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
     * Set the manpage for this command
     *
     * @param manpage the manpage, with section appended
     */
    public void setManpage(String manpage) {
	this.manpage = manpage;
    }

    /**
     * Get the manpage for this command, if any
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
	return exists() ? new RunCommand(fullcmd).getOut() :
	    "Command not found";
    }

    /**
     * Return the textual output from executing this informational command,
     * broken up  into lines as a String array.
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
	StringBuilder sb = new StringBuilder();
	sb.append("<html>");
	sb.append(IlluminateResources.getString("INFO.OUTPUT.TEXT"));
	sb.append(": <b>").append(fullcmd);
	sb.append("</b></html>");
	return sb.toString();
    }
}
