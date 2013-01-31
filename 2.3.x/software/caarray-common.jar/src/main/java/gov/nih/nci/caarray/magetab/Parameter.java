//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import java.io.Serializable;

/**
 * A <code>Parameter</code> is a replaceable value in a <code>Protocol</code>.
 * Examples of Parameters include: scanning wavelength, laser power,
 * centrifuge speed, multiplicative errors, the number of input nodes
 * to a SOM, and PCR temperatures.
 */
public class Parameter implements Serializable {

    private static final long serialVersionUID = -2934407479314317380L;

    private String name;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
