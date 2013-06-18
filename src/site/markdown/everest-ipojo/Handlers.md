everest iPOJO Handler Resources
================================

This is the documentation of the *Handler Resources* of the everest iPOJO domain. Each handler resource is a representation of an iPOJO handler factory.

## Path
[/ipojo/handler/$ns/$name](ReferenceCard.md "everest iPOJO Reference Card") - Where *$ns* stand for the namespace of the handler (e.g. *"org.apache.felix.ipojo"*) and *$name* for its name (e.g. *"requires"*).

## Supported operations
- **READ**: get the current state of the handler factory

*NOTE: This type of resource is* **observable**

## Metadata
- **namespace** *(string)*: The namespace of the handler.
- **name** *(string)*: The name of the handler.
- **state** *(string)*: The current state of the handler. One of *{"valid", "invalid", "unknown"}*.
- **missingHandlers** *(list\<string\>)*: The fully qualified names of the missing handlers that are required by the handler.

## Relations
- **service**: to the HandlerFactory OSGi service of the factory
- **bundle**: to the OSGi bundle declaring the handler.
- **declaration**: to the TypeDeclarationResource of the handler.
- **requiredHandler**\[$fqn\]: to the handlers required by the handler. *$fqn* stands for the fully qualified name of the targeted HandlerResource.

## Supported Adaptations
- to **org.apache.felix.ipojo.HandlerFactory**.class: to the HandlerFactory service object.

## Example
READ /ipojo/handler/org.apache.felix.ipojo/requires
```json
{
  "namespace":"org.apache.felix.ipojo",
  "name":"requires",
  "state":"valid",
  "missingHandlers":[],
  "__relations": {
    "service": {
      "href":"http://localhost:8080/everest/osgi/services/44",
      "action":"READ",
      "name":"service",
      "description":"The HandlerFactory OSGi service",
      "parameters":[]
    },
    "bundle": {
      "href":"http://localhost:8080/everest/osgi/bundles/11",
      "action":"READ",
      "name":"bundle",
      "description":"The declaring OSGi bundle",
      "parameters":[]
    },
    "declaration": {
      "href":"http://localhost:8080/everest/ipojo/declaration/type/requires/null",
      "action":"READ",
      "name":"declaration",
      "description":"The declaration of this handler",
      "parameters":[]
    }
  },
  "__observable":true
}
```
