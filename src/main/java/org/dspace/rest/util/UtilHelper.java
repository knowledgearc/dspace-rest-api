/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Here are implemented and coupled helper methods used by several providers
 * especially in the custom actions
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 * @see GenComparator
 * @see AbstractBaseProvider
 */
public class UtilHelper {

    protected UtilHelper() {
    }

    public static final boolean DEBUG_ACTIVE = true;
    // methods for getObjects
    public static final int PARENTS = 1;
    public static final int CHILDREN = 2;
    public static final int COLLECTIONS = 3;
    public static final int RECENT_SUBMISSIONS = 4;
    public static final int COMMUNITIES_INVOLVED = 5;
    public static final int ITEMS_INVOLVED = 6;
    public static final int ITEM_PERMISSION = 7;
    public static final int ITEM_IN_COMMUNITIES = 8;
    public static final int ITEM_IN_COLLECTIONS = 9;
    // sort methods for GenComparator
    public static final int SORT_ID = 210;
    public static final int SORT_NAME = 211;
    public static final int SORT_LASTMODIFIED = 212;
    public static final int SORT_SUBMITTER = 213;
    public static final int SORT_COUNT_ITEMS = 214;
    public static final int SORT_LANGUAGE = 215;
    public static final int SORT_LASTNAME = 216;
    public static final int SORT_FULL_NAME = 217;
    public static final int SORT_FIRSTNAME = 218;
    public static final int SORT_EMAIL = 219;
    public static final int SORT_ID_REV = 310;
    public static final int SORT_NAME_REV = 311;
    public static final int SORT_LASTMODIFIED_REV = 312;
    public static final int SORT_SUBMITTER_REV = 313;
    public static final int SORT_COUNT_ITEMS_REV = 314;
    public static final int SORT_LANGUAGE_REV = 315;
    public static final int SORT_LASTNAME_REV = 316;
    public static final int SORT_FULL_NAME_REV = 317;
    public static final int SORT_FIRSTNAME_REV = 318;
    public static final int SORT_EMAIL_REV = 319;
    public static final int DEPTH_LEVEL = 2;
    public static final int DEPTH_MINIMAL = 1;
    public static final int DEPTH_STANDARD = 2;
    public static final int DEPTH_EXTENDED = 3;
    public static final Map<String, String> mappings = new HashMap<String, String>() {

        {
            put("getId", "id");
            put("getName", "name");
            put("getCountItems", "countItems");
            put("getHandle", "handle");
            put("getType", "type");
            put("getCollections", "collections");
            put("getCanEdit", "canedit");
            put("getParentCommunity", "parents");
            put("getSubCommunities", "children");


        }
    };
    private static Map<String, Class<?>[]> mappings_parameters = new HashMap<String, Class<?>[]>();
    private static Map<String, String> mappings_rev = new HashMap<String, String>();

}
