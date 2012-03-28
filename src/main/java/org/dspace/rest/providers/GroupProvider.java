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
import org.sakaiproject.entitybus.entityprovider.capabilities.Createable;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupProvider extends AbstractBaseProvider implements CoreEntityProvider, Createable {

    private static Logger log = Logger.getLogger(GroupProvider.class);

    public GroupProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = GroupEntity.class;
        func2actionMapPOST.put("addUser", "users");
        inputParamsPOST.put("addUser", new String[]{"email", "firstName", "lastName"});
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

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_group:" + reference.getId());
        String segments[] = {};

        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/");
        }

        if (segments.length > 3) {
            return super.getEntity(reference);
        }

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);
            if (entityExists(reference.getId())) {
                return new GroupEntity(reference.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_groups:");

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