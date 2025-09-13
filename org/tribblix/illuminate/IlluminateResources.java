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

import java.util.ResourceBundle;

/**
 * A handler for resource bundles.
 */
public final class IlluminateResources {

    private static final ResourceBundle ILLUMINATERES =
			ResourceBundle.getBundle("properties/illuminate");

    private IlluminateResources() {
    }

    /**
     * Return the translated String associated with the given key.
     *
     * @param key the key to look up.
     *
     * @return the matching String.
     */
    public static String getString(final String key) {
	return ILLUMINATERES.getString(key);
    }
}
