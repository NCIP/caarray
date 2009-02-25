package gov.nih.nci.carpla.rplatab;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Vector;

public final class RplaConstants {

	public enum SradfSectionType {
		Samples, Array, ArrayData
	}

	public enum RplaDatasetFileType {
		RplaIdf, Sradf, Image, ArrayData, DerivedArrayData
	}

	public static final String	EMPTYFIELDSTRING			= "->";
	
	
	public static final Float UNKNOWNDILUTIONVALUE = 0.0F;

	public static String		SradfSamplesSectionEnd		= "END_SAMPLES_SECTION";

	public static String		SradfArraySectionEnd		= "END_ARRAY_SECTION";

	public static String		SradfArrayDataSectionEnd	= "END_ARRAYDATA_SECTION";

	// public enum SradfHeaderType {
	// AntibodyNameH,
	// ArrayDataFileH,
	// ArrayLocationH,
	// ArrayNameH,
	// SampleH,
	// SourceH,
	// CharacteristicsH,
	// CommentH,
	// DateH,
	// DescriptionH,
	// DilutionH,
	// FactorValueH,
	// ImageFileH,
	// MaterialTypeH,
	// ParameterValueH,
	// PerformerH,
	// ProtocolRefH,
	// DerivedArrayDataFileH,
	// ProviderH,
	// ReporterGroupH,
	// TermH,
	// TermSourceRefH,
	// UnitsH, HybridizationNameH
	//
	// }

}
