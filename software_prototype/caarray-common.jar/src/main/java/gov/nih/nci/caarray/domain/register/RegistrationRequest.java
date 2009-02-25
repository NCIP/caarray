/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
