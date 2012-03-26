package org.dspace.rest.content;

import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContentHelper {
    //EPerson
    public static int countItemsEPerson(Context context) throws SQLException {
        int itemcount = 0;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            String query = "SELECT COUNT(*) FROM eperson";

            statement = context.getDBConnection().prepareStatement(query);

            rs = statement.executeQuery();
            if (rs != null) {
                rs.next();
                itemcount = rs.getInt(1);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqle) {
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqle) {
                }
            }
        }

        return itemcount;
    }

    public static EPerson[] findAllEPerson(Context context, int offset, int limit) throws SQLException {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append("SELECT * FROM eperson ORDER BY eperson_id");

        // Add offset and limit restrictions - Oracle requires special code
        if ("oracle".equals(ConfigurationManager.getProperty("db.name"))) {
            // First prepare the query to generate row numbers
            if (limit > 0 || offset > 0) {
                queryBuf.insert(0, "SELECT /*+ FIRST_ROWS(n) */ rec.*, ROWNUM rnum  FROM (");
                queryBuf.append(") rec ");
            }

            // Restrict the number of rows returned based on the limit
            if (limit > 0) {
                queryBuf.append("WHERE rownum<=? ");
                // If we also have an offset, then convert the limit into the maximum row number
                if (offset > 0) {
                    limit += offset;
                }
            }

            // Return only the records after the specified offset (row number)
            if (offset > 0) {
                queryBuf.insert(0, "SELECT * FROM (");
                queryBuf.append(") WHERE rnum>?");
            }
        } else {
            if (limit > 0) {
                queryBuf.append(" LIMIT ? ");
            }

            if (offset > 0) {
                queryBuf.append(" OFFSET ? ");
            }
        }

        // Create the parameter array, including limit and offset if part of the query
        Object[] paramArr = new Object[]{};
        if (limit > 0 && offset > 0) {
            paramArr = new Object[]{limit, offset};
        } else if (limit > 0) {
            paramArr = new Object[]{limit};
        } else if (offset > 0) {
            paramArr = new Object[]{offset};
        }

        TableRowIterator rows = DatabaseManager.query(context, queryBuf.toString(), paramArr);

        try {
            List<TableRow> epeopleRows = rows.toList();

            EPerson[] epeople = new EPerson[epeopleRows.size()];

            for (int i = 0; i < epeopleRows.size(); i++) {
                TableRow row = epeopleRows.get(i);

                // First check the cache
                EPerson fromCache = (EPerson) context.fromCache(EPerson.class, row.getIntColumn("eperson_id"));

                if (fromCache != null) {
                    epeople[i] = fromCache;
                } else {
                    epeople[i] = new EPerson(context, row);
                }
            }

            return epeople;
        } finally {
            if (rows != null) {
                rows.close();
            }
        }
    }

    public static EPerson[] searchSubmittersinWorkflow(Context context, String query, int offset, int limit, String orderby) throws SQLException {

        String params = "%" + query.toLowerCase() + "%";
        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append("SELECT DISTINCT (eperson.firstname ||' '|| eperson.lastname) fullname, eperson.* FROM workflowitem, item, eperson WHERE workflowitem.item_id = item.item_id AND item.submitter_id = eperson.eperson_id AND ");
        queryBuf.append("(LOWER(firstname) LIKE LOWER(?) OR LOWER(lastname) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)) " + (!"".equals(orderby) ? "ORDER BY " + orderby : ""));

        // Add offset and limit restrictions - Oracle requires special code
        if ("oracle".equals(ConfigurationManager.getProperty("db.name"))) {
            // First prepare the query to generate row numbers
            if (limit > 0 || offset > 0) {
                queryBuf.insert(0, "SELECT /*+ FIRST_ROWS(n) */ rec.*, ROWNUM rnum  FROM (");
                queryBuf.append(") rec ");
            }

            // Restrict the number of rows returned based on the limit
            if (limit > 0) {
                queryBuf.append("WHERE rownum<=? ");
                // If we also have an offset, then convert the limit into the maximum row number
                if (offset > 0) {
                    limit += offset;
                }
            }

            // Return only the records after the specified offset (row number)
            if (offset > 0) {
                queryBuf.insert(0, "SELECT * FROM (");
                queryBuf.append(") WHERE rnum>?");
            }
        } else {
            if (limit > 0) {
                queryBuf.append(" LIMIT ? ");
            }

            if (offset > 0) {
                queryBuf.append(" OFFSET ? ");
            }
        }

        // Create the parameter array, including limit and offset if part of the query
        Object[] paramArr = new Object[]{params, params, params};
        if (limit > 0 && offset > 0) {
            paramArr = new Object[]{params, params, params, limit, offset};
        } else if (limit > 0) {
            paramArr = new Object[]{params, params, params, limit};
        } else if (offset > 0) {
            paramArr = new Object[]{params, params, params, offset};
        }

        // Get all the epeople that match the query
        TableRowIterator rows = DatabaseManager.query(context, queryBuf.toString(), paramArr);
        try {
            List<TableRow> epeopleRows = rows.toList();
            EPerson[] epeople = new EPerson[epeopleRows.size()];

            for (int i = 0; i < epeopleRows.size(); i++) {
                TableRow row = epeopleRows.get(i);

                // First check the cache
                EPerson fromCache = (EPerson) context.fromCache(EPerson.class, row.getIntColumn("eperson_id"));

                if (fromCache != null) {
                    epeople[i] = fromCache;
                } else {
                    epeople[i] = new EPerson(context, row);
                }
            }

            return epeople;
        } finally {
            if (rows != null) {
                rows.close();
            }
        }
    }

    public static int countItemsCollection(Context context) throws SQLException {
        int itemcount = 0;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            String query = "SELECT COUNT(*) FROM collection";

            statement = context.getDBConnection().prepareStatement(query);

            rs = statement.executeQuery();
            if (rs != null) {
                rs.next();
                itemcount = rs.getInt(1);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqle) {
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqle) {
                }
            }
        }

        return itemcount;
    }

    public static Collection[] findAllCollection(Context context, int offset, int limit) throws SQLException {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append("SELECT * FROM collection ORDER BY name");

        // Add offset and limit restrictions - Oracle requires special code
        if ("oracle".equals(ConfigurationManager.getProperty("db.name"))) {
            // First prepare the query to generate row numbers
            if (limit > 0 || offset > 0) {
                queryBuf.insert(0, "SELECT /*+ FIRST_ROWS(n) */ rec.*, ROWNUM rnum  FROM (");
                queryBuf.append(") rec ");
            }

            // Restrict the number of rows returned based on the limit
            if (limit > 0) {
                queryBuf.append("WHERE rownum<=? ");
                // If we also have an offset, then convert the limit into the maximum row number
                if (offset > 0) {
                    limit += offset;
                }
            }

            // Return only the records after the specified offset (row number)
            if (offset > 0) {
                queryBuf.insert(0, "SELECT * FROM (");
                queryBuf.append(") WHERE rnum>?");
            }
        } else {
            if (limit > 0) {
                queryBuf.append(" LIMIT ? ");
            }

            if (offset > 0) {
                queryBuf.append(" OFFSET ? ");
            }
        }

        // Create the parameter array, including limit and offset if part of the query
        Object[] paramArr = new Object[]{};
        if (limit > 0 && offset > 0) {
            paramArr = new Object[]{limit, offset};
        } else if (limit > 0) {
            paramArr = new Object[]{limit};
        } else if (offset > 0) {
            paramArr = new Object[]{offset};
        }

        TableRowIterator tri = DatabaseManager.query(context, queryBuf.toString(), paramArr);

        List<Collection> collections = new ArrayList<Collection>();

        try {
            while (tri.hasNext()) {
                TableRow row = tri.next();

                // First check the cache
                Collection fromCache = (Collection) context.fromCache(Collection.class, row.getIntColumn("collection_id"));

                if (fromCache != null) {
                    collections.add(fromCache);
                } else {
                    collections.add(new Collection(context, row));
                }
            }
        } finally {
            // close the TableRowIterator to free up resources
            if (tri != null) {
                tri.close();
            }
        }

        return collections.toArray(new Collection[collections.size()]);
    }

    public static int countItemsItem(Context context, int collectionID) throws SQLException {
        int itemcount = 0;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            String query = "SELECT COUNT(*) FROM collection2item, item WHERE "
                   + "collection2item.collection_id =  ? "
                   + "AND collection2item.item_id = item.item_id ";

            statement = context.getDBConnection().prepareStatement(query);
            statement.setInt(1,collectionID);

            rs = statement.executeQuery();
            if (rs != null) {
                rs.next();
                itemcount = rs.getInt(1);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqle) {
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqle) {
                }
            }
        }

        return itemcount;
    }

    public static Item[] findAllItem(Context context, int collectionID, int offset, int limit) throws SQLException {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append("SELECT item.* FROM item, collection2item " +
                    "WHERE item.item_id=collection2item.item_id " +
                    "AND collection2item.collection_id = ? " +
                    "order by item.item_id desc");

        // Add offset and limit restrictions - Oracle requires special code
        if ("oracle".equals(ConfigurationManager.getProperty("db.name"))) {
            // First prepare the query to generate row numbers
            if (limit > 0 || offset > 0) {
                queryBuf.insert(0, "SELECT /*+ FIRST_ROWS(n) */ rec.*, ROWNUM rnum  FROM (");
                queryBuf.append(") rec ");
            }

            // Restrict the number of rows returned based on the limit
            if (limit > 0) {
                queryBuf.append("WHERE rownum<=? ");
                // If we also have an offset, then convert the limit into the maximum row number
                if (offset > 0) {
                    limit += offset;
                }
            }

            // Return only the records after the specified offset (row number)
            if (offset > 0) {
                queryBuf.insert(0, "SELECT * FROM (");
                queryBuf.append(") WHERE rnum>?");
            }
        } else {
            if (limit > 0) {
                queryBuf.append(" LIMIT ? ");
            }

            if (offset > 0) {
                queryBuf.append(" OFFSET ? ");
            }
        }

        // Create the parameter array, including limit and offset if part of the query
        Object[] paramArr = new Object[]{collectionID};
        if (limit > 0 && offset > 0) {
            paramArr = new Object[]{collectionID, limit, offset};
        } else if (limit > 0) {
            paramArr = new Object[]{collectionID, limit};
        } else if (offset > 0) {
            paramArr = new Object[]{collectionID, offset};
        }

        TableRowIterator tri = DatabaseManager.query(context, queryBuf.toString(), paramArr);

        List<Item> items = new ArrayList<Item>();

        try {
            while (tri.hasNext()) {
                TableRow row = tri.next();

                // First check the cache
                Item fromCache = (Item) context.fromCache(Item.class, row.getIntColumn("item_id"));

                if (fromCache != null) {
                    items.add(fromCache);
                } else {
                    items.add(new Item(context, row));
                }
            }
        } finally {
            if (tri != null) {
                tri.close();
            }
        }

        return items.toArray(new Item[items.size()]);
    }
}
