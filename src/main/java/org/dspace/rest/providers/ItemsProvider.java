/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.content.*;
import org.dspace.core.Context;
import org.dspace.rest.entities.CommentEntity;
import org.dspace.rest.entities.ItemEntity;
import org.dspace.rest.entities.ItemEntityId;
import org.dspace.rest.entities.MetadataFieldEntity;
import org.dspace.rest.util.GenComparator;
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
import java.util.Collections;
import java.util.List;

/**
 * Provides interface for access to item entities
 * @see ItemEntityId
 * @see ItemEntity
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class ItemsProvider extends AbstractBaseProvider implements CoreEntityProvider, Updateable, Createable, Deleteable {

    private static Logger log = Logger.getLogger(ItemsProvider.class);

    /**
     * Constructor handles registration of provider
     * @param entityProviderManager
     * @throws java.sql.SQLException
     */
    public ItemsProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = ItemEntity.class;
        func2actionMapGET.put("getMetadata", "metadata");
        func2actionMapGET.put("getSubmitter", "submitter");
        func2actionMapGET.put("getIsArchived", "isArchived");
        func2actionMapGET.put("getIsWithdrawn", "isWithdrawn");
        func2actionMapGET.put("getOwningCollection", "owningCollection");
        func2actionMapGET.put("getLastModified", "lastModified");
        func2actionMapGET.put("getCollections", "collections");
        func2actionMapGET.put("getCommunities", "communities");
        func2actionMapGET.put("getName", "name");
        func2actionMapGET.put("getBitstreams", "bitstreams");
        func2actionMapGET.put("getHandle", "handle");
        func2actionMapGET.put("getCanEdit", "canedit");
        func2actionMapGET.put("getId", "id");
        func2actionMapGET.put("getType", "type");
        func2actionMapGET.put("getBundles", "bundles");
        func2actionMapGET.put("getPolicies", "policies");
        func2actionMapPUT.put("addBundle", "bundles");
        func2actionMapPOST.put("createBundle", "createBundle");
        inputParamsPOST.put("createBundle", new String[]{"name", "id"});

        func2actionMapPOST.put("addMetadata", "addMetadata");
        inputParamsPOST.put("addMetadata", new String[]{"id", "fieldID", "value"});
        func2actionMapPOST.put("editMetadata", "editMetadata");
        inputParamsPOST.put("editMetadata", new String[]{"id", "metadata"});
        func2actionMapDELETE.put("removeMetadata", "metadata");

        entityConstructor = processedEntity.getDeclaredConstructor(new Class<?>[]{String.class, Context.class, Integer.TYPE, UserRequestParams.class});
        initMappings(processedEntity);
    }

    // this is the prefix where provider is registered (URL path)
    public String getEntityPrefix() {
        return "items";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "entity_exists:" + id);

        // sample entity
        if (id.equals(":ID:")) {
            return true;
        }

        if (id.equals("metadataFields")) {
            return true;
        }

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        boolean result = false;

        // search for existence for particular item
        try {
            Item col = Item.find(context, Integer.parseInt(id));
            if (col != null) {
                result = true;
            }
        } catch (SQLException ex) {
            result = false;
        }

        // handles manual deregistration by sql server to lower load
        removeConn(context);
        return result;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_entity:" + reference.getId());
        String segments[] = {};

        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/", 10);
        }

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        UserRequestParams uparams;
        uparams = refreshParams(context);

        // first check if there is sub-field requested
        // if so then invoke appropriate method inside of entity
        if (segments.length > 3) {
            String act = segments[3];
            if (act.lastIndexOf(".") > 0) {
                act = act.substring(0, segments[3].lastIndexOf("."));
            }
            if (act.equals("comments")) {
                List l = new ArrayList();
                ItemEntity itemEntity = new ItemEntity(reference.getId(),context);
                itemEntity.setComments(l);
                try {
                    itemEntity.setCountItems(Comment.countItems(context, Integer.parseInt(reference.getId())));
                    Comment[] comments = Comment.findAll(context, Integer.parseInt(reference.getId()),_perpage*_page, _perpage);
                    for (Comment comment : comments) {
                        l.add(new CommentEntity(comment));
                    }
                } catch (SQLException e) {
                    throw new EntityException("Internal server error", "SQL error", 500);
                }

                removeConn(context);
                return itemEntity;
            }

            return super.getEntity(reference);
        }

        // sample entity
        if (reference.getId().equals(":ID:")) {
            return new ItemEntity();
        }

        if (reference.getId().equals("metadataFields")) {
            List<MetadataFieldEntity> l = new ArrayList<MetadataFieldEntity>();
            try {
                MetadataField[] fields = MetadataField.findAll(context);
                for (MetadataField field : fields)
                {
                    int fieldID = field.getFieldID();
                    MetadataSchema schema = MetadataSchema.find(context, field.getSchemaID());
                    String name = schema.getName() +"."+field.getElement();
                    if (field.getQualifier() != null)
                    {
                        name += "." + field.getQualifier();
                    }

                    l.add(new MetadataFieldEntity(fieldID, name));
                }
            } catch (SQLException e) {
                throw new EntityException("Internal server error", "SQL error", 500);
            }

            removeConn(context);
            return l;
        }

        if (entityExists(reference.getId())) {
            // return basic or full info, according to requirements
            if (idOnly) {
                return new ItemEntityId(reference.getId(), context);
            } else {
                return new ItemEntity(reference.getId(), context, 1, uparams);
            }
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_entities");

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        UserRequestParams uparams;
        uparams = refreshParams(context);
        List<Object> entities = new ArrayList<Object>();

        try {
            ItemIterator items = Item.findAll(context);
            while (items.hasNext()) {
                entities.add(idOnly ? new ItemEntityId(items.next()) : new ItemEntity(items.next(), 1, uparams));
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        removeConn(context);
        if (!idOnly && sortOptions.size() > 0) {
            Collections.sort(entities, new GenComparator(sortOptions));
        }

        removeTrailing(entities);
        return entities;
    }

    /**
     * Return sample entity
     * @return
     */
    public Object getSampleEntity() {
        return new ItemEntity();
    }
}