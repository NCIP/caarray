package gov.nih.nci.carpla.rplatab;

import java.io.File;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.carpla.rplatab.model.Antibody;
import gov.nih.nci.carpla.rplatab.model.ArrayDataSectionPrincipal;
import gov.nih.nci.carpla.rplatab.model.ArraySectionPrincipal;
import gov.nih.nci.carpla.rplatab.model.Assay;
import gov.nih.nci.carpla.rplatab.model.Characteristic;
import gov.nih.nci.carpla.rplatab.model.Comment;
import gov.nih.nci.carpla.rplatab.model.Dilution;
import gov.nih.nci.carpla.rplatab.model.ExperimentalFactor;
import gov.nih.nci.carpla.rplatab.model.HasName;
import gov.nih.nci.carpla.rplatab.model.Protocol;
import gov.nih.nci.carpla.rplatab.model.Provider;
import gov.nih.nci.carpla.rplatab.model.RPLArray;
import gov.nih.nci.carpla.rplatab.model.RPLArrayGroup;
import gov.nih.nci.carpla.rplatab.model.RplArrayFeature;
import gov.nih.nci.carpla.rplatab.model.RplArrayLocation;
import gov.nih.nci.carpla.rplatab.model.Sample;
import gov.nih.nci.carpla.rplatab.model.SamplesSectionPrincipal;
import gov.nih.nci.carpla.rplatab.model.SectionPrincipal;

import gov.nih.nci.carpla.rplatab.model.Source;

import gov.nih.nci.carpla.rplatab.RplaConstants.SradfSectionType;
import gov.nih.nci.carpla.rplatab.files.ArrayDataFile;
import gov.nih.nci.carpla.rplatab.files.DerivedArrayDataFile;
import gov.nih.nci.carpla.rplatab.files.ImageFile;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.SradfFile;

import gov.nih.nci.carpla.rplatab.sradf.SradfHeaders;

public class RplaTabDocumentSet {

	
	
	//###############################################################
	private RplaTabInputFileSet	_rinputfileset;

	public void setRplaTabInputFileSet ( RplaTabInputFileSet rplaTabInputFileSet)
	{
		_rinputfileset = rplaTabInputFileSet;

	}
	//###############################################################
	private ValidationResult	_validationResult	= new ValidationResult();

	public ValidationResult getValidationResult () {
		return _validationResult;
	}

	
	//###############################################################
	private RplaIdfFile	_rplaidffile;

	public RplaIdfFile getRplaIdfFile () {
		return _rplaidffile;
	}

	public void setRplaIdfFile ( RplaIdfFile rplaIdfFile) {
		_rplaidffile = rplaIdfFile;

	}

	//###############################################################
	private SradfFile	_sradfFile;

	public SradfFile getSradfFile () {
		return _sradfFile;
	}

	public void setSradfFile ( SradfFile sradfFile) {
		_sradfFile = sradfFile;

	}


	//###############################################################
	private SradfHeaders	_sradfHeaders;

	public SradfHeaders getSradfHeaders () {
		return _sradfHeaders;
	}

	public void setSradfHeaders ( SradfHeaders sradfHeaders) {
		_sradfHeaders = sradfHeaders;
	}

	
	//###############################################################
	private SortedMap<Integer, List<SectionPrincipal>>	_samplessection_principal_objects_per_row	= new TreeMap<Integer, List<SectionPrincipal>>();
	private SortedMap<Integer, List<SectionPrincipal>>	_arraysection_principal_objects_per_row		= new TreeMap<Integer, List<SectionPrincipal>>();
	private SortedMap<Integer, List<SectionPrincipal>>	_arraydatasection_principal_objects_per_row	= new TreeMap<Integer, List<SectionPrincipal>>();

