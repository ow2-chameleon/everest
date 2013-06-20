everest iPOJO Type Declaration Resources
========================================

This is the documentation of the *Type Declaration Resources* of the everest iPOJO domain. Each type declaraion resource is a representation of one iPOJO type declaration.

## Path
[/ipojo/declaration/type/$name/$version](ReferenceCard.html "everest iPOJO Reference Card") - Where *$name* stand for the name of the type and *$version* for its version (may be *null*).

## Supported operations
- **READ**: get the current state of the type declaration

*NOTE: This type of resource is* **observable**

## Metadata
- **name** *(string)*: The name of the iPOJO type declaration.
- **version** *(string)*: The version of the type declaration. May be *null*.
- **extension** *(string)*: The iPOJO extension used by the type declaration. *"component"* or *"handler"* most of the time.
- **isPublic** *(boolean)*: The flag indicating if the declaration type is public (i.e. its factory service is exposed).
- **componentMetadata** *(string)*: The detailed representation of the type's content.
- **status.isBound** *(boolean)*: The flag indicating if the declaration is resolved.
- **status.message** *(string)*: The message indicating why the declaration is not resolved, or *"Declaration bound"* if the declaration is bound.
- **status.throwable** *(java.lang.Throwable)*: The exception that caused the declaration not to be resolved. *null* if the declaration is bound.

## Relations
- **resolvedBy**: to the representation of the resolved Factory or HandlerFactory, depending on **extension** \( *"component"* or *"handler"* respectively\). For other extensions, the relation is *not* present. 
- **service**: to the TypeDeclaration OSGi service.
- **bundle**: to the OSGi bundle declaring the extension.
- **extension**: to the ExtensionDeclaration used by this type declaration.

## Supported Adaptations
- to **org.apache.felix.ipojo.extender.TypeDeclaration**.class: to the TypeDeclaration service object.

## Example
READ /ipojo/declaration/type/Foo/1.2.3.foo

```json
{
  "name":"Foo",
  "version":"1.2.3.foo",
  "extension":"component",
  "isPublic":true,
  "componentMetadata":
    "component name=\"Foo\"
    version=\"1.2.3.foo\"
    classname=\"org.apache.felix.ipojo.everest.ipojo.test.b1.FooProviderImpl\"\n\t
    provides specifications=\"{org.apache.felix.ipojo.everest.ipojo.test.b1.FooService}\"\n\t\t
    property name=\"fooCounter\" field=\"fooCounter\" value=\"0\" type=\"int\"\n\t
    properties\n\t\t
    property name=\"fooPrefix\" field=\"fooPrefix\" value=\"\" type=\"java.lang.String\"\n\t
    manipulation\n\t\t
    interface name=\"org.apache.felix.ipojo.everest.ipojo.test.b1.FooService\"\n\t\t
    field name=\"fooCounter\" type=\"int\"\n\t\t
    field name=\"fooPrefix\" type=\"java.lang.String\"\n\t\t
    method name=\"$init\"\n\t\t
    method name=\"getFoo\" return=\"java.lang.String\"",
  "status.isBound":true,
  "status.message":"Declaration bound",
  "status.throwable":null,
  "__relations": {
    "resolvedBy": {
      "href":"http://localhost:8080/everest/ipojo/factory/Foo/1.2.3.foo",
      "action":"READ",
      "name":"resolvedBy",
      "description":"The resolved iPOJO factory ",
      "parameters":[]
    },
    "service": {
      "href":"http://localhost:8080/everest/osgi/services/53",
      "action":"READ",
      "name":"service",
      "description":"The ExtensionDeclaration OSGi service",
      "parameters":[]
    },
    "bundle": {
      "href":"http://localhost:8080/everest/osgi/bundles/23",
      "action":"READ",
      "name":"bundle",
      "description":"The declaring OSGi bundle",
      "parameters":[]
    },
    "extension": {
      "href":"http://localhost:8080/everest/ipojo/declaration/extension/component",
      "action":"READ",
      "name":"extension",
      "description":"The iPOJO extension used by this declared type",
      "parameters":[]
    }
  },
  "__observable":true
}
```
