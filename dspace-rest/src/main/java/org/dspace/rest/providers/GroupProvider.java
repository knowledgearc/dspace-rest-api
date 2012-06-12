/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.rest.entities.GroupEntity;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.Deleteable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Updateable;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupProvider extends AbstractBaseProvider implements CoreEntityProvider, Updateable, Deleteable {

    private static Logger log = Logger.getLogger(GroupProvider.class);

    public GroupProvider(EntityProviderManager entityProviderManager) throws NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = GroupEntity.class;
        func2actionMapGET.put("groups", "groups");
        func2actionMapGET.put("users", "users");
//        func2actionMapPOST.put("createUser", "users");
//        inputParamsPOST.put("createUser", new String[]{"email", "firstName", "lastName"});
        func2actionMapPUT.put("assignGroup", "groups");
        func2actionMapPUT.put("assignUser", "users");
        func2actionMapDELETE.put("removeGroup", "groups");
        func2actionMapDELETE.put("removeUser", "users");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "groups";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "group_exists:" + id);

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            Group comm = Group.find(context, Integer.parseInt(id));
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
        log.info(userInfo() + "get_group:" + ref.getId());
        String segments[] = getSegments();

        if (segments.length > 3) {
            return super.getEntity(ref);
        }

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            if (entityExists(ref.getId())) {
                return new GroupEntity(ref.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + ref.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_groups");

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            List<Object> entities = new ArrayList<Object>();
            Group[] groups = Group.findAll(context, Group.NAME);
            for (Group g : groups) {
                entities.add(new GroupEntity(g));
            }

            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new GroupEntity();
    }
}