/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import java.util.ArrayList;
import java.util.List;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.dspace.core.Context;
import java.sql.SQLException;
import org.sakaiproject.entitybus.exception.EntityException;
import org.dspace.rest.entities.*;
import org.dspace.search.*;
import org.apache.log4j.Logger;
import java.text.ParseException;
import org.dspace.rest.util.UserRequestParams;

public class HarvestProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(UserProvider.class);

    public HarvestProvider(EntityProviderManager entityProviderManager) {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "harvest";
    }

    public boolean entityExists(String id) {
        return true;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_harvest:" + reference.getId());
        throw new EntityException("Not Acceptable", "The data is not available", 406);
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_harvests");

        Context context = null;
        try {
            context = new Context();
            UserRequestParams uparams = refreshParams(context);

            List<Object> entities = new ArrayList<Object>();
            List<HarvestedItemInfo> res;

            /**
             * check requirement for communities and collections, they should be
             * mutually excluded as underlying architecture accepts searching
             * in only one subject (community or collection)
             */
            if (_community != null) {
                res = Harvest.harvest(context, _community, _sdate, _edate, _start, _limit, true, true, withdrawn, true);
            } else if (_collection != null) {
                res = Harvest.harvest(context, _collection, _sdate, _edate, _start, _limit, true, true, withdrawn, true);
            } else {
                res = Harvest.harvest(context, null, _sdate, _edate, _start, _limit, true, true, withdrawn, true);
            }

            entities.add(new HarvestResultsInfoEntity(res.size()));
            for (int x = 0; x < res.size(); x++) {
                entities.add(new ItemEntity(res.get(x).item, context, uparams));
            }

            return entities;
        } catch (ParseException ex) {
            throw new EntityException("Bad request", "Incompatible date format", 400);
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    /**
     * Returns a Entity object with sample data
     */
    public Object getSampleEntity() {
        return null;
    }
}