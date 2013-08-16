everest iPOJO Factory Resources
================================

This is the documentation of the *Factory Resources* of the everest iPOJO domain. Each factory resource is a representation of an iPOJO component factory.

## Path
[/ipojo/factory/$name/$version](ReferenceCard.html "everest iPOJO Reference Card") - Where *$name* stand for the name of the factory, and *$version* for its version (or *null* if the factory defines no version).

## Supported operations
- **READ**: get the current state of the component factory
- **CREATE**: [create a new instance](#how-to-create-instances) using the factory
- **DELETE**: destroy the component factory

*NOTE: This type of resource is* **observable**

## Metadata
- **name** *(string)*: The name of the factory.
- **version** *(string)*: The version of the factory.  May be *null*
- **className** *(string)*: The name of the implementation class of the factory.
- **state** *(string)*: The current state of the factory. One of *{"valid", "invalid", "unknown"}*.
- **missingHandlers** *(list\<string\>)*: The fully qualified names of the missing handlers that are required by the factory.

## Relations
- **service**: to the Factory OSGi service of the factory
- **bundle**: to the OSGi bundle declaring the factory.
- **declaration**: to the TypeDeclarationResource of the factory.
- **requiredHandler**\[$fqn\]: to the handlers required by the factory. *$fqn* stands for the fully qualified name of the targeted HandlerResource.
- **instance**\[$name\]: to the instances created by the factory. *$name* stands for the name of the targeted instance.

## Supported Adaptations
- to **org.apache.felix.ipojo.Factory**.class: to the Factory service object.

## HOW-TOs

### <a name="how-to-create-instances"></a>How to create instances
The factory resource allows to create component instance of the represented factory. While the semantic of the resource operation could appear a bit weird (from the CRUD's point of view), it allows you, when you hold the resource of a specific factory, to create an instance very easily.

All you have to do is to make a **CREATE** request **on the factory resource**. The request parameters will be considered as the wanted configuration for the instance to create. The result of the request will be the resource representing the created instance. If the created instance does not expose the *Architecture* service (i.e. *architecture="false"*), the returned resource is a [fake instance resource](Instances.html#fake-instance-resource-wtf "Fake instance resource! WTF?").

Here is a short example to illustrate instance creation from factory resources:

Request:

```
CREATE /ipojo/factory/Foo/1.2.3.foo
- instance.name="FooExample"
- fooPrefix="__example"
```

Result:

```json
{
  "name":"FooExample",
  "factory.name":"Foo",
  "factory.version":"1.2.3.foo",
  "state":"valid",
  "configuration": {
    "fooPrefix":"__example"
  },
  ..
}
```

## Example

READ /ipojo/factory/Foo/1.2.3.foo

```json
{
  "name":"Foo",
  "version":"1.2.3.foo",
  "className":"org.ow2.chameleon.everest.ipojo.test.b1.FooProviderImpl",
  "state":"valid",
  "missingHandlers":[],
  "__relations": {
    "service": {
      "href":"http://localhost:8080/everest/osgi/services/66",
      "action":"READ",
      "name":"service",
      "description":"The Factory OSGi service",
      "parameters":[]
    },
    "bundle": {
      "href":"http://localhost:8080/everest/osgi/bundles/23",
      "action":"READ",
      "name":"bundle",
      "description":"The declaring OSGi bundle",
      "parameters":[]
    },
    "declaration": {
      "href":"http://localhost:8080/everest/ipojo/declaration/type/Foo/1.2.3.foo",
      "action":"READ",
      "name":"declaration",
      "description":"The declaration of this factory",
      "parameters":[]
    },
    
    "requiredHandler[org.apache.felix.ipojo:properties]": {
      "href":"http://localhost:8080/everest/ipojo/handler/org.apache.felix.ipojo/properties",
      "action":"READ",
      "name":"requiredHandler[org.apache.felix.ipojo:properties]",
      "description":"Required handler 'org.apache.felix.ipojo:properties'",
      "parameters":[]
    },
    "requiredHandler[org.apache.felix.ipojo:provides]": {
      "href":"http://localhost:8080/everest/ipojo/handler/org.apache.felix.ipojo/provides",
      "action":"READ",
      "name":"requiredHandler[org.apache.felix.ipojo:provides]",
      "description":"Required handler 'org.apache.felix.ipojo:provides'",
      "parameters":[]
    },
    "requiredHandler[org.apache.felix.ipojo:architecture]": {
      "href":"http://localhost:8080/everest/ipojo/handler/org.apache.felix.ipojo/architecture",
      "action":"READ",
      "name":"requiredHandler[org.apache.felix.ipojo:architecture]",
      "description":"Required handler 'org.apache.felix.ipojo:architecture'",
      "parameters":[]
    },
    
    "instance[DeclaredFoo123]": {
      "href":"http://localhost:8080/everest/ipojo/instance/DeclaredFoo123",
      "action":"READ","name":"instance[DeclaredFoo123]",
      "description":"Instance 'DeclaredFoo123'",
      "parameters":[]
    }
  },
  "__observable":true
}
```
