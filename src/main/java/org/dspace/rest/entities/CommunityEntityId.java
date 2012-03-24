/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */


package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Community;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;

public class CommunityEntityId {

    @EntityId
    private int id;
    protected Community res;

    public CommunityEntityId() {

    }

    public CommunityEntityId(String uid, Context context) {
        try {

            res = Community.find(context, Integer.parseInt(uid));
            // Check authorisation
            AuthorizeManager.authorizeAction(context, res, Constants.READ);

            this.id = res.getID();
            //context.complete();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public CommunityEntityId(Community community) {
        this.id = community.getID();
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }
}
