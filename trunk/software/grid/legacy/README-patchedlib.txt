This service uses a patched version of lib/caGrid-1.5-core.jar, and a modified caGrid 1.5.
The changes are:

- The grid transfer introduce upgrader has a typo in the class name.  Changed class name.
- The transfer's web.xml would not deploy in JBoss 5.1. Fixed web.xml.
- Transfer packaged incompatible XML parsing libraries in JBoss 5.1.  Removed conflicts from copy.
- Added static methods to the serialization factories so that castor can use them
- Patched EncodingUtils to enable fallback behavior in the serialization routines
- Patched ServiceConfigUtil.hasConfigProperty.  Class.getMethod throws an exception, not return null, if a method is not present.
- build fix for introduce with missing classpath initialization
- case sensitivity problem with grid transfer patch to wsdl

The patch is core.patch, and can be applied to the following SVN directory:

https://ncisvn.nci.nih.gov/svn/cagrid/branches/caGrid-1_5_release/Software/core/caGrid
