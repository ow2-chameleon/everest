everest iPOJO Type Declaration Resources
========================================

This is the documentation of the *Type Declaration Resources* of the everest iPOJO domain. Each type declaraion resource is a representation of one iPOJO type declaration.

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
