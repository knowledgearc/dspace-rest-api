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

public class ItemsProvider extends AbstractBaseProvider implements CoreEntityProvider, Createable, Updateable, Deleteable {

    private static Logger log = Logger.getLogger(ItemsProvider.class);

    public ItemsProvider(EntityProviderManager entityProviderManager) throws NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = ItemEntity.class;
        func2actionMapGET.put("getBundles", "bundles");
        func2actionMapGET.put("getMetadataFields", "metadatafields");
        func2actionMapPOST.put("createItem", "");
        func2actionMapPOST.put("createMetadata", "metadata");
        inputParamsPOST.put("createMetadata", new String[]{"id", "value"});
        func2actionMapPUT.put("editMetadata", "metadata");
        func2actionMapDELETE.put("removeMetadata", "metadata");
        func2actionMapDELETE.put("removeItem", "");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "items";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "item_exists:" + id);

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException ex) {
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
        } finally {
            removeConn(context);
        }
    }

    public Object getEntity(EntityReference ref) {
        log.info(userInfo() + "get_item:" + ref.getId());
        String segments[] = getSegments();

        if (segments.length > 3) {
            return super.getEntity(ref);
        } else {
            try {
                Integer.parseInt(ref.getId());
            } catch (NumberFormatException ex) {
                return super.getEntity(ref, ref.getId());
            }
        }

        Context context = null;
        try {
            context = new Context();
            UserRequestParams uparams = refreshParams(context);

            if (entityExists(ref.getId())) {
                return new ItemEntity(ref.getId(), context, uparams);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + ref.getId());
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
                entities.add(new ItemEntity(items.next(), context, uparams));
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