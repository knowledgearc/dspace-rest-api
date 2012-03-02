/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.apache.log4j.Logger;
import org.dspace.content.DCValue;
import org.dspace.core.Context;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;

/**
 * @author Lewis
 */
public class MetadataFieldEntity {

    @EntityId
    private int fieldID;
    @EntityFieldRequired
    private String name;


    /** log4j category */
    private static final Logger log = Logger.getLogger(MetadataFieldEntity.class);


    public MetadataFieldEntity(String uid, Context context, int level, UserRequestParams uparams) throws SQLException
    {
    }

    public MetadataFieldEntity(int fieldID, String name)
    {
        this.fieldID = fieldID;
        this.name = name;
    }

    public MetadataFieldEntity()
    {

    }

    public int getFieldID() {
        return fieldID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "fieldID:" + this.fieldID + ", stuff.....";
    }

}
