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
package gov.nih.nci.carpla.rplatab.rplaidf;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import gov.nih.nci.caarray.magetab.AbstractMageTabDocument; // import
//// gov.nih.nci.caarray.magetab.EntryHeading;
//import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
//import gov.nih.nci.caarray.magetab.MageTabOntologyCategory;
//import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.Parameter;
import gov.nih.nci.caarray.magetab.Protocol;
import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;
import gov.nih.nci.caarray.magetab.idf.Publication;
// import
// gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.carpla.rplatab.AbstractRplaTabDocument;
import gov.nih.nci.carpla.rplatab.EntryHeading;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;
import gov.nih.nci.carpla.rplatab.RplaTabOntologyCategory;
import gov.nih.nci.carpla.rplatab.RplaTabParsingException;
import gov.nih.nci.carpla.rplatab.fileholders.RplaIdfFileHolder;
import gov.nih.nci.carpla.rplatab.sradf.SradfDocument;

@SuppressWarnings("PMD.CyclomaticComplexity")
// warning suppressed due to long switch statement
public class RplaIdfDocument extends AbstractRplaTabDocument {

	public RplaIdfDocument(RplaTabDocumentSet documentSet, RplaIdfFileHolder fileholder) {
		super(documentSet, fileholder);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 149154919398572572L;
	private static final Log LOG = LogFactory.getLog(RplaIdfDocument.class);

	private final RplaInvestigation investigation = new RplaInvestigation();
	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
			Locale.US);
	private final List<TermSource> docTermSources = new ArrayList<TermSource>();
	private final List<SradfDocument> sradfDocuments = new ArrayList<SradfDocument>();

	
	public RplaInvestigation getInvestigation() {
		return investigation;
	}

	
	protected void parse() throws RplaTabParsingException {
		DelimitedFileReader tabDelimitedReader = createTabDelimitedReader();
		while (tabDelimitedReader.hasNextLine()) {
			handleLine(tabDelimitedReader.nextLine());
		}
		updateTermSourceRefs();
	}

