//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.country;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author John Hedden
 *
 */
@Entity
@org.hibernate.annotations.Entity(mutable = false)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Country extends AbstractCaArrayEntity implements Comparable<Country> {

    private static final long serialVersionUID = 3434506314749437341L;
    private static final String PREFERRED_COUNTRY = "UNITED STATES";

    private String code;
    private String name;
    private String printableName;
    private String iso3;
    private String numcode;

    /**
     * @return the code
     */
    @Column(unique = true)
    public String getCode() {
        return code;
    }
    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
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
     * @return the printable_name
     */
    public String getPrintableName() {
        return printableName;
    }
    /**
     * @param printableName the printable_name to set
     */
    public void setPrintableName(String printableName) {
        this.printableName = printableName;
    }
    /**
     * @return the iso3
     */
    @Column(unique = true)
    public String getIso3() {
        return iso3;
    }
    /**
     * @param iso3 the iso3 to set
     */
    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }
    /**
     * @return the numcode
     */
    public String getNumcode() {
        return numcode;
    }
    /**
     * @param numcode the numcode to set
     */
    public void setNumcode(String numcode) {
        this.numcode = numcode;
    }

    /**
     * Compares countries by name, putting a preferred country ahead of all others.
     * @param o other country to compare to
     * @return result of comparison
     */
    public int compareTo(Country o) {
        if (this.name.equals(o.name)) {
            return 0;
        }

        if (this.name.equals(PREFERRED_COUNTRY)) {
            return -1;
        }

        if (o.name.equals(PREFERRED_COUNTRY)) {
            return 1;
        }

        return this.name.compareToIgnoreCase(o.name);
    }
}
