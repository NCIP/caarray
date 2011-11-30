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

import gov.nih.nci.caarray.magetab.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab.EntryHeading;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabOntologyCategory;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.Parameter;
import gov.nih.nci.caarray.magetab.Protocol;
import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.TermSourceable;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.util.NCICommonsUtils;
import gov.nih.nci.caarray.magetab.io.FileRef;

/**
 * Represents an Investigation Description Format (IDF) file - a tab-delimited file providing general information about
 * the investigation, including its name, a brief description, the investigatorﾒs contact details, bibliographic
 * references, and free text descriptions of the protocols used in the investigation.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.TooManyMethods" })
public final class IdfDocument extends AbstractMageTabDocument {

    private static final long serialVersionUID = 149154919398572572L;

    private final Investigation investigation = new Investigation();
    private final List<TermSource> docTermSources = new ArrayList<TermSource>();
    private final List<SdrfDocument> sdrfDocuments = new ArrayList<SdrfDocument>();
    private int currentLineNumber;
    private int currentColumnNumber;

    /**
     * Creates a new IDF from an existing file.
     *
     * @param documentSet the MAGE-TAB document set the IDF belongs to.
     * @param file the file containing the IDF content.
     */
    public IdfDocument(MageTabDocumentSet documentSet, FileRef file) {
        super(documentSet, file);
    }

    /**
     * @return the investigation
     */
    public Investigation getInvestigation() {
        return investigation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse() throws MageTabParsingException {
        DelimitedFileReader tabDelimitedReader = createTabDelimitedReader();
        // do two passes. first, process the term sources only, then process the other lines.
        // this is so we can accurately detect invalid term source references
        try {
            parse(tabDelimitedReader, true);
            tabDelimitedReader.reset();
            parse(tabDelimitedReader, false);
            validateMatchingColumns();
        } catch (IOException e) {
            throw new MageTabParsingException("Couldn't create the tab-delimited file reader", e);
        } finally {
            tabDelimitedReader.close();
        }
    }

    private void parse(DelimitedFileReader tabDelimitedReader, boolean processingTermSources) throws IOException {
        while (tabDelimitedReader.hasNextLine()) {
            List<String> lineValues = tabDelimitedReader.nextLine();
            currentLineNumber = tabDelimitedReader.getCurrentLineNumber();
            handleLine(lineValues, processingTermSources);
        }
    }

    /**
     * Exports the IDF elements into the file corresponding to this document.
     */
    @Override
    protected void export() {
        DelimitedWriter writer = createTabDelimitedWriter();

        // Write experiment overview.
        writeElement(IdfRowType.INVESTIGATION_TITLE, writer);
        writeElement(IdfRowType.EXPERIMENT_DESCRIPTION, writer);

        // Write term sources.
        writeElement(IdfRowType.TERM_SOURCE_NAME, writer);
        writeElement(IdfRowType.TERM_SOURCE_FILE, writer);
        writeElement(IdfRowType.TERM_SOURCE_VERSION, writer);

        // Write associated SDRF file names.
        writeElement(IdfRowType.SDRF_FILE, writer);

        //TODO Write other IDF rows.

        writer.close();
    }

    private void writeElement(IdfRowType rowType, DelimitedWriter writer) {
        List<String> currentRow = new ArrayList<String>();
        currentRow.add(rowType.toString());
        switch (rowType) {
          case INVESTIGATION_TITLE:
            currentRow.add(investigation.getTitle());
            break;
          case EXPERIMENT_DESCRIPTION:
            currentRow.add(investigation.getDescription());
            break;
          case SDRF_FILE:
            for (SdrfDocument sdrfDoc : sdrfDocuments) {
              currentRow.add(sdrfDoc.getFile().getName());
            }
            break;
          case TERM_SOURCE_NAME:
              for (TermSource termSource : getDocumentSet().getTermSources()) {
                  currentRow.add(termSource.getName());
              }
              break;
          case TERM_SOURCE_FILE:
              for (TermSource termSource : getDocumentSet().getTermSources()) {
                  if (termSource.getFile() != null) {
                      currentRow.add(termSource.getFile());
                  } else {
                      currentRow.add("");
                  }
              }
              break;
          case TERM_SOURCE_VERSION:
              for (TermSource termSource : getDocumentSet().getTermSources()) {
                  if (termSource.getVersion() != null) {
                      currentRow.add(termSource.getVersion());
                  } else {
                      currentRow.add("");
                  }
              }
              break;
          default:
              // Not yet implemented
              break;
        }
        writeRow(currentRow, writer);
    }

    private void validateMatchingColumns() {
        for (Person aPerson : investigation.getPersons()) {
            if (StringUtils.isEmpty(aPerson.getFirstName()) || StringUtils.isEmpty(aPerson.getLastName())) {
                addWarningMessage("Name is missing : First name = "
                        + StringUtils.defaultIfEmpty(aPerson.getFirstName(), "<MISSING>") + " Last name = "
                        + StringUtils.defaultIfEmpty(aPerson.getLastName(), "<MISSING>"));
            }
        }
        for (Person aPerson : investigation.getPersons()) {
            if (aPerson.getRoles().size() == 0) {
                addWarningMessage("Role is missing for : " + StringUtils.defaultIfEmpty(aPerson.getFirstName(), "")
                        + " " + StringUtils.defaultIfEmpty(aPerson.getLastName(), ""));
            }
        }
        for (TermSource aTermSource : docTermSources) {
            if (StringUtils.isEmpty(aTermSource.getFile())) {
                addWarningMessage("File is missing from term source : " + aTermSource.getName());
            }
            if (StringUtils.isEmpty(aTermSource.getName())) {
                addWarningMessage("No term source associated with the file name : " + aTermSource.getFile());
            }
        }
        for (ExperimentalFactor experimentalFactor : investigation.getFactors()) {
            if (StringUtils.isEmpty(experimentalFactor.getName())) {
                addErrorMessage("Experimental Factors must have a non-empty name");
            }
        }
    }

    private void handleLine(List<String> lineContents, boolean processingTermSources) {
        if (!isEmpty(lineContents)) {
            EntryHeading heading = createHeading(lineContents.get(0));
            IdfRow idfRow = new IdfRow(heading, IdfRowType.get(heading.getTypeName()));
            if (ArrayUtils.contains(IdfRowType.TERM_SOURCE_TYPES, idfRow.getType()) != processingTermSources) {
                return;
            }
            validateColumnValues(idfRow, lineContents);
            for (int columnIndex = 1; columnIndex < lineContents.size(); columnIndex++) {
                currentColumnNumber = columnIndex + 1;
                int valueIndex = columnIndex - 1;
                String value = StringUtils.trim(lineContents.get(columnIndex));
                if (!StringUtils.isEmpty(value)) {
                    value = NCICommonsUtils.performXSSFilter(value, true, true);
                    handleValue(idfRow, value, valueIndex);
                }
            }
        }
    }

    @SuppressWarnings("fallthrough")
    private void validateColumnValues(IdfRow idfRow, List<String> lineContents) {
        switch (idfRow.getType()) {
        case INVESTIGATION_TITLE:
        case DATE_OF_EXPERIMENT:
        case PUBLIC_RELEASE_DATE:
        case EXPERIMENT_DESCRIPTION:
            if (lineContents.size() > 2) {
                addLineWarning(lineContents.get(0) + " can only have one element but found "
                        + (lineContents.size() - 1));
            }
            // falling through on purpose here
        case EXPERIMENTAL_FACTOR_NAME:
        case PERSON_EMAIL:
        case PERSON_PHONE:
        case PUBMED_ID:
        case PUBLICATION_TITLE:
        case PROTOCOL_NAME:
        case EXPERIMENTAL_DESIGN:
            if (lineContents.size() == 1) {
                addLineWarning(lineContents.get(0) + " value is missing ");
            }
            break;
        case SDRF_FILE:
        case SDRF_FILES:
            if (lineContents.size() == 1) {
                addLineError(lineContents.get(0) + " value is missing ");
            }
        default:
            break;
        }
    }

    private boolean isEmpty(List<String> lineContents) {
        // remove and empty items from the bottom up.
        for (int j = lineContents.size() - 1; j > 0; j--) {
            if (StringUtils.isEmpty(lineContents.get(j))) {
                lineContents.remove(j);
            } else {
                // once data is found stop the removal process
                break;
            }
        }
        return lineContents.isEmpty() || "".equals(lineContents.get(0))
                || lineContents.get(0).startsWith(COMMENT_CHARACTER);
    }

    @SuppressWarnings("PMD")
    // warnings suppressed due to long switch statement
    private void handleValue(IdfRow idfRow, String value, int valueIndex) {
        switch (idfRow.getType()) {
        case INVESTIGATION_TITLE:
            handleTitle(value);
            break;
        case EXPERIMENTAL_DESIGN:
            handleExperimentalDesign(value);
            break;
        case EXPERIMENTAL_DESIGN_TERM_SOURCE_REF:
            handleExperimentalDesignTermSourceRef(value, valueIndex);
            break;
        case EXPERIMENTAL_FACTOR_NAME:
            handleExperimentalFactorName(value, valueIndex);
            break;
        case EXPERIMENTAL_FACTOR_TYPE:
            handleExperimentalFactorType(value, valueIndex);
            break;
        case EXPERIMENTAL_FACTOR_TERM_SOURCE_REF:
        case EXPERIMENTAL_FACTOR_TYPE_TERM_SOURCE_REF:
            handleExperimentalFactorTermSourceRef(value, valueIndex);
            break;
        case PERSON_LAST_NAME:
        case PERSON_LAST_NAMES:
            handlePersonLastName(value, valueIndex);
            break;
        case PERSON_FIRST_NAME:
        case PERSON_FIRST_NAMES:
            handlePersonFirstName(value, valueIndex);
            break;
        case PERSON_MID_INITIAL:
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
        case QUALITY_CONTROL_TYPES:
            handleQualityControlType(value);
            break;
        case QUALITY_CONTROL_TERM_SOURCE_REF:
        case QUALITY_CONTROL_TYPES_TERM_SOURCE_REF:
            handleQualityControlTermSourceRef(value, valueIndex);
            break;
        case REPLICATE_TYPE:
            handleReplicateType(value);
            break;
        case REPLICATE_TERM_SOURCE_REF:
        case REPLICATE_TYPE_TERM_SOURCE_REF:
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
        case SDRF_FILES:
            handleSdrfFile(value);
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
        case COMMENT:
            // no-op
            break;
        default:
            addError("IDF type not found: " + idfRow.getType());
        }
    }

    private void handleSdrfFile(String value) {
        SdrfDocument sdrfDocument = getDocumentSet().getSdrfDocument(value);
        if (sdrfDocument == null) {
            addError("Referenced SDRF file " + value
                    + " was not included in the MAGE-TAB document set");
        } else {
            getSdrfDocuments().add(sdrfDocument);
            sdrfDocument.setIdfDocument(this);
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
        for (String element : parameterNames) {
            Parameter parameter = new Parameter();
            parameter.setName(element);
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
        protocol.setType(addOntologyTerm(MageTabOntologyCategory.PROTOCOL_TYPE, value));
    }

    private void handleProtocolTermSourceRef(String value, int valueIndex) {
        if (valueIndex < investigation.getProtocols().size()) {
            Protocol protocol = investigation.getProtocols().get(valueIndex);
            if (protocol.getType() != null) {
                handleTermSourceRef(protocol.getType(), value);
            }
        } else {
            addWarning("Term Source specified for blank Protocol column");
        }
    }

    private void handleTitle(String title) {
        investigation.setTitle(title);
    }

    private void handleExperimentDescription(String description) {
        investigation.setDescription(description);
    }

    private void handleExperimentDate(String dateString) {
        investigation.setDateOfExperiment(parseDateValue(dateString, "Experiment Date"));
    }

    private void handlePublicReleaseDate(String dateString) {
        investigation.setPublicReleaseDate(parseDateValue(dateString, "Public Release Date"));
    }

    private void handleExperimentalDesign(String value) {
        investigation.getDesigns().add(addOntologyTerm(MageTabOntologyCategory.EXPERIMENTAL_DESIGN_TYPE, value));
    }

    private void handleExperimentalDesignTermSourceRef(String value, int valueIndex) {
        if (valueIndex < investigation.getDesigns().size()) {
            handleTermSourceRef(investigation.getDesigns().get(valueIndex), value);
        } else {
            addWarning("Term Source specified for blank Experimental Design column");
        }
    }

    private void handleExperimentalFactorName(String value, int valueIndex) {
        investigation.getOrCreateFactor(valueIndex).setName(value);
    }

    private void handleExperimentalFactorType(String value, int valueIndex) {
        ExperimentalFactor factor = investigation.getOrCreateFactor(valueIndex);
        factor.setType(addOntologyTerm(MageTabOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY, value));
    }

    private void handleExperimentalFactorTermSourceRef(String value, int valueIndex) {
        if (valueIndex < investigation.getFactors().size()) {
            handleTermSourceRef(investigation.getFactors().get(valueIndex).getType(), value);
        } else {
            addWarning("Term Source specified for blank Experimental Factor column");
        }
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
        List<String> roles = new ArrayList<String>();
        java.util.Scanner scanner = new java.util.Scanner(value).useDelimiter(";");
        while (scanner.hasNext()) {
            roles.add(scanner.next());
        }
        Iterator<String> rolesIter = roles.iterator();
        while (rolesIter.hasNext()) {
            investigation.getOrCreatePerson(valueIndex).getRoles().add(
                    addOntologyTerm(MageTabOntologyCategory.ROLES, rolesIter.next()));
        }
    }

    private void handlePersonRoleTermSourceRef(String value, int valueIndex) {
        if (valueIndex < investigation.getPersons().size()) {
            Iterator<OntologyTerm> roles = investigation.getPersons().get(valueIndex).getRoles().iterator();
            while (roles.hasNext()) {
                handleTermSourceRef(roles.next(), value);
            }
        } else {
            addWarning("Term Source specified for blank Person Role column");
        }
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
        investigation.getOrCreatePublication(valueIndex).setStatus(
                addOntologyTerm(MageTabOntologyCategory.PUBLICATION_STATUS, value));
    }

    private void handlePublicationStatusTermSourceRef(String value, int valueIndex) {
        if (valueIndex < investigation.getPublications().size()) {
            Publication publication = investigation.getPublications().get(valueIndex);
            if (publication.getStatus() != null) {
                handleTermSourceRef(publication.getStatus(), value);
            }
        } else {
            addWarning("Term Source specified for blank Publication column");
        }
    }

    private void handleQualityControlType(String value) {
        investigation.getQualityControlTypes().add(
                addOntologyTerm(MageTabOntologyCategory.QUALITY_CONTROL_TYPE, value));
    }

    private void handleQualityControlTermSourceRef(String value, int valueIndex) {
        if (valueIndex < investigation.getQualityControlTypes().size()) {
            handleTermSourceRef(investigation.getQualityControlTypes().get(valueIndex), value);
        } else {
            addWarning("Term Source specified for blank Quality Control Type column");
        }
    }

    private void handleReplicateType(String value) {
        investigation.getReplicateTypes().add(addOntologyTerm(MageTabOntologyCategory.REPLICATE_TYPE, value));
    }

    private void handleReplicateTypeTermSourceRef(String value, int valueIndex) {
        if (valueIndex < investigation.getReplicateTypes().size()) {
            handleTermSourceRef(investigation.getReplicateTypes().get(valueIndex), value);
        } else {
            addWarning("Term Source specified for blank Replicate Type column");
        }
    }

    private void handleNormalizationType(String value) {
        investigation.getNormalizationTypes().add(
                addOntologyTerm(MageTabOntologyCategory.NORMALIZATION_TYPE, value));
    }

    private void handleNormalizationTypeTermSourceRef(String value, int valueIndex) {
        if (valueIndex < investigation.getNormalizationTypes().size()) {
            handleTermSourceRef(investigation.getNormalizationTypes().get(valueIndex), value);
        } else {
            addWarning("Term Source specified for blank Normalization Type column");
        }
    }

    private void handleTermSourceRef(TermSourceable termSourceable, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        TermSource termSource = getTermSource(value);
        if (termSource == null) {
            addWarning("Term Source " + value + " is not defined in the IDF document");
        }
        termSourceable.setTermSource(termSource);
    }


    private void handleTermSourceName(String value, int valueIndex) {
        if (docTermSources.size() <= valueIndex) {
            TermSource trmSource = getOrCreateTermSource(value);
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

    /**
     * Returns all the SDRF files referenced by the IDF.
     *
     * @return all related SDRF documents.
     */
    public List<SdrfDocument> getSdrfDocuments() {
        return sdrfDocuments;
    }

    /**
     * @return ExperimentalFactor the factor
     * @param factorName the name of the factor being returned
     */
    public ExperimentalFactor getFactor(String factorName) {
        for (ExperimentalFactor aFactor : investigation.getFactors()) {
            if (factorName.equalsIgnoreCase(aFactor.getName())) {
                return aFactor;
            }
        }
        addWarningMessage("Experimental Factor " + factorName + " not defined in the IDF file");
        return null;
    }

    /**
     * @return the List of TermSource objects associated with this IdfDocument.
     */
    public List<TermSource> getDocTermSources() {
        return docTermSources;
    }

    /**
     * Adds an error message with the current line and column number.
     * @param message error message
     */
    private void addError(String message) {
        addErrorMessage(currentLineNumber, currentColumnNumber, message);
    }
    /**
     * Adds an error message with the current line number.
     * @param message error message
     */
    private void addLineError(String message) {
        addErrorMessage(currentLineNumber, 0, message);
    }
    /**
     * Adds a warning message with the current line and column number.
     * @param message warning message
     */
    private void addWarning(String message) {
        addWarningMessage(currentLineNumber, currentColumnNumber, message);
    }
    /**
     * Adds a warning message with the current line number.
     * @param message warning message
     */
    private void addLineWarning(String message) {
        addWarningMessage(currentLineNumber, 0, message);
    }
}
