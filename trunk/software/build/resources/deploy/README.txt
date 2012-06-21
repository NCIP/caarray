The jboss-globus-serverinstance-config.zip file was created by running the caGrid 1.5 provided Ant scripts, see http://www.cagrid.org/display/knowledgebase/Manually+Configure+JBoss+Container

The 1.5 source was patched first, per core.patch in /software/grid/legacy/core.patch.  See the readme in that directory for more information.

$CAGRID_HOME\antfiles\jboss>ant -f jboss.xml deployJBoss -Djboss.dir /tmp/jbossFake -DdefaultPort=18080
cd $CAGRID_HOME/projects/transfer
export JBOSS_HOME=/tmp/jbossFake/
ant -Dno.deployment.validation=true deployJBoss
cd /tmp/jbossFake/server/default
zip -r jboss-globus-serverinstance-config.zip *
