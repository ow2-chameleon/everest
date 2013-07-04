
# Concepts

everest relies on [**H**ypermedia **a**s **t**he **E**ngine **o**f **A**pplication **S**tate (HATEOAS)][1] principles. It proposes simple concepts that helps to implement a *true* REST architectural style for representating resources.

## Resource

A resource is **something** and **anything** that you want to represent. It serves as a representation of the state of that **thing** at *a moment in time* and is capable of handling requests that target them for manipulating this state. 

A good analogy for resources is web pages. For example you can have a web page for the [Mount Everest][3], and another one for [Schrödinger's cat][4]. Each of these web pages contain information representing a **thing**, with its properties, explications etc., and they contain hyperlinks to other related web pages. Then we can imagine that the second page contains a link that 'looks' into the box, either killing or surviving the cat, which changes its state. 

Just as web pages, Resources provide an abstract, untyped interface that allows clients uniformally access and manipulate state representations.

Everest is all about resources, and resources are constructed on some primitive concepts:

## Path

Resources are addressed by requests through their Path. Just as an [URI][2], a resource's path designates its name and how to access to it by a Request. Each resource has also a **canonical path** that is their authentic path. 

For example the Path to the Import-Package headers of the OSGi bundle with bundle id 5, is:

> /osgi/bundles/5/headers/import-package

Starting from the **"/"**, which is the Everest root path, a path is actually a route that passes from several resources, until the final destination as such:

```
/
/osgi
/osgi/bundles
/osgi/bundles/5
/osgi/bundles/headers
/osgi/bundles/headers/import-package
```

## Sub-resources

The  hierarchical nature of paths give way to a hierarchical composition of resources, which means that for a given resource, it is possible that there are some **sub-resources** (or children) that are logically attached to it. Therefore resources are organized into a tree structure starting from the Everest root, all the way down to the leafs.

## Metadata

Resources contain metadata, basic *\<key, value\>* properties, where key is a String and value is any type of object.

## Actions

Everest relies on familiar [CRUD][5] actions to be performed on an identified resource:

* **CREATE**: Creates a new resource and returns that resource
* **READ**: Returns the resource on a Path
* **UPDATE**: Updates the resource with given parameters 
* **DELETE**: Deletes the resource 

If a resource exists, it must respond to requests with READ actions, which should only retreive the resource, without having any effect on its state. Other actions may or may not be implemented by resources. 

## Relations

The most important aspect for implementing a [HATEOAS][1] is to have links between resources that describe the state transitions. Relations allow exactly this. Just as web pages that have hyperlinks to other pages, resources have relations that are links to other resources (or themselves). Following relations, it is possible to discover resources by *browsing* them.

Relations are self-descriptive: In addition to a path that points to the target resource of the relation, a relation describes how an action should be applied, passing a set of parameters, in order to follow the relation.

Utility of relations is twofold: While some relations describe a relationship between two distinct resources, others describe the state transition of a resource. 

## Observable

Everest lets resource implementations send events on state changes. 

* **CREATED**:
* **UPDATED**:
* **DELETED**:

## Adaptable

Even though resources are supposed to be untyped, which favors discoverability, it is sometimes useful to transform a resource to a well-known type, and access directly to its functionallities. Thus resource implementations may know how to 'adapt' themselves to the represented object, or another useful type.

## Dynamism

Dynamism is an important property to have in mind whilst accessing and designing resources. A resource returned by a request has a state that *was* true at the time *t* of the request, and may be incoherent in time *t+1*, because meanwhile the represented **thing** could have changed state or disappeared completely.

Note that this may not be the case for all resources, meaning a resource designer can choose to implement the resource in a reflexive way, delivering the most recent state of the represented resource. However, clients should suppose that the most up-to-date state of a resource is when they just retreive a resource by a request.

## Extensions

extending resources


[1]:	http://en.wikipedia.org/wiki/HATEOAS "HATEOAS"
[2]:	http://en.wikipedia.org/wiki/Uniform_resource_identifier "Uniform Resource Identifier"
[3]:	http://en.wikipedia.org/wiki/Mount_Everest "Mount Everest"
[4]:	http://en.wikipedia.org/wiki/Schr%C3%B6dinger%27s_cat "Schrödinger's Cat"
[5]:	http://en.wikipedia.org/wiki/Create,_read,_update_and_delete "CRUD"

