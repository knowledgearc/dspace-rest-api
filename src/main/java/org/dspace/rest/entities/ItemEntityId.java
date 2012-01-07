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
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;

/**
 * Entity describing item, basic version
 * @see ItemEntityId
 * @see Item
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class ItemEntityId {

    @EntityId
    private int id;
    protected Item res;

    protected ItemEntityId() {
    }

    public ItemEntityId(String uid, Context context) {
        if (uid!=null&&!"".equals(uid)) {
            try {
                Item res = Item.find(context, Integer.parseInt(uid));

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

    public ItemEntityId(Item item) throws SQLException {
        this.id = item.getID();
    }

    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ItemEntityId)) {
            return false;
        } else {
            ItemEntityId castObj = (ItemEntityId) obj;
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
