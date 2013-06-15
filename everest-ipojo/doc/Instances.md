everest iPOJO Instance Resources
================================

This is the documentation of the *Instance Resources* of the everest iPOJO domain. Each instance resource is a representation of an iPOJO component instance.

## Supported operations
- **READ**: get the current state of the component instance
- **CREATE**: [create a new instance](#how-to-create-instances)
- **UPDATE**: [reconfigure the instance](#how-to-reconfigure-instances) and/or [change its state](#how-to-change-instance-state)
- **DELETE**: destroy the component instance

*NOTE: This type of resource is* **observable**

## Metadata
- **name** *(string)*: The name of the instance.
- **factory.name** *(string)*: The name of the factory that has created this instance.
- **factory.version** *(string)*: The version of the factory that has created this instance. May be *null*.
- **state** *(string)*: The current state of the instance. One of *{"valid", "invalid", "stopped", "disposed", "changing", "unknown"}*.
- **configuration** *(map<string, ?>)*: The current configuration properties of the component.

## Relations
- **service**: to the Architecture OSGi service of the instance
- **factory**: to the FactoryResource representing the factory that has created this instance.
- **delete**: to destroy the component instance.
- **reconfigure**: to reconfigure this component instance. See [reconfigure instance](#how-to-reconfigure-instances) and [change instance state](#how-to-change-instance-state)
- **dependencies**: to the ServiceDependencyResources of the instance (i.e. the */dependency* sub-resource).
- **providings**: to the ServiceProvidingResources of the instance (i.e. the */providing* sub-resource).

## Sub-resources
- **/dependency**: the service dependencies of the instance.
- **/provinding**: the service providings of the instance.

## Supported Adaptations
- to **org.apache.felix.ipojo.architecture.Architecture**.class: to the Architecture service object.
- to **org.apache.felix.ipojo.ComponentInstance**.class: to the iPOJO ComponentInstance object.
- to **java.util.Map**.class: to the contained sub-resources, indexed by path.
- to **java.util.Collection**.class: to the contained sub-resources.

## HOW-TOs

### How to create instances

### How to reconfigure instances

### How to change instance state

### Fake instance resource! WTF?
