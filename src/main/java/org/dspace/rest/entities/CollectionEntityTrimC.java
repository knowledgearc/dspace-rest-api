/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.content.Collection;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

public class CollectionEntityTrimC extends CollectionEntityId {

    @EntityFieldRequired
    private String name;
    private String handle;

    public CollectionEntityTrimC(Collection collection) {

        super(collection);
        this.name = collection.getName();
        this.handle = collection.getHandle();
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }
}