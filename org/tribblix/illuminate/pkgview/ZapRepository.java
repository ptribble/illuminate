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

import java.io.File;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * ZapRepository - describe and query a zap repository
 * @author Peter Tribble
 * @version 1.0
 */
public class ZapRepository {

    // cannot be final as they're assigned in a loop
    private String repoNAME;
    private String repoDESC;
    private String repoURL;
    private String repoSIGNED;

    /**
     * Create a zap repository configuration.
     *
     * @param altroot  An alternate root directory for this OS image
     * @param repo  The repository represented by this ZapRepository
     */
    public ZapRepository(String altroot, String repo) {
	File f = new File(altroot + ZapConfig.ZAP_ROOT + "/repositories",
			repo + ".repo");

	if (f.exists()) {
	    for (String line : JumbleFile.getLines(f)) {
		String[] ds = line.split("=", 2);
		if (ds[0].equals("NAME")) {
		    repoNAME = ds[1];
		} else if (ds[0].equals("DESC")) {
		    repoDESC = ds[1];
		} else if (ds[0].equals("URL")) {
		    repoURL = ds[1];
		} else if (ds[0].equals("SIGNED")) {
		    repoSIGNED = ds[1];
		}
	    }
	}
    }

    public String getName() {
	return repoNAME;
    }

    public String getDescription() {
	return repoDESC;
    }

    public String getURL() {
	return repoURL;
    }

    public String getSIGNED() {
	return repoSIGNED;
    }
}
