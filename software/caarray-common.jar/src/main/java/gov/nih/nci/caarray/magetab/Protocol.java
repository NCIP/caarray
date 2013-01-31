//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Protocol is a parameterizable description of a method.
 */
public final class Protocol implements Serializable, TermSourceable {

    private static final long serialVersionUID = 3130057952908619310L;

    private String name;
    private OntologyTerm type;
    private String description;
    private final List<Parameter> parameters = new ArrayList<Parameter>();
    private String hardware;
    private String software;
    private String contact;
    private TermSource termSource;

    /**
     * @return the contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the hardware
     */
    public String getHardware() {
        return hardware;
    }

    /**
     * @param hardware the hardware to set
     */
    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

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

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @return the software
     */
    public String getSoftware() {
        return software;
    }

    /**
     * @param software the software to set
     */
    public void setSoftware(String software) {
        this.software = software;
    }

    /**
     * @return the termSource
     */
    public TermSource getTermSource() {
        return termSource;
    }

    /**
     * @param termSource the termSource to set
     */
    public void setTermSource(TermSource termSource) {
        this.termSource = termSource;
    }

    /**
     * @return the type
     */
    public OntologyTerm getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(OntologyTerm type) {
        this.type = type;
    }

}
