/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.data;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ucar.nc2.NetcdfFileWriteable;


/**
 * @author John Pike
 *
 */
public class NetcdfDataStoreFactory implements DataStoreFactory {

    private static final Log LOG = LogFactory.getLog(NetcdfDataStoreFactory.class);

    /**
     *
     */
    public NetcdfDataStoreFactory() {
        // TODO Auto-generated constructor stub
    }

    static NetcdfDataStoreFactory getInstance() {
        return new NetcdfDataStoreFactory();
    }

    /**
     * This method creates a new datastore in the file system, to be eventually populated.
     * @see gov.nih.nci.caarray.data.DataStoreFactory#createDataStore(DataStoreDescriptor, File)
     * @param descriptor the desciptor
     * @param file the file
     * @return DataStore
     */
    public DataStore createDataStore(AbstractDataStoreDescriptor descriptor, File file) {

        NetcdfFileWriteable ncFile = null;
        NetCdfDataStore netCdfDS = null;
        try {
            file.getAbsoluteFile();
            URL url = file.toURL();
            ncFile = NetcdfFileWriteable.createNew(url.getFile(), false);
            netCdfDS = new NetCdfDataStore(ncFile);
        } catch (IOException ie) {
            LOG.error("error getting file in createDataStore()", ie);
        } catch (DataStoreException dse) {
            LOG.error("error creating NetcdfDataStore in createDataStore()", dse);
        }
        return netCdfDS;

    }

    /**
     * This method retrieves an existing ncdf file from the file system, converting
     * it to a DataStore object.
     * @see gov.nih.nci.caarray.data.DataStoreFactory#getDataStore(java.io.File)
     * @return DataStore
     * @param file the file
     */
    public DataStore getDataStore(File file) {
        NetcdfFileWriteable ncFile = null;
        NetCdfDataStore netCdfDS = null;
        try {
            file.getAbsoluteFile();
            URL url = file.toURL();
            ncFile = NetcdfFileWriteable.openExisting(url.getPath());
            netCdfDS = new NetCdfDataStore(ncFile);
        } catch (IOException ie) {
            LOG.error("error getting file in createDataStore()", ie);
        } catch (DataStoreException dse) {
            LOG.error("error creating NetcdfDataStore in createDataStore()", dse);
        }
        return netCdfDS;
    }





}
