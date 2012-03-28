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
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;

public class UserEntityId {

    @EntityId
    protected int id;
    protected EPerson res;

    protected UserEntityId() {
    }

    public UserEntityId(String uid, Context context) {
        try {
            try {
                int id = Integer.parseInt(uid);
                res = EPerson.find(context, id);
            } catch (NumberFormatException ex) {
                res = EPerson.findByEmail(context, uid);
            }

            AuthorizeManager.authorizeAction(context, res, Constants.READ);

            this.id = res.getID();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }
    }

    public UserEntityId(EPerson eperson) {
        this.id = eperson.getID();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }
}
