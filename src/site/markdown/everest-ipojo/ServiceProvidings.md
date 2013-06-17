everest iPOJO Service Providing Resources
=========================================

This is the documentation of the *Service Providing Resources* of the everest iPOJO domain. Each service providing of an iPOJO component instance is represented by an everest iPOJO service providing resource.

## Path
[/ipojo/instance/$name/providing/$index](ReferenceCard.md "everest iPOJO Reference Card") - Where *$name* stand for the name of the instance and *$index* for the index of the providing.

## Supported operations
- **READ**: get the current state of the service dependency.
- **UPDATE**: [reconfigure the dependency](#how-to-reconfigure-providings)
    - **state** *(string)*: the wanted state for the providing. One of *{"REGISTERED", "UNREGISTERED"}*. May be *null*.

*NOTE: This type of resource is* **observable**

## Metadata
- **serviceSpecifications** *(string array)*: The specification of the provided service.
- **policy** *(string)*: The providing policy if the service. One of *{"SINGLETON_STRATEGY", "SERVICE_STRATEGY", "STATIC_STRATEGY", "INSTANCE_STRATEGY", "CUSTOM_STRATEGY", "unknown"}*.
- **creationStrategy** *(string)*: The name of the *CreationStrategy* class for the service providing.
- **controller** *(string)*: The provided service controller.
- **state** *(string)*: The current state of the provided service. One of *{"REGISTERED", "UNREGISTERED"}*.
- **properties** *(map)*: The current properties exposed by the provided service.
- **__isFake** *(boolean)*: Set to *true* if the resource is a [fake resource](Instances.md#fake-instance-resource-wtf "Fake instance resource! WTF?"). Unset otherwise.

## Relations
- **service**: to the provided OSGi service.

## Supported Adaptations
- to **org.apache.felix.ipojo.handlers.providedservice.ProvidedService**.class: to the iPOJO ProvidedService.
- to **org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceDescription**.class: to the iPOJO ProvidedServiceDescription.
- to **org.osgi.framework.ServiceReference**.class: to the reference of the currently provided service. *null* if the service is not currently provided.

## HOW-TOs

### How to reconfigure providings
You can reconfigure dynamically service providings by sending an **UPDATE** request on their resource representation. For now, the only possible action is to register/unregister the service. Here is a quick example:

Request:
```
UPDATE /ipojo/instance/org.apache.felix.ipojo.everest.core.Everest-0/providing/0
- state="unregistered"
```

Result:
```json
{
  "state":"UNREGISTERED",
  ...
}
```

**WARNING**: Reconfiguring the service providings may have quite strange side effects on the component instance, especially if it has a service controller or providing callbacks. Prepare for unforeseen consequences...

**NOTE**: The above example is a perfect illustration of a *BAD* example. If you are wondering why: it is exactly as cutting off the branch you're sitting on. Prepare for predictable resource damages.

## Example
READ /ipojo/instance/org.apache.felix.ipojo.everest.core.Everest-0/providing/0
```json
{
  "state":"REGISTERED",
  "properties": {
    "factory.name":"org.apache.felix.ipojo.everest.core.Everest",
    "instance.name":"org.apache.felix.ipojo.everest.core.Everest-0"
  },
  "__relations": {
    "service": {
      "href":"http://localhost:8080/everest/osgi/services/73",
      "action":"READ",
      "name":"service",
      "description":"Provided service",
      "parameters":[]
    }
  },
  "__observable":true
}
```
