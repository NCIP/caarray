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
package gov.nih.nci.caarray.application.translation.rplatab;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.translation.magetab.ProtocolKey;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.Image;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.ParameterValue;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.protocol.ProtocolTypeAssociation;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab.sdrf.Characteristic;
import gov.nih.nci.caarray.magetab.sdrf.Normalization;
import gov.nih.nci.caarray.magetab.sdrf.Provider;
import gov.nih.nci.caarray.magetab.sdrf.Scan;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfNodeType;
import gov.nih.nci.carpla.domain.antibody.Antibody;
import gov.nih.nci.carpla.domain.rplahybridization.RplaHybridization;
import gov.nih.nci.carpla.domain.rplarray.RplArray;
import gov.nih.nci.carpla.domain.rplarray.RplaFeature;

import gov.nih.nci.carpla.domain.rplarray.RplaSample;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;
import gov.nih.nci.carpla.rplatab.files.ImageFile;
import gov.nih.nci.carpla.rplatab.model.SectionPrincipal;
import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.log4j.Logger;

import gov.nih.nci.carpla.rplatab.RplaConstants.SradfSectionType;

/**
 * Translates entities in SDRF documents into caArray entities.
 */
@SuppressWarnings("PMD")
final class SradfTranslator extends RplaTabAbstractTranslator {

	private static final Logger								LOG								= Logger.getLogger(SradfTranslator.class);

	private final VocabularyService							vocabularyService;

	private final SortedMap<String, Source>					_sources						= new TreeMap<String, Source>();
	private final SortedMap<String, RplaSample>				_samples						= new TreeMap<String, RplaSample>();

	private final SortedMap<String, RplaHybridization>		_rplaHybridizations				= new TreeMap<String, RplaHybridization>();

	private final SortedMap<String, Image>					_imageFiles						= new TreeMap<String, Image>();

	private final SortedMap<String, RplArray>				_rplArrays						= new TreeMap<String, RplArray>();
	private final SortedMap<String, Antibody>				_antibodies						= new TreeMap<String, Antibody>();

	private SortedMap<Integer, List<AbstractCaArrayObject>>	_domain_samplessection_rows		= new TreeMap<Integer, List<AbstractCaArrayObject>>();
	private SortedMap<Integer, List<AbstractCaArrayObject>>	_domain_arraysection_rows		= new TreeMap<Integer, List<AbstractCaArrayObject>>();
	private SortedMap<Integer, List<AbstractCaArrayObject>>	_domain_arraydatasection_rows	= new TreeMap<Integer, List<AbstractCaArrayObject>>();

	// misc
	private final Map<Term, Organism>						termToOrganism					= new HashMap<Term, Organism>();

	// #########################################################################################################

	SradfTranslator(RplaTabDocumentSet rset,
					CaArrayFileSet fileSet,
					RplaTabTranslationResult translationResult,
					CaArrayDaoFactory daoFactory,
					VocabularyService vocabularyService) {
		super(rset, fileSet, translationResult, daoFactory);
		this.vocabularyService = vocabularyService;
	}

	// #########################################################################################################
	@Override
	void translate () {

		translateSradf(super.getDocumentSet());

	}

	// ############################################################################

	private void translateSradf ( RplaTabDocumentSet rset) {

		for (Antibody ab : getTranslationResult()	.getInvestigations()
													.iterator()
													.next()
													.getAntibodies()) {
			_antibodies.put(ab.getName(), ab);

		}
		translatePrincipals(rset);

		for (RplArray rplArray : this._rplArrays.values()) {
			LOG.info(rplArray.getName());
			LOG.info("features:" + rplArray.getRplaFeatures().size());

		}

		System.out.println("There are " + getTranslationResult().getInvestigations()
																.size()
							+ "number of investigations");
		for (Experiment investigation : getTranslationResult()	.getInvestigations()) {

			investigation.getSources().addAll(this._sources.values());

			investigation	.getRplaHybridizations()
							.addAll(this._rplaHybridizations.values());
			investigation.getRplaSamples().addAll(this._samples.values());

			investigation.getAntibodies().addAll(this._antibodies.values());
			investigation.getRplArrays().addAll(this._rplArrays.values());
		}

	}

