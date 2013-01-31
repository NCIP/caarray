//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

import java.io.Serializable;

/**
 * The provider of a biological source.
 */
public final class Provider implements Serializable,HasAttribute {

    private static final long serialVersionUID = 7422539362935439627L;

    private String _name;

    
    public Provider(String name){
    	name = name;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this._name = name;
    }

}
