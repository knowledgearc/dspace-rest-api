/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.Bundle;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

public class BundleEntity extends BundleEntityId {

    @EntityFieldRequired
    private String name;
    private String handle;
    private int pid;
//    List<Object> bitstreams = new ArrayList<Object>();
//    List<Object> items = new ArrayList<Object>();

    public BundleEntity() {
    }

    public BundleEntity(String uid, Context context) {
        super(uid, context);
        this.pid = res.getPrimaryBitstreamID();
        this.handle = res.getHandle();
        this.name = res.getName();
    }

    public BundleEntity(Bundle bundle) {
        super(bundle);
        this.handle = bundle.getHandle();
        this.name = bundle.getName();
        this.pid = bundle.getPrimaryBitstreamID();
    }

    public int getPrimaryBitstreamId() {
        return this.pid;
    }

    public String getName() {
        return this.name;
    }

    public String getHandle() {
        return this.handle;
    }
}