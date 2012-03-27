/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import java.util.List;

public class PoolEntity {

    private int count;
    private List<Object> workflows;

    public PoolEntity(int count, List<Object> workflows) {
        this.count = count;
        this.workflows = workflows;
    }

    public int getCount() {
        return count;
    }

    public List<Object> getWorkflows() {
        return workflows;
    }
}