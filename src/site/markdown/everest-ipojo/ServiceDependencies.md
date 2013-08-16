everest iPOJO Service Dependency Resources
==========================================

This is the documentation of the *Service Dependency Resources* of the everest iPOJO domain. Each service dependency of an iPOJO component instance is represented by an everest iPOJO service dependency resource.

## Path
[/ipojo/instance/$name/dependency/$id](ReferenceCard.html "everest iPOJO Reference Card") - Where *$name* stand for the name of the instance and *$id* for the identifier of the dependency.

## Supported operations
- **READ**: get the current state of the service dependency.
- **UPDATE**: [reconfigure the dependency](#how-to-reconfigure-dependencies)
    - **filter** *(string)*: the wanted silter for the dependency. May be *null* (or empty, it has the same meaning).
    - **isAggregate** *(string)*: the wanted value for the isAggregate flag of the dependency. One of *{"true", "false"}*. May be *null*.
    - **isOptional** *(string)*: the wanted value for the isAggregate flag of the dependency. One of *{"true", "false"}*. May be *null*.

*NOTE: This type of resource is* **observable**

## Metadata
- **id** *(string)*: The identifier of the dependency.
- **specification** *(string)*: The specification of the service dependency.
- **isNullable** *(boolean)*: The flag indicating if the dependency uses *Nullable* objects.
- **isProxy** *(boolean)*: The flag indicating if the dependency uses proxies.
- **defaultImplementation** *(string)*: The name of the dependency default implementation class. May be *null*.
- **state** *(string)*: The current state of the dependency. One of *{"RESOLVED", "UNRESOLVED", "BROKEN"}*.
- **policy** *(string)*: The current binding policy of the dependency. One of *{"DYNAMIC_BINDING", "STATIC_BINDING", "DYNAMIC_PRIORITY_BINDING", "CUSTOMIZED"}*.
- **comparator** *(string)*: The name of the service comparator class of the dependency. May be *null*.
- **isAggregate** *(boolean)*: The flag indicating if the dependency is aggregate.
- **isOptional** *(boolean)*: The flag indicating if the dependency is optional.
- **isFrozen** *(boolean)*: The flag indicating if the dependency is frozen.
- __isFake *(boolean)*: Set to *true* if the resource is a [fake resource](Instances.html#fake-instance-resource-wtf "Fake instance resource! WTF?"). Unset otherwise.

## Relations
- **matchingService**[$i]: to all the OSGi services that are currently **matching** the dependency. *$i* stands for the service identifier.
- **usedService**[$i]: to all the OSGi services that are currently **used** the dependency. *$i* stands for the service identifier.

## Supported Adaptations
- to **org.apache.felix.ipojo.util.DependencyModel**.class: to the iPOJO DependencyModel  service object.
- to **org.apache.felix.ipojo.handlers.dependency.DependencyDescription**.class: to the iPOJO DependencyDescription of the dependency.

## HOW-TOs

### <a name="how-to-reconfigure-dependencies"></a>How to reconfigure dependencies
You can reconfigure dynamically service dependencies by sending an **UPDATE** request on their resource representation. Here is a quick example:

Request:

```
UPDATE /ipojo/instance/org.ow2.chameleon.everest.core.Everest-0/dependency/RootResource
- filter="(type=ipojo)"
```

Result:

```json
{
  "state":"RESOLVED",
  "filter":"(type=ipojo)",
  ...
}
```

**WARNING**: Reconfiguring the *isAggregate* and/or *isOptional* flags may have very strange side effects on the dependency and containing component instance. Prepare for unforeseen consequences...

## Example
READ /ipojo/instance/org.ow2.chameleon.everest.core.Everest-0/dependency/RootResource

```json
{
  "state":"RESOLVED",
  "filter":"(type=ipojo)",
  "policy":"DYNAMIC_BINDING",
  "comparator":null,
  "isAggregate":true,
  "isOptional":true,
  "isFrozen":false,
  "__relations": {
    "matchingService[75]": {
      "href":"http://localhost:8080/everest/osgi/services/75",
      "action":"READ",
      "name":"matchingService[75]",
      "description":"Matching service with id '75'",
      "parameters":[]
    },
    "usedService[75]": {
      "href":"http://localhost:8080/everest/osgi/services/75",
      "action":"READ",
      "name":"usedService[75]",
      "description":"Used service with id '75'",
      "parameters":[]
    }
  },
  "__observable":true
}
```
