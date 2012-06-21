JMeter Tests to Load-test and Stress-test CaArray
-------------------------------------------------

This project contains CaArray-specific JMeter samplers which are
used to test CaArray's remote Java API and the CaArray Grid Service.
It also contains JMeter tests for testing CaArray's web UI, remote
Java API and the CaArray Grid Service.

Steps:

1. First install Jmeter 2.3.
2. Set the environment variable JMETER_HOME to the root directory where you installed JMeter.
3. Set the right CaArray Server host name and ports in default.properties.
4. Run the ant targets "samplers:clean" and "samplers:deploy" to compile and deploy CaArray custom samplers for JMeter.
5. Uncomment the particular tests you want to run in build.xml.
6. Run the ant targets "tests:clean" and "tests:run" to clear previous output and run the tests.