	// ############################################################################

	private void translatePrincipals ( RplaTabDocumentSet rset) {

		translateSamplesSectionPrincipals(rset, _domain_samplessection_rows);
		translateArraySectionPrincipals(rset, _domain_arraysection_rows);
		translateArrayDataSectionPrincipals(rset, _domain_arraydatasection_rows);

	}

	// ############################################################################

	public void translateSamplesSectionPrincipals ( RplaTabDocumentSet rset,
													SortedMap<Integer, List<AbstractCaArrayObject>> domain_samplessection_rows)
	{
		LOG.info("translateSamplesSectionPrincipals  1");

		SortedMap<Integer, List<SectionPrincipal>> section_rows = rset.getSectionRowsPrincipalObjects(SradfSectionType.Samples);
		Iterator<Entry<Integer, List<SectionPrincipal>>> sectionRowIterator = section_rows	.entrySet()
																							.iterator();
		while (sectionRowIterator.hasNext()) {
			Entry<Integer, List<SectionPrincipal>> entry = sectionRowIterator.next();
			Integer rowInteger = entry.getKey();
			List<SectionPrincipal> principals = entry.getValue();

			Iterator<SectionPrincipal> spIterator = principals.iterator();

			if (domain_samplessection_rows.get(rowInteger) == null) {
				domain_samplessection_rows.put(	rowInteger,
												new ArrayList<AbstractCaArrayObject>());
			}

			while (spIterator.hasNext()) {
				SectionPrincipal sp = spIterator.next();
				// carplatofix replace ugly if-elseifs

				LOG.info(sp.getClass().getName());

				if (sp	.getClass()
						.getName()
						.compareTo("gov.nih.nci.carpla.rplatab.model.Source") == 0) {
					gov.nih.nci.carpla.rplatab.model.Source rplatabSource = (gov.nih.nci.carpla.rplatab.model.Source) sp;
					Source domainSource = getOrCreateSource(rplatabSource.getName());
					translateBioMaterial(domainSource, rplatabSource);

					domain_samplessection_rows	.get(rowInteger)
												.add(domainSource);

				} else if (sp	.getClass()
								.getName()
								.compareTo("gov.nih.nci.carpla.rplatab.model.Sample") == 0) {

					gov.nih.nci.carpla.rplatab.model.Sample rplatabSample = (gov.nih.nci.carpla.rplatab.model.Sample) sp;
					RplaSample domainSample = getOrCreateRplaSample(rplatabSample.getName());
					translateBioMaterial(domainSample, rplatabSample);

					domain_samplessection_rows	.get(rowInteger)
												.add(domainSample);
				}

				else if (sp	.getClass()
							.getName()
							.compareTo("ProtocolApplication") == 0) {

					// carplatodo

				}

				else if (sp.getClass().getName().compareTo("FactorValue") == 0) {

				}

			} // end while
			LOG.info("translateSamplesSectionPrincipals  2");
			// source-sample
			// source-protocol
			// protocol-sample
			// sample-sample
			// sample-factorvalue
			// fv-fv
			// protocol-protocol

			applyBioMaterialAssociations(domain_samplessection_rows.get(rowInteger));
			// applyFactorValueAssociations(domain_samplessection_rows.get(rowInteger));
			// applyProtocolAssociations(domain_samplessection_rows.get(rowInteger));
			LOG.info("translateSamplesSectionPrincipals  3");
		}

	}

	// ############################################################################
	private void applyFactorValueAssociations ( List<AbstractCaArrayObject> list)
	{
	// TODO Auto-generated method stub

	}

