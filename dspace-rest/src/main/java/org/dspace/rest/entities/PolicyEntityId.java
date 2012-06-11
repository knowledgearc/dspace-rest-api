/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authorize.ResourcePolicy;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;

import java.sql.SQLException;

public class PolicyEntityId {

    @EntityId
    private int id;

    public PolicyEntityId() {
    }

    public PolicyEntityId(ResourcePolicy c) throws SQLException {

        this.id = c.getID();
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }
}