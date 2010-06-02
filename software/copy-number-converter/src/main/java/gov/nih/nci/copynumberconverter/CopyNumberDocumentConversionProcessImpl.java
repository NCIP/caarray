/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and 
 * have distributed to and by third parties the caarray-ejb-jar Software and any 
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
package gov.nih.nci.copynumberconverter;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactory;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileWriterFactory;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFilesModule;
import com.fiveamsolutions.nci.commons.util.io.DelimitedWriter;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * This class does the actual processing for copy number to MAGE-TAB data matrix conversion.
 * @author dharley
 *
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.CyclomaticComplexity" })
public final class CopyNumberDocumentConversionProcessImpl implements CopyNumberDocumentConversionProcess {
    
    private static final String HYBRIDIZATION_REF_OUTPUT_COLUMN_HEADER = "Hybridization REF";
    private static final String REPORTER_REF_OUTPUT_COLUMN_HEADER = "Reporter REF";
    private static final String CHROMOSOME_OUTPUT_COLUMN_HEADER = "Chromosome";
    private static final String POSITION_OUTPUT_COLUMN_HEADER = "Position";
    private static final String LOG2RATIO_OUTPUT_COLUMN_HEADER = "Log2Ratio";
    private static final String NULL_STRING = "";
    
    private File inputFile;
    private File outputFile;
    private String probeNameColumnHeader;
    private int probeNameColumnIndex = -1;
    private String chromosomeIdColumnHeader;
    private int chromosomeIdColumnIndex = -1;
    private String chromosomePositionColumnHeader;
    private int chromosomePositionColumnIndex = -1;
    private String log2RatiosColumnHeader;
    private int log2RatiosColumnIndex = -1;
    private String delimiter;
    private boolean isUsingPrefix; //false in multi-hyb situation means using suffix
    private String hybridizationName;
    private int numberOfOutputColumns = 0;
    private int numberOfHybridizations = 0;
    private List<String> hybridizationNamesForCaseOfMultiples;
    private List<Integer> hybridizationColumnIndexes;
    
    /**
     * Creates a new CopyNumberDocumentConversionProcess instance.
     */
    public CopyNumberDocumentConversionProcessImpl() {
        // this is intentionally empty
    }
    
    /**
     * {@inheritDoc}
     */
    public void convert(final String inputFileString, final String outputFileString,
            final ColumnInfo columnInfo) throws ConversionException {
        this.inputFile = new File(inputFileString);
        this.outputFile = new File(outputFileString);
        this.probeNameColumnHeader = columnInfo.getProbeNameColumnHeader();
        this.chromosomeIdColumnHeader = columnInfo.getChromosomeIdColumnHeader();
        this.chromosomePositionColumnHeader = columnInfo.getChromosomePositionColumnHeader();
        this.log2RatiosColumnHeader = columnInfo.getLog2RatiosColumnHeader();
        this.delimiter = columnInfo.getDelimiter();
        this.isUsingPrefix = columnInfo.isUsingPrefix();
        this.hybridizationName = columnInfo.getHybridizationName();
        DelimitedFileReader  tabDelimitedReader = null;
        DelimitedWriter tabDelimitedWriter = null;
        try {
            Injector injector = Guice.createInjector(new DelimitedFilesModule());
            DelimitedFileReaderFactory readerFactory = injector.getInstance(DelimitedFileReaderFactory.class);
            tabDelimitedReader = readerFactory.createTabDelimitedFileReader(inputFile);
            DelimitedFileWriterFactory delimitedFileWriterFactory =
                injector.getInstance(DelimitedFileWriterFactory.class);
            tabDelimitedWriter = delimitedFileWriterFactory.createTabDelimitedWriter(outputFile);
            List<String> headerLine = tabDelimitedReader.nextLine();
            analyzeColumns(headerLine);
            writeHeader(headerLine, tabDelimitedWriter);
            while (tabDelimitedReader.hasNextLine()) {
                tabDelimitedWriter.writeLine(getNewLineOfData(tabDelimitedReader.nextLine()));
            }
        } catch (IOException e) {
            throw new ConversionException("Cannot convert: " + e.getMessage(), e);
        } finally {
            if (null != tabDelimitedReader) {
                tabDelimitedReader.close();
            }
            if (null != tabDelimitedWriter) {
                tabDelimitedWriter.close();
            }
        }
    }
    
