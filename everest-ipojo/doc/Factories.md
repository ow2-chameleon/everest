everest iPOJO Factory Resources
================================

This is the documentation of the *Factory Resources* of the everest iPOJO domain. Each factory resource is a representation of an iPOJO component factory.

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
- **missingHandlers** *(list<string>)*: The fully qualified names of the missing handlers that are required by the factory.

## Relations
- **service**: to the Factory OSGi service of the factory
- **bundle**: to the OSGi bundle declaring the factory.
- **declaration**: to the TypeDeclarationResource of the factory.
- **requiredHandler**\[$fqn\]: to the handlers required by the factory. *$fqn* stands for the fully qualified name of the targeted HandlerResource.
- **instance**\[$name\]: to the instances created by the factory. *$name* stands for the name of the targeted instance.

## Supported Adaptations
- to **org.apache.felix.ipojo.Factory**.class

## HOW-TOs

### How to create instances

### Fake instance resource! WTF?
