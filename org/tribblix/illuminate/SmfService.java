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
import java.util.Map;
import java.util.HashMap;
import org.tribblix.illuminate.helpers.RunCommand;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * SmfService - describes an SMF service.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfService implements Comparable<SmfService> {

    /*
     * FIXME refresh service details. Update status. We can't return the status
     * dynamically because the UI calls getStatus() incessantly for cell
     * rendering.
     */

    private final String fmri;
    private final String status;
    private Map<String, String> props;
    private Map<String, String> manpages;

    /**
     * Create a new SmfService object.
     *
     * @param fmri the FMRI of this SMF service
     * @param status the current status of this service
     */
    public SmfService(String fmri, String status) {
	this.fmri = fmri;
	this.status = status;
    }

    /**
     * Return the FMRI. (Fault management resource identifier.)
     *
     * @return the fmri of this service.
     */
    public String getFMRI() {
	return fmri;
    }

    /**
     * Return the service's status.
     *
     * @return the status of this service.
     */
    public String getStatus() {
	return status;
    }

    /**
     * Return the service's start time.
     *
     * @return the start time of this service.
     */
    public String getStartTime() {
	return getDetails(2);
    }

    /**
     * Return the numeric ID of the process contract associated with this
     * service. If there is no process contract, return 0.
     *
     * @return the contract id of this service
     */
    public int getContract() {
	try {
	    return Integer.parseInt(getDetails(3));
	} catch (NumberFormatException nfe) {
	    return 0;
	}
    }

    /**
     * Get the explanation of why this service isn't running. This is exactly
     * the output from svcs -xv.
     *
     * @return an explanation of why this service isn't running
     */
    public String getExplanation() {
	String explanation = getSVCS("-xv");
	if (manpages == null) {
	    parseManpages(explanation);
	}
	return explanation;
    }

    /**
     * Get the list of processes associated with this service. This is exactly
     * the output from svcs -p.
     *
     * @return the list of processes associated with this service
     */
    public String getProcesses() {
	return getSVCS("-p");
    }

    /**
     * Get those services upon which this service depends. This is exactly
     * the output from svcs -dH.
     *
     * @return the services upon which this service depends
     */
    public String getDependencies() {
	return getSVCS("-dH");
    }

    /**
     * Get those services which depend on this service. This is exactly
     * the output from svcs -DH.
     *
     * @return the services which depend on this service
     */
    public String getDependents() {
	return getSVCS("-DH");
    }

    /*
     * Get the i'th field of svcs -vH output. 0=status 1=nstate
     * 2=start time 3=ctid 4=fmri
     */
    private String getDetails(int i) {
	String[] ds = getSVCS("-vH").split("\\s+", 5);
	return ds[i];
    }

    private String getSVCS(String s) {
	RunCommand svcs = new RunCommand("/usr/bin/svcs " + s + " " + fmri);
	return svcs.getOut();
    }

    /**
     * Get the service properties of this SmfService.
     *
     * @return null for a legacy service, the output of svcprop otherwise
     */
    public String getSvcProperties() {
	if ("legacy_run".equals(status)) {
	    return null;
	}
	RunCommand svcs = new RunCommand("/usr/bin/svcprop " + fmri);
	return svcs.getOut();
    }

    /**
     * Get the name of this service, for display purposes.
     *
     * @return the human readable name of this service
     */
    public String getName() {
	return getProperty("name");
    }

    /**
     * Get the set of man pages for this service. This is a map with the man
     * page name as the key and the filename (qualified with the section) as
     * the value
     *
     * @return a Map listing the manual pages for this service
     */
    public Map<String, String> getManPages() {
	if (manpages == null) {
	    parseManpages(getExplanation());
	}
	return manpages;
    }

    /*
     * Strip the manpages out of the output, which looks like:
     *
     * See: man -M /usr/share/man -s 5 smf
     *
     * This is done automatically and saved if the explanation is
     * requested, to avoid having to call getExplanation() repeatedly
     */
    private void parseManpages(String explanation) {
	manpages = new HashMap<>();
	for (String line : explanation.split("\n")) {
	    String[] ds = line.trim().split("\\s+", 7);
	    if (ds.length == 7 && "man".equals(ds[1])) {
		manpages.put(ds[6], ds[6] + "." + ds[5]);
	    }
	}
    }

    /**
     * Return the log file for this service, if it exists. If the log
     * file doesn't exist, return null.
     *
     * @return a File referring to the log file for this service, if it
     * exists, else null.
     */
    public File getLog() {
	String logfile = getProperty("logfile");
	if (logfile != null) {
	    File fl = new File(logfile);
	    if (fl.exists()) {
		return fl;
	    }
	}
	return null;
    }

    /*
     * Get the value of the given property. This is based on the output of
     * svcs -l broken into keys and values. This is only valid for unique
     * keys, so you can't get dependency information this way.
     */
    private String getProperty(String s) {
	if (props == null) {
	    props = new HashMap<>();
	    /*
	     * Properties aren't supported for legacy services.
	     */
	    if (!fmri.startsWith("lrc:")) {
		for (String line : getSVCS("-l").split("\n")) {
		    String[] ds = line.split("\\s+", 2);
		    props.put(ds[0], ds[1]);
		}
	    }
	}
	return props.get(s);
    }

    /**
     * Produce an html formatted table containing dependency information for
     * this service.
     *
     * @return  an html formatted table containing dependency information for
     * the given service
     */
    public String getDepInfo() {
	if ("legacy_run".equals(status)) {
	    return null;
	}
	StringBuilder sb = new StringBuilder(200);
	sb.append("<h3>")
	    .append(getName())
	    .append("</h3><p bgcolor=\"#cccccc\">"
		    +"<b>Services this service depends on</b></p><pre>")
	    .append(getDependencies())
	    .append("</pre><p bgcolor=\"#cccccc\">"
		    +"<b>Services that depend on this service</b></p><pre>")
	    .append(getDependents())
	    .append("</pre>");
	return sb.toString();
    }

    /**
     * Produce an html formatted table describing this service.
     *
     * @return an html formatted table describing this service
     */
    public String getHtmlInfo() {
	if ("legacy_run".equals(status)) {
	    StringBuilder sb = new StringBuilder(96);
	    File fl = getScriptFile();
	    sb.append("<p bgcolor=\"#cccccc\"><b>");
	    if (fl == null) {
		sb.append("This is an unknown legacy script.</b></p>");
	    } else {
		sb.append("This is a legacy script, file name ")
		    .append(fl.getPath())
		    .append("</b></p><pre>\n\n\nScript Contents:\n\n")
		    .append(JumbleFile.getStringContents(fl))
		    .append("</pre>\n");
	    }
	    return sb.toString();
	} else {
	    return "<pre>" + getExplanation() + "</pre>";
	}
    }

    /*
     * The legacy FMRIs are constructed from the actual filenames, but
     * modified. In particular, any "." is replaced by "_". So we look for the
     * original by trying to substitute back the other way. If it works we
     * return the File, and if we run out of substitutions and still can't
     * find a file, return null.
     */
    private File getScriptFile() {
	// start with the FMRI, strip the leading svc: off
	String s = fmri.substring(4);
	String script = s.replaceFirst("_", ".");
	File f = new File(script);
	if (f.exists()) {
	    return f;
	}
	while (!script.equals(s)) {
	    s = script;
	    script = s.replaceFirst("_", ".");
	    f = new File(script);
	    if (f.exists()) {
		return f;
	    }
	}
	return null;
    }

    /**
     * For Comparable.
     *
     * @param ss the SmfService to compare with this SmfService
     *
     * @return whether the given SmfService is greater than or less than this
     * SmfService
     */
    @Override
    public int compareTo(SmfService ss) {
	return fmri.compareTo(ss.getFMRI());
    }
}
