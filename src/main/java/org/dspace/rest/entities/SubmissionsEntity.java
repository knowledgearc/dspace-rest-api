/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import java.util.List;

public class SubmissionsEntity {

    private int count;
    private List<Object> submissions;

    public SubmissionsEntity(int count, List<Object> submissions) {
        this.count = count;
        this.submissions = submissions;
    }

    public int getCount() {
        return count;
    }

    public List<Object> getSubmissions() {
        return submissions;
    }
}