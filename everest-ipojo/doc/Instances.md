everest iPOJO Instance Resources
================================

This is the documentation of the *Instance Resources* of the everest iPOJO domain. Each instance resource is a representation of an iPOJO component instance.

## Path
[/ipojo/instance/$name](ReferenceCard.md "everest iPOJO Reference Card") - Where *$name* stand for the name of the instance.

## Supported operations
- **READ**: get the current state of the component instance
- **CREATE**: [create a new instance](#how-to-create-instances)
    - **factory.name** *(string)*: the name of the factory to use to create the instance.
    - **factory.version** *(string)*: the version of the factory to use to create the instance. May be *null*.
- **UPDATE**: [reconfigure the instance](#how-to-reconfigure-instances) and/or [change its state](#how-to-change-instance-state)
    - **configuration** *(map)*: the wanted configuration of the instance.
    - **state** *(string)* the wanted state of the instance. One of *{"valid", "invalid", "stopped", "disposed"}*.
- **DELETE**: destroy the component instance

*NOTE: This type of resource is* **observable**

## Metadata
- **name** *(string)*: The name of the instance.
- **factory.name** *(string)*: The name of the factory that has created this instance.
- **factory.version** *(string)*: The version of the factory that has created this instance. May be *null*.
- **state** *(string)*: The current state of the instance. One of *{"valid", "invalid", "stopped", "disposed", "changing", "unknown"}*.
- **configuration** *(map<string, ?>)*: The current configuration properties of the component.
- **__isFake** *(boolean)*: Set to *true* if the resource is a [fake instance resource](Instances.md#fake-instance-resource-wtf "Fake instance resource! WTF?"). Unset otherwise.

## Relations
- **service**: to the Architecture OSGi service of the instance
- **factory**: to the FactoryResource representing the factory that has created this instance.
- **delete**: to destroy the component instance.
- **reconfigure**: to reconfigure this component instance. See [reconfigure instance](#how-to-reconfigure-instances) and [change instance state](#how-to-change-instance-state)
- **dependencies**: to the ServiceDependencyResources of the instance (i.e. the */dependency* sub-resource).
- **providings**: to the ServiceProvidingResources of the instance (i.e. the */providing* sub-resource).

## Sub-resources
- **/dependency**: the service dependencies of the instance.
- **/provinding**: the service providings of the instance.

## Supported Adaptations
- to **org.apache.felix.ipojo.architecture.Architecture**.class: to the Architecture service object.
- to **org.apache.felix.ipojo.ComponentInstance**.class: to the iPOJO ComponentInstance object.
- to **java.util.Map**.class: to the contained sub-resources, indexed by path.
- to **java.util.Collection**.class: to the contained sub-resources.

## HOW-TOs

### How to create instances
You can create iPOJO component instances by sending a **CREATE** request on an unexistent instance resource path. The request parameters are used as the configuration of the instance to create. However, everest iPOJO needs to know what kind of instance you want to create. That's why you need to specify the factory you want to use, by setting the *factory.name* parameter (and, optionally, *factory.version*). If the specified factory cannot be found, everest iPOJO will raise an *IllegalActionOnResource* error (HTTP code *405*).

Here is an example:

Request:
```
CREATE /ipojo/instance/CreatedFoo
- factory.name="Foo"
- factory.version="1.2.3.foo"
```
Result:
```json
{
  "name":"CreatedFoo",
  "factory.name":"Foo",
  "factory.version":"1.2.3.foo",
  "state":"valid",
  "configuration": {
    "fooPrefix":"__example"
  },
  ...
}
```

Beware that if the created instance does not expose the *Architecture* service, the returned resource will be a [fake instance resource](Instances.md#fake-instance-resource-wtf "Fake instance resource! WTF?").

### How to reconfigure instances

### How to change instance state

### Fake instance resource! WTF?
Fake instance resource are maybe the most bizarre thing you will encounter when using the everest iPOJO domain. The principle is quite simple : a fake instance resource represents something that exists, but that is not accessible by the everest iPOJO domain.

Technically, iPOJO component instances usually expose an *Architecture* service, to introspect their state. The everest iPOJO domain uses this service to detect instance and represent them as resources.

But sometimes, some instances don't provide this service... and everest cannot detect those instance. That should not be an issue: you just can't see any resource representing those instance.

The point is that: *yes you can* create such instances, e.g. by [sending a CREATE request on a factory resource](Factories.md#how-to-create-instances "How to create instances"). When such a case happens, instead of returning nothing, the everest iPOJO domain creates a snapshot of the instance that it just has created and return this resource.

Fake instance resources are very limited, compared to real instance resource:
- they only support **READ** operations.
- they are **not observable**
- once they are returned, they **cannot** be retrieved with a READ operation on their path.

To identify if an instance resource is fake or not, just check the presence of the **"__isFake"** metadata. If it is set to *true*, then the resource is fake and only supports a reduced set of operations.

## Example

READ /ipojo/instance/DeclaredFoo123
```json
{
  "name":"DeclaredFoo123",
  "factory.name":"Foo",
  "factory.version":"1.2.3.foo",
  "state":"valid",
  "configuration": {
    "fooPrefix":"__declared"
  },
  "__relations": {
    "service": {
      "href":"http://localhost:8080/everest/osgi/services/82",
      "action":"READ",
      "name":"service",
      "description":"The Architecture OSGi service",
      "parameters":[]
    },
    "factory": {
      "href":"http://localhost:8080/everest/ipojo/factory/Foo/1.2.3.foo",
      "action":"READ",
      "name":"factory",
      "description":"The factory of this component instance",
      "parameters":[]
    },
    "delete": {
      "href":"http://localhost:8080/everest/ipojo/instance/DeclaredFoo123",
      "action":"DELETE",
      "name":"delete",
      "description":"Destroy this component instance",
      "parameters":[]
    },
    "reconfigure": {
      "href":"http://localhost:8080/everest/ipojo/instance/DeclaredFoo123",
      "action":"UPDATE",
      "name":"reconfigure",
      "description":"Reconfigure this component instance",
      "parameters": [
        {
          "name":"state",
          "type":"java.lang.String",
          "description":"The wanted state of the component instance",
          "optional":true
        },
        {
          "name":"configuration",
          "type":"java.util.Map",
          "description":"The wanted configuration of the component instance",
          "optional":true
        }
      ]
    },
    "dependencies": {
      "href":"http://localhost:8080/everest/ipojo/instance/DeclaredFoo123/dependency",
      "action":"READ",
      "name":"dependencies",
      "description":"The service dependencies of this component instance",
      "parameters":[]
    },
    "providings": {
      "href":"http://localhost:8080/everest/ipojo/instance/DeclaredFoo123/providing",
      "action":"READ",
      "name":"providings",
      "description":"The service providings of this component instance",
      "parameters":[]
    }
  },
  "__observable":true
}
```
