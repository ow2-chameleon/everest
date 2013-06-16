# everest OSGi

This domain is a resource-base representation of OSGi entities, including framework, bundles, packages, services, configurations, log entries and deployment packages.

- [OSGi Framework](#osgi-root-resource)
- [Bundle](#bundle)
- [Bundle Headers](#bundle-headers)
- [Bundle Capability](#bundle-capability)
- [Bundle Requirement](#bundle-requirement)
- [Bundle Wire](#bundle-wire)
- [Bundle Services](#bundle-services)
- [Package](#package)
- [Service](#service)
- [Configuration](#configuration)
- [Log Entry](#log-entry)
- [Deployment Package](#deployment-package)

## Requirements

everest OSGi domain needs an OSGi r4.3 compliant framework to represent its entities as resources.

It depends on: 
- [Apache Felix iPOJO][1], version 1.10.1 or above
- everest-core, version ${everest.core.version}

Optionally:
- OSGi Configuration Admin, version *to be completed*
- OSGi Log Service, version *to be completed*
- OSGi Deployment Package Admin, version *to be completed*

## Entities

## OSGi Root Resource
Root resource is the starting point for OSGi domain and represents the OSGi Framework.  

Path: **/osgi**  
Observable: **true**  

### Operations
- **UPDATE**: Update initial bundle startlevel, framework startlevel or Restart the framework.
- **READ**: Get the current state of OSGi framework
- **DELETE**: Stop the OSGi framework

### Metadata
- **org.osgi.framework.version** *(string)*: Framework version
*to be completed*
- ...
- **startlevel.bundle** *(int)*: Initial bundle start level
- **startlevel** *(int)*: Framework start level

### Relations
Static relations:
- **(UPDATE)** "update": Update initial bundle startlevel, framework startlevel.  
**Parameter** ("startlevel.bundle"): **Type**: Integer, **Optional**: true  
**Parameter** ("startlevel"): **Type**: Integer, **Optional**: true

- **(UPDATE)** "restart": Restart the framework.  
**Parameter** ("restart"), **Type**: Boolean, **Optional**: true
- **(DELETE)** "stop": Stops the OSGi framework.

### Sub-resources
Static sub-resources:
- **[/bundles](#bundle)**: bundles on this framework
- **[/packages](#package)**: packages on this framework
- **[/services](#service)**: services on this framework

Dynamic sub-resources:
- **[/configurations](#configuration)**: configurations on this framework
- **[/logs](#log-entry)**: logs on this framework
- **[/deployments](#deployment-package)**: deployment packages on this framework

### Adaptations
- **org.osgi.framework.Bundle**: Framework bundle
- **org.osgi.framework.wiring.FrameworkWiring**: Framework Wiring object
- **org.osgi.framework.startlevel.FrameworkStartLevel**: Framework StartLevel object

### Events
- **UPDATED**: Startlevel changed, packages refreshed, one of the dynamic sub-resources arrived/disappeared

## Bundles
Root of all bundles

Path: **/osgi/bundles**  
Observable: **false**

### Operations
- **CREATE**: Install bundle

### Relations
- **(CREATE)** "install": Install new bundle.  
**Parameter** ("location"): **Type**: String, **Optional**: false  
**Parameter** ("input"): **Type**: Integer, **Optional**: true  
- **(UPDATE)** "update": Update state of bundles.  
**Parameter** ("refresh"): **Type**: List, **Optional**: true  
**Parameter** ("resolve"): **Type**: List, **Optional**: true  

### Sub-resources
- **[/[bundle-id]](#bundle)**: bundles

## Bundle
Bundle resources represent a OSGi bundle.  

Path: **/osgi/bundles/[bundle-id]**  
Observable: **true**  

### Operations
- **UPDATE**: Update start level parameter and/or bundle state, refresh/update bundle
- **READ**: Get the current state of the bundle
- **DELETE**: Uninstall the bundle

***Note:*** CREATE operation on bundles (which basically installs a bundle) is done in [Bundles](#bundles) resource.  

### Metadata
- **bundle-id** *(long)*: Bundle id
- **bundle-state** *(string)*: Bundle state as string {"ACTIVE",...}
- **bundle-symbolic-name** *(string)*: Bundle Symbolic Name
- **bundle-version** *(version)*: Bundle Version
- **bundle-location** *(string)*: Bundle Location
- **bundle-last-modified** *(long)*: Bundle Last Modified
- **bundle-fragment** *(boolean)*: is this bundle a fragment

### Relations
Static relations:
- **(UPDATE)** "update": Update initial bundle startlevel, framework startlevel.  
**Parameter** ("newState"): **Type**: String, **Optional**: true  
**Parameter** ("startlevel"): **Type**: Integer, **Optional**: true  
**Parameter** ("update"): **Type**: Boolean, **Optional**: true  
**Parameter** ("refresh"): **Type**: Boolean, **Optional**: true  
**Parameter** ("input"): **Type**: ByteArrayInputStream, **Optional**: true  

- **(DELETE)** "stop": Stops the OSGi framework.

### Sub-resources
Static sub-resources:
- **[/headers](#bundle-headers)**: Bundle Headers of this OSGi bundle
- **[/capabilities](#bundle-capability)**: Bundle Capabilities of this OSGi bundle
- **[/requirements](#bundle-requirement)**: Bundle Requirement of this OSGi bundle
- **[/wires](#bundle-wire)**: Bundle Wires of this OSGi bundle
- **[/services](#bundle-services)**: Services that can be linked to this OSGi bundle

### Adaptations
- **org.osgi.framework.Bundle**: Bundle object
- **org.apache.felix.ipojo.everest.osgi.bundle.BundleResource**: BundleResource class that is used to represent this bundle

### Events
- **CREATED** : Arrival of a new bundle 
- **UPDATED** : Update on bundle state
- **DELETED** : Departure of a bundle

## Bundle Headers
Bundle headers resources represent header information of a specific OSGi bundle.

Path: **/osgi/bundles/[bundle-id]/headers**
Observable: **false**

### Operations
- **READ**: Get headers of this OSGi Bundle

### Metadata
All the metadata information available on the Bundle

### Sub-resources
- **[/export-package](#package)**: Package export headers
- **/import-package**: Import package headers
- **/dynamicimport-package**: Dynamic import package headers
- **/require-bundle**: Require bundle headers

## Bundle Capability
Bundle Capability resources represent bundle capability of a specific OSGi bundle.

Path: **/osgi/bundles/[bundle-id]/capabilities/[unique-capability-id]**  
Observable: **false**  

### Operations
- **READ**: Get current state of this bundle capability

### Metadata
All capability attributes and directives

### Relations
Dynamic Relations:
- **[/package](#package)**: Link to the package resource if this capability is a package
- **[/export-package](#bundle-headers)**: Link to the bundle header if this capability is a package 
- **[/[unique-wire-id]](#bundle-wire)**: Links to the wires connected to this capability

### Adaptations
- **org.osgi.wiring.framework.BundleCapability**: BundleCapability object
- **org.apache.felix.ipojo.everest.osgi.bundle.BundleCapabilityResource**: BundleCapabilityResource class used to represent this capability  

## Bundle Requirement
Bundle Requirement resources represent bundle requirement of a specific OSGi bundle.

- Path: **/osgi/bundles/[bundle-id]/requirements/[unique-requirement-id]**
- Observable: **false**

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

## Bundle Wire
Bundle wire resources represent a bundle wire between a capability and a requirement.

- Path: **/osgi/bundles/[bundle-id]/wires/[unique-wire-id]**
- Observable: **false**

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

## Bundle Services
Services that can be linked to an OSGi bundle

- Path: **/osgi/bundles/[bundle-id]/services**

### Operations
- **READ**: get current state of the bundle services

### Sub-resources
- **[/registered](#service)**: Services registered by this OSGi bundle
- **[/uses](#service)**: Services used by this OSGi bundle

## Package
Package resource represents a package provided by an OSGi bundle.

- Path: **/osgi/packages/[unique-capability-id]**
- Observable: **true**

### Operations
- **READ**: Get current state of the package

### Metadata
- **osgi.wiring.package** *(string)*: Package Name
- **version** *(version)*: Package Version
- **bundle-symbolic-name** *(string)*: Symbolic name of the provider bundle
- **bundle-version** *(version)*: Version of the provider bundle
- **in-use** *(boolean)*: Is this packages is used by anyone?

### Relations
- **[/provider-bundle](#bundle): OSGi Bundle providing this package

### Sub-resources
- **[/importer-bundles](#bundle): A collection of relations to OSGi bundles that import this package

### Adaptations
- **org.osgi.wiring.framework.BundleCapability**: BundleCapability object
- **org.apache.felix.ipojo.everest.osgi.package.PackageResource**: PackageResource class used to represent this package 

### Events
- **CREATED**: Package available
- **DELETED**: Package unavailable

## Service
Service resource represents an OSGi service published in the service registry.

- Path: **/osgi/services/[service.id]**
- Observable: **true**

### Operations
- **READ**: Get current state of the service

### Metadata
All service properties  

### Sub-resources
- **[/using-bundles](#bundle)**: A collection of relations to bundles using this service 

### Adaptations
- **org.osgi.framework.ServiceReference**: ServiceReference object of this service
- **org.apache.felix.ipojo.everest.osgi.service.ServiceResource**: ServiceResource class used to represent this service

### Events
- **CREATED**: Service registered
- **UPDATED**: Service modified
- **DELETED**: Service unregistered

## Configurations
Root of all configurations 

Path: **/osgi/configurations**
Observable: **false**

### Operations
- **CREATE**: Create a new configuration

### Sub-resources
- **[/[configuration-pid]](#configuration)**: configurations by pid

### Relations
- **(CREATE)** "create": Create a new configuration  
**Parameter** ("location"): **Type**: String, **Optional**: false  
**Parameter** ("pid"): **Type**: String, **Optional**: true  
**Parameter** ("factoryPid"): **Type**: String, **Optional**: true  

## Configuration
Configuration resource represents a Config Admin configuration.

Path: **/osgi/configurations/[configuration-pid]**  
Observable: **true**  

### Operations
- **UPDATE**: Update configuration
- **READ**: Get current state of the configuration
- **DELETE**: Delete this configuration

***Note:*** CREATE operation for configurations (which creates a new configuration) is done in [Configurations](#configurations) resource.   

### Metadata
- **service.pid** *(string)*: Configuration pid
- **location** *(string)*: Bundle location attributed by this configuration
- **serviceFactory.pid** *(string)*: Factory configuration pid
- All proprties of this configuration

### Relations
- **(UPDATE)** "update": update properties of this configuration  
**Parameter** ("properties"): **Type**: Dictionary, **Optional**: true  
**Parameter** ("location"): **Type**: String, **Optional**: true  
- **(DELETE)** "delete": delete this configuration

### Adaptations
- **org.osgi.service.cm.Configuration**: Configuration object
- **org.apache.felix.ipojo.everest.osgi.config.ConfigurationResource**: ConfigurationResource class used to represent this resource

## Log Entry
Log Entry resource represents a Log Entry of OSGi Log Service

- Path: **/osgi/logs/[log-time]**
- Observable: **true**

### Operations
- **READ**: Get the current state of this log entry

### Metadata
- **log level** *(string)*: log level as string
- **time** *(long)*: log time
- **message** *(string)*: message of this log 
- **bundle** *(long)*: bundle id that sent this log
- **service** *(long): service id that sent this log
- **exception** *(StackTraceElement)*: stack trace of the exception associated by this log

### Relations
- **[/bundle](#bundle)**: Link to the OSGi bundle that sent this log
- **[/service](#service)**: Link to the OSGi service that sent this log

### Adaptations
- **org.osgi.service.log.LogEntry**: LogEntry object
- **org.apache.felix.ipojo.everest.osgi.log.LogEntryResource**: LogEntryResource class used to represent this resource

## Deployment Packages
Root of all deployment packages

Path: **/osgi/deployments**
Observable: **false**

### Operations
- **CREATE**: Install new deployment package

### Relations
- **(CREATE)** "install": Install new deployment package
**Parameter** ("input"): **Type**: InputStream, **Optional**: false  

### Sub-resources
- **[/[deployment-package-name]](#deployment-package)**: deployment pakcages

## Deployment Package
Deployment Package resource represents a Deployment Package deployed by the OSGi Deployment Package Admin

Path: **/osgi/deployments/[deployment-package-name]**   
Observable: **true**  

### Operations
- **READ**: Get the current state of this deployment package
- **DELETE**: Uninstall this deployment package

***Note:*** CREATE opearation for Deployment Packages is done in [Deployment Packages](#deployment-packages) resource.

### Metadata
- **Name** *(string)*: Name of this deployment package
- **DisplayName** *(string)*: display name of this deployment package
- **Version** *(Version)*: version of this deployment package
- **isStale** *(boolean)*: is this deployment package stale?

### Relations
- **(DELETE)** "uninstall": Uninstall this deployment package

### Sub-resources
- **[/bundles](#bundle)** : A collection of links to the OSGi bundles deployed with this deployment package  

### Adaptations
- **org.osgi.service.deploymentadmin.DeploymentPackage**: DeploymentPackage object

[1]:  www.ipojo.org
