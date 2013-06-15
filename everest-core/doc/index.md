# everest core
everest is a simple and extensible framework to create representations of *_any type of resource_* on an OSGi Framework. 

## Introduction
everest adopts [REST][1] principles for representing resources. 
*to be completed*

## Requirements
everest resides in an [OSGi][2] r4.3 compliant framework. 

It depends on:
- [Apache Felix iPOJO][3], version 1.10.1 or above

Optionally:
- OSGi Event Admin Service, version 1.3.2 for sending resource notifications

## Concepts
Using everest to access resource representations is plain simple: 
The only method of EverestService is
```java
 Resource process(Request request)  throws IllegalActionOnResourceException, ResourceNotFoundException;
```

### Resource
Path

- Metadata
- Child Resources
- Relations
*to be completed*

### Relation
*to be completed*

### Request
*to be completed*

## Domains
The second thing that you would want to do with everest _(after admiring its name)_ is to create your own domain.

Here are some links to existing domains:
- everest OSGi
- everest iPOJO
- everest System
*to be completed*

## everest servlet
everest servlet provides a nice and easy way for accessing any resource representation.

[1]:  http://en.wikipedia.org/wiki/Representational_State_Transfer "REST"
[2]:	www.osgi.org "OSGi"
[3]:	www.ipojo.org
