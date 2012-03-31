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
import org.dspace.content.Item;
import org.dspace.content.Rating;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class RatingEntity extends RatingEntityId {

    private int itemId;
    private int submitterId;
    private int rating;

    public RatingEntity() {
    }

    public RatingEntity(String uid, Context context) throws SQLException {
        super(uid, context);
        this.itemId = res.getItem().getID();
        this.submitterId = res.getSubmitter().getID();
        this.rating = res.getRating();
    }

    public RatingEntity(Rating rating) throws SQLException {
        super(rating);
        this.itemId = rating.getItem().getID();
        this.submitterId = rating.getSubmitter().getID();
        this.rating = rating.getRating();
    }

    public String create(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            //Check if user null
            if (context.getCurrentUser() == null) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }
            //Check if user has read item auth
            int itemId = Integer.parseInt((String) inputVar.get("itemId"));
            int ratingNum = Integer.parseInt((String) inputVar.get("rating"));
            Item item = Item.find(context, itemId);
            AuthorizeManager.authorizeAction(context, item, Constants.READ);
            //Check if submitted alrady
            Rating rating = Rating.find(context, itemId, context.getCurrentUser().getID());
            if (rating == null) {
                rating = Rating.create(context);
                rating.setItem(item);
                rating.setSubmitter(context.getCurrentUser());
                rating.setRating(ratingNum);
                rating.setLastModified(new Date());
                rating.update();
            } else {
                throw new EntityException("Internal server error", "Already rated", 500);
            }

            return String.valueOf(rating.getID());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public int getItemId() {
        return itemId;
    }

    public int getSubmitterId() {
        return submitterId;
    }

    public int getRating() {
        return rating;
    }
}
