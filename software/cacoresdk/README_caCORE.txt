How to generate POJOs and other artifacts for caArray using the caCORE SDK:
---------------------------------------------------------------------------

1. The "cacoresdk" project is checked into SVN.
   It is the 3.2 release, but with bug fixes and modifications to
   suit our purposes.
2. Open the caArray domain model in Enterprise Architect and select
   the "Logical View" package. Choose to "Export to XMI".
   Deselect "Enable full EA Roundtrip".
   Deselect "Export Diagrams" and "Write log file".
   Select "Unisys/Rose format".
   Hit "Export".
3. Copy the resulting "caarray.xmi" file to the cacoresdk/models/xmi
   directory.
4. From a Command prompt, cd to the "cacoresdk" directory and run
   "ant build-beans". The POJOs for the domain objects will be
   generated in cacoresdk/output/caarray/src.
   Copy these generated files over to the caarray2 project (domain
   package), keeping in mind that some of the files have been
   changed (either for functionality improvements or to pass
   code quality checks).
   Note: If there is a problem in the generation of the POJOs,
   look for the following common errors in your model:
   (a) Are there any associations for which the direction is unspecified?
   (b) Are there any associations for which the target end role is not named?
   (c) Are there any classes for which there is an attribute and an association
       named the same thing?
   (d) Are there any associations for which the multiplicity is not
       specified at both ends?
5. From a Command prompt, cd to the "cacoresdk" directory and run
   "ant build-artifacts". The XSDs and castor xml-mapping.xml for
   the domain objects will be generated in cacoresdk/output/caarray/src.
   You will need these for the caArray grid service.
6. If you run into problems:
     Configuration properties are in conf/deploy.properties.
     JET (code generation) templates are in cacoresdk/conf/codegen/template.
     BeanSelfContained.javajet is the relevant template.
