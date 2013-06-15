everest iPOJO Handler Resources
================================

This is the documentation of the *Handler Resources* of the everest iPOJO domain. Each handler resource is a representation of an iPOJO handler factory.

## Supported operations
- **READ**: get the current state of the handler factory

*NOTE: This type of resource is* **observable**

## Metadata
- **namespace** *(string)*: The namespace of the handler.
- **name** *(string)*: The name of the handler.
- **state** *(string)*: The current state of the handler. One of *{"valid", "invalid", "unknown"}*.
- **missingHandlers** *(list<string>)*: The fully qualified names of the missing handlers that are required by the handler.

## Relations
- **service**: to the HandlerFactory OSGi service of the factory
- **bundle**: to the OSGi bundle declaring the handler.
- **declaration**: to the TypeDeclarationResource of the handler.
- **requiredHandler**\[$fqn\]: to the handlers required by the handler. *$fqn* stands for the fully qualified name of the targeted HandlerResource.

## Supported Adaptations
- to **org.apache.felix.ipojo.HandlerFactory**.class