	public SortedMap<Integer, List<SectionPrincipal>> getSectionRowsPrincipalObjects ( SradfSectionType sectionType)
	{

		switch (sectionType) {

		case Samples:
			return _samplessection_principal_objects_per_row;

		case Array:
			return _arraysection_principal_objects_per_row;

		case ArrayData:
			return _arraydatasection_principal_objects_per_row;

		default:
			return null;

		}

	}
	//###############################################################
	public List<SectionPrincipal> getPrincipalObjectsBySectionAndRow (	SradfSectionType sectionType,
																		int rowNumber)
	{

		switch (sectionType) {

		case Samples:

			List<SectionPrincipal> list = _samplessection_principal_objects_per_row.get(rowNumber);
			if (list != null)
				return list;
			else
				_samplessection_principal_objects_per_row.put(	rowNumber,
																new ArrayList<SectionPrincipal>());
			return _samplessection_principal_objects_per_row.get(rowNumber);

		case Array:

			List<SectionPrincipal> lista = _arraysection_principal_objects_per_row.get(rowNumber);
			if (lista != null)
				return lista;
			else
				_arraysection_principal_objects_per_row.put(rowNumber,
															new ArrayList<SectionPrincipal>());
			return _arraysection_principal_objects_per_row.get(rowNumber);

		case ArrayData:

			List<SectionPrincipal> listb = _arraydatasection_principal_objects_per_row.get(rowNumber);
			if (listb != null)
				return listb;
			else
				_arraydatasection_principal_objects_per_row.put(rowNumber,
																new ArrayList<SectionPrincipal>());
			return _arraydatasection_principal_objects_per_row.get(rowNumber);

		default:
			return null;

		}

	}

	
	//###############################################################
	private String	_investigationTitle;

	public void setInvestigationTitle ( String title) {
		_investigationTitle = title;
	}

	public String getInvestigationTitle () {
		return _investigationTitle;
	}

	// ---------------------------------------------------------------------------
	// ---------------------------
	// Experimental Design
	// ---------------------------

	
	//###############################################################
	private Map<String, ExperimentalFactor>	_experimentalFactors	= new Hashtable<String, ExperimentalFactor>();

	public Map<String, ExperimentalFactor> getExperimentalFactors () {
		return _experimentalFactors;
	}

	public ExperimentalFactor createExperimentalFactor ( String name) {

		ExperimentalFactor factor = new ExperimentalFactor();
		factor.setName(name);
		return factor;
	}

	
	//###############################################################
	private SortedMap<String, Antibody>	_antibodies	= new TreeMap<String, Antibody>();

	public Antibody createAntibody ( String name) {
		Antibody antibody = new Antibody(name);
		_antibodies.put(antibody.getName(), antibody);
		return antibody;
	}

	public SortedMap<String, Antibody> getAntibodies () {
		return _antibodies;
	}

	public Antibody getAntibody ( String name) {
		return _antibodies.get(name);
	}

	
	// ---------------------------------------------------------------------------
	// ---------------------------
	// People
	// ---------------------------

	// ---------------------------------------------------------------------------
	// ---------------------------
	// QC, Replicates, Normalization
	// ---------------------------

	// ---------------------------------------------------------------------------
	// ---------------------------
	// Dates
	// ---------------------------

	
	//###############################################################
	private Map<String, ImageFile>	_imageFiles	= new Hashtable<String, ImageFile>();

	public ImageFile getOrCreateImageFile ( String name) {

		if (_imageFiles.get(name) == null) {
			boolean found = false;
			try {

				Iterator it = _rinputfileset.getMiscFiles().iterator();
				while (it.hasNext()) {
					File file = (File) it.next();
					if (file.getName().compareTo(name) == 0) {
						ImageFile ifile = new ImageFile();
						ifile.setFile(file);
						_imageFiles.put(name, ifile);
						found = true;
					}

				}

				if (found == false) {
					this.getValidationResult()
							.addMessage(this.getSradfFile().getFile(),
										Type.ERROR,
										"Image file for name=" + name
												+ " cannot be found");
				}

			} catch (Exception e) {
				this.getValidationResult()
						.addMessage(this.getSradfFile().getFile(),
									Type.ERROR,
									"Image file for name=" + name
											+ " cannot be found");

				e.printStackTrace();

			}

		}
		return _imageFiles.get(name);
	}

	
	//###############################################################
	private Map<String, ArrayDataFile>	_arrayDataFiles	= new Hashtable<String, ArrayDataFile>();

	public ArrayDataFile getOrCreateArrayDataFile ( String name) {

		if (_arrayDataFiles.get(name) == null) {
			boolean found = false;
			try {

				Iterator it = _rinputfileset.getMiscFiles().iterator();
				while (it.hasNext()) {
					File file = (File) it.next();
					if (file.getName().compareTo(name) == 0) {
						ArrayDataFile adfile = new ArrayDataFile();
						adfile.setFile(file);
						_arrayDataFiles.put(name, adfile);
						found = true;
					}

				}

				if (found == false) {
					this.getValidationResult()
							.addMessage(this.getSradfFile().getFile(),
										Type.ERROR,
										"Array data file for name=" + name
												+ " cannot be found");
				}

			} catch (Exception e) {
				this.getValidationResult()
						.addMessage(this.getSradfFile().getFile(),
									Type.ERROR,
									"Array data file for name=" + name
											+ " cannot be found");

				e.printStackTrace();

			}

		}
		return _arrayDataFiles.get(name);

	}

	
	//###############################################################
	private Map<String, DerivedArrayDataFile>	_derivedArrayDataFiles	= new Hashtable<String, DerivedArrayDataFile>();

