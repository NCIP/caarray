//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.copynumberconverter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Command-line utility users can utilize in order to convert copy number data files into MAGE-TAB data matrix files.
 * 
 * @author dharley
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public final class CopyNumberToMageTabDataMatrixConverter {
    
    private static final String INPUT_FILE = "input_file";
    private static final String OUTPUT_FILE = "output_file";
    private static final String PROBE_NAMES_HEADER = "probe_names_header";
    private static final String CHROMOSOME_ID_HEADER = "chromosome_id_header";
    private static final String CHROMOSOME_POSITION_HEADER = "chromosome_position_header";
    private static final String LOG2RATIO_VALUES_HEADER = "log2ratio_values_header";
    private static final String HYBRIDIZATION_PREFIX = "hybridization_prefix";
    private static final String HYBRIDIZATION_SUFFIX = "hybridization_suffix";
    private static final String HYBRIDIZATION_DELIMITER = "hybridization_delimiter";
    private static final String HYBRIDIZATION_NAME = "hybridization_name";
    private static final Options OPTIONS = getOptions();
    
    /**
     * Runs the conversion process for copy number data file -> MAGE-TAB data matrix file.
     * 
     * @param arguments -input_file REQUIRED path to the copy number data file to convert
     * -output_file REQUIRED MAGE-TAB path to the data matrix file to generate (will replace if already exists)
     * -probe_names_header REQUIRED header of column containing probe names
     * -chromosome_id_header OPTIONAL header of column containing chromosome IDs
     * -chromosome_position_header OPTIONAL header of column containing chromosome positions
     * -log2ratio_values_header REQUIRED header of column containing log2ratio data
     * -hybridization_prefix OPTIONAL flag indicates multi-hybridization input file has hybridization name as prefix to
     * log2ratio_values_header value with specified delimiter
     * -hybridization_suffix OPTIONAL flag indicates multi-hybridization input file has hybridization name as suffix to
     *  log2ratio_values_header value with specified delimiter
     * -hybridization_delimiter OPTIONAL delimter used to seperate the hybridization name from log2ratio_values_header
     * value
     * -hybridization_name OPTIONAL name of the hybridization for single-hybridization input file
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(final String[] arguments) {
        Injector injector = Guice.createInjector(new ConversionModule());
        CopyNumberDocumentConversionProcess conversionProcess = 
            injector.getInstance(CopyNumberDocumentConversionProcess.class);
        try {
            System.out.println("Starting conversion process...");
            new CopyNumberToMageTabDataMatrixConverter(arguments, conversionProcess);
            System.out.println("Done.");
        } catch (InvalidOptionsException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("java -jar cn2magetab.jar", OPTIONS);
        } catch (ConversionException e) {
            System.err.println("Conversion process failed: " + e.getMessage());
            System.err.println("" + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
    
    /**
     * Constructs and executes a new CopyNumberToMageTabDataMatrixConverter.
     * @param options the conversion process options
     * @param conversionProcess the conversion process to execute.
     * @throws InvalidOptionsException if incorrect options
     * @throws ConversionException if conversion fails
     */
    public CopyNumberToMageTabDataMatrixConverter(final String[] options,
            final CopyNumberDocumentConversionProcess conversionProcess)
            throws InvalidOptionsException, ConversionException {
        CommandLine commandLine = validateOptions(options);
        conversionProcess.convert(commandLine.getOptionValue(INPUT_FILE),
                commandLine.getOptionValue(OUTPUT_FILE), buildColumnInfo(commandLine));
    }
    
    private ColumnInfo buildColumnInfo(final CommandLine commandLine) {
        return new ColumnInfo(
                commandLine.getOptionValue(PROBE_NAMES_HEADER),
                commandLine.getOptionValue(CHROMOSOME_ID_HEADER),
                commandLine.getOptionValue(CHROMOSOME_POSITION_HEADER),
                commandLine.getOptionValue(LOG2RATIO_VALUES_HEADER),
                commandLine.getOptionValue(HYBRIDIZATION_DELIMITER),
                commandLine.hasOption(HYBRIDIZATION_PREFIX),
                commandLine.getOptionValue(HYBRIDIZATION_NAME));
    }

    @SuppressWarnings({ "PMD.NPathComplexity" })
    private CommandLine validateOptions(final String[] options) throws InvalidOptionsException {
        CommandLine commandLine;
        try {
            commandLine = new PosixParser().parse(OPTIONS, options);
        } catch (ParseException e) {
            throw new InvalidOptionsException(e.getMessage(), e);
        }
        checkForMandatoryOption(INPUT_FILE, commandLine);
        checkForMandatoryOption(OUTPUT_FILE, commandLine);
        checkForMandatoryOption(PROBE_NAMES_HEADER, commandLine);
        checkForMandatoryOption(LOG2RATIO_VALUES_HEADER, commandLine);
        checkForMandatoryOption(CHROMOSOME_ID_HEADER, commandLine);
        checkForMandatoryOption(CHROMOSOME_POSITION_HEADER, commandLine);
        if (commandLine.hasOption(HYBRIDIZATION_NAME)
            && (commandLine.hasOption(HYBRIDIZATION_PREFIX) || commandLine.hasOption(HYBRIDIZATION_SUFFIX)
            || commandLine.hasOption(HYBRIDIZATION_DELIMITER))) {
            throw new InvalidOptionsException("The " + HYBRIDIZATION_NAME + " option indicates single hybridization, "
                    + "so all multi-hybridization options are illegal.");
        }
        if (!commandLine.hasOption(HYBRIDIZATION_NAME)
                && !(commandLine.hasOption(HYBRIDIZATION_PREFIX) || commandLine.hasOption(HYBRIDIZATION_SUFFIX))) {
            throw new InvalidOptionsException("No -" + HYBRIDIZATION_NAME + " option indicates multi hybridization, "
                    + "but neither -" + HYBRIDIZATION_PREFIX + " nor -" + HYBRIDIZATION_SUFFIX + " were specified.");
        }
        if (commandLine.hasOption(HYBRIDIZATION_PREFIX) && commandLine.hasOption(HYBRIDIZATION_SUFFIX)) {
            throw new InvalidOptionsException("Cannot specify both -" + HYBRIDIZATION_PREFIX + " and -"
                    + HYBRIDIZATION_SUFFIX + ".");
        }
        if ((commandLine.hasOption(HYBRIDIZATION_PREFIX) || commandLine.hasOption(HYBRIDIZATION_SUFFIX))
                && !commandLine.hasOption(HYBRIDIZATION_DELIMITER)) {
            throw new InvalidOptionsException("Must specify -" + HYBRIDIZATION_DELIMITER + ".");
        }
        return commandLine;
    }
    
    private void checkForMandatoryOption(final String optionName, final CommandLine commandLine)
        throws InvalidOptionsException {
        if (!commandLine.hasOption(optionName)) {
            throw new InvalidOptionsException("-" + optionName + " is a required option.");
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption(new Option("help", "print this message"));
        options.addOption(OptionBuilder.
                withArgName("FILE").
                hasArg().withDescription("REQUIRED: path to the tab-separated-values file containing copy number data "
                        + "to be converted").
                create(INPUT_FILE));
        options.addOption(OptionBuilder.
                withArgName("FILE").
                hasArg().withDescription("REQUIRED: the desired output file path").
                create(OUTPUT_FILE));
        options.addOption(OptionBuilder.
                withArgName("COLUMN").
                hasArg().withDescription("REQUIRED: the header for the column that contains probe names").
                create(PROBE_NAMES_HEADER));
        options.addOption(OptionBuilder.
                withArgName("COLUMN").
                hasArg().withDescription("REQUIRED: the header for the column that contains chromosome IDs").
                create(CHROMOSOME_ID_HEADER));
        options.addOption(OptionBuilder.
                withArgName("COLUMN").
                hasArg().withDescription("REQUIRED: the header for the column that contains chromosome positions").
                create(CHROMOSOME_POSITION_HEADER));
        options.addOption(OptionBuilder.
                withArgName("COLUMN").
                hasArg().withDescription("REQUIRED: the header for the column that contains log2ratio values").
                create(LOG2RATIO_VALUES_HEADER));
        options.addOption(new Option(HYBRIDIZATION_PREFIX,
                "indicates that hybridization name is a prefix to the log2ratio column header"));
        options.addOption(new Option(HYBRIDIZATION_SUFFIX,
                "indicates that hybridization name is a suffix to the log2ratio column header"));
        options.addOption(OptionBuilder.
                withArgName("DELIMITER").
                hasArg().withDescription("REQUIRED FOR MULTI-HYBRIDIZATION: the delimiter which seperates the "
                        + "hybridization name from the rest of log2ratio column header").
                create(HYBRIDIZATION_DELIMITER));
        options.addOption(OptionBuilder.
                withArgName("NAME").
                hasArg().withDescription("REQUIRED FOR SINGLE-HYBRIDIZATION:the explicit name of the hybridization "
                        + "when " + INPUT_FILE + " only contains single hybridization data").
                create(HYBRIDIZATION_NAME));
        return options;
    }

}
