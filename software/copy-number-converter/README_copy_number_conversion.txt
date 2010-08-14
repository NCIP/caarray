Contents
----------------------------------------------

   1. Introduction
   2. Single Hybridization Copy Number File Usage
   	2.1 Invocation Example
   	2.2 Arguments List
   	2.3 Input/Output Example
   3. Multi-Hybridization Copy Number File Usage
   	3.1 Invocation Example
   	3.2 Arguments List
   	3.3 Input/Output Example


1. Introduction
----------------------------------------------

The copy number conversion utility allows users to automatically convert copy number data into a
MAGE-TAB data matrix format so that it can be import the data into caArray in a parsed fashion.  It
can be used for both single, and multi-hybridization copy number files.

The utility runs under Java 1.5, or greater.


2. Single Hybridization Copy Number File Usage
----------------------------------------------
	
	2.1 Invocation Example
	------------------------
	
	java -jar cn2magetab.jar -input_file single_hyb_copy_number.in.txt
		-output_file single_hyb_copy_number.mage-tab_out.txt -probe_names_header ID
		-chromosome_id_header Chromosome -chromosome_position_header Physical.Position
		-log2ratio_values_header "log2 lowess normalized(cy5/cy3)"
		-hybridization_name my_hybridization_1
	
	
	2.2 Arguments List
	------------------------

	-input_file                       REQUIRED: path to the tab-separated-values file containing
	                                  copy number data to be converted
	                                  
	-output_file                      REQUIRED: the desired output file path
	
	-probe_names_header               REQUIRED: the header for the column that contains probe names
	
	-chromosome_id_header             REQUIRED: the header for the column that contains chromosome
	                                  IDs
	
	-chromosome_position_header       REQUIRED: the header for the column that contains chromosome
	                                  positions
	
	-log2ratio_values_header          REQUIRED: the header for the column that contains log2ratio
	                                  values
	
	-hybridization_name               REQUIRED:the explicit name of the hybridization when
	                                  input_file only contains single hybridization data
	
	2.3 Input/Output Example
	------------------------

    * Input: (hyb name = "my_hybridization_1" explicitly specified)
    ID	Chromosome	Physical.Position	log2 lowess normalized(cy5/cy3)
    A_16_P37638626	6	62964904	-0.447480618012438

    * Output:
    Hybridization REF			my_hybridization_1
    Reporter REF	Chromosome	Position	Log2Ratio
    A_16_P37638626	6	62964904	-0.447480618012438


3. Multi-Hybridization Copy Number File Usage
----------------------------------------------
	
	3.1 Invocation Example
	------------------------
	
	java -jar cn2magetab.jar -input_file multi_hyb_copy_number.in.txt
		-output_file multi_hyb_copy_number.mage-tab_out.txt -chromosome_id_header Chromosome
		-chromosome_position_header Position -log2ratio_values_header log2ratio
		-hybridization_suffix -probe_names_header Name -hybridization_delimiter _
	
	
	3.2 Arguments List
	------------------------

	-input_file                       REQUIRED: path to the tab-separated-values file containing
	                                  copy number data to be converted
	                                  
	-output_file                      REQUIRED: the desired output file path
	
	-probe_names_header               REQUIRED: the header for the column that contains probe names
	
	-chromosome_id_header             REQUIRED: the header for the column that contains chromosome
	                                  IDs
	
	-chromosome_position_header       REQUIRED: the header for the column that contains chromosome
	                                  positions
	
	-log2ratio_values_header          REQUIRED: the header for the column that contains log2ratio
	                                  values
	
	-hybridization_suffix             indicates that hybridization name is a suffix to the log2ratio
                                      column header
	
	-hybridization_prefix             indicates that hybridization name is a prefix to the log2ratio
                                      column header
    
    -hybridization_delimiter          REQUIRED: the delimiter which seperates the hybridization name
                                      from the rest of log2ratio column header


	
	3.3 Input/Output Example
	------------------------

    * Input: (hyb names are in the data file itself)
    Name	Chromosome	Position	log2ratio_AdCA10	loss_AdCA10	normal_AdCA10	gain_AdCA10	log2ratio_SCC27	loss_SCC27	normal_SCC27	gain_SCC27
    RP11-465B22	1	986153	-0.1850384	4.639416e-07	4.482618e-06	0.999995	0.5984648	4.883237e-14	2.771675e-17	1

    * Output:
    Hybridization REF			AdCA10	SCC27
    Reporter REF	Chromosome	Position	Log2Ratio	Log2Ratio
    RP11-465B22	1	986153	-0.1850384	0.5984648