everest iPOJO Instance Declaration Resources
============================================

This is the documentation of the *Instance Declaration Resources* of the everest iPOJO domain. Each iPOJO instance declaration is represented by one instance declaration resource.

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
