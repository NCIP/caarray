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

package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.staticinjection.CaArrayCommonStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.mindprod.ledatastream.LEDataOutputStream;

/**
 * Tests the AffymetrixCelHandler class
 */
public class AffymetrixCelHandlerTest {
    private static final int NumberOfRows = 10;
    private static final int NumberOfColumns = 10;
    private static final int NumberOfCells = NumberOfColumns * NumberOfRows;
    private static final int NumberOfSubgrids = 0;
    private static final int NumberOfMaskedCells = 0;
    private static final int NumberOfOutliers = 0;

    private static final float[][] ExpectedIntensityValues = new float[NumberOfRows][NumberOfColumns];
    private static final float[][] ExpectedStdDevValues = new float[NumberOfRows][NumberOfColumns];
    private static final int[][] ExpectedPixelCountValues = new int[NumberOfRows][NumberOfColumns];

    static {
        for (int y = 0; y < NumberOfColumns; y++) {
            for (int x = 0; x < NumberOfRows; x++) {
                final float intensity = 100f + x * 10f + y;
                final float stdDev = 10f + x / 10f + y / 100f;
                final int pixelCount = 200 + x * 10 + y;

                ExpectedIntensityValues[x][y] = intensity;
                ExpectedStdDevValues[x][y] = stdDev;
                ExpectedPixelCountValues[x][y] = pixelCount;
            }
        }
    }

