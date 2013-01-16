//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.register;

import gov.nih.nci.caarray.domain.country.Country;
import gov.nih.nci.caarray.domain.state.State;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * @author John Hedden
 * @author Akhil Bhaskar (Amentra, Inc.)
 *
 */
@Entity
@org.hibernate.annotations.Entity(mutable = false)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@SuppressWarnings("PMD.TooManyFields")
public class RegistrationRequest implements PersistentObject {

    private static final long serialVersionUID = -9198769730580475043L;

    private static final int LOGIN_NAME_FIELD_LENGTH = 30;
    private static final int FIRST_NAME_FIELD_LENGTH = 30;
    private static final int MIDDLE_INITIAL_FIELD_LENGTH = 1;
    private static final int LAST_NAME_FIELD_LENGTH = 50;
    private static final int EMAIL_FIELD_LENGTH = 50;
    private static final int PHONE_NUMBER_FIELD_LENGTH = 25;
    private static final int FAX_FIELD_LENGTH = 25;
    private static final int ORGANIZATION_FIELD_LENGTH = 200;
    private static final int ADDRESS1_FIELD_LENGTH = 200;
    private static final int ADDRESS2_FIELD_LENGTH = 200;
    private static final int CITY_FIELD_LENGTH = 50;
    private static final int PROVINCE_FIELD_LENGTH = 50;
    private static final int ZIP_FIELD_LENGTH = 10;
    private static final int ROLE_FIELD_LENGTH = 200;

    private String loginName;
    private String firstName;
    private String middleInitial;
    private String lastName;
    private String email;
    private String phone;
    private String fax;
    private String organization;
    private String address1;
    private String address2;
    private String city;
    private State state;
    private String province;
    private Country country;
    private String zip;
    private String role;

    private Long id;

    /**
     * @return database identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    @SuppressWarnings({"PMD.UnusedPrivateMethod", "unused" })
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the loginName
     */
    @Column(length = LOGIN_NAME_FIELD_LENGTH)
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the firstName
     */
     @Column(length = FIRST_NAME_FIELD_LENGTH)
     @NotNull
     @Length(min = 1, max = FIRST_NAME_FIELD_LENGTH)
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the middleInitial
     */
    @Column(length = MIDDLE_INITIAL_FIELD_LENGTH)
    @Length(max = 1)
    public String getMiddleInitial() {
        return middleInitial;
    }

    /**
     * @param middleInitial the middleInitial to set
     */
    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    /**
     * @return the lastName
     */
    @Column(length = LAST_NAME_FIELD_LENGTH)
    @NotNull
    @Length(min = 1, max = LAST_NAME_FIELD_LENGTH)
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the emaiId
     */
    @Column(length = EMAIL_FIELD_LENGTH)
    @NotNull
    @Email
    @Length(min = 1, max = EMAIL_FIELD_LENGTH)
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phone
     */
    @Column(length = PHONE_NUMBER_FIELD_LENGTH)
    @NotNull
    @Length(min = 1, max = PHONE_NUMBER_FIELD_LENGTH)
    @Pattern(regex = "(\\+)?([-\\._\\(\\) ]?[\\d]{3,20}[-\\._\\(\\) ]?){2,10}")
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the fax
     */
    @Column(length = FAX_FIELD_LENGTH)
    @Length(max = FAX_FIELD_LENGTH)
    @Pattern(regex = "(\\+)?([-\\._\\(\\) ]?[\\d]{3,20}[-\\._\\(\\) ]?){2,10}||^$")
    public String getFax() {
        return fax;
    }

    /**
     * @param fax the fax to set
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return the organization
     */
    @Column(length = ORGANIZATION_FIELD_LENGTH)
    @NotNull
    @Length(min = 1, max = ORGANIZATION_FIELD_LENGTH)
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @return the address1
     */
    @Column(length = ADDRESS1_FIELD_LENGTH)
    @NotNull
    @Length(min = 1, max = ADDRESS1_FIELD_LENGTH)
    public String getAddress1() {
        return address1;
    }

    /**
     * @param address1 the address1 to set
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * @return the address2
     */
    @Column(length = ADDRESS2_FIELD_LENGTH)
    @Length(max = ADDRESS2_FIELD_LENGTH)
    public String getAddress2() {
        return address2;
    }

    /**
     * @param address2 the address2 to set
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * @return the city
     */
    @Column(length = CITY_FIELD_LENGTH)
    @NotNull
    @Length(min = 1, max = CITY_FIELD_LENGTH)
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the state
     */
    @ManyToOne
    @ForeignKey(name = "registrationrequest_state_fk")
    public State getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * @return the province
     */
    @Column(length = PROVINCE_FIELD_LENGTH)
    @Length(max = PROVINCE_FIELD_LENGTH)
    public String getProvince() {
        return province;
    }

    /**
     * @param province the province to set
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * @return the zip
     */
    @Column(length = ZIP_FIELD_LENGTH)
    @NotNull
    @Length(min = 1, max = ZIP_FIELD_LENGTH)
    public String getZip() {
        return zip;
    }

    /**
     * @param zip the zip to set
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * @return the role
     */
    @Column(length = ROLE_FIELD_LENGTH)
    @NotNull
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the country
     */
    @ManyToOne
    @NotNull
    @ForeignKey(name = "registrationrequest_country_fk")
    public Country getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(Country country) {
        this.country = country;
    }
}
