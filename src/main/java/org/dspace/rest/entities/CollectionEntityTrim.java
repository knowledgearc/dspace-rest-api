/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.content.Collection;
import org.dspace.core.Context;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

import java.sql.SQLException;

public class CollectionEntityTrim extends CollectionEntityId {

    @EntityFieldRequired
    private String name;
    private String handle;

    public CollectionEntityTrim(String uid, Context context, int level, UserRequestParams uparams) {
    }

    public CollectionEntityTrim(Collection collection, int level, UserRequestParams uparams) throws SQLException {
    }

    public CollectionEntityTrim(Collection collection, UserRequestParams uparams) throws SQLException {

        super(collection);
        this.name = collection.getName();
        this.handle = collection.getHandle();
    }

    public CollectionEntityTrim() {
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }
}