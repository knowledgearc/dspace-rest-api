/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollectionEntityTrim extends CollectionEntityId {

    @EntityFieldRequired
    private String name;
    private String handle;
    private List<Object> communities = new ArrayList<Object>();

    public CollectionEntityTrim() {

    }

    public CollectionEntityTrim(String uid, Context context) throws SQLException {
        super(uid, context);

        this.name = res.getName();
        this.handle = res.getHandle();

        Community[] communities = res.getCommunities();
        for (Community community : communities) {
            this.communities.add(new CommunityEntityId(community));
        }
    }

    public CollectionEntityTrim(Collection collection) throws SQLException {

        super(collection);
        this.name = collection.getName();
        this.handle = collection.getHandle();

        Community[] communities = collection.getCommunities();
        for (Community community : communities) {
            this.communities.add(new CommunityEntityId(community));
        }
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }

    public List<Object> getCommunities() {
        return communities;
    }
}