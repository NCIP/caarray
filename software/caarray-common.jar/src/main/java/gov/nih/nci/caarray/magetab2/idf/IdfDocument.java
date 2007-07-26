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
package gov.nih.nci.caarray.magetab2.idf;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nih.nci.caarray.magetab2.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab2.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab2.MageTabParsingException;
import gov.nih.nci.caarray.magetab2.OntologyTerm;
import gov.nih.nci.caarray.magetab2.Parameter;
import gov.nih.nci.caarray.magetab2.Protocol;
import gov.nih.nci.caarray.magetab2.TermSource;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;

/**
 * Represents an Investigation Description Format (IDF) file - a tab-delimited file
 * providing general information about the investigation, including its name,
 * a brief description, the investigator’s contact details, bibliographic references,
 * and free text descriptions of the protocols used in the investigation.
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // warning suppressed due to long switch statement
public final class IdfDocument extends AbstractMageTabDocument {

    private static final long serialVersionUID = 149154919398572572L;
    private static final Log LOG = LogFactory.getLog(IdfDocument.class);

    private final Investigation investigation = new Investigation();
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final List<TermSource> docTermSources = new ArrayList<TermSource>();

    /**
     * Creates a new IDF from an existing file.
     *
     * @param documentSet the MAGE-TAB document set the IDF belongs to.
     * @param file the file containing the IDF content.
     */
    public IdfDocument(MageTabDocumentSet documentSet, File file) {
        super(documentSet, file);
    }

    /**
     * @return the investigation
     */
    public Investigation getInvestigation() {
        return investigation;
    }

    /**
     * Parses the MAGE-TAB document, creating the object graph of entities.
     *
     * @throws MageTabParsingException if the document couldn't be read.
     */
    @Override
    protected void parse() throws MageTabParsingException {
        DelimitedFileReader tabDelimitedReader = createTabDelimitedReader();
        while (tabDelimitedReader.hasNextLine()) {
            handleLine(tabDelimitedReader.nextLine());
        }
        updateTermSourceRefs();
    }

    private void handleLine(List<String> lineContents) {
        if (!isEmpty(lineContents)) {
            IdfRowHeading rowHeading = IdfRowHeading.get(lineContents.get(0));
            for (int columnIndex = 1; columnIndex < lineContents.size(); columnIndex++) {
                int valueIndex = columnIndex - 1;
                String value = lineContents.get(columnIndex);
                if (!isEmpty(value)) {
                    handleValue(rowHeading, value, valueIndex);
                }
            }
        }
    }

    private boolean isEmpty(String value) {
        return value == null || "".equals(value.trim());
    }

    private boolean isEmpty(List<String> lineContents) {
        return lineContents.isEmpty() || "".equals(lineContents.get(0));
    }

    @SuppressWarnings("PMD") // warnings suppressed due to long switch statement
    private void handleValue(IdfRowHeading rowHeading, String value, int valueIndex) {
        switch (rowHeading) {
        case INVESTIGATION_TITLE:
            handleTitle(value);
            break;
        case EXPERIMENTAL_DESIGN:
            handleExperimentalDesign(value);
            break;
        case EXPERIMENTAL_FACTOR_NAME:
            handleExperimentalFactorName(value, valueIndex);
            break;
        case EXPERIMENTAL_FACTOR_TYPE:
            handleExperimentalFactorType(value, valueIndex);
            break;
        case EXPERIMENTAL_FACTOR_TERM_SOURCE_REF:
            handleExperimentalFactorTermSourceRef(value, valueIndex);
            break;
        case PERSON_LAST_NAME:
            handlePersonLastName(value, valueIndex);
            break;
        case PERSON_FIRST_NAME:
            handlePersonFirstName(value, valueIndex);
            break;
        case PERSON_MID_INITIALS:
            handlePersonMidInit(value, valueIndex);
            break;
        case PERSON_EMAIL:
            handlePersonEmail(value, valueIndex);
            break;
        case PERSON_PHONE:
            handlePersonPhone(value, valueIndex);
            break;
        case PERSON_FAX:
            handlePersonFax(value, valueIndex);
            break;
        case PERSON_ADDRESS:
            handlePersonAddress(value, valueIndex);
            break;
        case PERSON_AFFILIATION:
            handlePersonAffiliation(value, valueIndex);
            break;
        case PERSON_ROLES:
            handlePersonRole(value, valueIndex);
            break;
        case PERSON_ROLES_TERM_SOURCE_REF:
            handlePersonRoleTermSourceRef(value, valueIndex);
            break;
        case QUALITY_CONTROL_TYPE:
            handleQualityControlType(value);
            break;
        case QUALITY_CONTROL_TERM_SOURCE_REF:
            handleQualityControlTermSourceRef(value, valueIndex);
            break;
        case REPLICATE_TYPE:
            handleReplicateType(value);
            break;
        case REPLICATE_TERM_SOURCE_REF:
            handleReplicateTypeTermSourceRef(value, valueIndex);
            break;
        case NORMALIZATION_TYPE:
            handleNormalizationType(value);
            break;
        case NORMALIZATION_TERM_SOURCE_REF:
            handleNormalizationTypeTermSourceRef(value, valueIndex);
            break;
        case DATE_OF_EXPERIMENT:
            handleExperimentDate(value);
            break;
        case PUBLIC_RELEASE_DATE:
            handlePublicReleaseDate(value);
            break;
        case PUBMED_ID:
            handlePubMedId(value, valueIndex);
            break;
        case PUBLICATION_DOI:
            handlePublicationDoi(value, valueIndex);
            break;
        case PUBLICATION_AUTHOR_LIST:
            handlePublicationAuthorList(value, valueIndex);
            break;
        case PUBLICATION_TITLE:
            handlePublicationTitle(value, valueIndex);
            break;
        case PUBLICATION_STATUS:
            handlePublicationStatus(value, valueIndex);
            break;
        case PUBLICATION_STATUS_TERM_SOURCE_REF:
            handlePublicationStatusTermSourceRef(value, valueIndex);
            break;
        case EXPERIMENT_DESCRIPTION:
            handleExperimentDescription(value);
            break;
        case PROTOCOL_NAME:
            handleProtocolName(value, valueIndex);
            break;
        case PROTOCOL_TYPE:
            handleProtocolType(value, valueIndex);
            break;
        case PROTOCOL_DESCRIPTION:
            handleProtocolDescription(value, valueIndex);
            break;
        case PROTOCOL_PARAMETERS:
            handleProtocolParameters(value, valueIndex);
            break;
        case PROTOCOL_HARDWARE:
            handleProtocolHardware(value, valueIndex);
            break;
        case PROTOCOL_SOFTWARE:
            handleProtocolSoftware(value, valueIndex);
            break;
        case PROTOCOL_CONTACT:
            handleProtocolContact(value, valueIndex);
            break;
        case PROTOCOL_TERM_SOURCE_REF:
            handleProtocolTermSourceRef(value, valueIndex);
            break;
        case SDRF_FILE:
           // TODO handleSdrfFile(value, valueIndex);
            break;
        case TERM_SOURCE_NAME:
            handleTermSourceName(value, valueIndex);
            break;
        case TERM_SOURCE_FILE:
            handleTermSourceFile(value, valueIndex);
            break;
        case TERM_SOURCE_VERSION:
            handleTermSourceVersion(value, valueIndex);
            break;
        default:
            throw new IllegalArgumentException("Unknown IdfRowHeading " + rowHeading);
        }
    }


    private void handleProtocolName(String protocolId, int valueIndex) {
        Protocol protocol = investigation.getOrCreateProtcol(valueIndex);
        protocol.setName(protocolId);
        addProtocol(protocol);
    }

    private void handleProtocolDescription(String value, int valueIndex) {
        Protocol protocol = investigation.getOrCreateProtcol(valueIndex);
        protocol.setDescription(value);
    }

    private void handleProtocolParameters(String value, int valueIndex) {
        Protocol protocol = investigation.getOrCreateProtcol(valueIndex);
        String[] parameterNames = value.split(";");
        for (int i = 0; i < parameterNames.length; i++) {
            Parameter parameter = new Parameter();
            parameter.setName(parameterNames[i]);
            protocol.getParameters().add(parameter);
        }
    }

    private void handleProtocolHardware(String value, int valueIndex) {
        Protocol protocol = investigation.getOrCreateProtcol(valueIndex);
        protocol.setHardware(value);
    }

    private void handleProtocolSoftware(String value, int valueIndex) {
        Protocol protocol = investigation.getOrCreateProtcol(valueIndex);
        protocol.setSoftware(value);
    }

    private void handleProtocolContact(String value, int valueIndex) {
        Protocol protocol = investigation.getOrCreateProtcol(valueIndex);
        protocol.setContact(value);
    }

    private void handleProtocolType(String value, int valueIndex) {
        Protocol protocol = investigation.getOrCreateProtcol(valueIndex);
        protocol.setType(getMgedOntologyTerm(IdfOntologyCategory.PROTOCOL_TYPE, value));
    }


    private void handleProtocolTermSourceRef(String value, int valueIndex) {
        investigation.getProtocols().get(valueIndex).getType().setTermSource(getTermSource(value));

    }

    private void handleTitle(String title) {
        investigation.setTitle(title);
    }

    private void handleExperimentDescription(String description) {
        investigation.setDescription(description);
    }

    private void handleExperimentDate(String dateString) {
        try {
            investigation.setDateOfExperiment(format.parse(dateString));
        } catch (ParseException pe) {
            LOG.error("Error parsing experiment date", pe);
        }
    }

    private void handlePublicReleaseDate(String dateString) {
        try {
            investigation.setPublicReleaseDate(format.parse(dateString));
        } catch (ParseException pe) {
            LOG.error("Error parsing PubRelease Date", pe);
        }
    }

    private void handleExperimentalDesign(String value) {
        investigation.getDesigns().add(getMgedOntologyTerm(IdfOntologyCategory.EXPERIMENTAL_DESIGN_TYPE, value));
    }

    private void handleExperimentalFactorName(String value, int valueIndex) {
        investigation.getOrCreateFactor(valueIndex).setName(value);
    }

    private void handleExperimentalFactorType(String value, int valueIndex) {
        ExperimentalFactor factor = investigation.getOrCreateFactor(valueIndex);
        factor.setType(getMgedOntologyTerm(IdfOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY, value));
    }

    private void handleExperimentalFactorTermSourceRef(String value, int valueIndex) {
        ExperimentalFactor factor = investigation.getFactors().get(valueIndex);
        factor.getType().setTermSource(getTermSource(value));
    }

    private OntologyTerm getMgedOntologyTerm(IdfOntologyCategory category, String value) {
        OntologyTerm term = getOntologyTerm(category.getCategoryName(), value);
        term.setTermSource(getTermSource("MO"));
        return term;
    }

    private void handlePersonLastName(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).setLastName(value);
    }

    private void handlePersonFirstName(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).setFirstName(value);
    }

    private void handlePersonMidInit(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).setMidInitials(value);
    }

    private void handlePersonEmail(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).setEmail(value);
    }

    private void handlePersonPhone(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).setPhone(value);
    }

    private void handlePersonFax(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).setFax(value);
    }

    private void handlePersonAddress(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).setAddress(value);
    }

    private void handlePersonAffiliation(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).setAffiliation(value);
    }

    private void handlePersonRole(String value, int valueIndex) {
        investigation.getOrCreatePerson(valueIndex).getRoles()
                .add(getMgedOntologyTerm(IdfOntologyCategory.PERSON_ROLE, value));
    }

    private void handlePersonRoleTermSourceRef(String value, int valueIndex) {
        Person person = investigation.getPersons().get(valueIndex);
        person.getRoles().get(valueIndex).setTermSource(getTermSource(value));
    }

    private void handlePubMedId(String value, int valueIndex) {
        investigation.getOrCreatePublication(valueIndex).setPubMedId(value);
    }

    private void handlePublicationDoi(String value, int valueIndex) {
        investigation.getOrCreatePublication(valueIndex).setDoi(value);
    }

    private void handlePublicationAuthorList(String value, int valueIndex) {
        investigation.getOrCreatePublication(valueIndex).setAuthorList(value);
    }

    private void handlePublicationTitle(String value, int valueIndex) {
        investigation.getOrCreatePublication(valueIndex).setTitle(value);
    }

    private void handlePublicationStatus(String value, int valueIndex) {
        investigation.getOrCreatePublication(valueIndex)
            .setStatus(getMgedOntologyTerm(IdfOntologyCategory.PUBLICATION_STATUS, value));
    }

    private void handlePublicationStatusTermSourceRef(String value, int valueIndex) {
        investigation.getOrCreatePublication(valueIndex).getStatus().setTermSource(getTermSource(value));
    }

    private void handleQualityControlType(String value) {
        investigation.getQualityControlTypes().add(
                getMgedOntologyTerm(IdfOntologyCategory.QUALITY_CONTROL_TYPE, value));
    }

    private void handleQualityControlTermSourceRef(String value, int valueIndex) {
        investigation.getQualityControlTypes().get(valueIndex).setTermSource(getTermSource(value));
    }

    private void handleReplicateType(String value) {
        investigation.getReplicateTypes().add(getMgedOntologyTerm(IdfOntologyCategory.REPLICATE_TYPE, value));
    }

    private void handleReplicateTypeTermSourceRef(String value, int valueIndex) {
        investigation.getReplicateTypes().get(valueIndex).setTermSource(getTermSource(value));
    }

    private void handleNormalizationType(String value) {
        investigation.getNormalizationTypes().add(getMgedOntologyTerm(IdfOntologyCategory.NORMALIZATION_TYPE, value));
    }

    private void handleNormalizationTypeTermSourceRef(String value, int valueIndex) {
        investigation.getNormalizationTypes().get(valueIndex).setTermSource(getTermSource(value));
    }

    private void handleTermSourceName(String value, int valueIndex) {

        if (docTermSources.size() <= valueIndex) {
            TermSource trmSource = new TermSource();
            trmSource.setName(value);
            docTermSources.add(trmSource);
        } else {
            docTermSources.get(valueIndex).setName(value);
        }
    }


    private void handleTermSourceFile(String value, int valueIndex) {

        if (docTermSources.size() > valueIndex) {
            docTermSources.get(valueIndex).setFile(value);
        }
    }

    private void handleTermSourceVersion(String value, int valueIndex) {

        if (docTermSources.size() > valueIndex) {
            docTermSources.get(valueIndex).setVersion(value);
        }
    }


    private void updateTermSourceRefs() {
        Iterator<TermSource> docSetSources = getDocumentSet().getTermSources().iterator();
        while (docSetSources.hasNext()) {
            TermSource docSetTrmSrc = docSetSources.next();
            Iterator<TermSource> idfSources = docTermSources.iterator();
            while (idfSources.hasNext()) {
                TermSource idfSource = idfSources.next();
                if (idfSource.getName().equals(docSetTrmSrc.getName())) {
                    docSetTrmSrc.setFile(idfSource.getFile());
                    docSetTrmSrc.setVersion(idfSource.getVersion());
                }
            }
        }
    }

}