    @SuppressWarnings({ "PMD.NPathComplexity" })
    private void analyzeColumns(final List<String> headerLine) throws ConversionException {
        if (!StringUtils.isEmpty(hybridizationName)) {
            numberOfHybridizations = 1;
            int index = 0;
            for (String columnHeader : headerLine) {
                if (columnHeader.equalsIgnoreCase(log2RatiosColumnHeader)) {
                    log2RatiosColumnIndex = index;
                    break;
                }
                index++;
            }
            if (-1 == log2RatiosColumnIndex) {
                throw new ConversionException("Cannot locate " + log2RatiosColumnHeader + " column in single " 
                        + "hybridization file " + inputFile.getAbsolutePath() + ".");
            }
        } else {
            numberOfHybridizations = processMultipleHybridizationsColumnHeaders(headerLine);
        }
        numberOfOutputColumns = numberOfHybridizations;
        int index = 0;
        for (String columnHeader : headerLine) {
            if (columnHeader.equalsIgnoreCase(probeNameColumnHeader)) {
                probeNameColumnIndex = index;
                numberOfOutputColumns++;
            } else if (columnHeader.equalsIgnoreCase(chromosomeIdColumnHeader)) {
                chromosomeIdColumnIndex = index;
                numberOfOutputColumns++;
            } else if (columnHeader.equalsIgnoreCase(chromosomePositionColumnHeader)) {
                chromosomePositionColumnIndex = index;
                numberOfOutputColumns++;
            }
            index++;
        }
        if (-1 == probeNameColumnIndex) {
            throw new ConversionException("Cannot locate " + probeNameColumnHeader + " column in "
                    + "hybridization file " + inputFile.getAbsolutePath() + ".");
        }
        if (-1 == chromosomeIdColumnIndex) {
            throw new ConversionException("Cannot locate " + chromosomeIdColumnHeader + " column in "
                    + "hybridization file " + inputFile.getAbsolutePath() + ".");
        }
        if (-1 == chromosomePositionColumnIndex) {
            throw new ConversionException("Cannot locate " + chromosomePositionColumnHeader + " column in "
                    + "hybridization file " + inputFile.getAbsolutePath() + ".");
        }
    }
    
    @SuppressWarnings({ "PMD.UseLocaleWithCaseConversions" })
    private int processMultipleHybridizationsColumnHeaders(final List<String> headerLine) throws ConversionException {
        int numberOfHybridizationsFound = 0;
        hybridizationNamesForCaseOfMultiples = new ArrayList<String>();
        hybridizationColumnIndexes = new ArrayList<Integer>();
        int index = 0;
        for (String columnHeader : headerLine) {
            if (columnHeader.toUpperCase().indexOf(log2RatiosColumnHeader.toUpperCase()) >= 0) {
                String[] splitColumnHeader = StringUtils.split(columnHeader, delimiter);
                if (splitColumnHeader.length != 2) {
                    throw new ConversionException("'" + columnHeader + "' appears to be an invalid column header "
                            + "as it did not split correctly using the specified delimiter '" + delimiter + "'.");
                } else {
                    if (isUsingPrefix) {
                        hybridizationNamesForCaseOfMultiples.add(splitColumnHeader[0]);
                    } else {
                        hybridizationNamesForCaseOfMultiples.add(splitColumnHeader[1]);
                    }
                }
                numberOfHybridizationsFound++;
                hybridizationColumnIndexes.add(Integer.valueOf(index));
            }
            index++;
        }
        return numberOfHybridizationsFound;
    }
    
    private List<String> getNewLineOfData(final List<String> rawDataLine) {
        List<String> newLineOfData = new ArrayList<String>();
        newLineOfData.add(rawDataLine.get(probeNameColumnIndex));
        if (chromosomeIdColumnIndex != -1) {
            newLineOfData.add(rawDataLine.get(chromosomeIdColumnIndex));
        }
        if (chromosomePositionColumnIndex != -1) {
            newLineOfData.add(rawDataLine.get(chromosomePositionColumnIndex));
        }
        if (numberOfHybridizations > 1) {
            for (int i = 0; i < numberOfHybridizations; i++) {
                newLineOfData.add(rawDataLine.get(hybridizationColumnIndexes.get(i).intValue()));
            }
        } else {
            newLineOfData.add(rawDataLine.get(log2RatiosColumnIndex));
        }
        return newLineOfData;
    }
    
    private void writeHeader(final List<String> inputHeaderLine, final DelimitedWriter delimitedWriter)
        throws IOException {
        List<String> firstHeaderLine = new ArrayList<String>();
        firstHeaderLine.add(HYBRIDIZATION_REF_OUTPUT_COLUMN_HEADER);
        for (int i = 0; i < (numberOfOutputColumns - (numberOfHybridizations + 1)); i++) {
            firstHeaderLine.add(NULL_STRING);
        }
        if (numberOfHybridizations > 1) {
            for (String aHybridizationName : hybridizationNamesForCaseOfMultiples) {
                firstHeaderLine.add(aHybridizationName);
            }
        } else {
            firstHeaderLine.add(hybridizationName);
        }
        delimitedWriter.writeLine(firstHeaderLine);
        firstHeaderLine = null;
        List<String> secondHeaderLine = new ArrayList<String>();
        secondHeaderLine.add(REPORTER_REF_OUTPUT_COLUMN_HEADER);
        secondHeaderLine.add(CHROMOSOME_OUTPUT_COLUMN_HEADER);
        secondHeaderLine.add(POSITION_OUTPUT_COLUMN_HEADER);
        for (int i = 0; i < numberOfHybridizations; i++) {
            secondHeaderLine.add(LOG2RATIO_OUTPUT_COLUMN_HEADER);
        }
        delimitedWriter.writeLine(secondHeaderLine);
    }

}