	// ############################################################################
	public void translateArraySectionPrincipals (	RplaTabDocumentSet rset,
													SortedMap<Integer, List<AbstractCaArrayObject>> domain_arraysection_rows)
	{

		SortedMap<Integer, List<SectionPrincipal>> section_rows = rset.getSectionRowsPrincipalObjects(SradfSectionType.Array);

		LOG.info("translateArraySectionPrincipals: num of rows = " + section_rows.size());

		Iterator<Entry<Integer, List<SectionPrincipal>>> sectionRowIterator = section_rows	.entrySet()
																							.iterator();

		int arrayIndexInList = -1;
		int featureIndexInList = -1;
		int dilutionIndexInList = -1;
		int sampleIndexInList = -1;

		int count = 0;
		boolean first = true;

		while (sectionRowIterator.hasNext()) {
			LOG.info("translateArraySectionPrincipals 1");
			Entry<Integer, List<SectionPrincipal>> entry = sectionRowIterator.next();
			Integer rowInteger = entry.getKey();
			List<SectionPrincipal> principals = entry.getValue();

			Iterator<SectionPrincipal> spIterator = principals.iterator();

			if (domain_arraysection_rows.get(rowInteger) == null) {
				domain_arraysection_rows.put(	rowInteger,
												new ArrayList<AbstractCaArrayObject>());
			}

			while (spIterator.hasNext()) {
				SectionPrincipal sp = spIterator.next();
				// carplatofix replace ugly if-elseifs

				LOG.info(sp.getClass().getName());

				if (sp	.getClass()
						.getName()
						.compareTo("gov.nih.nci.carpla.rplatab.model.RplArray") == 0) {
					gov.nih.nci.carpla.rplatab.model.RplArray rplatabRplArray = (gov.nih.nci.carpla.rplatab.model.RplArray) sp;
					RplArray domainRplArray = getOrCreateRplArray(rplatabRplArray.getName());

					domain_arraysection_rows.get(rowInteger)
											.add(domainRplArray);
					if (first) {
						arrayIndexInList = count;
						count++;
					}

				} else if (sp	.getClass()
								.getName()
								.compareTo("gov.nih.nci.carpla.rplatab.model.RplArrayLocation") == 0) {
					// i know from the rplatab parser that each line necessarily
					// has a unique location
					gov.nih.nci.carpla.rplatab.model.RplArrayLocation rloc = (gov.nih.nci.carpla.rplatab.model.RplArrayLocation) sp;

					RplaFeature rfeat = new RplaFeature();

					rfeat.setBlockColumn(rloc.getBlockColumn());
					rfeat.setBlockRow(rloc.getBlockRow());
					rfeat.setCol(rloc.getColumn());
					rfeat.setRow(rloc.getRow());
					domain_arraysection_rows.get(rowInteger).add(rfeat);

					if (first) {
						featureIndexInList = count;
						count++;
					}

				} else if (sp	.getClass()
								.getName()
								.compareTo("gov.nih.nci.carpla.rplatab.model.Dilution") == 0) {
					gov.nih.nci.carpla.rplatab.model.Dilution dil = (gov.nih.nci.carpla.rplatab.model.Dilution) sp;

					// --

					Category category = TermTranslator.getOrCreateCategory(	this.vocabularyService,
																			this.getTranslationResult(),
																			"Measurement");
					OntologyTerm ot = new OntologyTerm();
					ot.setValue(dil.getUnit());

					// Term term = getTerm(ot);
					// LOG.info("????" + term.getValueAndSource());

					// Term term = new Term();
					// term.setCategory(category);
					// term.setValue("x_times");
					TermSource ts = vocabularyService.getSource("MO", "1.3.1.1");
					Term term = vocabularyService.getTerm(ts, "x_times");
					// new TermSource();
					// ts.setName("MO");
					// ts.setVersion("1.3.1.1");
					// ts.setUrl("http://mged.sourceforge.net/ontologies/MGEDontology.php");

					term.setSource(ts);

					MeasurementCharacteristic mc = new MeasurementCharacteristic(	category,
																					Float.valueOf(dil.getValue()),
																					term);

					domain_arraysection_rows.get(rowInteger).add(mc);

					if (first) {
						dilutionIndexInList = count;
						count++;
					}

				}

				else if (sp	.getClass()
							.getName()
							.compareTo("gov.nih.nci.carpla.rplatab.model.Sample") == 0) {

					gov.nih.nci.carpla.rplatab.model.Sample rplatabSample = (gov.nih.nci.carpla.rplatab.model.Sample) sp;
					RplaSample domainSample = getRplaSample(rplatabSample.getName());
					domain_arraysection_rows.get(rowInteger).add(domainSample);

					if (first) {
						sampleIndexInList = count;
						count++;
					}

				}

			} // end while

			applyRplArrayDetailAssociations(domain_arraysection_rows.get(rowInteger),
											arrayIndexInList,
											featureIndexInList,
											dilutionIndexInList,
											sampleIndexInList

			);

			first = false;
		}

	}

