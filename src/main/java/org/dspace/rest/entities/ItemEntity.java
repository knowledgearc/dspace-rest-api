/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.content.*;
import org.dspace.content.Collection;
import org.dspace.content.authority.Choices;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.exception.EntityException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ItemEntity extends ItemEntityId {

    @EntityFieldRequired
    private String name;
    private String handle;
    private int countItems;
    //    List<Object> bundles = new ArrayList<Object>();
//    List<Object> bitstreams = new ArrayList<Object>();
//    List<Object> collections = new ArrayList<Object>();
//    List<Object> communities = new ArrayList<Object>();
    List<Object> metadata = new ArrayList<Object>();
    //    List<Object> policies = new ArrayList<Object>();
    List<Object> comments = new ArrayList<Object>();
    //String metadata;
    Date lastModified;
    //    Object owningCollection;
    boolean isArchived, isWithdrawn;
//    UserEntity submitter;
//    private DisseminationCrosswalk xHTMLHeadCrosswalk;

    public ItemEntity(String uid, Context context, int level, UserRequestParams uparams) {
        try {
            Item res = Item.find(context, Integer.parseInt(uid));

            // Check authorisation
            AuthorizeManager.authorizeAction(context, res, Constants.READ);

//            this.id = res.getID();
//            this.canEdit = res.canEdit();
            this.handle = res.getHandle();
            this.name = res.getName();
//            this.type = res.getType();
            this.lastModified = res.getLastModified();
            this.isArchived = res.isArchived();
            this.isArchived = res.isWithdrawn();
//            this.submitter = new UserEntity(res.getSubmitter());

            Bundle[] bun = res.getBundles();
            Bitstream[] bst = res.getNonInternalBitstreams();
            Collection[] col = res.getCollections();
            Community[] com = res.getCommunities();
            List<ResourcePolicy> ps = AuthorizeManager.getPolicies(context, res);

            boolean includeFull = false;
            level++;
            if (level <= uparams.getDetail()) {
                includeFull = true;
            }

            for (ResourcePolicy c : ps) {
//                this.policies.add(new PolicyEntity(c, "Policies for Item ("+c.getResourceID()+")", level, uparams));
            }
            Collection ownCol = res.getOwningCollection();
            if (ownCol != null) {
//                this.owningCollection = includeFull ? new CollectionEntity(ownCol, level, uparams) : new CollectionEntityId(ownCol);
            }
            for (Bundle b : bun) {
//                this.bundles.add(includeFull ? new BundleEntity(b, level, uparams) : new BundleEntityId(b));
                List<ResourcePolicy> bs = AuthorizeManager.getPolicies(context, b);
                for (ResourcePolicy c : bs) {
//                    this.policies.add(new PolicyEntity(c, "Policies for Bundle "+b.getName()+" ("+c.getResourceID()+")", level, uparams));
                }
                Bitstream[] bes = b.getBitstreams();
                for (Bitstream bi : bes) {
                    List<ResourcePolicy> bts = AuthorizeManager.getPolicies(context, bi);
                    for (ResourcePolicy ci : bts) {
//                        this.policies.add(new PolicyEntity(ci, "Policies for Bitsteam "+bi.getName()+" ("+ci.getResourceID()+")", level, uparams));
                    }
                }
            }
            for (Bitstream b : bst) {
//                this.bitstreams.add(includeFull ? new BitstreamEntity(b, level, uparams) : new BitstreamEntityId(b));
            }
            for (Collection c : col) {
//                this.collections.add(includeFull ? new CollectionEntity(c, level, uparams) : new CollectionEntityId(c));
            }
            for (Community c : com) {
//                this.communities.add(includeFull ? new CommunityEntity(c, level, uparams) : new CommunityEntityId(c));
            }

            DCValue[] dcValues = res.getMetadata(Item.ANY, Item.ANY, Item.ANY, Item.ANY);
            for (DCValue dcValue : dcValues) {
//                this.metadata.add(includeFull ? new MetadataEntity(dcValue, level, uparams) : new MetadataEntityId(dcValue));
            }

            //context.complete();

        } catch (NumberFormatException ex) {
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }
    }

    public ItemEntity(Item item, int level, UserRequestParams uparams) throws SQLException {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        level++;
        if (level <= uparams.getDetail()) {
            includeFull = true;
        }

//        this.canEdit = item.canEdit();
        this.handle = item.getHandle();
        this.name = item.getName();
//        this.type = item.getType();
//        this.id = item.getID();
        this.lastModified = item.getLastModified();
        Collection ownCol = item.getOwningCollection();
        if (ownCol != null) {
//            this.owningCollection = includeFull ? new CollectionEntity(ownCol, level, uparams) : new CollectionEntityId(ownCol);
        }
        this.isArchived = item.isArchived();
        this.isWithdrawn = item.isWithdrawn();
//        this.submitter = new UserEntity(item.getSubmitter());

        Bundle[] bun = item.getBundles();
        Bitstream[] bst = item.getNonInternalBitstreams();
        Collection[] col = item.getCollections();
        Community[] com = item.getCommunities();
        for (Bundle b : bun) {
//            this.bundles.add(includeFull ? new BundleEntity(b, level, uparams) : new BundleEntityId(b));
        }
        for (Bitstream b : bst) {
//            this.bitstreams.add(includeFull ? new BitstreamEntity(b, level, uparams) : new BitstreamEntityId(b));
        }
        for (Collection c : col) {
//            this.collections.add(includeFull ? new CollectionEntity(c, level, uparams) : new CollectionEntityId(c));
        }
        for (Community c : com) {
//            this.communities.add(includeFull ? new CommunityEntity(c, level, uparams) : new CommunityEntityId(c));
        }

        DCValue[] dcValues = item.getMetadata(Item.ANY, Item.ANY, Item.ANY, Item.ANY);
        for (DCValue dcValue : dcValues) {
            this.metadata.add(includeFull ? new MetadataEntity(dcValue, level, uparams) : new MetadataEntityId(dcValue));
        }

    }

    public ItemEntity(Item item) throws SQLException {
        super(item);
        this.handle = item.getHandle();
        this.name = item.getName();
        this.lastModified = item.getLastModified();
        Collection ownCol = item.getOwningCollection();
        if (ownCol != null) {
//            this.owningCollection = includeFull ? new CollectionEntity(ownCol, level, uparams) : new CollectionEntityId(ownCol);
        }
        this.isArchived = item.isArchived();
        this.isWithdrawn = item.isWithdrawn();
//        this.submitter = new UserEntity(item.getSubmitter());

        Bundle[] bun = item.getBundles();
        Bitstream[] bst = item.getNonInternalBitstreams();
        Collection[] col = item.getCollections();
        Community[] com = item.getCommunities();
        for (Bundle b : bun) {
//            this.bundles.add(includeFull ? new BundleEntity(b, level, uparams) : new BundleEntityId(b));
        }
        for (Bitstream b : bst) {
//            this.bitstreams.add(includeFull ? new BitstreamEntity(b, level, uparams) : new BitstreamEntityId(b));
        }
        for (Collection c : col) {
//            this.collections.add(includeFull ? new CollectionEntity(c, level, uparams) : new CollectionEntityId(c));
        }
        for (Community c : com) {
//            this.communities.add(includeFull ? new CommunityEntity(c, level, uparams) : new CommunityEntityId(c));
        }

        DCValue[] dcValues = item.getMetadata(Item.ANY, Item.ANY, Item.ANY, Item.ANY);
        for (DCValue dcValue : dcValues) {
            this.metadata.add(new MetadataEntity(dcValue));
        }

    }

    public ItemEntity() {
    }

    public ItemEntity(String uid, Context context) {
        try {
            Item res = Item.find(context, Integer.parseInt(uid));
            // Check authorisation
            AuthorizeManager.authorizeAction(context, res, Constants.READ);

//        this.id = res.getID();
        } catch (NumberFormatException ex) {
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }

    }

    public List getMetadata() {
        return this.metadata;
    }

    public boolean getIsArchived() {
        return this.isArchived;
    }

    public boolean getIsWithdrawn() {
        return this.isWithdrawn;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public String getName() {
        return this.name;
    }

    public String getHandle() {
        return this.handle;
    }

    public void setCountItems(int countItems) {
        this.countItems = countItems;
    }

    public void setComments(List<Object> comments) {
        this.comments = comments;
    }

    public Object addBundle(EntityReference ref, Map<String, Object> inputVar, Context context) {
        if (inputVar.containsKey("id")) {
            try {
                Item item = Item.find(context, Integer.parseInt(ref.getId().toString()));
                Bundle bundle = Bundle.find(context, Integer.parseInt(inputVar.get("id").toString()));
                if ((item != null) && (item != null)) {
                    item.addBundle(bundle);
                    return bundle.getID();
                } else {
                    throw new EntityException("Not found", "Entity not found", 404);
                }
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            } catch (AuthorizeException ex) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }
        } else {
            throw new EntityException("Bad request", "Value not included", 400);
        }
    }

    public String createBundle(EntityReference ref, Map<String, Object> inputVar, Context context) {
        String result = "";
        String id = "";
        String name = "";
        try {
            id = (String) inputVar.get("id");
            name = (String) inputVar.get("name");
        } catch (NullPointerException ex) {
            throw new EntityException("Bad request", "Value not included", 400);
        }

        try {
            Item item = Item.find(context, Integer.parseInt(id));
            if (item != null) {
                //Bundle bundle = item.createBundle(name);
                //bundle.setName(name);
                //item.update();
                //result = Integer.toString(bundle.getID());
            } else {
                throw new EntityException("Internal server error", "Could not create subcommunity", 500);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
            //}        catch (AuthorizeException ex) {
            //throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String addMetadata(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            Integer id = Integer.parseInt((String) inputVar.get("id"));
            Item item = Item.find(context, id);

            AuthorizeManager.authorizeAction(context, item, Constants.WRITE);

            Integer fieldID = Integer.parseInt((String) inputVar.get("fieldID"));
            String value = (String) inputVar.get("value");
            String lang = (String) inputVar.get("lang");

            MetadataField field = MetadataField.find(context, Integer.valueOf(fieldID));
            MetadataSchema schema = MetadataSchema.find(context, field.getSchemaID());

            item.addMetadata(schema.getName(), field.getElement(), field.getQualifier(), lang, value);

            item.update();
            context.complete();
            return String.valueOf(item.getID());

        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String editMetadata(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            Integer id = Integer.parseInt((String) inputVar.get("id"));
            Item item = Item.find(context, id);

            AuthorizeManager.authorizeAction(context, item, Constants.WRITE);
            item.clearMetadata(Item.ANY, Item.ANY, Item.ANY, Item.ANY);
            List<Map> fieldList = new ArrayList<Map>();
            Object o = inputVar.get("metadata");
            if (o instanceof Map) {
                Map map = (Map) o;
                fieldList.add((Map) map.get("field"));
            } else if (o instanceof Vector) {
                Vector vector = (Vector) o;
                for (int i = 0; i < vector.size(); i++) {
                    fieldList.add((Map) vector.get(i));
                }
            }

            for (Map fieldMap : fieldList) {
                String name = (String) fieldMap.get("name");
                String value = (String) fieldMap.get("value");
                String authority = (String) fieldMap.get("authority");
                String confidence = (String) fieldMap.get("confidence");
                String lang = (String) fieldMap.get("lang");

                // get the field's name broken up
                String[] parts = parseName(name);
                // probe for a confidence value
                int iconf = Choices.CF_UNSET;
                if (confidence != null && confidence.length() > 0) {
                    iconf = Choices.getConfidenceValue(confidence);
                }
                // upgrade to a minimum of NOVALUE if there IS an authority key
                if (authority != null && authority.length() > 0 && iconf == Choices.CF_UNSET) {
                    iconf = Choices.CF_NOVALUE;
                }
                item.addMetadata(parts[0], parts[1], parts[2], lang, value, authority, iconf);
            }

            item.update();
            context.complete();
            return String.valueOf(item.getID());

        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removeMetadata(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer id = Integer.parseInt((String) inputVar.get("id"));
            Item item = Item.find(context, id);

            AuthorizeManager.authorizeAction(context, item, Constants.WRITE);

            Integer eid = Integer.parseInt((String) inputVar.get("eid"));
            MetadataValue metadataValue = MetadataValue.find(context, eid);
            if (metadataValue != null && metadataValue.getItemId() == id) {
                metadataValue.delete(context);
            } else {
                throw new EntityException("Internal server error", "No such metadata value or not belongs to same item", 500);
            }

        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException ie) {
            throw new EntityException("Internal server error", "SQL error, cannot remove metadata value", 500);
        }
    }

    private static String[] parseName(String name) {
        String[] parts = new String[3];

        String[] split = name.split("\\.");
        if (split.length == 2) {
            parts[0] = split[0];
            parts[1] = split[1];
            parts[2] = null;
        } else if (split.length == 3) {
            parts[0] = split[0];
            parts[1] = split[1];
            parts[2] = split[2];
        } else {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
        return parts;
    }

}
