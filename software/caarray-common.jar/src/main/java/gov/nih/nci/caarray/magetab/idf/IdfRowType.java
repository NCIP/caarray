/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.magetab.idf;

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration of legal row headings in an IDF document.
 */
public enum IdfRowType {
    /**
     * Investigation Title.
     */
    INVESTIGATION_TITLE ("Investigation Title"),

    /**
     * Experimental Design.
     */
    EXPERIMENTAL_DESIGN ("Experimental Design"),

    /**
     * Experimental Design Term Source REF.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    EXPERIMENTAL_DESIGN_TERM_SOURCE_REF ("Experimental Design Term Source REF"),
    /**
     * Experimental Factor Name.
     */
    EXPERIMENTAL_FACTOR_NAME ("Experimental Factor Name"),

    /**
     * Experimental Factor Type.
     */
    EXPERIMENTAL_FACTOR_TYPE ("Experimental Factor Type"),

    /**
     * Experimental Factor Term Source REF.
     */
    EXPERIMENTAL_FACTOR_TERM_SOURCE_REF ("Experimental Factor Term Source REF"),

    /**
     * Experimental Factor Type Term Source REF.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    EXPERIMENTAL_FACTOR_TYPE_TERM_SOURCE_REF ("Experimental Factor Type Term Source REF"),

    /**
     * Person Last Name.
     */
    PERSON_LAST_NAME ("Person Last Name"),

    /**
     * Person Last Names. (accepting a variation)
     */
    PERSON_LAST_NAMES ("Person Last Names"),

    /**
     * Person First Name.
     */
    PERSON_FIRST_NAME ("Person First Name"),


    /**
     * Person First Names. (accepting a variation)
     */
    PERSON_FIRST_NAMES ("Person First Names"),

    /**
     * Person Mid Initials.
     */
    PERSON_MID_INITIALS ("Person Mid Initials"),

    /**
     * Person Mid Initial. (accepting a variation)
     */
    PERSON_MID_INITIAL ("Person Mid Initial"),

    /**
     * Person Email.
     */
    PERSON_EMAIL ("Person Email"),

    /**
     * Person Phone.
     */
    PERSON_PHONE ("Person Phone"),

    /**
     * Person Fax.
     */
    PERSON_FAX ("Person Fax"),

    /**
     * Person Address.
     */
    PERSON_ADDRESS ("Person Address"),

    /**
     * Person Affiliation.
     */
    PERSON_AFFILIATION ("Person Affiliation"),

    /**
     * Person Roles.
     */
    PERSON_ROLES ("Person Roles"),

    /**
     * Person Roles Term Source REF.
     */
    PERSON_ROLES_TERM_SOURCE_REF ("Person Roles Term Source REF"),

    /**
     * Quality Control Type.
     */
    QUALITY_CONTROL_TYPE ("Quality Control Type"),


    /**
     * Quality Control Types.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    QUALITY_CONTROL_TYPES ("Quality Control Types"),

    /**
     * Quality Control Term Source REF.
     */
    QUALITY_CONTROL_TERM_SOURCE_REF ("Quality Control Term Source REF"),

    /**
     * Quality Control Types Term Source REF.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    QUALITY_CONTROL_TYPES_TERM_SOURCE_REF ("Quality Control Types Term Source REF"),

    /**
     * Replicate Type.
     */
    REPLICATE_TYPE ("Replicate Type"),

    /**
     * Replicate Term Source REF.
     */
    REPLICATE_TERM_SOURCE_REF ("Replicate Term Source REF"),

    /**
     * Replicate Type Term Source REF.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    REPLICATE_TYPE_TERM_SOURCE_REF ("Replicate Type Term Source REF"),

    /**
     * Normalization Type.
     */
    NORMALIZATION_TYPE ("Normalization Type"),

    /**
     * Normalization Term Source REF.
     */
    NORMALIZATION_TERM_SOURCE_REF ("Normalization Term Source REF"),

    /**
     * Date of Experiment.
     */
    DATE_OF_EXPERIMENT ("Date of Experiment"),

    /**
     * Public Release Date.
     */
    PUBLIC_RELEASE_DATE ("Public Release Date"),

    /**
     * PubMed ID.
     */
    PUBMED_ID ("PubMed ID"),

    /**
     * Publication DOI.
     */
    PUBLICATION_DOI ("Publication DOI"),

    /**
     * Publication Author List.
     */
    PUBLICATION_AUTHOR_LIST ("Publication Author List"),

    /**
     * Publication Title.
     */
    PUBLICATION_TITLE ("Publication Title"),

    /**
     * Publication Status.
     */
    PUBLICATION_STATUS ("Publication Status"),

    /**
     * Publication Status Term Source REF.
     */
    PUBLICATION_STATUS_TERM_SOURCE_REF ("Publication Status Term Source REF"),

    /**
     * Experiment Description.
     */
    EXPERIMENT_DESCRIPTION ("Experiment Description"),

    /**
     * Protocol Name.
     */
    PROTOCOL_NAME ("Protocol Name"),

    /**
     * Protocol Type.
     */
    PROTOCOL_TYPE ("Protocol Type"),

    /**
     * Protocol Description.
     */
    PROTOCOL_DESCRIPTION ("Protocol Description"),

    /**
     * Protocol Parameters.
     */
    PROTOCOL_PARAMETERS ("Protocol Parameters"),

    /**
     * Protocol Hardware.
     */
    PROTOCOL_HARDWARE ("Protocol Hardware"),

    /**
     * Protocol Software.
     */
    PROTOCOL_SOFTWARE ("Protocol Software"),

    /**
     * Protocol Contact.
     */
    PROTOCOL_CONTACT ("Protocol Contact"),

    /**
     * Protocol Term Source REF.
     */
    PROTOCOL_TERM_SOURCE_REF ("Protocol Term Source REF"),

    /**
     * SDRF File.
     */
    SDRF_FILE ("SDRF File"),

    /**
     * SDRF Files.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    SDRF_FILES ("SDRF Files"),

    /**
     * Term Source Name.
     */
    TERM_SOURCE_NAME ("Term Source Name"),

    /**
     * Term Source File.
     */
    TERM_SOURCE_FILE ("Term Source File"),

    /**
     * Term Source Version.
     */
    TERM_SOURCE_VERSION ("Term Source Version"),

    /**
     * Comment.
     */
    COMMENT ("Comment"),

    /**
     * Catchall placeholder for row headings that did not match any of the above.
     */
    INVALID_TYPE ("Invalid Type");

    private final String displayName;

    static final IdfRowType[] TERM_SOURCE_TYPES = {TERM_SOURCE_FILE, TERM_SOURCE_NAME, TERM_SOURCE_VERSION };

    /**
     * An IDF row header with the specified display name.
     *
     * @param displayName the actual name of the row that will be displayed.
     */
    IdfRowType(String displayName) {
        this.displayName = displayName;
    }

    static IdfRowType get(String name) {
        String enumName = StringUtils.replaceChars(name, ' ', '_').toUpperCase();
        try {
            return valueOf(enumName);
        } catch (IllegalArgumentException e) {
            return INVALID_TYPE;
        }
    }

    /**
     * Returns the display name of the row.
     *
     * @return the display name of the row.
     */
    public String toString() {
        return displayName;
    }
}
