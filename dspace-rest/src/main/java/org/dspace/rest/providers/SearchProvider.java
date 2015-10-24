/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.rest.entities.SearchResultsInfoEntity;
import org.dspace.rest.util.UserRequestParams;
import org.dspace.search.DSQuery;
import org.dspace.search.QueryArgs;
import org.dspace.search.QueryResults;
import org.dspace.sort.SortOption;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(SearchProvider.class);

    public SearchProvider(EntityProviderManager entityProviderManager) {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "search";
    }

    public boolean entityExists(String id) {
        return true;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_entity:" + reference.getId());
        throw new EntityException("Not Acceptable", "The data is not available", 406);
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "get_entities");

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        // refresh parameters for this request
        UserRequestParams uparams;
        uparams = refreshParams(context);
        List<Object> entities = new ArrayList<Object>();

        try {
            // extract query arguments from the request
            // deprecated - this is now handled at the end of function
            QueryArgs arg = new QueryArgs();
            arg.setQuery(query);

//            if (_perpage > 0) {
//                arg.setPageSize(_perpage);
//            }
            arg.setStart(_start);

            if ((_order.equalsIgnoreCase("descending")) || (_order.equalsIgnoreCase("desc"))) {
                arg.setSortOrder(SortOption.DESCENDING);
            } else {
                arg.setSortOrder(SortOption.ASCENDING);
            }

            QueryResults qre;

            /**
             * search can be performed only on community or collection selected
             * or all, not on the both in same time; check this requirement
             */
//            if (_community != null) {
//                qre = DSQuery.doQuery(context, arg, _community);
//            } else if (_collection != null) {
//                qre = DSQuery.doQuery(context, arg, _collection);
//            } else {
                qre = DSQuery.doQuery(context, arg);
//            }
            entities.add(new SearchResultsInfoEntity(qre.getHitCount(), qre.getHitTypes(), qre.getHitHandles(), qre.getHitIds()));

            /**
             * check returned objects, recognize them and put in result
             * list as expected
             */
            for (int x = 0; x < qre.getHitTypes().size(); x++) {
                switch ((Integer) (qre.getHitTypes().get(x))) {
                    case Constants.ITEM: {
//                            entities.add(idOnly ? new ItemEntityId(qre.getHitIds().get(x).toString(), context) : new ItemEntity(qre.getHitIds().get(x).toString(), context,1, uparams));
                    }
                    break;

                    case Constants.COMMUNITY: {
//                            entities.add(idOnly ? new CommunityEntityId(qre.getHitIds().get(x).toString(), context) : new CommunityEntity(qre.getHitIds().get(x).toString(), context,1, uparams));
                    }
                    break;

                    case Constants.COLLECTION: {
//                            entities.add(idOnly ? new CollectionEntityId(qre.getHitIds().get(x).toString(), context) : new CollectionEntity(qre.getHitIds().get(x).toString(), context,1, uparams));
                    }
                    break;

                    case Constants.BITSTREAM: {
//                            entities.add(idOnly ? new BitstreamEntityId(qre.getHitIds().get(x).toString(), context) : new BitstreamEntity(qre.getHitIds().get(x).toString(), context,1, uparams));
                    }
                    break;

                    case Constants.BUNDLE: {
//                            entities.add(idOnly ? new BundleEntityId(qre.getHitIds().get(x).toString(), context) : new BundleEntity(qre.getHitIds().get(x).toString(), context,1, uparams));
                    }
                    break;

                    case Constants.EPERSON: {
//                            entities.add(idOnly ? new UserEntityId(qre.getHitIds().get(x).toString(), context) : new UserEntity(qre.getHitIds().get(x).toString(), context,1, uparams));
                    }
                    break;

                }
            }

            context.complete();
        } catch (SQLException sql) {
            throw new EntityException("Internal Server Error", "SQL Problem", 500);
        } catch (IOException io) {
            throw new EntityException("Internal Server Error", "Could not execute query", 500);
        }

        /**
         * if the full info are requested and there are sorting requirements
         * process entities through sorting filter first
         */
//        if (!idOnly && sortOptions.size() > 0) {
//            Collections.sort(entities, new GenComparator(sortOptions));
//        }

        /**
         * process entities according to _limit, _perpage etc
         */
//        removeTrailing(entities);

        return entities;
    }

    /**
     * Returns a Entity object with sample data
     */
    public Object getSampleEntity() {
        return null;
    }
}