	// ############################################################################
	private void applyBioMaterialAssociations ( List<AbstractCaArrayObject> rowDomainPrincipals)
	{

		for (int ii = 0; ii < rowDomainPrincipals.size(); ii++) {

			AbstractCaArrayObject domainPrincipal = rowDomainPrincipals.get(ii);

			if (domainPrincipal instanceof Source) {
				Source source = (Source) domainPrincipal;

				for (int jj = ii + 1; jj < rowDomainPrincipals.size(); jj++) {
					AbstractCaArrayObject nextDomainPrincipal = rowDomainPrincipals.get(jj);

					if (nextDomainPrincipal instanceof RplaSample) {
						RplaSample rplasample = (RplaSample) nextDomainPrincipal;

						if (!source	.getSuccessorsOfType(ExperimentDesignNodeType.RPLASAMPLE)
									.contains(rplasample)) {

							rplasample.setSource(source);
						}
					}

				}

			} else if (domainPrincipal instanceof RplaSample) {

				RplaSample rplaSample = (RplaSample) domainPrincipal;

				for (int jj = ii + 1; jj < rowDomainPrincipals.size(); jj++) {
					AbstractCaArrayObject nextDomainPrincipal = rowDomainPrincipals.get(jj);

					if (nextDomainPrincipal instanceof RplaSample) {

						System.out.println("Should never have gotten here: grammar shoudl have prevented it: RplaSample<->RplaSample not implemented");

					}

				}

			}

		}

	}