	private void handleLine(List<String> lineContents) {
		if (!isEmpty(lineContents)) {
			EntryHeading heading = createHeading(lineContents.get(0));
			RplaIdfRow idfRow = new RplaIdfRow(heading, RplaIdfRowType
					.get(heading.getTypeName()));
			for (int columnIndex = 1; columnIndex < lineContents.size(); columnIndex++) {
				int valueIndex = columnIndex - 1;
				String value = lineContents.get(columnIndex);
				if (!isEmpty(value)) {
					handleValue(idfRow, value, valueIndex);
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

	@SuppressWarnings("PMD")
	// warnings suppressed due to long switch statement
	private void handleValue(RplaIdfRow idfRow, String value, int valueIndex) {
		switch (idfRow.getType()) {

		case ANTIBODY_NAME:
			handleAntibodyName(value, valueIndex);
			break;

		case ANTIGEN_NAME:
			handleAntigenName(value, valueIndex);
			break;

		case ANTIGEN_NAME_TERM_SOURCE_REF:
			handleAntigenNameTermSourceRef(value, valueIndex);
			break;

		case ANTIBODY_PROVIDER:
			handleAntibodyProvider(value, valueIndex);
			break;
		case ANTIBODY_CATALOG_ID:
			handleAntibodyCatalogID(value, valueIndex);

			break;
		case ANTIBODY_LOT_ID:
			handleAntibodyLotID(value, valueIndex);
			break;

		case ANTIBODY_COMMENT:
			handleAntibodyComment(value, valueIndex);
			break;

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
		case QUALITY_CONTROL_TYPES:
			handleQualityControlType(value);
			break;
		case QUALITY_CONTROL_TERM_SOURCE_REF:
		case QUALITY_CONTROL_TYPES_TERM_SOURCE_REF:
			handleQualityControlTermSourceRef(value);
			break;
		case REPLICATE_TYPE:
			handleReplicateType(value);
			break;
		case REPLICATE_TERM_SOURCE_REF:
		case REPLICATE_TYPE_TERM_SOURCE_REF:
			handleReplicateTypeTermSourceRef(value);
			break;
		case NORMALIZATION_TYPE:
			handleNormalizationType(value);
			break;
		case NORMALIZATION_TERM_SOURCE_REF:
			handleNormalizationTypeTermSourceRef(value);
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
			handlePublicationStatusTermSourceRef(value);
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
			handleProtocolTermSourceRef(value);
			break;
		// case SDRF_FILE:
		// case SDRF_FILES:
		// handleSradfFile(value);
		// break;

		case SRADF_FILE:
		case SRADF_FILES:
			handleSradfFile(value);
			// case SDRF_FILES:
			// handleSradfFile(value);
			// break;
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
			throw new IllegalArgumentException("Unknown RplaIdfRowType " + idfRow);
		}
	}

	private void handleAntibodyComment(String value, int valueIndex) {
		Antibody ab = investigation.getOrCreateAntibody(valueIndex);

		ab.setAntibodyComment(value);
	}

	private void handleAntibodyLotID(String value, int valueIndex) {
		Antibody ab = investigation.getOrCreateAntibody(valueIndex);

		ab.setAntibodyLotID(value);
	}

	private void handleAntibodyCatalogID(String value, int valueIndex) {
		Antibody ab = investigation.getOrCreateAntibody(valueIndex);

		ab.setAntibodyCatalogID(value);
	}

	private void handleAntibodyProvider(String value, int valueIndex) {
		Antibody ab = investigation.getOrCreateAntibody(valueIndex);

		ab.setAntibodyProvider(value);
	}

	private void handleAntigenNameTermSourceRef(String value, int valueIndex) {
		Antibody ab = investigation.getOrCreateAntibody(valueIndex);

		ab.setAntigenNameTermSourceRef(value);

	}

	private void handleAntigenName(String value, int valueIndex) {
		Antibody ab = investigation.getOrCreateAntibody(valueIndex);

		ab.setAntigenName(value);

	}

	private void handleAntibodyName(String value, int valueIndex) {
		// antibody name should *NOT* be repeated in document
		Antibody ab = investigation.getOrCreateAntibody(valueIndex);

		ab.setAntibodyName(value);

		addAntibody(ab);

	}

	private void handleSradfFile(String value) {
		SradfDocument sradfDocument = getDocumentSet().getSradfDocument(value);
		getSradfDocuments().add(sradfDocument);
		sradfDocument.setRplaIdfDocument(this);
	}

	private void handleProtocolName(String protocolId, int valueIndex) {
		Protocol protocol = investigation.getOrCreateProtocol(valueIndex);
		protocol.setName(protocolId);
		addProtocol(protocol);
	}

	private void handleProtocolDescription(String value, int valueIndex) {
		Protocol protocol = investigation.getOrCreateProtocol(valueIndex);
		protocol.setDescription(value);
	}

	private void handleProtocolParameters(String value, int valueIndex) {
		Protocol protocol = investigation.getOrCreateProtocol(valueIndex);
		String[] parameterNames = value.split(";");
		for (int i = 0; i < parameterNames.length; i++) {
			Parameter parameter = new Parameter();
			parameter.setName(parameterNames[i]);
			protocol.getParameters().add(parameter);
		}
	}

	private void handleProtocolHardware(String value, int valueIndex) {
		Protocol protocol = investigation.getOrCreateProtocol(valueIndex);
		protocol.setHardware(value);
	}

	private void handleProtocolSoftware(String value, int valueIndex) {
		Protocol protocol = investigation.getOrCreateProtocol(valueIndex);
		protocol.setSoftware(value);
	}

	private void handleProtocolContact(String value, int valueIndex) {
		Protocol protocol = investigation.getOrCreateProtocol(valueIndex);
		protocol.setContact(value);
	}

	private void handleProtocolType(String value, int valueIndex) {
		Protocol protocol = investigation.getOrCreateProtocol(valueIndex);
		protocol.setType(getMgedOntologyTerm(
				RplaTabOntologyCategory.PROTOCOL_TYPE, value));
	}

	private void handleProtocolTermSourceRef(String value) {
		if (!value.trim().equals("")) {
			Iterator<Protocol> protocols = investigation.getProtocols()
					.iterator();
			while (protocols.hasNext()) {
				protocols.next().getType().setTermSource(getTermSource(value));
			}
		}
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
		investigation
				.getDesigns()
				.add(
						getMgedOntologyTerm(
								RplaTabOntologyCategory.EXPERIMENTAL_DESIGN_TYPE,
								value));
	}

	private void handleExperimentalDesignTermSourceRef(String value,
			int valueIndex) {
		investigation.getDesigns().get(valueIndex).setTermSource(
				getTermSource(value));
	}

	private void handleExperimentalFactorName(String value, int valueIndex) {
		investigation.getOrCreateFactor(valueIndex).setName(value);
	}

	private void handleExperimentalFactorType(String value, int valueIndex) {
		ExperimentalFactor factor = investigation.getOrCreateFactor(valueIndex);
		factor.setType(getMgedOntologyTerm(
				RplaTabOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY, value));
	}

	private void handleExperimentalFactorTermSourceRef(String value,
			int valueIndex) {
		ExperimentalFactor factor = investigation.getFactors().get(valueIndex);
		factor.getType().setTermSource(getTermSource(value));
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
		java.util.Scanner scanner = new java.util.Scanner(value)
				.useDelimiter(";");
		while (scanner.hasNext()) {
			roles.add(scanner.next());
		}
		Iterator<String> rolesIter = roles.iterator();
		while (rolesIter.hasNext()) {
			investigation.getOrCreatePerson(valueIndex).getRoles().add(
					getMgedOntologyTerm(RplaTabOntologyCategory.ROLES,
							rolesIter.next()));
		}
	}

	private void handlePersonRoleTermSourceRef(String value, int valueIndex) {
		Iterator<OntologyTerm> roles = investigation.getPersons().get(
				valueIndex).getRoles().iterator();
		while (roles.hasNext()) {
			roles.next().setTermSource(getTermSource(value));
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
				getMgedOntologyTerm(RplaTabOntologyCategory.PUBLICATION_STATUS,
						value));
	}

	private void handlePublicationStatusTermSourceRef(String value) {
		if (!value.trim().equals("")) {
			Iterator<Publication> publications = investigation
					.getPublications().iterator();
			while (publications.hasNext()) {
				publications.next().getStatus().setTermSource(
						getTermSource(value));
			}
		}
	}

	private void handleQualityControlType(String value) {
		investigation.getQualityControlTypes().add(
				getMgedOntologyTerm(
						RplaTabOntologyCategory.QUALITY_CONTROL_TYPE, value));
	}

	private void handleQualityControlTermSourceRef(String value) {
		if (!value.trim().equals("")) {
			Iterator<OntologyTerm> qcTypes = investigation
					.getQualityControlTypes().iterator();
			while (qcTypes.hasNext()) {
				qcTypes.next().setTermSource(getTermSource(value));
			}
		}
	}

	private void handleReplicateType(String value) {
		investigation.getReplicateTypes().add(
				getMgedOntologyTerm(RplaTabOntologyCategory.REPLICATE_TYPE,
						value));
	}

	private void handleReplicateTypeTermSourceRef(String value) {
		if (!value.trim().equals("")) {
			Iterator<OntologyTerm> replTypes = investigation
					.getReplicateTypes().iterator();
			while (replTypes.hasNext()) {
				replTypes.next().setTermSource(getTermSource(value));
			}
		}
	}

	private void handleNormalizationType(String value) {
		investigation.getNormalizationTypes().add(
				getMgedOntologyTerm(RplaTabOntologyCategory.NORMALIZATION_TYPE,
						value));
	}

	private void handleNormalizationTypeTermSourceRef(String value) {
		if (!value.trim().equals("")) {
			Iterator<OntologyTerm> normTypes = investigation
					.getNormalizationTypes().iterator();
			while (normTypes.hasNext()) {
				normTypes.next().setTermSource(getTermSource(value));
			}
		}
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
		Iterator<TermSource> docSetSources = getDocumentSet().getTermSources()
				.iterator();
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

	/**
	 * Returns all the SDRF files referenced by the IDF.
	 * 
	 * @return all related SDRF documents.
	 */
	public List<SradfDocument> getSradfDocuments() {
		return sradfDocuments;
	}

	/**
	 * @return ExperimentalFactor the factor
	 * @param factorName
	 *            the name of the factor being returned
	 */
	public ExperimentalFactor getFactor(String factorName) {
		for (ExperimentalFactor aFactor : investigation.getFactors()) {
			if (aFactor.getName().equalsIgnoreCase(factorName)) {
				return aFactor;
			}
		}
		return null;
	}
}
