<a name="osgi"></a>
# everest OSGi
This domain is a resource-base representation of OSGi entities, including framework, bundles, packages, services, configurations, log entries and deployment packages.

#### Entity Index:

* [OSGi Framework](#osgi-root-resource)
* [Bundle](#bundle)
    * [Bundle Headers](#bundle-headers)
    * [Bundle Capability](#bundle-capability)
    * [Bundle Requirement](#bundle-requirement)
    * [Bundle Wire](#bundle-wire)
    * [Bundle Services](#bundle-services)
* [Package](#package)
* [Service](#service)
* [Configuration](#configuration)
* [Log Entry](#log-entry)
* [Deployment Package](#deployment-package)

## Requirements

everest OSGi domain needs an OSGi r4.3 compliant framework to represent its entities as resources. Additionally, it depends on:

* [Apache Felix iPOJO][1], *version 1.10.1* or above
* everest-core, version ${everest.core.version}

everest OSGi has **optional** dependencies on following OSGi services:

* org.osgi.service.log, *version 1.3* or above (OSGi Log Service)
* org.osgi.service.cm, *version 1.4* or above (OSGi Configuration Admin Service)
* org.osgi.service.deploymentadmin, *version 1.1* or above (OSGi Deployment Package Admin)

* * * 

## Installation

For a quick start, here is a minimal OSGi framework that will let you test everest and everest OSGi domain. 

```console
g! 
g! lb
START LEVEL 1
   ID|State      |Level|Name
    0|Active     |    0|System Bundle (4.2.1)
    1|Active     |    1|Java Servlet API (2.5.0)
    2|Active     |    1|Commons IO (2.4.0)
    3|Active     |    1|everest-core (1.0.0.SNAPSHOT)
    4|Active     |    1|everest-osgi (1.0.0.SNAPSHOT)
    5|Active     |    1|everest-servlet (1.0.0.SNAPSHOT)
    6|Active     |    1|Apache Felix Gogo Command (0.12.0)
    7|Active     |    1|Apache Felix Gogo Runtime (0.10.0)
    8|Active     |    1|Apache Felix Gogo Shell (0.10.0)
    9|Active     |    1|Apache Felix Http Jetty (2.2.0)
   10|Active     |    1|Apache Felix iPOJO (1.10.1)
   11|Active     |    1|osgi.cmpn (4.3.1.201210102024)
g! 
``` 

* * * 

## Usage

Considering that you have the framework mentioned above, running on localhost and listening on 8080 port, you can test the OSGi domain by entering the following URL on your browser

> http://localhost:8080/everest/osgi

With the help of a little formatting, you should have a result, similar to this:

```json
{
    name: "osgi"
    description: "This root represents osgi framework and its subresources"
    org.osgi.framework.version: "1.5"
    org.osgi.framework.vendor: "Apache Software Foundation"
    ...
    startlevel.bundle: 1
    startlevel: 1
    __relations: {
        everest:bundles: {
        href: "http://localhost:8080/everest/osgi/bundles"
        action: "READ"
        name: "everest:bundles"
        description: "Get bundles"
    ...
}
```

We recommend using a REST client browser extension for easily "browsing" the resources of in your platform.

Of course, you can always access the resources represented by the everest OSGi domain programmatically on your OSGi framework. For this you need to acquire the `EverestService` and make requests on `/osgi` resource, such as:

```java
 // This is how you can get the Everest service.
 // Beware that the EverestService may not be present (i.e. ref == null)
 ServiceReference<EverestService> ref = bundleContext.getServiceReference(EverestService.class);
 EverestService everest = bc.getService(ref);
 
 // This is how you can access the everest OSGi domain.
 // The process() method throws a ResourceNotFoundException exception
 // if the OSGi domain is not active.
 Resource osgiRootResource = everest.process(new DefaultRequest(Action.READ, Path.from("/osgi"), null));
```

You can continue with [OSGi Root Resource](#osgi-root-resource) and start discovering the resources on everest OSGi domain.   
Or, you can head out to [Tutorials](#tutorials) and find out how to manipulate OSGi resources in a RESTful way.
   
* * *

<a name="osgi-root-resource"/>
## OSGi Root Resource
Root resource is the starting point for OSGi domain and represents the OSGi Framework.  

### Path
> /osgi  

### Observable 
This resource is **observable**. It delivers events for the following:

* **UPDATED**: Startlevel changed.   
* **UPDATED**: Packages refreshed.
* **UPDATED**: One of the dynamic sub-resources arrived/disappeared.

### Operations  
- **UPDATE** *"update"*: Update initial bundle startlevel, framework startlevel.  
    - **Parameter** ("startlevel.bundle"): **Type**: Integer, **Optional**: true
    - **Parameter** ("startlevel"): **Type**: Integer, **Optional**: true
- **UPDATE** *"restart"*: Restart the framework.  
    - **Parameter** ("restart"), **Type**: Boolean, **Optional**: true
- **DELETE** *"stop"*: Stops the OSGi framework.

### Metadata
- **startlevel.bundle** *(int)*: Initial bundle start level
- **startlevel** *(int)*: Framework start level
- **org.osgi.framework.version** *(string)*: Framework version
- Other framework properties provided by the framework

### Sub-resources
- **[/bundles](#bundle)**: bundles on this framework
- **[/packages](#package)**: packages on this framework
- **[/services](#service)**: services on this framework
- **[/configurations](#configuration)**: configurations on this framework
- **[/logs](#log-entry)**: logs on this framework
- **[/deployments](#deployment-package)**: deployment packages on this framework

### Adaptations
- **org.osgi.framework.Bundle**: Framework bundle
- **org.osgi.framework.wiring.FrameworkWiring**: Framework Wiring object
- **org.osgi.framework.startlevel.FrameworkStartLevel**: Framework StartLevel object

[Scroll To Top ↑](#osgi)   

* * *  

<a name="bundles"/>
## Bundles
Root of all bundles

### Path
> /[osgi](#osgi-root-resource)/bundles 

### Observable
This resource is **not observable**.

### Operations
- **CREATE** *"install"*: Install new bundle.  
    - **Parameter** ("location"): **Type**: String, **Optional**: false
    - **Parameter** ("input"): **Type**: ByteArrayInputStream, **Optional**: true
- **UPDATE** *"update"*: Update state of bundles.  
    - **Parameter** ("refresh"): **Type**: List<, **Optional**: true
    - **Parameter** ("resolve"): **Type**: List, **Optional**: true

### Sub-resources
- **[/[bundle-id]](#bundle)**: bundles

[Scroll To Top ↑](#osgi)

* * *

<a name="bundle"/>
## Bundle
Bundle resources represent an OSGi bundle.  

### Path
> /[osgi](#osgi-root-resource)/[bundles](#bundles)/[bundle-id]  

### Observable
This resource is **observable**. It deliveres events for the following:

* **CREATED** : Arrival of a new bundle 
* **UPDATED** : Update on bundle state
* **DELETED** : Departure of a bundle

### Operations
- **READ**: Get the current state of the bundle
- **UPDATE** *"update"*: Update initial bundle startlevel, framework startlevel.  
    - **Parameter** ("newState"): **Type**: String, **Optional**: true
    - **Parameter** ("startlevel"): **Type**: Integer, **Optional**: true
    - **Parameter** ("update"): **Type**: Boolean, **Optional**: true
    - **Parameter** ("refresh"): **Type**: Boolean, **Optional**: true
    - **Parameter** ("input"): **Type**: ByteArrayInputStream, **Optional**: true
- **DELETE** *"stop"*: Stops the OSGi framework.

***Note:*** CREATE operation on bundles (which basically installs a bundle) is done in [Bundles](#bundles) resource.  

### Metadata
- **bundle-id** *(long)*: Bundle id
- **bundle-state** *(string)*: Bundle state as string {"ACTIVE",...}
- **bundle-symbolic-name** *(string)*: Bundle Symbolic Name
- **bundle-version** *(Version)*: Bundle Version
- **bundle-location** *(string)*: Bundle Location
- **bundle-last-modified** *(long)*: Bundle Last Modified
- **bundle-fragment** *(boolean)*: is this bundle a fragment

### Sub-resources
- **[/headers](#bundle-headers)**: Bundle Headers of this OSGi bundle
- **[/capabilities](#bundle-capability)**: Bundle Capabilities of this OSGi bundle
- **[/requirements](#bundle-requirement)**: Bundle Requirement of this OSGi bundle
- **[/wires](#bundle-wire)**: Bundle Wires of this OSGi bundle
- **[/services](#bundle-services)**: Services that can be linked to this OSGi bundle

### Adaptations
- **org.osgi.framework.Bundle**: Bundle object
- **org.apache.felix.ipojo.everest.osgi.bundle.BundleResource**: BundleResource class that is used to represent this bundle

[Scroll To Top ↑](#osgi)

* * *
 
<a name="bundle-headers"/>
## Bundle Headers
Bundle headers resources represent header information of a specific OSGi bundle.

### Path
> /[osgi](#osgi-root-resource)/[bundles](#bundles)/[bundle-id](#bundle)/headers

### Observable
This resource is **not observable**.

### Operations
- **READ**: Get headers of this OSGi Bundle

### Metadata
All the metadata information available on the Bundle

### Sub-resources
- **[/export-package](#package)**: Package export headers
- **/import-package**: Import package headers
- **/dynamicimport-package**: Dynamic import package headers
- **/require-bundle**: Require bundle headers

[Scroll To Top ↑](#osgi)

* * *

<a name="bundle-capability"/>
## Bundle Capability
Bundle Capability resources represent bundle capability of a specific OSGi bundle.

### Path
> /[osgi](#osgi-root-resource)/[bundles](#bundles)/[bundle-id](#bundle)/capabilities/[unique-capability-id]  

### Observable
This resource is **not observable**

### Operations
- **READ**: Get current state of this bundle capability

### Metadata
All capability attributes and directives

### Relations
- **[/package](#package)**: Link to the package resource if this capability is a package
- **[/export-package](#bundle-headers)**: Link to the bundle header if this capability is a package 
- **[/[unique-wire-id]](#bundle-wire)**: Links to the wires connected to this capability

### Adaptations
- **org.osgi.wiring.framework.BundleCapability**: BundleCapability object
- **org.apache.felix.ipojo.everest.osgi.bundle.BundleCapabilityResource**: BundleCapabilityResource class used to represent this capability  

[Scroll To Top ↑](#osgi)

* * *

<a name="bundle-requirement"/>
## Bundle Requirement
Bundle Requirement resources represent bundle requirement of a specific OSGi bundle.

### Path
> /[osgi](#osgi-root-resource)/[bundles](#bundles)/[bundle-id](#bundle)/requirements/[unique-requirement-id]

### Observable
This resource is **not observable**.

### Operations
- **READ**: Get current state of the bundle requirement

### Metadata
All requirement attributes and directives

### Relations
- **[/dynamicimport-package](#bundle-headers)**: Link to the bundle header if this requirement is a dynamic import package
- **[/require-bundle](#bundle-headers)**: Link to the bundle header if this requirement is a require bundle
- **[/[unique-wire-id]](#bundle-wire)**: Links to the wires connected to this requirement  

### Adaptations
- **org.osgi.wiring.framework.BundleRequirement**: BundleRequirement object
- **org.apache.felix.ipojo.everest.osgi.bundle.BundleRequirementResource**: BundleRequirementResource class used to represent this requirement  

[Scroll To Top ↑](#osgi)

* * *

<a name="bundle-wire"/>
## Bundle Wire
Bundle wire resources represent a bundle wire between a capability and a requirement.

### Path
> /[osgi](#osgi-root-resource)/[bundles](#bundles)/[bundle-id](#bundle)/wires/[unique-wire-id]

### Observable
This resource is **not observable**.

### Operations
- **READ**: Get current state of bundle wire

### Metadata
- **requirement** *(string)*: [unique-requirement-id] of linked Bundle Requirement
- **capability** *(string)*: [unique-capability-id] of linked Bundle Capability

### Relations
- **[/requirement](#bundle-requirement)**: Bundle Requirement linked by this wire
- **[/capability](#bundle-capability)**: Bundle Capability linked by this wire

### Adaptations
- **org.osgi.wiring.framework.BundleWire**: BundleWire object  

[Scroll To Top ↑](#osgi)

* * *
 
<a name="bundle-services"/>
## Bundle Services
Services that can be linked to an OSGi bundle

### Path
> /[osgi](#osgi-root-resource)/[bundles](#bundles)/[bundle-id](#bundle)/services

### Observable
This resource is **not observable**.

### Operations
- **READ**: get current state of the bundle services

### Sub-resources
- **[/registered](#service)**: Services registered by this OSGi bundle
- **[/uses](#service)**: Services used by this OSGi bundle

[Scroll To Top ↑](#osgi)   

* * *
 
<a name="package"/>
## Package
Package resource represents a package provided by an OSGi bundle.

### Path
> /[osgi](#osgi-root-resource)/packages/[unique-capability-id]

### Observable: 
This resource is observable. It delivers events for the following:

- **CREATED**: Package available
- **DELETED**: Package unavailable

### Operations
- **READ**: Get current state of the package

### Metadata
- **osgi.wiring.package** *(string)*: Package Name
- **version** *(Version)*: Package Version
- **bundle-symbolic-name** *(string)*: Symbolic name of the provider bundle
- **bundle-version** *(Version)*: Version of the provider bundle
- **in-use** *(boolean)*: Is this packages is used by anyone?

### Relations
- **[/provider-bundle](#bundle)**: OSGi Bundle providing this package.

### Sub-resources
- **[/importer-bundles](#bundle)**: A collection of relations to OSGi bundles that import this package.

### Adaptations
- **org.osgi.wiring.framework.BundleCapability**: BundleCapability object
- **org.apache.felix.ipojo.everest.osgi.package.PackageResource**: PackageResource class used to represent this package 

[Scroll To Top ↑](#osgi)   

* * *
 
<a name="service"/>
## Service
Service resource represents an OSGi service published in the service registry.

### Path
> /[osgi](#osgi-root-resource)/services/[service.id]

### Observable
This resource is **observable**. It delivers events for the following:

* **CREATED**: Service registered
* **UPDATED**: Service modified
* **DELETED**: Service unregistered

### Operations
- **READ**: Get current state of the service

### Metadata
All service properties  

### Sub-resources
- **[/using-bundles](#bundle)**: A collection of relations to bundles using this service 

### Adaptations
- **org.osgi.framework.ServiceReference**: ServiceReference object of this service
- **org.apache.felix.ipojo.everest.osgi.service.ServiceResource**: ServiceResource class used to represent this service

[Scroll To Top ↑](#osgi)   

* * *
 
<a name="configurations"/>
## Configurations
Root of all configurations 

### Path
> /[osgi](#osgi-root-resource)/configurations

### Observable
This resource is **not observable**. 

### Operations
- **CREATE**: Create a new configuration

### Sub-resources
- **[/[configuration-pid]](#configuration)**: configurations by pid

### Relations
- **(CREATE)** "create": Create a new configuration  
    - **Parameter** ("location"): **Type**: String, **Optional**: false
    - **Parameter** ("pid"): **Type**: String, **Optional**: true
    - **Parameter** ("factoryPid"): **Type**: String, **Optional**: true

[Scroll To Top ↑](#osgi) 
  
* * *
 
<a name="configuration"/>
## Configuration
Configuration resource represents a Config Admin configuration.

### Path 
> /[osgi](#osgi-root-resource)/[configurations](#configurations)/[configuration-pid]  

### Observable  
This resource is **observable**. It deliveres events for the following:

* **CREATED**: A new configuration created.
* **UPDATED**: Configuration reconfigured.
* **DELETED**: Configuration deleted.

### Operations
- **READ**: Get current state of the configuration
- **UPDATE** *"update"*: update properties of this configuration  
    - **Parameter** ("properties"): **Type**: Dictionary, **Optional**: true  
    - **Parameter** ("location"): **Type**: String, **Optional**: true  
- **DELETE** *"delete"*: delete this configuration

***Note:*** CREATE operation for configurations (which creates a new configuration) is done in [Configurations](#configurations) resource.   

### Metadata
- **service.pid** *(string)*: Configuration pid
- **location** *(string)*: Bundle location attributed by this configuration
- **serviceFactory.pid** *(string)*: Factory configuration pid
- All proprties of this configuration

### Adaptations
- **org.osgi.service.cm.Configuration**: Configuration object
- **org.apache.felix.ipojo.everest.osgi.config.ConfigurationResource**: ConfigurationResource class used to represent this resource

[Scroll To Top ↑](#osgi)  
 
* * *
 
<a name="log-entry"/>
## Log Entry
Log Entry resource represents a Log Entry of OSGi Log Service.

### Path
> /[osgi](#osgi-root-resource)/logs/[log-time]

### Observable
This resource is **not observable**.

### Operations
- **READ**: Get the current state of this log entry

### Metadata
- **log level** *(string)*: log level as string
- **time** *(long)*: log time
- **message** *(string)*: message of this log 
- **bundle** *(long)*: bundle id that sent this log
- **service** *(long)*: service id that sent this log
- **exception** *(StackTraceElement)*: stack trace of the exception associated by this log

### Relations
- **[/bundle](#bundle)**: Link to the OSGi bundle that sent this log
- **[/service](#service)**: Link to the OSGi service that sent this log

### Adaptations
- **org.osgi.service.log.LogEntry**: LogEntry object
- **org.apache.felix.ipojo.everest.osgi.log.LogEntryResource**: LogEntryResource class used to represent this resource

[Scroll To Top ↑](#osgi)  
 
* * *

<a name="deployement-packages"/>
## Deployment Packages
Root of all deployment packages

### Path
> /[osgi](#osgi-root-resource)/deployments

### Observable
This resource is **not observable**.

### Operations
- **CREATE** *"install"*: Install new deployment package
    - **Parameter** ("input"): **Type**: InputStream, **Optional**: false

### Sub-resources
- **[/[deployment-package-name]](#deployment-package)**: deployment pakcages

[Scroll To Top ↑](#osgi)   

* * *

<a name="deployment-package"/>
## Deployment Package
Deployment Package resource represents a Deployment Package deployed by the OSGi Deployment Package Admin

### Path
> /[osgi](#osgi-root-resource)/[deployments](#deployment-packages)/[deployment-package-name]  

### Observable
This resource is **not observable**.

### Operations
- **READ**: Get the current state of this deployment package
- **DELETE** *"uninstall"*: Uninstall this deployment package

***Note:*** CREATE opearation for Deployment Packages is done in [Deployment Packages](#deployment-packages) resource.

### Metadata
- **Name** *(string)*: Name of this deployment package
- **DisplayName** *(string)*: display name of this deployment package
- **Version** *(Version)*: version of this deployment package
- **isStale** *(boolean)*: is this deployment package stale?

### Sub-resources
- **[/bundles](#bundle)** : A collection of links to the OSGi bundles deployed with this deployment package  

### Adaptations
- **org.osgi.service.deploymentadmin.DeploymentPackage**: DeploymentPackage object

[Scroll To Top ↑](#osgi)   

* * *

<a name="tutorials"/>
## Tutorials

Here you will find some tutorials on how to change some of the resource states on OSGi domain.

### Installing a bundle



### Changing the state of a bundle



### Working with Config Admin 



### Installing/Uninstalling Deployment Packages


[Scroll To Top ↑](#osgi)   


[1]:  www.ipojo.org