	// ############################################################################
	public void translateArrayDataSectionPrincipals (	RplaTabDocumentSet rset,
														SortedMap<Integer, List<AbstractCaArrayObject>> domain_arraydatasection_rows)
	{
		LOG.info("translateArrayDataSectionPrincipals");
		SortedMap<Integer, List<SectionPrincipal>> section_rows = rset.getSectionRowsPrincipalObjects(SradfSectionType.ArrayData);
		Iterator<Entry<Integer, List<SectionPrincipal>>> sectionRowIterator = section_rows	.entrySet()

																							.iterator();
		int count = 0;
		boolean first = true;
		int rplArrayIndexInList = -1;
		int antibodyIndexInList = -1;
		int rplaHybridizationIndexInList = -1;
		while (sectionRowIterator.hasNext()) {
			Entry<Integer, List<SectionPrincipal>> entry = sectionRowIterator.next();
			Integer rowInteger = entry.getKey();
			List<SectionPrincipal> principals = entry.getValue();

			Iterator<SectionPrincipal> spIterator = principals.iterator();

			if (domain_arraydatasection_rows.get(rowInteger) == null) {
				domain_arraydatasection_rows.put(	rowInteger,
													new ArrayList<AbstractCaArrayObject>());
			}

			while (spIterator.hasNext()) {
				SectionPrincipal sp = spIterator.next();

				LOG.info(sp.getClass().getName());

				// carplatofix replace ugly if-elseifs
				if (sp	.getClass()
						.getName()
						.compareTo("gov.nih.nci.carpla.rplatab.model.RplArray") == 0) {
					gov.nih.nci.carpla.rplatab.model.RplArray rplatabRplArray = (gov.nih.nci.carpla.rplatab.model.RplArray) sp;
					RplArray domainRplArray = getOrCreateRplArray(rplatabRplArray.getName());
					domain_arraydatasection_rows.get(rowInteger)
												.add(domainRplArray);

					if (first) {
						rplArrayIndexInList = count;
						count++;
					}

				} else if (sp	.getClass()
								.getName()
								.compareTo("gov.nih.nci.carpla.rplatab.model.Antibody") == 0) {

					gov.nih.nci.carpla.rplatab.model.Antibody rplatabAntibody = (gov.nih.nci.carpla.rplatab.model.Antibody) sp;

					Antibody domainAntibody = getOrCreateAntibody(rplatabAntibody.getName());

					domain_arraydatasection_rows.get(rowInteger)
												.add(domainAntibody);

					if (first) {
						antibodyIndexInList = count;
						count++;
					}

				} else if (sp	.getClass()
								.getName()
								.compareTo("gov.nih.nci.carpla.rplatab.model.Assay") == 0) {

					gov.nih.nci.carpla.rplatab.model.Assay assay = (gov.nih.nci.carpla.rplatab.model.Assay) sp;

					RplaHybridization rplaHyb = getOrCreateRplaHybridization(assay.getName());

					domain_arraydatasection_rows.get(rowInteger).add(rplaHyb);

					if (first) {
						rplaHybridizationIndexInList = count;
						count++;
					}

				}

				else if (sp	.getClass()
							.getName()
							.compareTo("gov.nih.nci.carpla.rplatab.files.ImageFile") == 0) {

					gov.nih.nci.carpla.rplatab.files.ImageFile imageFile = (gov.nih.nci.carpla.rplatab.files.ImageFile) sp;
					Image image = getOrCreateImageFile(imageFile.getName());
					domain_arraydatasection_rows.get(rowInteger).add(image);

					if (first) {

						count++;
					}

				}

			}
			applyRplArrayDataDetailAssociations(domain_arraydatasection_rows.get(rowInteger),
												rplArrayIndexInList,
												antibodyIndexInList,
												rplaHybridizationIndexInList);

		} // end while

	}

	// ############################################################################
	private void applyRplArrayDataDetailAssociations (	List<AbstractCaArrayObject> rowDomainPrincipals,
														int rplArrayIndexInList,
														int antibodyIndexInList,
														int rplaHybridizationIndexInList)
	{

		RplaHybridization rh = (RplaHybridization) rowDomainPrincipals.get(rplaHybridizationIndexInList);

		Antibody ab = (Antibody) rowDomainPrincipals.get(antibodyIndexInList);

		RplArray rarray = (RplArray) rowDomainPrincipals.get(rplArrayIndexInList);

		rh.setAntibody(ab);
		ab.getRplaHybridizations().add(rh);
		rh.setRplArray(rarray);

		for (int ii = 0; ii < rowDomainPrincipals.size(); ii++) {

			AbstractCaArrayObject domainPrincipal = rowDomainPrincipals.get(ii);

			if (domainPrincipal instanceof RplaHybridization) {
				RplaHybridization rhyb = (RplaHybridization) domainPrincipal;

				for (int jj = ii + 1; jj < rowDomainPrincipals.size(); jj++) {
					AbstractCaArrayObject nextDomainPrincipal = rowDomainPrincipals.get(jj);

					if (nextDomainPrincipal instanceof Image) {
						Image image = (Image) nextDomainPrincipal;

						if (!rhyb.getImages().contains(image)) {

							rhyb.addImage(image);
							// Image is nto bidirectional
						}
					}

				}

			}

		}

	}

