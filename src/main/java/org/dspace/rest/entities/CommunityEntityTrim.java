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
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommunityEntityTrim extends CommunityEntityId {

    @EntityFieldRequired
    private String name;
    private String handle;
    List<Object> collections = new ArrayList<Object>();
    List<Object> subCommunities = new ArrayList<Object>();
    private Object parentCommunity;

    public CommunityEntityTrim() {

    }

    public CommunityEntityTrim(String uid, Context context, UserRequestParams uparams) throws SQLException {
        super(uid, context);
        this.handle = res.getHandle();
        this.name = res.getName();

        boolean trim = uparams.getTrim();
        boolean collections = uparams.getCollections();
        boolean parents = uparams.getParents();
        boolean children = uparams.getChildren();

        Collection[] cols = res.getCollections();
        for (Collection c : cols) {
            this.collections.add(collections ? trim ? new CollectionEntityTrimC(c) : new CollectionEntityC(c) : new CollectionEntityId(c));
        }

        if (parents) {
            CommunityEntityTrim cet = this;
            Community[] parentCommunities = res.getAllParents();
            for (Community c : parentCommunities) {
                cet.parentCommunity = trim ? new CommunityEntityTrim(c, uparams, false, false) : new CommunityEntity(c, uparams, false, false);
                cet = (CommunityEntityTrim) cet.parentCommunity;
            }
        }
        if (children) {
            Community[] coms = res.getSubcommunities();
            for (Community c : coms) {
                this.subCommunities.add(trim ? new CommunityEntityTrim(c, uparams) : new CommunityEntity(c, uparams));
            }
        }
    }

    public CommunityEntityTrim(Community community, UserRequestParams uparams) throws SQLException {
        this(community, uparams, true, true);
    }

    public CommunityEntityTrim(Community community, UserRequestParams uparams, boolean hasCollections, boolean hasSubCommunities) throws SQLException {

        super(community);
        this.handle = community.getHandle();
        this.name = community.getName();

        boolean trim = uparams.getTrim();
        boolean collections = uparams.getCollections();

        if (hasCollections) {
            Collection[] cols = community.getCollections();
            for (Collection c : cols) {
                this.collections.add(collections ? trim ? new CollectionEntityTrimC(c) : new CollectionEntityC(c) : new CollectionEntityId(c));
            }
        }
        if (hasSubCommunities) {
            Community[] coms = community.getSubcommunities();
            for (Community c : coms) {
                this.subCommunities.add(trim ? new CommunityEntityTrim(c, uparams, hasCollections, hasSubCommunities) : new CommunityEntity(c, uparams, hasCollections, hasSubCommunities));
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }

    public List<Object> getCollections() {
        return collections;
    }

    public List<Object> getSubCommunities() {
        return subCommunities;
    }

    public Object getParentCommunity() {
        return parentCommunity;
    }
}