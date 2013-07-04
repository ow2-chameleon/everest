
# Concepts

everest rely on [**H**ypermedia **a**s **t**he **E**ngine **o**f **A**pplication **S**tate (HATEOAS)][1] principles. It proposes simple concepts that helps to implement a *true* REST architectural style for representating resources.

## Resource

A resource is **something** and **anything** that you want to represent. It serves as a the representation of the state of that **thing** at a moment in time and it is capable of handling [Requests](#request) that target them for manipulating this state. This temporality is an important aspect to have in mind whilst designing and accessing resources. It means that a Resource returned by a Request has a state that *was* true at the time *t* of the request, and may be incoherent in time *t+1*.

Resources provide an abstract, untyped interface that allows clients uniformally access to state representations. In order to do so, they are constructed on some primitive concepts:

#### Path

Resources are addressed through their Path by requests. Just as an [URI][2], a resource's Path designates its name and how to access to that it by a Request. Each resource has also a **canonical path** that is their authentic path. 

For example the Path to the Import-Package headers of the OSGi bundle with bundle-id 5, is:

> /osgi/bundles/5/headers/import-package

The naturally hiearchical composition of Paths give way to a logical hierachical composition of resources, which means that for a given resource, it is possible that there are some [Sub-resources](#sub-resources), some children that are logically attached to it. 

#### Metadata

metadata

#### Relations

relations

#### Sub-Resources

subresources

#### Observable

Observing resources

#### Adaptable

adaptable

## Request

requests

## Extensions

extending resources


[1]:  http://en.wikipedia.org/wiki/HATEOAS "HATEOAS"
[2]:  http://en.wikipedia.org/wiki/Uniform_resource_identifier "Uniform Resource Identifier"
