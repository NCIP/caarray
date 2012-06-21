INSTALL INSTRUCTIONS
Build an environment
  * Download distribution from https://gforge.nci.nih.gov/svnroot/lsd/trunk/dist/caarray_upgrade_[latest_version].zip
  * Transfer to server
  * Login as user who applications belongs
  * Verify java and ant version
    * ant -version # should contain (version 1.7.0)
    * java -version # should contain (build 1.5.0_10-b03)
  * extract zip to a working folder
  * edit carray2-upgrade.properties replace any propery with "REPLACE" or "replace" in it
    * Use your previous version of properties to get the correct values for the new properties
  * Run installer
    * ant
      * The installer may fail if properties cannot be verified (like connectivity testing)
      * Check ./logs/install-@date@.log for full log of build.
  * Browse to http://localhost:${jboss.server.port}/caarray and browse some data to test
