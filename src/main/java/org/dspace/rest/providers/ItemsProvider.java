/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.Context;
import org.dspace.rest.entities.ItemEntity;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.Createable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Deleteable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Updateable;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemsProvider extends AbstractBaseProvider implements CoreEntityProvider, Updateable, Createable, Deleteable {

    private static Logger log = Logger.getLogger(ItemsProvider.class);

    public ItemsProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = ItemEntity.class;
        func2actionMapGET.put("getBitstreams", "bitstreams");
        func2actionMapGET.put("getCommentsCount", "commentscount");
        func2actionMapGET.put("getComments", "comments");
        func2actionMapGET.put("getMetadataFields", "metadatafields");
        func2actionMapPOST.put("createMetadata", "metadata");
        inputParamsPOST.put("createMetadata", new String[]{"id", "value"});
        func2actionMapPUT.put("editMetadata", "metadata");
        func2actionMapDELETE.put("removeMetadata", "metadata");

        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "items";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "item_exists:" + id);

        if ("metadatafields".equals(id)) {
            return true;
        }

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);

            Item comm = Item.find(context, Integer.parseInt(id));
            return comm != null ? true : false;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } finally {
            removeConn(context);
        }
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_item:" + reference.getId());
        String segments[] = {};

        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/");
        }

        if (segments.length > 3) {
            return super.getEntity(reference);
        }else if ("metadatafields".equals(reference.getId())) {
            return super.getEntity(reference, "metadatafields");
        }

        Context context = null;
        try {
            context = new Context();

            UserRequestParams uparams = refreshParams(context);
            if (entityExists(reference.getId())) {
                return new ItemEntity(reference.getId(), context, uparams);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_items");

        Context context = null;
        try {
            context = new Context();

            UserRequestParams uparams = refreshParams(context);
            List<Object> entities = new ArrayList<Object>();

            ItemIterator items = Item.findAll(context);
            while (items.hasNext()) {
                entities.add(new ItemEntity(items.next(), uparams));
            }

            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new ItemEntity();
    }
}