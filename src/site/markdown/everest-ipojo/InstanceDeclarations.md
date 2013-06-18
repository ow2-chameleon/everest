everest iPOJO Instance Declaration Resources
============================================

This is the documentation of the *Instance Declaration Resources* of the everest iPOJO domain. Each iPOJO instance declaration is represented by one instance declaration resource.

## Path
[/ipojo/declaration/instance/$name/$index](ReferenceCard.md "everest iPOJO Reference Card") - Where :

- *$name* stand for the name of the instance declaration.
- *$index* is a generated number, because several instances can be *declared* with the same name (while only one instance can be *created* with a specific name). Indexes are unique, start from 0 (zero) and are incremented automatically.

## Supported operations
- **READ**: get the current state of the instance declaration

*NOTE: This type of resource is* **observable**

## Metadata
- **name** *(string)*: The name of the iPOJO instance declaration. *"unnamed"* if no name has been specified.
- **factory.name** *(string)*: The name of the component factory.
- **factory.version** *(string)*: The version of the component factory. May be *null*.
- **configuration** *(dictionary)*: The configuration of the instance declaration.
- **status.isBound** *(boolean)*: The flag indicating if the declaration is resolved.
- **status.message** *(string)*: The message indicating why the declaration is not resolved, or *"Declaration bound"* if the declaration is bound.
- **status.throwable** *(java.lang.Throwable)*: The exception that caused the declaration not to be resolved. *null* if the declaration is bound.

## Relations
- **service**: to the InstanceDeclaration OSGi service
- **bundle**: to the OSGi bundle declaring the instance.

## Supported Adaptations
- to **org.apache.felix.ipojo.extender.InstanceDeclaration**.class: to the InstanceDeclaration service object.

## Example
READ /ipojo/declaration/instance/DeclaredFoo123/0

```json
{
  "name":"DeclaredFoo123",
  "factory.name":"Foo",
  "factory.version":"1.2.3.foo",
  "configuration": {
    "component":"Foo",
    "factory.version":"1.2.3.foo",
    "fooCounter":"123",
    "instance.name":"DeclaredFoo123",
    "fooPrefix":"__declared"
  },
  "status.isBound":true,
  "status.message":"Declaration bound",
  "status.throwable":null,
  "__relations": {
    "service": {
      "href":"http://localhost:8080/everest/osgi/services/55",
      "action":"READ",
      "name":"service",
      "description":"The InstanceDeclaration OSGi service",
      "parameters":[]
    },
    "bundle": {
      "href":"http://localhost:8080/everest/osgi/bundles/23",
      "action":"READ",
      "name":"bundle",
      "description":"The declaring OSGi bundle",
      "parameters":[]
    }
  },
  "__observable":true
}
```
