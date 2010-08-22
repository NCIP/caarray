#!/bin/bash
rm -rf legacy
svn update legacy
#tar -xzf legacy-before.tar.gz 
cp FOR_LEGACY/.classpath legacy/
cp FOR_LEGACY/introduce.xml legacy/
cp FOR_LEGACY/dev-build.xml legacy/
cd ../build/
ant build
cd ../grid/
cp FOR_LEGACY/schema/CaArraySvc/* legacy/schema/CaArraySvc/
cp legacy/build/lib/CaArraySvc-common.jar legacy/lib/
