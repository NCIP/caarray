JMeter Tests to Load-test and Stress-test CaArray
-------------------------------------------------

This project contains CaArray-specific JMeter samplers which are
used to test CaArray's remote Java API and the CaArray Grid Service.
It also contains JMeter tests for testing CaArray's web UI, remote
Java API and the CaArray Grid Service.

Steps:

1. First install Jmeter 2.3.
2. Set the environment variable JMETER_HOME to the root directory where you installed JMeter.
3. Go to the %JMETER_HOME%/bin and edit system.properties. Add 2 properties to it:
server.host.name and server.jndi.port.
These properties should be set to point to the remote CaArray server.
By default, localhost and 1099 are used.
(Note: This step should be unnecessary once the Ant build is modified to do this automatically from a default.properties file.)
4. Run the ant targets "samplers:clean" and "samplers:deploy" to compile and deploy CaArray custom samplers for JMeter.
5. Run the ant targets "tests:clean" and "tests:run" to clear previous output and run the tests.

