# everest Core

everest is a simple and extensible framework to create representations of **anything** on top an OSGi framework. 


## Introduction

everest adopts [REST][1] principles for implementing a resource representation architecture. It allows:

* Resource creators or *'servers'* to create representations of resource states and implement resource state transitions.
* Resource browsers or *'clients'* to access and browse these representations.

In everest, a resource can be anything! OSGi bundles, packages, services, cats, dogs, streets and mountains.. It depends on what you want to represent, and how you want to represent it. Your imagination is the limit. 


## Requirements

everest resides in an [OSGi][2] r4.3 compliant framework. 
It is implemented on top of [Apache Felix iPOJO][3] and depends on the **version 1.10.1** or above.   
Optionally it depends on:

* *OSGi Event Admin Service*, **version 1.3.2** for sending resource notifications.


## Getting Started with Everest

Jump out to [Getting Started][4] for a quick start.


## Concepts

Dive into the details of everest [Concepts][5].


## Domains

everest Core implements the resource model explained above and allows plugging representations of new resource types. Each new addition plugged into everest representing a portion of the *world* is called **'a domain'**. 

Here are some links to existing domains:

* [everest OSGi][6]
* [everest iPOJO][7]
* [everest System][8]

[1]:  http://en.wikipedia.org/wiki/Representational_State_Transfer "REST"
[2]:  http://www.osgi.org "OSGi"
[3]:  http://www.ipojo.org "Apache Felix iPOJO"
[4]:  ../everest-core/getting-started.html "Getting Started with Everest"
[5]:  ../everest-core/concepts.html "everest Concepts"
[6]:  ../everest-osgi/index.html "everest OSGi"
[7]:  ../everest-ipojo/index.html "everest iPOJO"
[8]:  ../everest-system/index.html "everest System"
