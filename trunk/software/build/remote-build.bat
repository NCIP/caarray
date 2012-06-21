@echo off

REM ant -f remote-build.xml -Dnotest=true -Denvpropertyfile=C:\dev\ncicb\scm-private\trunk\caarray2\properties\QA.properties -Dcreate.tag=true -Dsvn.tag=CAARRAY_R2_0_0_BETA
REM ant -f remote-build.xml -Dnotest=true -Denvpropertyfile=C:\dev\ncicb\scm-private\trunk\caarray2\properties\DEV.properties -Dcreate.tag=true -Dsvn.tag

ant -f remote-build.xml -Dnotest=true -Dbuild.tag_built=trunk012308-1236 -Dnodbintegration=true -Denvpropertyfile=C:\dev\scm-private\trunk\caarray2\properties\DEV.properties