    @Before
    public void baseIntegrationSetUp() {
        final FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(
                Sets.<DataFileHandler> newHashSet(new CelHandler(mock(DataStorageFacade.class))),
                Sets.<DesignFileHandler> newHashSet());
        Guice.createInjector(new CaArrayCommonStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(FileTypeRegistry.class).toInstance(typeRegistry);
                        requestStaticInjection(CaArrayFile.class);
                    }
                });
    }

    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();

    @Test
    public void loadsVersion3AsciiCelFile() throws IOException {
        final File testFile = generateVersion3AsciiCelFile();
        loadsCelFile(testFile);
    }

    @Test
    public void loadsVersion4BinaryXdaCelFile() throws IOException {
        final File testFile = generateVersion4BinaryXdaCelFile();
        loadsCelFile(testFile);
    }

    @Test
    public void loadsCommandConsoleVersion1CelFile() throws IOException {
        final File testFile = generateCommandConsoleVersion1CelFile();
        loadsCelFile(testFile);
    }

    private void loadsCelFile(final File testFile) {
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(testFile);
        caArrayFile.setFileType(CelHandler.CEL_FILE_TYPE);

        final DataStorageFacade dataStorageFacade = this.fileAccessServiceStub.createStorageFacade();
        final CelHandler affymetrixCelHandler = new CelHandler(dataStorageFacade);
        final DataSet dataSet = callLoadData(caArrayFile, affymetrixCelHandler);
        assertDataSetIsCorrect(dataSet);
    }

    private DataSet callLoadData(final CaArrayFile testFile, final CelHandler affymetrixCelHandler) {
        final DataSet dataSet = new DataSet();
        final List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();

        final Hybridization hybridization = new Hybridization();
        dataSet.addHybridizationData(hybridization);

        addQuantitationType(AffymetrixCelQuantitationType.CEL_X, dataSet, quantitationTypes);
        addQuantitationType(AffymetrixCelQuantitationType.CEL_Y, dataSet, quantitationTypes);
        addQuantitationType(AffymetrixCelQuantitationType.CEL_INTENSITY, dataSet, quantitationTypes);
        addQuantitationType(AffymetrixCelQuantitationType.CEL_INTENSITY_STD_DEV, dataSet, quantitationTypes);
        addQuantitationType(AffymetrixCelQuantitationType.CEL_PIXELS, dataSet, quantitationTypes);

        final ArrayDesign design = new ArrayDesign();
        design.setName("Test3");

        try {
            if (!affymetrixCelHandler.openFile(testFile)) {
                fail("Affymetrix handler did not recognize " + testFile.getName() + " as valid CEL");
            }
            affymetrixCelHandler.loadData(dataSet, quantitationTypes, design);
        } catch (final PlatformFileReadException e) {
            fail("Could not open CEL file: " + e);
        } finally {
            affymetrixCelHandler.closeFiles();
        }

        return dataSet;
    }

    private void addQuantitationType(final AffymetrixCelQuantitationType affymetrixQuantitationType,
            final DataSet dataSet, final List<QuantitationType> quantitationTypes) {
        final QuantitationType quantitationType = new QuantitationType();

        quantitationType.setName(affymetrixQuantitationType.getName());
        quantitationType.setTypeClass(affymetrixQuantitationType.getDataType().getTypeClass());

        quantitationTypes.add(quantitationType);
        dataSet.addQuantitationType(quantitationType);
    }

    private void assertDataSetIsCorrect(final DataSet dataSet) {
        final short[] xValues = getColumnShortValues(dataSet, AffymetrixCelQuantitationType.CEL_X);
        final short[] yValues = getColumnShortValues(dataSet, AffymetrixCelQuantitationType.CEL_Y);
        final float[] intensityValues = getColumnFloatValues(dataSet, AffymetrixCelQuantitationType.CEL_INTENSITY);
        final float[] stdDevValues = getColumnFloatValues(dataSet, AffymetrixCelQuantitationType.CEL_INTENSITY_STD_DEV);
        final short[] pixelValues = getColumnShortValues(dataSet, AffymetrixCelQuantitationType.CEL_PIXELS);

        assertColumnsHaveCorrectNumberOfValues(xValues, yValues, intensityValues, stdDevValues, pixelValues);
        assertColumnsHaveCorrectValues(xValues, yValues, intensityValues, stdDevValues, pixelValues);
    }

    private void assertColumnsHaveCorrectNumberOfValues(final short[] xValues, final short[] yValues,
            final float[] intensityValues, final float[] stdDevValues, final short[] pixelValues) {
        Assert.assertEquals(NumberOfCells, xValues.length);
        Assert.assertEquals(NumberOfCells, yValues.length);
        Assert.assertEquals(NumberOfCells, intensityValues.length);
        Assert.assertEquals(NumberOfCells, stdDevValues.length);
        Assert.assertEquals(NumberOfCells, pixelValues.length);
    }

    private void assertColumnsHaveCorrectValues(final short[] xValues, final short[] yValues,
            final float[] intensityValues, final float[] stdDevValues, final short[] pixelValues) {
        for (int y = 0; y < NumberOfColumns; y++) {
            for (int x = 0; x < NumberOfRows; x++) {

                final int index = y * NumberOfColumns + x;
                final float expectedIntensity = ExpectedIntensityValues[x][y];
                final float expectedStdDev = ExpectedStdDevValues[x][y];
                final int expectedPixels = ExpectedPixelCountValues[x][y];

                Assert.assertEquals(x, xValues[index]);
                Assert.assertEquals(y, yValues[index]);
                Assert.assertEquals(expectedIntensity, intensityValues[index], 0.01f);
                Assert.assertEquals(expectedStdDev, stdDevValues[index], 0.01f);
                Assert.assertEquals(expectedPixels, pixelValues[index]);
            }
        }
    }

    private short[] getColumnShortValues(final DataSet dataSet, final AffymetrixCelQuantitationType quantitationType) {
        final ShortColumn column = (ShortColumn) GetColumn(dataSet, quantitationType);
        return column.getValues();
    }

    private float[] getColumnFloatValues(final DataSet dataSet, final AffymetrixCelQuantitationType quantitationType) {
        final FloatColumn column = (FloatColumn) GetColumn(dataSet, quantitationType);
        return column.getValues();
    }

    private AbstractDataColumn GetColumn(final DataSet dataSet, final AffymetrixCelQuantitationType quantitationType) {
        return dataSet.getHybridizationDataList().get(0).getColumn(quantitationType);
    }

    private File generateVersion3AsciiCelFile() throws IOException {
        return generateCelFile("version_3_ascii_test_", new Version3AsciiCelFileContentsGenerator());
    }

    private File generateVersion4BinaryXdaCelFile() throws IOException {
        return generateCelFile("version_4_binary_xda_test_", new Version4BinaryXdaCelFileContentsGenerator());
    }

    private File generateCommandConsoleVersion1CelFile() throws IOException {
        return generateCelFile("command_console_version_1_test", new CommandConsoleVersion1CelFileContentsGenerator());
    }

    private File generateCelFile(final String fileNamePrefix, final CelFileContentsGenerator contentsGenerator)
            throws IOException {
        final File testFile = File.createTempFile(fileNamePrefix, ".CEL");
        final FileOutputStream outputStream = new FileOutputStream(testFile);

        final byte[] contents = contentsGenerator.generateContents();
        outputStream.write(contents);

        outputStream.close();
        return testFile;
    }

    private abstract class CelFileContentsGenerator {
        protected static final String DatHeader = "[12..40151]  Fetal 3:CLS=1167 RWS=1167 XIN=3  YIN=3  VE=17        2.0 08/16/01 17:28:31    \u0014  \u0014 Test3.1sq \u0014  \u0014  \u0014  \u0014  \u0014  \u0014  \u0014  \u0014  \u0014 6";
        protected static final int GridCornerULX = 154;
        protected static final int GridCornerULY = 164;
        protected static final int GridCornerURX = 995;
        protected static final int GridCornerURY = 160;
        protected static final int GridCornerLRX = 999;
        protected static final int GridCornerLRY = 1003;
        protected static final int GridCornerLLX = 158;
        protected static final int GridCornerLLY = 1006;
        protected static final int Percentile = 75;
        protected static final int CellMargin = 2;
        protected static final double OutlierHigh = 1.500;
        protected static final double OutlierLow = 1.004;

        public abstract byte[] generateContents() throws IOException;
    }

    private class Version3AsciiCelFileContentsGenerator extends CelFileContentsGenerator {
        @Override
        public byte[] generateContents() throws IOException {
            final ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            final PrintWriter writer = new PrintWriter(byteArrayStream);

            writer.println("[CEL]");
            writer.println("Version=3");
            writer.println();
            writer.println("[HEADER]");
            writer.format("Cols=%d\n", NumberOfColumns);
            writer.format("Rows=%d\n", NumberOfRows);
            writer.format("TotalX=10");
            writer.println("TotalY=10");
            writer.println("OffsetX=0");
            writer.println("OffsetY=0");
            writer.format("GridCornerUL=%d %d\n", GridCornerULX, GridCornerULY);
            writer.format("GridCornerUR=%d %d\n", GridCornerURX, GridCornerURY);
            writer.format("GridCornerLR=%d %d\n", GridCornerLRX, GridCornerLRY);
            writer.format("GridCornerLL=%d %d\n", GridCornerLLX, GridCornerLLY);
            writer.println("Axis-invertX=0");
            writer.println("AxisInvertY=0");
            writer.println("swapXY=0");
            writer.println("DatHeader=" + DatHeader);
            writer.println("Algorithm=Percentile");
            writer.format("AlgorithmParameters=Percentile:%d;CellMargin:%d;OutlierHigh:%.3f;OutlierLow:%.3f\n",
                    Percentile, CellMargin, OutlierHigh, OutlierLow);
            writer.println();
            writer.println("[INTENSITY]");
            writer.format("NumberCells=%d\n", NumberOfCells);
            writer.println("CellHeader=X\tY\tMEAN\tSTDV\tNPIXELS");

            for (int y = 0; y < NumberOfColumns; y++) {
                for (int x = 0; x < NumberOfRows; x++) {
                    final float intensity = ExpectedIntensityValues[x][y];
                    final float stdDev = ExpectedStdDevValues[x][y];
                    final int pixels = ExpectedPixelCountValues[x][y];

                    writer.format("%d\t%d\t%f\t%f\t%d", x, y, intensity, stdDev, pixels);
                    writer.println();
                }
            }

            writer.println();
            writer.println("[MASKS]");
            writer.println("NumberCells=0");
            writer.println("CellHeader=X\tY");
            writer.println();
            writer.println("[OUTLIERS]");
            writer.println("NumberCells=0");
            writer.println("CellHeader=X\tY");
            writer.println();
            writer.println("[MODIFIED]");
            writer.println("NumberCells=0");
            writer.println("CellHeader=X\tY\tORIGMEAN");

            writer.close();
            return byteArrayStream.toByteArray();
        }

    }

    private class Version4BinaryXdaCelFileContentsGenerator extends CelFileContentsGenerator {
        @Override
        public byte[] generateContents() throws IOException {
            final int MagicNumber = 64;

            final ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            final LEDataOutputStream leDataStream = new LEDataOutputStream(byteArrayStream);

            final byte[] header = generateHeader();

            leDataStream.writeInt(MagicNumber);
            leDataStream.writeInt(4);
            leDataStream.writeInt(NumberOfColumns);
            leDataStream.writeInt(NumberOfRows);
            leDataStream.writeInt(NumberOfCells);
            leDataStream.writeInt(header.length);
            leDataStream.write(header);
            writeString(leDataStream, "Percentile");
            writeString(leDataStream, "Percentile:%d;CellMargin:%d;OutlierHigh:%.3f;OutlierLow:%.3f", Percentile,
                    CellMargin, OutlierHigh, OutlierLow);
            leDataStream.writeInt(CellMargin);
            leDataStream.writeInt(NumberOfOutliers);
            leDataStream.writeInt(NumberOfMaskedCells);
            leDataStream.writeInt(NumberOfSubgrids);

            for (int y = 0; y < NumberOfColumns; y++) {
                for (int x = 0; x < NumberOfRows; x++) {
                    final float intensity = ExpectedIntensityValues[x][y];
                    final float stdDev = ExpectedStdDevValues[x][y];
                    final int pixels = ExpectedPixelCountValues[x][y];

                    leDataStream.writeFloat(intensity);
                    leDataStream.writeFloat(stdDev);
                    leDataStream.writeShort(pixels);
                }
            }

            leDataStream.close();
            return byteArrayStream.toByteArray();
        }

        private byte[] generateHeader() throws IOException {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final PrintWriter writer = new PrintWriter(byteArrayOutputStream);

            writer.format("Cols=%d\n", NumberOfColumns);
            writer.format("Rows=%d\n", NumberOfRows);
            writer.println("TotalX=10");
            writer.println("TotalY=10");
            writer.println("OffsetX=0");
            writer.println("OffsetY=0");
            writer.format("GridCornerUL=%d %d\n", GridCornerULX, GridCornerULY);
            writer.format("GridCornerUR=%d %d\n", GridCornerURX, GridCornerURY);
            writer.format("GridCornerLR=%d %d\n", GridCornerLRX, GridCornerLRY);
            writer.format("GridCornerLL=%d %d\n", GridCornerLLX, GridCornerLLY);
            writer.println("AxisInvertY=0");
            writer.println("swapXY=0");
            writer.println("DatHeader=" + DatHeader);
            writer.println("Algorithm=Percentile");
            writer.format("AlgorithmParameters=Percentile:%d;CellMargin:%d;OutlierHigh:%.3f;OutlierLow:%.3f",
                    Percentile, CellMargin, OutlierHigh, OutlierLow);

            writer.close();
            return byteArrayOutputStream.toByteArray();
        }

        private void writeString(final LEDataOutputStream leDataStream, final String text) throws IOException {
            leDataStream.writeInt(text.length());
            leDataStream.write(text.getBytes());
        }

        private void writeString(final LEDataOutputStream leDataStream, final String format,
                final java.lang.Object... objects) throws IOException {
            final String text = String.format(format, objects);
            leDataStream.writeInt(text.length());
            leDataStream.write(text.getBytes());
        }

    }

    private class CommandConsoleVersion1CelFileContentsGenerator extends CelFileContentsGenerator {

        @Override
        public byte[] generateContents() throws IOException {
            byte[] contents = null;

            final int numberOfDataSets = 5;

            int dataGroupOffset = 0;
            final int[] dataSetFileOffsets = new int[numberOfDataSets + 1];
            final int[] dataSetElementsFileOffsets = new int[numberOfDataSets];

            // In order to compute the embedded file offsets, we make two passes
            for (int pass = 0; pass < 2; pass++) {
                final ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
                final DataOutputStream dataStream = new DataOutputStream(byteArrayStream);

                final int numberOfDataGroups = 1;

                WriteFileHeader(dataGroupOffset, dataStream, numberOfDataGroups);
                WriteGenericDataHeader(dataStream);
                WriteParentFileHeader(dataStream);
                WriteGrandparentFileHeader(dataStream);

                dataGroupOffset = getCurrentOffset(dataStream, byteArrayStream);
                WriteDataGroupHeader(dataSetFileOffsets[0], dataStream, numberOfDataSets);

                dataSetFileOffsets[0] = getCurrentOffset(dataStream, byteArrayStream);
                dataSetElementsFileOffsets[0] = WriteFirstDataSet(dataSetElementsFileOffsets[0], dataSetFileOffsets[1],
                        byteArrayStream, dataStream);

                dataSetFileOffsets[1] = getCurrentOffset(dataStream, byteArrayStream);
                dataSetElementsFileOffsets[1] = WriteSecondDataSet(dataSetElementsFileOffsets[1],
                        dataSetFileOffsets[2], byteArrayStream, dataStream);

                dataSetFileOffsets[2] = getCurrentOffset(dataStream, byteArrayStream);
                dataSetElementsFileOffsets[2] = WriteThirdDataSet(dataSetElementsFileOffsets[2], dataSetFileOffsets[3],
                        byteArrayStream, dataStream);

                dataSetFileOffsets[3] = getCurrentOffset(dataStream, byteArrayStream);
                dataSetElementsFileOffsets[3] = WriteFourthDataSet(dataSetElementsFileOffsets[3],
                        dataSetFileOffsets[4], byteArrayStream, dataStream);

                dataSetFileOffsets[4] = getCurrentOffset(dataStream, byteArrayStream);
                dataSetElementsFileOffsets[4] = WriteFifthDataSet(dataSetElementsFileOffsets[4], dataSetFileOffsets[5],
                        byteArrayStream, dataStream);

                dataSetFileOffsets[5] = getCurrentOffset(dataStream, byteArrayStream);

                dataStream.close();
                contents = byteArrayStream.toByteArray();
            }

            return contents;
        }

        private void WriteFileHeader(final int dataGroupOffset, final DataOutputStream dataStream,
                final int numberOfDataGroups) throws IOException {
            final int magicNumber = 59;
            final int fileVersion = 1;

            dataStream.writeByte(magicNumber);
            dataStream.writeByte(fileVersion);
            dataStream.writeInt(numberOfDataGroups);
            dataStream.writeInt(dataGroupOffset);
        }

        private void WriteGenericDataHeader(final DataOutputStream dataStream) throws IOException {
            final int numberOfGenericDataParameters = 34;
            final int numberOfParentFileHeaders = 1;

            writeString(dataStream, "affymetrix-calvin-intensity");
            writeString(dataStream, "0000025988-1167932903-0000026962-0000029358-0000011478");
            writeWideString(dataStream, "2010-02-14T13:45:53Z");
            writeWideString(dataStream, "en-US");
            dataStream.writeInt(numberOfGenericDataParameters);
            writePlainTextParameter(dataStream, "program-company", "Affymetrix, Inc.");
            writePlainTextParameter(dataStream, "program-name", "Data Exchange Console");
            writePlainTextParameter(dataStream, "program-id", "9999.9999.9999.9999");
            writePlainTextParameter(dataStream, "affymetrix-system-type", "RUO BETA3");
            writePlainTextParameter(dataStream, "affymetrix-file-creator", "");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-Percentile", "%d", Percentile);
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-CellMargin", "%d", CellMargin);
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-OutlierHigh", "%.3f", OutlierHigh);
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-OutlierLow", "%.3f", OutlierLow);
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-AlgVersion", "6.0");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-FixedCellSize", "TRUE");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-FullFeatureWidth", "7");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-FullFeatureHeight", "7");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-IgnoreOutliersInShiftRows", "FALSE");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-FeatureExtraction", "TRUE");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-PoolWidthExtenstion", "2");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-PoolHeightExtension", "2");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-UseSubgrids", "FALSE");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-RandomizePixels", "FALSE");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-ErrorBasis", "StdvMean");
            writeAsciiTextParameter(dataStream, "affymetrix-algorithm-param-StdMult", "1.000000");
            writeCalvinFloatTextParameter(dataStream, "affymetrix-algorithm-param-GridULX", GridCornerULX);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-algorithm-param-GridULY", GridCornerULY);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-algorithm-param-GridURX", GridCornerURX);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-algorithm-param-GridURY", GridCornerURY);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-algorithm-param-GridLRX", GridCornerLRX);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-algorithm-param-GridLRY", GridCornerLRY);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-algorithm-param-GridLLX", GridCornerLLX);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-algorithm-param-GridLLY", GridCornerLLY);
            writePlainTextParameter(dataStream, "affymetrix-algorithm-name", "Percentile");
            writePlainTextParameter(dataStream, "affymetrix-array-type", "Cotton");
            writeCalvinInteger32Parameter(dataStream, "affymetrix-cel-cols", NumberOfColumns);
            writeCalvinInteger32Parameter(dataStream, "affymetrix-cel-rows", NumberOfRows);
            writeCalvinUnsignedInteger8Parameter(dataStream, "affymetrix-file-version", 1);
            dataStream.writeInt(numberOfParentFileHeaders);
        }

        private void WriteParentFileHeader(final DataOutputStream dataStream) throws IOException {
            final int numberOfParameters = 11;
            final int numberOfGrandparentFileHeaders = 1;

            final String fileDataTypeIdentifier = "affymetrix-calvin-scan-acquisition";
            final String fileUniqueIdentifier = "";
            final String fileTimeStamp = "";
            final String fileLocale = "en-US";

            writeString(dataStream, fileDataTypeIdentifier);
            writeString(dataStream, fileUniqueIdentifier);
            writeWideString(dataStream, fileTimeStamp);
            writeWideString(dataStream, fileLocale);

            dataStream.writeInt(numberOfParameters);
            writePlainTextParameter(dataStream, "affymetrix-dat-header", DatHeader);
            writePlainTextParameter(dataStream, "affymetrix-array-type", "Cotton");
            writeCalvinInteger32Parameter(dataStream, "affymetrix-pixel-rows", 5521);
            writeCalvinInteger32Parameter(dataStream, "affymetrix-pixel-cols", 5521);
            writePlainTextParameter(dataStream, "affymetrix-scan-date", "2006-10-27T11:43:45");
            writePlainTextParameter(dataStream, "affymetrix-scanner-id", "50206820");
            writePlainTextParameter(dataStream, "affymetrix-scanner-type", "M10");
            writeCalvinFloatTextParameter(dataStream, "affymetrix-fusion-arcradius", 25410.4f);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-fusion-laser-spotsize", 3.5f);
            writeCalvinFloatTextParameter(dataStream, "affymetrix-pixel-size", 1.56f);
            writeCalvinUnsignedInteger8Parameter(dataStream, "affymetrix-image-orientation", 6);
            dataStream.writeInt(numberOfGrandparentFileHeaders);
        }

        private void WriteGrandparentFileHeader(final DataOutputStream dataStream) throws IOException {
            final int numberOfParameters = 2;
            final int numberOfGreatGrandparentFileHeaders = 0;

            writeString(dataStream, "affymetrix-calvin-array");
            writeString(dataStream, "");
            writeWideString(dataStream, "");
            writeWideString(dataStream, "en-US");
            dataStream.writeInt(numberOfParameters);
            writeAsciiTextParameter(dataStream, "affymetrix-array-id", "08ae4e3a-42db-45e5-8b21-2addc4e4b19e");
            writePlainTextParameter(dataStream, "affymetrix-array-barcode", "");
            dataStream.writeInt(numberOfGreatGrandparentFileHeaders);
        }

        private void WriteDataGroupHeader(final int nextDataSetFileOffset, final DataOutputStream dataStream,
                final int numberOfDataSets) throws IOException {
            final String dataGroupName = "Default Group";
            final int nextDataGroupFileOffset = 0; // Because there is no second data group, this is set to zero

            dataStream.writeInt(nextDataGroupFileOffset);
            dataStream.writeInt(nextDataSetFileOffset);
            dataStream.writeInt(numberOfDataSets);
            writeWideString(dataStream, dataGroupName);
        }

        private int WriteFirstDataSet(int currentDataSetElementsFileOffset, final int nextDataSetFileOffset,
                final ByteArrayOutputStream byteArrayStream, final DataOutputStream dataStream) throws IOException {
            final String dataSetName = "Intensity";

            final int numberOfParameters = 0;
            final int numberOfColumns = 1;
            final int numberOfRows = NumberOfCells;

            dataStream.writeInt(currentDataSetElementsFileOffset);
            dataStream.writeInt(nextDataSetFileOffset);
            writeWideString(dataStream, dataSetName);

            dataStream.writeInt(numberOfParameters);

            dataStream.writeInt(numberOfColumns);
            writeFloatColumnDescriptor(dataStream, "Intensity");

            // Write data set elements
            dataStream.writeInt(numberOfRows);
            currentDataSetElementsFileOffset = getCurrentOffset(dataStream, byteArrayStream);
            for (int y = 0; y < NumberOfColumns; y++) {
                for (int x = 0; x < NumberOfRows; x++) {
                    final float intensity = ExpectedIntensityValues[x][y];
                    dataStream.writeFloat(intensity);
                }
            }
            return currentDataSetElementsFileOffset;
        }

        private int WriteSecondDataSet(int currentDataSetElementsFileOffset, final int nextDataSetFileOffset,
                final ByteArrayOutputStream byteArrayStream, final DataOutputStream dataStream) throws IOException {
            final String dataSetName = "StdDev";

            final int numberOfParameters = 0;
            final int numberOfColumns = 1;
            final int numberOfRows = NumberOfCells;

            dataStream.writeInt(currentDataSetElementsFileOffset);
            dataStream.writeInt(nextDataSetFileOffset);
            writeWideString(dataStream, dataSetName);

            dataStream.writeInt(numberOfParameters);

            dataStream.writeInt(numberOfColumns);
            writeFloatColumnDescriptor(dataStream, "StdDev");

            // Write data set elements
            dataStream.writeInt(numberOfRows);
            currentDataSetElementsFileOffset = getCurrentOffset(dataStream, byteArrayStream);
            for (int y = 0; y < NumberOfColumns; y++) {
                for (int x = 0; x < NumberOfRows; x++) {
                    final float stdDev = ExpectedStdDevValues[x][y];
                    dataStream.writeFloat(stdDev);
                }
            }
            return currentDataSetElementsFileOffset;
        }

        private int WriteThirdDataSet(int currentDataSetElementsFileOffset, final int nextDataSetFileOffset,
                final ByteArrayOutputStream byteArrayStream, final DataOutputStream dataStream) throws IOException {
            final String dataSetName = "Pixel";

            final int numberOfParameters = 0;
            final int numberOfColumns = 1;
            final int numberOfRows = NumberOfCells;

            dataStream.writeInt(currentDataSetElementsFileOffset);
            dataStream.writeInt(nextDataSetFileOffset);
            writeWideString(dataStream, dataSetName);

            dataStream.writeInt(numberOfParameters);

            dataStream.writeInt(numberOfColumns);
            writeShortColumnDescriptor(dataStream, "Pixel");

            // Write data set elements
            dataStream.writeInt(numberOfRows);
            currentDataSetElementsFileOffset = getCurrentOffset(dataStream, byteArrayStream);
            for (int y = 0; y < NumberOfColumns; y++) {
                for (int x = 0; x < NumberOfRows; x++) {
                    final int pixels = ExpectedPixelCountValues[x][y];
                    dataStream.writeShort(pixels);
                }
            }
            return currentDataSetElementsFileOffset;
        }

        private int WriteFourthDataSet(final int currentDataSetElementsFileOffset, int nextDataSetFileOffset,
                final ByteArrayOutputStream byteArrayStream, final DataOutputStream dataStream) throws IOException {
            final String dataSetName = "Outlier";

            final int numberOfParameters = 0;
            final int numberOfColumns = 2;
            final int numberOfRows = 0;

            dataStream.writeInt(currentDataSetElementsFileOffset);
            dataStream.writeInt(nextDataSetFileOffset);
            writeWideString(dataStream, dataSetName);

            dataStream.writeInt(numberOfParameters);

            dataStream.writeInt(numberOfColumns);
            writeShortColumnDescriptor(dataStream, "X"); // Column name
            writeShortColumnDescriptor(dataStream, "Y"); // Column name

            nextDataSetFileOffset = getCurrentOffset(dataStream, byteArrayStream);

            // Write data set elements
            dataStream.writeInt(numberOfRows);
            // No data elements

            return nextDataSetFileOffset;
        }

        private int WriteFifthDataSet(int currentDataSetElementsFileOffset, final int nextDataSetFileOffset,
                final ByteArrayOutputStream byteArrayStream, final DataOutputStream dataStream) throws IOException {
            final String dataSetName = "Mask";

            final int numberOfParameters = 0;
            final int numberOfColumns = 2;
            final int numberOfRows = 0;

            dataStream.writeInt(currentDataSetElementsFileOffset);
            dataStream.writeInt(nextDataSetFileOffset);
            writeWideString(dataStream, dataSetName);

            dataStream.writeInt(numberOfParameters);

            dataStream.writeInt(numberOfColumns);
            writeShortColumnDescriptor(dataStream, "X"); // Column name
            writeShortColumnDescriptor(dataStream, "Y"); // Column name

            currentDataSetElementsFileOffset = getCurrentOffset(dataStream, byteArrayStream);

            // Write data set elements
            dataStream.writeInt(numberOfRows);
            // No data elements

            return currentDataSetElementsFileOffset;
        }

        private int getCurrentOffset(final DataOutputStream dataStream, final ByteArrayOutputStream byteArrayStream)
                throws IOException {
            int offset;
            dataStream.flush();
            offset = byteArrayStream.size();
            return offset;
        }

        private void writePlainTextParameter(final DataOutputStream dataStream, final String name, final String value)
                throws IOException {
            writeWideString(dataStream, name);
            dataStream.writeInt(value.length() * 2);
            dataStream.writeChars(value);
            writeWideString(dataStream, "text/plain");
        }

        private void writeAsciiTextParameter(final DataOutputStream dataStream, final String name, final String format,
                final Object... objects) throws IOException {
            final String value = String.format(format, objects);
            writeAsciiTextParameter(dataStream, name, value);
        }

        private void writeAsciiTextParameter(final DataOutputStream dataStream, final String name, final String value)
                throws IOException {
            writeWideString(dataStream, name);
            writeString(dataStream, value);
            writeWideString(dataStream, "text/ascii");
        }

        private void writeString(final DataOutputStream dataStream, final String text) throws IOException {
            dataStream.writeInt(text.length());
            dataStream.write(text.getBytes());
        }

        private void writeWideString(final DataOutputStream dataStream, final String text) throws IOException {
            dataStream.writeInt(text.length());
            dataStream.writeChars(text);
        }

        private void writeCalvinFloatTextParameter(final DataOutputStream dataStream, final String name,
                final float value) throws IOException {
            writeWideString(dataStream, name);

            final int valueSize = 16;
            dataStream.writeInt(valueSize);
            dataStream.writeFloat(value);
            WriteTwelveNullBytes(dataStream);

            writeWideString(dataStream, "text/x-calvin-float");
        }

        private void writeCalvinInteger32Parameter(final DataOutputStream dataStream, final String name, final int value)
                throws IOException {
            writeWideString(dataStream, name);

            final int valueSize = 16;
            dataStream.writeInt(valueSize);
            dataStream.writeInt(value);
            WriteTwelveNullBytes(dataStream);

            writeWideString(dataStream, "text/x-calvin-integer-32");
        }

        private void writeCalvinUnsignedInteger8Parameter(final DataOutputStream dataStream, final String name,
                final int value) throws IOException {
            writeWideString(dataStream, name);

            final int valueSize = 16;
            dataStream.writeInt(valueSize);
            dataStream.writeInt(value);
            WriteTwelveNullBytes(dataStream);

            writeWideString(dataStream, "text/x-calvin-unsigned-integer-8");
        }

        private void WriteTwelveNullBytes(final DataOutputStream dataStream) throws IOException {
            dataStream.writeInt(0);
            dataStream.writeInt(0);
            dataStream.writeInt(0);
        }

        private void writeFloatColumnDescriptor(final DataOutputStream dataStream, final String columnName)
                throws IOException {
            final int columnDataType = 6;
            final int columnDataTypeSize = 4;

            writeWideString(dataStream, columnName);
            dataStream.writeByte(columnDataType);
            dataStream.writeInt(columnDataTypeSize);
        }

        private void writeShortColumnDescriptor(final DataOutputStream dataStream, final String columnName)
                throws IOException {
            final int columnDataType = 2;
            final int columnDataTypeSize = 2;

            writeWideString(dataStream, columnName);
            dataStream.writeByte(columnDataType);
            dataStream.writeInt(columnDataTypeSize);
        }

    }
}
