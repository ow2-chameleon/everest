# everest OSGi

This domain is a resource-base representation of OSGi entities, including framework, bundles, packages, services, configurations, log entries and deployment packages.

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

- [Osgi Root Resource](#osgi-root-resource)
- [Bundle](#bundle)
- [Bundle Headers](#bundle-headers)
- [Bundle Capability](#bundle-capability)
- [Bundle Requirement](#bundle-requirement)
- [Bundle Wire](#bundle-wire)
- [Package](#package)
- [Service](#service)
- [Configuration](#configuration)
- [Log Entry](#log-entry)
- [Deployment Package](#deployment-package)

### OSGi Root Resource
Root resource is the starting point for OSGi domain and represents the OSGi Framework.

Path: **/osgi**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*
### Bundle
Bundle resources represent a OSGi bundle.

Path: **/osgi/bundles/[bundle-id]**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations


*to be completed*
### Bundle Headers
Bundle headers resources represent header information of a specific OSGi bundle.

Path: **/osgi/bundles/[bundle-id]/headers**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*
### Bundle Capability
Bundle Capability resources represent bundle capability of a specific OSGi bundle.

Path: **/osgi/bundles/[bundle-id]/capabilities/[unique-capability-id]**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*
### Bundle Requirement
Bundle Requirement resources represent bundle requirement of a specific OSGi bundle.

Path: **/osgi/bundles/[bundle-id]/requirements/[unique-requirement-id]**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*
### Bundle Wire
Bundle wire resources represent a bundle wire between a capability and a requirement.

Path: **/osgi/bundles/[bundle-id]/wires/[unique-wire-id]**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*
### Package
Package resource represents a package provided by an OSGi bundle.

Path: **/osgi/packages/[unique-capability-id]**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*
### Service
Service resource represents an OSGi service published in the service registry.

Path: **/osgi/services/[service.id]**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*
### Configuration
Configuration resource represents a Config Admin configuration.

Path: **/osgi/configurations/[configuration-pid]**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*
### Log Entries
Log Entry resource represents a Log Entry of OSGi Log Service

Path: **/osgi/logs/[log-time]**
Observable: **true**

#### Operations
- **CREATE**: 
- **UPDATE**: 
- **READ**: 
- **DELETE**: 

#### Metadata

#### Relations

#### Sub-resources

#### Adaptations

*to be completed*

[1]:  www.ipojo.org