	// ############################################################################

	private void applyRplArrayDetailAssociations (	List<AbstractCaArrayObject> rowDomainPrincipals,
													int arrayIndexInList,
													int featureIndexInList,
													int dilutionIndexInList,
													int sampleIndexInList)
	{

		RplArray rplArray = (RplArray) rowDomainPrincipals.get(arrayIndexInList);
		RplaFeature rplaFeature = (RplaFeature) rowDomainPrincipals.get(featureIndexInList);
		MeasurementCharacteristic dilution = (MeasurementCharacteristic) rowDomainPrincipals.get(dilutionIndexInList);
		RplaSample rplaSample = (RplaSample) rowDomainPrincipals.get(sampleIndexInList);

		rplaSample.setRplArray(rplArray);
		System.out.println("!!!!!!!about to add sample to array: " + rplaSample.toString());
		rplArray.getRplaSamples().add(rplaSample);

		rplArray.getRplaFeatures().add(rplaFeature);

		rplaSample.getRplaFeatures().add(rplaFeature);

		rplaFeature.setDilution(dilution);

	}

	// ############################################################################
	private Image getOrCreateImageFile ( String name) {

		if (_imageFiles.containsKey(name)) {
			return _imageFiles.get(name);
		}

		Image image = new Image();
		image.setName(name);
		_imageFiles.put(name, image);
		return image;
	}

	// ############################################################################
	private RplaHybridization getOrCreateRplaHybridization ( String name) {

		if (_rplaHybridizations.containsKey(name)) {
			return _rplaHybridizations.get(name);
		}

		RplaHybridization rplaHyb = new RplaHybridization();
		rplaHyb.setName(name);
		_rplaHybridizations.put(name, rplaHyb);
		return rplaHyb;
	}

	// ############################################################################
	public Source getOrCreateSource ( String name) {

		if (_sources.containsKey(name)) {
			return _sources.get(name);
		}
		Source source = new Source();
		source.setName(name);
		_sources.put(name, source);
		return source;

	}

	// ############################################################################
	public RplaSample getOrCreateRplaSample ( String name) {

		if (_samples.containsKey(name)) {
			return _samples.get(name);
		}
		RplaSample sample = new RplaSample();
		sample.setName(name);
		_samples.put(name, sample);

		System.out.println("There are now thsi number of RplaSamples in SradfTranslator" + _samples.size());

		return sample;

	}

	// ############################################################################
	public RplaSample getRplaSample ( String name) {

		return _samples.get(name);

		// carplatodo

	}

	// ############################################################################
	private Antibody getOrCreateAntibody ( String name) {

		if (_antibodies.containsKey(name)) {
			return _antibodies.get(name);
		}
		Antibody antibody = new Antibody();
		antibody.setName(name);
		_antibodies.put(name, antibody);
		return antibody;

	}

