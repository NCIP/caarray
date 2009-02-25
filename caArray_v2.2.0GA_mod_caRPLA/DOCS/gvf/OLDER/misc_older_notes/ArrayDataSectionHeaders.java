package gov.nih.nci.carpla.rplatab.sradf;

import java.util.Vector;

public class ArrayDataSectionHeaders extends SradfSectionHeaders {

	private Vector<Integer> colNumbersWithImageFiles = new Vector<Integer>();
	private Vector<Integer> colNumbersWithMeasuredArrayDataFiles = new Vector<Integer>();
	private Vector<Integer> colNumbersWithDerivedArrayDataFiles = new Vector<Integer>();

	private Vector<String> distinctImageFileNames = new Vector<String>();
	private Vector<String> distinctArrayDataFilenames = new Vector<String>();

	public Vector<String> getDistinctImageFileNames() {
		return distinctImageFileNames;
	}

	public void setDistinctImageFileNames(Vector<String> distinctImagefiles) {
		this.distinctImageFileNames = distinctImagefiles;
	}

	public Vector<String> getDistinctArrayDataFilenames() {
		return distinctArrayDataFilenames;
	}

	public void setDistinctArrayDataFilenames(
			Vector<String> distinctArrayDataFilenames) {
		this.distinctArrayDataFilenames = distinctArrayDataFilenames;
	}

	public Vector<Integer> getColNumbersWithDerivedArrayDataFiles() {
		return colNumbersWithDerivedArrayDataFiles;
	}

	public void setColNumbersWithDerivedArrayDataFiles(
			Vector<Integer> colNumbersWithDerivedArrayDataFiles) {
		this.colNumbersWithDerivedArrayDataFiles = colNumbersWithDerivedArrayDataFiles;
	}

	public Vector<Integer> getColNumbersWithImageFiles() {
		return colNumbersWithImageFiles;
	}

	public void setColNumbersWithImageFiles(
			Vector<Integer> colNumbersWithImageFiles) {
		this.colNumbersWithImageFiles = colNumbersWithImageFiles;
	}

	public Vector<Integer> getColNumbersWithMeasuredArrayDataFiles() {
		return colNumbersWithMeasuredArrayDataFiles;
	}

	public void setColNumbersWithMeasuredArrayDataFiles(
			Vector<Integer> colNumbersWithMeasuredArrayDataFiles) {
		this.colNumbersWithMeasuredArrayDataFiles = colNumbersWithMeasuredArrayDataFiles;
	}

	public void loadAllReferencedFileNames() {
		// TODO Auto-generated method stub

	}

}
