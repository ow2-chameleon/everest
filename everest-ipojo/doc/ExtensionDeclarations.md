everest iPOJO Extension Declaration Resources
=============================================

This is the documentation of the *Extension Declaration Resources* of the everest iPOJO domain. Each extension declaraion resource is a representation of an iPOJO extension.

## Supported operations
- **READ**: get the current state of the extension declaration

*NOTE: This type of resource is* **observable**

## Metadata
- **name** *(string)*: The name of the iPOJO extension.
- **status.isBound** *(boolean)*: The flag indicating if the declaration is resolved.
- **status.message** *(string)*: The message indicating why the declaration is not resolved, or *"Declaration bound"* if the declaration is bound.
- **status.throwable** *(java.lang.Throwable)*: The exception that caused the declaration not to be resolved. *null* if the declaration is bound.

## Relations
- **service**: to the ExtensionDeclaration OSGi service
- **bundle**: to the OSGi bundle declaring the extension.

## Supported Adaptations
- to **org.apache.felix.ipojo.extender.ExtensionDeclaration**.class: to the ExtensionDeclaration service object.
