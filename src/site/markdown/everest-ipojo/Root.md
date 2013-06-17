everest iPOJO Resource
================================

This is the documentation of the *iPOJO Domain* root resource. Each OSGi framework running Apache Felix iPOJO has a unique iPOJO resource. This resource contains all other iPOJO-related resources (factories, instance, *etc.*)

## Supported operations
- **READ**: get information about iPOJO

## Metadata
- **name** *(string)*: The name the domain: *"ipojo"*.
- **description** *(string)*: The description of the domain: *"The iPOJO domain"*.
- **version** *(string)*: The version of iPOJO.

## Relations
- **bundle**: to the iPOJO bundle.
- **instances**: to the InstanceResources.
- **factories**: to the FactoryResources.
- **handlers**: to the HandlerResources.
- **declarations**: to the DeclarationResources.

## Supported Adaptations
- to **org.osgi.framework.Version**.class: to the version of iPOJO
- to **org.osgi.framework.Bundle**.class: the the iPOJO bundle
- to **java.util.Map**.class: to the contained sub-resources, indexed by path.
- to **java.util.Collection**.class: to the contained sub-resources.
