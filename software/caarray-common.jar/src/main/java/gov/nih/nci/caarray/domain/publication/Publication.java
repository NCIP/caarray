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

package gov.nih.nci.caarray.domain.publication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.vocabulary.Term;

/**
 *
 */
@Entity
public class Publication extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String authors;
    private String doi;
    private String editor;
    private String issue;
    private String pages;
    private String publisher;
    private String pubMedId;
    private String title;
    private String uri;
    private String volume;
    private String year;
    private Experiment experiment;
    private Term status;
    private Term type;


    /**
     * Gets the authors.
     *
     * @return the authors
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getAuthors() {
        return authors;
    }

    /**
     * Sets the authors.
     *
     * @param authorsVal the authors
     */
    public void setAuthors(final String authorsVal) {
        this.authors = authorsVal;
    }

    /**
     * Gets the doi.
     *
     * @return the doi
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getDoi() {
        return doi;
    }

    /**
     * Sets the doi.
     *
     * @param doiVal the doi
     */
    public void setDoi(final String doiVal) {
        this.doi = doiVal;
    }

    /**
     * Gets the pubMedId.
     *
     * @return the pubMedId
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getPubMedId() {
        return pubMedId;
    }

    /**
     * Sets the pubMedId.
     *
     * @param pubMedIdVal the pubMedId
     */
    public void setPubMedId(final String pubMedIdVal) {
        this.pubMedId = pubMedIdVal;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "PUBLICATION_STATUS_FK")
    public Term getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param statusVal the status
     */
    public void setStatus(final Term statusVal) {
        this.status = statusVal;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "PUBLICATION_TYPE_FK")
    public Term getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param typeVal the type
     */
    public void setType(final Term typeVal) {
        this.type = typeVal;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param titleVal the title
     */
    public void setTitle(final String titleVal) {
        this.title = titleVal;
    }

    /**
     * @return the experiment
     */
    @ManyToOne
    @ForeignKey(name = "PUBLICATION_EXPR")
    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the editor
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public final String getEditor() {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public final void setEditor(String editor) {
        this.editor = editor;
    }

    /**
     * @return the issue
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public final String getIssue() {
        return issue;
    }

    /**
     * @param issue the issue to set
     */
    public final void setIssue(String issue) {
        this.issue = issue;
    }

    /**
     * @return the pages
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public final String getPages() {
        return pages;
    }

    /**
     * @param pages the pages to set
     */
    public final void setPages(String pages) {
        this.pages = pages;
    }

    /**
     * @return the publisher
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public final String getPublisher() {
        return publisher;
    }

    /**
     * @param publisher the publisher to set
     */
    public final void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return the uri
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public final String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public final void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the volume
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public final String getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public final void setVolume(String volume) {
        this.volume = volume;
    }

    /**
     * @return the year
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public final String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public final void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public final String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public final void setName(String name) {
        this.name = name;
    }
}
