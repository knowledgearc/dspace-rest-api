/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

public class GroupEntityTrim extends GroupEntityId {

    @EntityFieldRequired
    private String name;

    public GroupEntityTrim() {
    }

    public GroupEntityTrim(String uid, Context context) {
        super(uid, context);
        this.name = res.getName();
    }

    public GroupEntityTrim(Group egroup) {
        super(egroup);
        this.name = egroup.getName();
    }

    public String getName() {
        return this.name;
    }
}
