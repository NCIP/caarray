#!/bin/bash

# Repackages the caarray.ear file assumed to be available in this directory with
# additional CaArrayServiceFacade EJB.

# Extract EJB jar from original caarray EAR file
jar xf caarray.ear caarray-ejb.jar

# Update the EJB jar by adding EJB extensions
jar uf caarray-ejb.jar -C target/classes org

# Update the EAR file with the modified EJB jar
jar uf caarray.ear caarray-ejb.jar

# Remove EJB jar
rm caarray-ejb.jar

