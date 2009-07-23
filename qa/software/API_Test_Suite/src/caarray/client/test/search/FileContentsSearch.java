/**
 * 
 */
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vaughng
 * Jul 14, 2009
 */
public class FileContentsSearch extends CriteriaSearch
{

    private Integer expectedBytes = null, minBytes = null, maxBytes = null, multiFileNum = null, idfBytes = null, sdrfBytes = null, numFiles = null;
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

    public List<CaArrayEntityReference> getFileReferences()
    {
        return fileReferences;
    }

    public String getExperimentName()
    {
        return experimentName;
    }

    public void setExperimentName(String experimentName)
    {
        this.experimentName = experimentName;
    }

    /**
     * @return the experimentRef
     */
    public CaArrayEntityReference getExperimentRef()
    {
        return experimentRef;
    }

    /**
     * @param experimentRef the experimentRef to set
     */
    public void setExperimentRef(CaArrayEntityReference experimentRef)
    {
        this.experimentRef = experimentRef;
    }

    /**
     * @return the mage
     */
    public boolean isMage()
    {
        return mage;
    }

    /**
     * @param mage the mage to set
     */
    public void setMage(boolean mage)
    {
        this.mage = mage;
    }

    /**
     * @return the idfBytes
     */
    public Integer getIdfBytes()
    {
        return idfBytes;
    }

    /**
     * @param idfBytes the idfBytes to set
     */
    public void setIdfBytes(Integer idfBytes)
    {
        this.idfBytes = idfBytes;
    }

    /**
     * @return the sdrfBytes
     */
    public Integer getSdrfBytes()
    {
        return sdrfBytes;
    }

    /**
     * @param sdrfBytes the sdrfBytes to set
     */
    public void setSdrfBytes(Integer sdrfBytes)
    {
        this.sdrfBytes = sdrfBytes;
    }

    /**
     * @return the numFiles
     */
    public Integer getNumFiles()
    {
        return numFiles;
    }

    /**
     * @param numFiles the numFiles to set
     */
    public void setNumFiles(Integer numFiles)
    {
        this.numFiles = numFiles;
    }
}
