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
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Bundle;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;

/**
 * Entity describing bundle, basic version
 * @see BundleEntityId
 * @see Bundle
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class BundleEntityId {

    @EntityId
    private int id;

    protected BundleEntityId() {
    }

    public BundleEntityId(String uid, Context context) throws SQLException {
        if (uid!=null&&!"".equals(uid)) {
            try {

                Bundle res = Bundle.find(context, Integer.parseInt(uid));
                // Check authorisation
                AuthorizeManager.authorizeAction(context, res, Constants.READ);

                this.id = res.getID();
                //context.complete();
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            } catch (AuthorizeException ex) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }
        } else {
            throw new EntityException("Bad request", "Value not included", 400);
        }

    }

    public BundleEntityId(Bundle bundle) throws SQLException {
        this.id = bundle.getID();
    }

    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof BundleEntityId)) {
            return false;
        } else {
            BundleEntityId castObj = (BundleEntityId) obj;
            return (this.id == castObj.id);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }
}
