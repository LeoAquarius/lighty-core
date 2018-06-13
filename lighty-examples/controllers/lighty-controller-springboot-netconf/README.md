lighty.io SpringBoot integration example
=========================================
This is simple demo application which uses Lighty with NetConf Southbound plugin inside Springboot.

Application initializes OpenDaylight core components (MD-SAL, yangtools and controller) and NetConf Southbound plugin
inside spring framework environment.

There is initialized fully functional md-sal inside the spring. The DataBroker is provided for SpringBoot dependency
injection subsystem services and used in exposed REST endpoints. The REST endpoints provides very simple functionality
for network-topology model inside global datastore.

Alongside the basic data broker, there is also integrated NetConf Southbound plugin and some very basic NetConf
functionality exposed through REST endpoints. The "lighty-toaster-device" was used as a NetConf device which uses
toaster model from ODL ([link](https://github.com/YangModels/yang/blob/19fea483099dbf2864b3c3186a789d12d919f4db/experimental/odp/toaster.yang)). 

Build
-----
```
mvn clean install
```


Start
-----
It is necessary to copy toaster@2009-11-20.yang file to $WORKING_DIR/cache/schema/toaster@2009-11-20.yang, to be 
possible to read NetConf data from testing device (lighty-toaster-device).
```
mvn spring-boot:run
```

or

```
java -jar target/lighty-controller-springboot-8.0.0-SNAPSHOT.jar
```

or in any IDE, run the method

```
io.lighty.core.controller.springboot.LightyControllerSpringbootApplication.main
```


Using
-----
When application has started, the REST endpoints are provided:

##### GET /topology/list
list all topology IDs stored in datastore
```
curl -X GET "http://localhost:8080/topology/list"
```
##### PUT /topology/id/{topologyId}
create new topology with topology id "test-topology-id"
```
curl -X PUT "http://localhost:8080/topology/id/test-topology-id"
```
##### DELETE /topology/id/{topologyId}
delete existing topology with topology id "test-topology-id"
```
curl -X DELETE "http://localhost:8080/topology/id/test-topology-id"
```
##### GET /netconf/list
list all NetConf devices with its connection status and "darknessFactor" data loaded from device
(darknessFactor is contained in toaster model from ODL)
```
curl -X GET "http://localhost:8080/netconf/list"
```
##### PUT /netconf/id/{netconfDeviceId}
attempt to connect to device "test-device" with specific credentials and address:port
```
curl -X PUT \
-H "Content-Type:application/json" \
--data \
'{
    "username": "admin",
    "password": "admin",
    "address": "127.0.0.1",
    "port": "17830"
}' \
"http://localhost:8080/netconf/id/test-device"
```
##### DELETE /netconf/id/{netconfDeviceId}
disconnect NetConf device "test-device"
```
curl -X DELETE "http://localhost:8080/netconf/id/test-device"
```


Notable Sources
---------------
#### LightyControllerSpringbootApplication.java
- main Springboot initializer class

#### LightyConfiguration.java
- Lighty.io services initialization and beans definition for SpringBoot dependency injection system

#### TopologyRestService.java
- REST endpoints definition
- uses beans defined in class LightyConfiguration for modifying topologies in ODL md-sal

#### NetconfDeviceRestService
- REST endpoints definition for ODL netconf
- uses beans defined in class LightyConfiguration for connecting, disconnecting and listing NetConf devices

#### pom.xml
- spring-boot-starter-parent

default springboot parent

##### maven dependencies:

org.springframework.boot/spring-boot-starter-web

- springboot dependency providing REST server running on spring environment

io.lighty.core/lighty-controller

- lighty.io core services

io.lighty.resources/singlenode-configuration

- lighty.io akka configuration providing the mandatory akka config on classpath
- this configuration is necessary to properly start lighty.io

io.lighty.modules.southbound.netconf/lighty-netconf-sb

- lighty.io NetConf plugin

io.lighty.kit.models/lighty-kit-sample-models

- lighty.io models 