//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.ResourceBasedEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration of different assay types that an Experiment can perform.
 */
public enum AssayType implements ResourceBasedEnum {
	/**
	 * Array design used to interrogate gene expression: transcription of
	 * genetically encoded information into an intermediary message (messenger
	 * RNA) and subsequent translation into a functional protein.
	 */
	GENE_EXPRESSION("geneExpression"),
	/**
	 * Array design used to interrogate single nucleotide polymorphisms (SNPs):
	 * variations of a single nucleotide at a specific location of the genome
	 * due to base substitution, present at an appreciable frequency between
	 * individuals of a single interbreeding population.
	 */
	SNP("snp"),
	/**
	 * Array design used to interrogate exons; the sequences of a gene that are
	 * present in the final, mature, spliced messenger RNA molecule from that
	 * gene.
	 */
	EXON("exon"),
	/**
	 * Array design used to interrogate aCGH.
	 */
	ACGH("aCGH"),

	/**
	 * Assay type used to interrogate miRNA.
	 */
	MICRORNA("microRNA"),

	// carpla
	// *********************************************************
	// Reverse phase protein lysate array assay type ( caRPLA)
	// *********************************************************
	RPLA("rpla"),
	

	/**
	 * Assay type used to interrogate methylation.
	 */
	METHYLATION("methylation");

	private static final String				RESOURCE_KEY_PREFIX	= "assayType.";

	private static Map<String, AssayType>	valueToTypeMap		= new HashMap<String, AssayType>();

	private final String					value;

	AssayType(String value) {
		this.value = value;
	}

	/**
	 * @return the resource key that should be used to retrieve a label for this
	 *         AssayType in the UI
	 */
	public String getResourceKey () {
		return RESOURCE_KEY_PREFIX + getValue();
	}

	/**
	 * @return the value
	 */
	public String getValue () {
		return value;
	}

	private static Map<String, AssayType> getValueToTypeMap () {
		if (valueToTypeMap.isEmpty()) {
			for (AssayType type : values()) {
				valueToTypeMap.put(type.getValue(), type);
			}
		}
		return valueToTypeMap;
	}

	/**
	 * Returns the <code>AssayType</code> corresponding to the given value.
	 * Returns null for null value.
	 * 
	 * @param value
	 *            the value to match
	 * @return the matching type.
	 */
	public static AssayType getByValue ( String value) {
		checkType(value);
		return getValueToTypeMap().get(value);
	}

	/**
	 * Checks to see that the value given is a legal <code>AssayType</code>
	 * value.
	 * 
	 * @param value
	 *            the value to check;
	 */
	public static void checkType ( String value) {
		if (value != null && !getValueToTypeMap().containsKey(value)) {
			throw new IllegalArgumentException("No matching type for " + value);
		}
	}
}