	// carplafix
	public DerivedArrayDataFile getOrCreateDerivedArrayDataFile ( String name) {
		return _derivedArrayDataFiles.get(name);

	}

	// ---------------------------------------------------------------------------
	// ---------------------------
	// Publications
	// ---------------------------

	// ---------------------------------------------------------------------------
	// ---------------------------
	// Experiment Description
	// ---------------------------

	
	//###############################################################
	private Map<String, Protocol>	_protocols	= new Hashtable<String, Protocol>();

	public void addProtocol ( Protocol protocol) {

		_protocols.put(protocol.getName(), protocol);

	}

	public Protocol createProtocol ( String name) {
		Protocol protocol = new Protocol();
		protocol.setName(name);
		this.addProtocol(protocol);

		return protocol;
	}

	public boolean protocolExists ( String name) {
		return (_protocols.containsKey(name));
	}

	public Map<String, Protocol> getProtocols () {
		return _protocols;
	}

	public Protocol getProtocol ( String name) {
		return _protocols.get(name);

	}
	
	
	
	
	//carplafix
	//###############################################################
	private final Map<String, TermSource>	termSourceCache	= new HashMap<String, TermSource>();

	public Collection<TermSource> getTermSources () {
		return termSourceCache.values();
	}

	private final Set<OntologyTerm>	termCache	= new HashSet<OntologyTerm>();

	// carplafix
	public Collection<OntologyTerm> getTerms () {
		return termCache;
	}
	

	private final Hashtable<String, TermSource>	_termSources	= new Hashtable<String, TermSource>();

	public TermSource getTermSource ( String value) {
		return _termSources.get(value);
	}

	public TermSource createTermSource ( String value) {
		TermSource termSource = new TermSource(value);

		_termSources.put(value, termSource);
		return termSource;

	}

	
	//###############################################################
	private final Map<String, Source>	_sources	= new Hashtable<String, Source>();

	public Source getOrCreateSource ( String bioSourceName) {

		if (_sources.get(bioSourceName) == null) {
			Source source = new Source();
			source.setName(bioSourceName);
			_sources.put(bioSourceName, source);

		}
		return _sources.get(bioSourceName);
	}

	public Source getSource ( String bioSourceName) {
		return _sources.get(bioSourceName);

	}

	
	//###############################################################
	private final SortedMap<String, Sample>	_samples	= new TreeMap<String, Sample>();

	public SortedMap<String, Sample> getSamples () {
		return _samples;
	}

	public Sample getOrCreateSample ( String bioSampleName) {

		if (_samples.get(bioSampleName) == null) {
			Sample sample = new Sample();
			sample.setName(bioSampleName);
			_samples.put(bioSampleName, sample);

		}
		return _samples.get(bioSampleName);
	}

	public Sample getSample ( String bioSampleName) {
		return _samples.get(bioSampleName);

	}

	public boolean sampleExists ( String name) {
		return (_samples.containsKey(name));
	}

	
	//###############################################################
	private final Map<String, RPLArray>	_rplarrays	= new Hashtable<String, RPLArray>();

	public RPLArray createRPLArray ( String arrayName) {

		RPLArray rarray = new RPLArray();
		rarray.setName(arrayName);
		_rplarrays.put(arrayName, rarray);

		return _rplarrays.get(arrayName);

	}

	public RPLArray getRPLArray ( String arrayName) {
		return _rplarrays.get(arrayName);
	}
	//###############################################################
	public void verifyRplArrayLocationUniqueness ( RplArrayLocation rplArrayLocation)
	{
	// TODO Auto-generated method stub

	}
	//###############################################################
	public RplArrayFeature createRplArrayFeature (	RPLArrayGroup rplArray,
													Sample sample,
													RplArrayLocation rplArrayLocation)
	{
		// TODO Auto-generated method stub
		return null;
	}

	

	
	

	
	// #############################################################################
	private Hashtable<String, Provider>	_providers	= new Hashtable<String, Provider>();

	public Provider getOrCreateProvider ( String providername) {
		if (_providers.get(providername) == null) {

			Provider provider = new Provider(providername);

			_providers.put(providername, provider);

		}
		return _providers.get(providername);

	}

	// #############################################################################

	public Characteristic createCharacteristic ( String name) {

		Characteristic characteristic = new Characteristic();
		characteristic.setValue(name);
		return characteristic;

	}

