# everest Core

everest is a simple and extensible framework to create representations of **any type of resource** on top an OSGi framework. 

## Introduction

everest adopts [REST][1] principles for implementing a resource representation architecture. It allows:

* *Resource Servers* to create representations and implement resource state transitions.
* *Resource Clients* to access and browse these representations.

* * * 

## Requirements

everest resides in an [OSGi][2] r4.3 compliant framework. 
It is implemented on top of [Apache Felix iPOJO][3] and depends on the **version 1.10.1** or above.   
Optionally it depends on:

* *OSGi Event Admin Service*, **version 1.3.2** for sending resource notifications.

* * * 

## Concepts

everest rely on **H**ypermedia **a**s **t**he **E**ngine **o**f **A**pplication **S**tate ([HATEOAS][4]) principles. It proposes simple concepts in order to implement a *true* REST architectural style for representating resources.

### Resource

#### Path

#### Metadata

#### Sub-Resources

#### Relations

#### Observable

#### Adaptable


### Relation



### Request



* * * 

## Usage

Using everest to access resource representations is plain simple: 
The only method of `EverestService` is

```java
 Resource process(Request request)  throws IllegalActionOnResourceException, ResourceNotFoundException;
```

* * * 

## Domains

everest Core implements the resource model explained above and allows plugging representations of new resource types. Each new addition plugged into everest representing a portion of the *world* is called **'a domain'**. 

Here are some links to existing domains:

* [everest OSGi][5]
* [everest iPOJO][6]
* [everest System][7]

[1]:  http://en.wikipedia.org/wiki/Representational_State_Transfer "REST"
[2]:  http://www.osgi.org "OSGi"
[3]:  http://www.ipojo.org "Apache Felix iPOJO"
[4]:  http://en.wikipedia.org/wiki/HATEOAS "HATEOAS"
[5]:  ../everest-osgi/index.html "everest OSGi"
[6]:  ../everest-ipojo/index.html "everest iPOJO"
[7]:  ../everest-system/index.html "everest System"
