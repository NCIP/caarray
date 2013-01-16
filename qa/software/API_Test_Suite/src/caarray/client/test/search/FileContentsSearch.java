//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a file download test.
 * 
 * @author vaughng
 * Jul 14, 2009
 */
public class FileContentsSearch extends CriteriaSearch
{

    private Integer expectedBytes = null, minBytes = null, maxBytes = null, multiFileNum = null, idfBytes = null, sdrfBytes = null, numMageTabFiles = null;
    private boolean compressed = false, zip = false, mage = false;
    private String multiFileType = null;
    private List<CaArrayEntityReference> fileReferences = new ArrayList<CaArrayEntityReference>();
    private String experimentName;
    private CaArrayEntityReference experimentRef = null;
    /**
     * 
     */
    public FileContentsSearch()
    {
        super();
    }

    public void addFileReference(CaArrayEntityReference ref)
    {
        fileReferences.add(ref);
    }

    public Integer getExpectedBytes()
    {
        return expectedBytes;
    }

    public void setExpectedBytes(Integer expectedBytes)
    {
        this.expectedBytes = expectedBytes;
    }

    public Integer getMinBytes()
    {
        return minBytes;
    }

    public void setMinBytes(Integer minBytes)
    {
        this.minBytes = minBytes;
    }

    public Integer getMaxBytes()
    {
        return maxBytes;
    }

    public void setMaxBytes(Integer maxBytes)
    {
        this.maxBytes = maxBytes;
    }

    public Integer getMultiFileNum()
    {
        return multiFileNum;
    }

    public void setMultiFileNum(Integer multiFileNum)
    {
        this.multiFileNum = multiFileNum;
    }

    public boolean isCompressed()
    {
        return compressed;
    }

    public void setCompressed(boolean compressed)
    {
        this.compressed = compressed;
    }

    public boolean isZip()
    {
        return zip;
    }

    public void setZip(boolean zip)
    {
        this.zip = zip;
    }

    public String getMultiFileType()
    {
        return multiFileType;
    }

    public void setMultiFileType(String multiFileType)
    {
        this.multiFileType = multiFileType;
    }

    /**
     * Returns the list of file references used in a file search.
     * @return the list of file references used in a file search.
     */
    public List<CaArrayEntityReference> getFileReferences()
    {
        return fileReferences;
    }

    /**
     * Name of the experiment containing the desired file.
     * 
     * @return Name of the experiment containing the desired file.
     */
    public String getExperimentName()
    {
        return experimentName;
    }

    /**
     * Name of the experiment containing the desired file.
     * 
     * @param experimentName Name of the experiment containing the desired file.
     */
    public void setExperimentName(String experimentName)
    {
        this.experimentName = experimentName;
    }

    /**
     * Return Experiment reference used in a file search.
     * @return Experiment reference used in a file search.
     */
    public CaArrayEntityReference getExperimentRef()
    {
        return experimentRef;
    }

    /**
     * Experiment reference used in a file search.
     * @param experimentRef Experiment reference used in a file search.
     */
    public void setExperimentRef(CaArrayEntityReference experimentRef)
    {
        this.experimentRef = experimentRef;
    }

    /**
     * Indicates whether this test is a mage tab download.
     * @return Indicates whether this test is a mage tab download.
     */
    public boolean isMage()
    {
        return mage;
    }

    /**
     * Indicates whether this test is a mage tab download.
     * 
     * @param mage Indicates whether this test is a mage tab download.
     */
    public void setMage(boolean mage)
    {
        this.mage = mage;
    }

    /**
     * Return the number of idf bytes returned in a MageTabFileSet.
     * 
     * @return the idfBytes the number of idf bytes returned in a MageTabFileSet.
     */
    public Integer getIdfBytes()
    {
        return idfBytes;
    }

    /**
     * Set the number of idf bytes returned in a MageTabFileSet.
     * @param idfBytes the number of idf bytes returned in a MageTabFileSet.
     */
    public void setIdfBytes(Integer idfBytes)
    {
        this.idfBytes = idfBytes;
    }

    /**
     * Return the number of sdrf bytes returned in a MageTabFileSet.
     * @return the number of sdrf bytes returned in a MageTabFileSet.
     */
    public Integer getSdrfBytes()
    {
        return sdrfBytes;
    }

    /**
     * Set the number of sdrf bytes returned in a MageTabFileSet.
     * 
     * @param sdrfBytes the number of sdrf bytes returned in a MageTabFileSet.
     */
    public void setSdrfBytes(Integer sdrfBytes)
    {
        this.sdrfBytes = sdrfBytes;
    }

    /**
     * Returns the number of files returned in a MageTabFileSet.
     * 
     * @return the number of files returned in a MageTabFileSet.
     */
    public Integer getNumMageTabFiles()
    {
        return numMageTabFiles;
    }

    /**
     * Set the number of files returned in a MageTabFileSet.
     * @param numFiles the number of files returned in a MageTabFileSet.
     */
    public void setNumMageTabFiles(Integer numFiles)
    {
        this.numMageTabFiles = numFiles;
    }
}
