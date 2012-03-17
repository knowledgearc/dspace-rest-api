/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.content.Comment;
import org.dspace.core.Context;
import org.dspace.rest.entities.CommentEntity;
import org.dspace.rest.entities.CommentEntityId;
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

/**
 * Provides interface for access to comment entities
 *
 * @author Lewis Lu
 */
public class CommentsProvider extends AbstractBaseProvider implements CoreEntityProvider, Updateable, Createable, Deleteable {

    private static Logger log = Logger.getLogger(CommentsProvider.class);

    public CommentsProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = CommentEntity.class;
        func2actionMapPOST.put("createComment", "createComment");
        inputParamsPOST.put("createComment", new String[]{"id", "replyCommentID", "subject", "body"});

        func2actionMapPUT.put("editComment", "comment");
//        inputParamsPOST.put("editComment", new String[]{"id", "subject", "body","deleted"});

        func2actionMapPOST.put("approveComment", "approveComment");
        inputParamsPOST.put("approveComment", new String[]{"id", "approved"});

        func2actionMapDELETE.put("removeComment", "");

        entityConstructor = processedEntity.getDeclaredConstructor(new Class<?>[]{String.class, Context.class, Integer.TYPE, UserRequestParams.class});
        initMappings(processedEntity);
    }

    // this is the prefix where provider is registered (URL path)
    public String getEntityPrefix() {
        return "comments";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "entity_exists:" + id);

        // sample entity
        if (id.equals(":ID:")) {
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
            Comment col = Comment.find(context, Integer.parseInt(id));
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

        System.out.println("Comment get entity");
        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/", 10);
        }

        // first check if there is sub-field requested
        // if so then invoke appropriate method inside of entity
        if (segments.length > 3) {
            return super.getEntity(reference);
        } else {

            // sample entity
            if (reference.getId().equals(":ID:")) {
                return new CommentEntity();
            }

            if (reference.getId() == null) {
                return new CommentEntity();
            }

            Context context;
            try {
                context = new Context();
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            }

            if (entityExists(reference.getId())) {
                // return just entity containg id or full info
                if (idOnly) {
                    return new CommentEntityId(reference.getId(), context);
                } else {
                    return new CommentEntity(reference.getId(), context, 1, null);
                }
            }

            removeConn(context);
            throw new IllegalArgumentException("Invalid id:" + reference.getId());
        }
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_entities");

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        List<Object> entities = new ArrayList<Object>();

        try {
            Comment[] comments = Comment.findAll(context);
            for (Comment comment : comments) {
                entities.add(idOnly ? new CommentEntityId(comment) : new CommentEntity(comment));
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        removeConn(context);
        return entities;
    }

    public Object getSampleEntity() {
        return new CommentEntity();
    }
}