	// ############################################################################
	private void translateBioMaterial ( AbstractBioMaterial bioMaterial,
										gov.nih.nci.carpla.rplatab.model.AbstractBioMaterial sBiomaterial)
	{
		bioMaterial.setName(sBiomaterial.getName());
		bioMaterial.setDescription(sBiomaterial.getDescription());
		bioMaterial.setMaterialType(getTerm(sBiomaterial.getMaterialType()));
		for (Characteristic sradfCharacteristic : sBiomaterial.getCharacteristics()) {
			AbstractCharacteristic characteristic = translateCharacteristic(sradfCharacteristic);
			String category = characteristic.getCategory().getName();
			if (ExperimentOntologyCategory.ORGANISM_PART.getCategoryName()
														.equals(category)) {
				bioMaterial.setTissueSite(((TermBasedCharacteristic) characteristic).getTerm());
			} else if (ExperimentOntologyCategory.CELL_TYPE	.getCategoryName()
															.equals(category)) {
				bioMaterial.setCellType(((TermBasedCharacteristic) characteristic).getTerm());
			} else if (ExperimentOntologyCategory.DISEASE_STATE	.getCategoryName()
																.equals(category)) {
				bioMaterial.setDiseaseState(((TermBasedCharacteristic) characteristic).getTerm());
			} else if (ExperimentOntologyCategory.ORGANISM	.getCategoryName()
															.equals(category)) {
				Organism organism = getOrganism(((TermBasedCharacteristic) characteristic).getTerm());
				bioMaterial.setOrganism(organism);
			} else {
				bioMaterial.getCharacteristics().add(characteristic);
				characteristic.setBioMaterial(bioMaterial);
			}
		}
		// will need to revisit this, I don't really understand this.
		// for (gov.nih.nci.caarray.magetab.ProtocolApplication
		// mageTabProtocolApplication : sBiomaterial.getProtocolApplications())
		// {
		// ProtocolApplication protocolApplication =
		// getProtocolApplicationFromMageTabProtocolApplication(mageTabProtocolApplication);
		// protocolApplication.setBioMaterial(bioMaterial);
		// bioMaterial.getProtocolApplications().add(protocolApplication);
		// }
	}

	// ############################################################################
	private Organism getOrganism ( Term term) {
		Organism o = termToOrganism.get(term);
		if (o == null && term.getSource().getId() != null) {
			o = vocabularyService.getOrganism(term.getSource(), term.getValue());
		}
		if (o == null) {
			o = new Organism();
			o.setScientificName(term.getValue());
			o.setTermSource(term.getSource());
			termToOrganism.put(term, o);
		}
		return o;
	}

	// ############################################################################
	public RplArray getOrCreateRplArray ( String name) {
		if (_rplArrays.containsKey(name)) {
			return _rplArrays.get(name);
		}
		RplArray rplArray = new RplArray();
		rplArray.setName(name);
		_rplArrays.put(name, rplArray);
		LOG.info("there are now this number of rplarrays:" + _rplArrays.size());

		return rplArray;

	}

	// ############################################################################
	private FactorValue translateFactor ( gov.nih.nci.caarray.magetab.sdrf.FactorValue sdrfFactorVal)
	{
		FactorValue factorValue = new FactorValue();
		factorValue.setValue(sdrfFactorVal.getValue());
		factorValue.setUnit(getTerm(sdrfFactorVal.getUnit()));
		Factor factor = getTranslationResult()	.getFactor(sdrfFactorVal.getFactor());
		factorValue.setFactor(factor);
		factor.getFactorValues().add(factorValue);
		return factorValue;
	}

	// ############################################################################
	private Term getUnknownProtocolType () {
		TermSource source = this.vocabularyService.getSource(	ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
																ExperimentOntology.MGED_ONTOLOGY.getVersion());
		return this.vocabularyService.getTerm(	source,
												VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME);
	}

	// ############################################################################
	private AbstractCharacteristic translateCharacteristic ( Characteristic sdrfCharacteristic)
	{

		LOG.info(sdrfCharacteristic.toString());
		LOG.info(sdrfCharacteristic.getValue());
		LOG.info(sdrfCharacteristic.getTerm());

		Category category = TermTranslator.getOrCreateCategory(	this.vocabularyService,
																this.getTranslationResult(),
																sdrfCharacteristic.getCategory());
		if (sdrfCharacteristic.getUnit() != null) {
			return new MeasurementCharacteristic(	category,
													Float.valueOf(sdrfCharacteristic.getValue()),
													getTerm(sdrfCharacteristic.getUnit()));
		} else {
			return new TermBasedCharacteristic(	category,
												getTerm(sdrfCharacteristic.getTerm()));
		}
	}

	@Override
	Logger getLog () {
		return LOG;
	}
}