	// #############################################################################

	public Comment createComment () {
		// TODO Auto-generated method stub
		return null;
	}

	// #############################################################################

	private Map<String, Dilution>	_dilutions	= new Hashtable<String, Dilution>();

	public Dilution getDilution ( String name) {
		return _dilutions.get(name);

	}

	public Dilution createDilution ( String dilutionName) {
		Dilution dil = new Dilution();
		dil.setName(dilutionName);

		_dilutions.put(dilutionName, dil);
		return dil;
	}

	// #############################################################################

	private SortedMap<String, Assay>	_assays	= new TreeMap<String, Assay>();

	public Assay getOrCreateAssay ( String name) {

		if (_assays.get(name) == null) {

			Assay assay = new Assay();
			assay.setName(name);

			_assays.put(name, assay);

		}
		return _assays.get(name);

	}

	
	
	
	
	
	
	
	
	
	
	
	// ###############################################################
	// ###############################################################
	// ###############################################################
	public static void debugPrint ( RplaTabDocumentSet rdataset, PrintStream out)
	{

		out.println("\n**************In debugPrint***********************************************");

		// for (int ii = 0; ii < messages.size(); ii++) {
		//
		// System.out.println("message " + ii
		// + " =\t"
		// + messages.get(ii).getMessage());
		// }

		out.println(rdataset.getRplaIdfFile().getFile().getAbsolutePath());
		out.println("*************************************************************");
		out.println("Investigation Title = " + rdataset.getInvestigationTitle());
		// debugPrintExperimentalDesigns
		out.println("*************************************************************");
		// debugPrintExperimentalFactors(rdataset, out);
		out.println("*************************************************************");
		// debugPrintAntibodies(rdataset, out);
		// debugPrintDilutions(rdataset,out);
		// debugPrintPeople(rdataset,out);
		// debugPrintQCs(rdataset,out);
		// debugPrintReplicates(rdataset,out);
		// debugPrintNormalizations(rdataset,out);
		// debugPrintDates(rdataset,out);
		// debugPrintPublications(rdataset,out);
		out.println("*************************************************************");
		out.println(rdataset.getSradfFile().getFile().getAbsolutePath());
		debugPrintTermSources(rdataset, out);
		out.println("*************************************************************");

		debugPrintSectionPrincipals(rdataset,
									RplaConstants.SradfSectionType.Samples,
									out);
		debugPrintSectionPrincipals(rdataset,
									RplaConstants.SradfSectionType.Array,
									out);
		debugPrintSectionPrincipals(rdataset,
									RplaConstants.SradfSectionType.ArrayData,
									out);

	}

	private static void debugPrintSectionPrincipals (	RplaTabDocumentSet rdataset,
														RplaConstants.SradfSectionType sectionType,
														PrintStream out)
	{

		System.out.println("*******debugPrintSectionPrincipals for " + sectionType.name());

		Iterator it = rdataset.getSectionRowsPrincipalObjects(sectionType)
				.keySet()
				.iterator();

		while (it.hasNext()) {

			Integer intI = (Integer) it.next();

			List<SectionPrincipal> principals_in_row = rdataset.getPrincipalObjectsBySectionAndRow(	sectionType,
																									intI);
			System.out.print("srownum=" + intI + "\t");
			for (int ii = 0; ii < principals_in_row.size(); ii++) {

				System.out.print(principals_in_row.get(ii)
						.getClass()
						.getSimpleName()

									+ ":"
									+ ((HasName) principals_in_row.get(ii)).getName()
									+ "\t");
			}
			System.out.println();
		}

	}

	// #############################################################################
	private static void debugPrintTermSources ( RplaTabDocumentSet rdataset,
												PrintStream out)
	{
	// Hashtable<String, TermSource> termsources =
	// rdataset.getTermSources();
	// for (Enumeration e = termsources.keys(); e.hasMoreElements();) {
	// TermSource ts = termsources.get(e.nextElement());
	// out.println(ts.getName());
	// // todo file, version...
	//
	// }
	}

	// #############################################################################
	private static void debugPrintExperimentalFactors ( RplaTabDocumentSet rdataset,
														PrintStream out)
	{
//		Map<String, ExperimentalFactor> factors = rdataset.getExperimentalFactors();
//
//		for (Enumeration e = factors.keySet().; e.hasMoreElements();) {
//			ExperimentalFactor factor = factors.get(e.nextElement());
//			out.println(factor.getName());
//			out.println(factor.getType());
//			// want to see term source ref too...
//		}

	}

	
	

}
