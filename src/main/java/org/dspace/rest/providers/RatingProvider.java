/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.content.Rating;
import org.dspace.core.Context;
import org.dspace.rest.entities.RatingEntity;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.Createable;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingProvider extends AbstractBaseProvider implements CoreEntityProvider, Createable {

    private static Logger log = Logger.getLogger(RatingProvider.class);

    public RatingProvider(EntityProviderManager entityProviderManager) throws NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = RatingEntity.class;
        func2actionMapPOST.put("create", "");
        inputParamsPOST.put("create", new String[]{"itemId", "rating"});
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "ratings";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "rating_exists:" + id);

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            Rating comm = Rating.find(context, Integer.parseInt(id));
            return comm != null ? true : false;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } finally {
            removeConn(context);
        }
    }

    public Object getEntity(EntityReference ref) {
        log.info(userInfo() + "get_rating:" + ref.getId());
        String segments[] = getSegments();

        if (segments.length > 3) {
            return super.getEntity(ref);
        }

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            if (entityExists(ref.getId())) {
                return new RatingEntity(ref.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + ref.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_ratings");

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            List<Object> entities = new ArrayList<Object>();
            Rating[] ratings = Rating.findAll(context);
            for (Rating rating : ratings) {
                entities.add(new RatingEntity(rating));
            }

            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new RatingEntity();
    }
}