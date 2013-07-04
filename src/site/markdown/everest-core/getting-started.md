# Getting Started with Everest

## Installing Everest

Installing Everest is plain simple: All you need is an OSGi framework compatible with r4.3 specification, **Apache Felix iPOJO 1.10.1 or above** and **everest-core** bundle installed, and you are good to go. Here is a minimal distribution running everest-core:

```
g! lb
START LEVEL 1
   ID|State      |Level|Name
    0|Active     |    0|System Bundle (4.2.1)
    1|Active     |    1|everest-core (1.0.0.SNAPSHOT)
    2|Active     |    1|Apache Felix EventAdmin (1.3.2)
    3|Active     |    1|Apache Felix Gogo Command (0.12.0)
    4|Active     |    1|Apache Felix Gogo Runtime (0.10.0)
    5|Active     |    1|Apache Felix Gogo Shell (0.10.0)
    6|Active     |    1|Apache Felix iPOJO (1.10.1)
g! 
```

Go ahead to [Downloads][1] and test it yourself! 

## Browsing Resources

To access resource representations you need to get the `EverestService` published by the everest-core bundle. 

```java
     // ...
     // This is how you can get the Everest service.
     // Beware that the EverestService may not be present (i.e. ref == null)
    ServiceReference<EverestService> ref = bundleContext.getServiceReference(EverestService.class); 
    EverestService everest = bc.getService(ref);

     // This is how you can access the everest introspection domain.
     Resource introspection = everest.process(new DefaultRequest(Action.READ, Path.from("/everest"), null));
     // ...
```

Or if you are writing an iPOJO component, you can let iPOJO get the service and inject it for you, it is easy:

```java
@Component
public class EverestTestComponent {

    // iPOJO injects the everest service into this field.
    @Requires
    EverestService everest;
    
    // ...
    // So in your code you can use this field to access to everest service 
    Resource introspection = everest.process(new DefaultRequest(Action.READ, Path.from("/everest"), null)); 
    // ...

}
```

Notice how we create a new Request by specifying the `Path` of the resource that we want to access and the `Action` we want to apply on this resource.

Response is a `Resource` that can be parsed into JSON like this:

```json
{
    name: "everest"
    description: "The everest introspection domain"
    __relations: {
        everest:domains: {
            href: "http://localhost:8080/everest/everest/domains"
            action: "READ"
            name: "everest:domains"
            description: "Get domains"
            parameters: [0]
        }
    }
    __observable: false
}
```

Returned resource representation has a set of metadata elements, relations, and if it is observable or not. But more on that later.

If a resource is not found on the given path a `ResourceNotFoundException` is thrown.

```json
{
    error: "resource not found"
    path: "/everest/domain"
    action: "READ"
    message: null
}
```

## Changing Resources

Above we have seen a READ action applied to a path, that returns the resource found on that path. 
Everest lets resources implement familiar [CRUD][2] actions: 

* **CREATE**: Creates a new resource and returns that resource
* **READ**: Returns the resource on a Path, without changing the state of the Resource
* **UPDATE**: Updates the resource with given parameters 
* **DELETE**: Deletes the resource 

While Resource implementations must respond to a READ action, they can choose not to respond to other actions. In that case an `IllegalActionOnResourceException` exception is thrown.

```json
{
    error: "illegal action on resource"
    path: "/everest"
    action: "UPDATE"
    message: null
}
```

For the resources that accept UPDATE action, `EverestService` can be called with update paramaters that contain necessary parameters for updating the resource. 

Below there is an UPDATE action applied to iPOJO instance 'myinstance-0' for changing its state to 'valid':

```java
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("state","valid");
    Resource updatedInstance = everest.process(new DefaultRequest(Action.UPDATE, Path.from("/ipojo/instance/myinstance-0"), params);
```

## Observing Resources

Everest lets resources publish events when they change their state. If Event Admin is installed in the framework, an observer can listen events of resource change through OSGi Event Admin. Each resource has a boolean flag that expresses if it publishes events on its change or not.

Below there is an event handler that listens to the events sent by bundle 5:

```java
    Hashtable<String, Object> props = new Hashtable<String, Object>();
    props.put(EventConstants.EVENT_TOPIC, new String[]{"everest/osgi/bundle/5"});
    bundleContext.registerService(EventHandler.class.getName(), new EventHandler() {
        public void handleEvent(Event event) {
            Object eventType = event.getProperty("eventType");
            Object resourcePath = event.getProperty("canonicalPath");
            System.out.println(eventType + " " + resourcePath
        }
    }, props);
```

## Your first resource

Desinging a resource

## Next Steps

If you would like to **'browse'** your resources and test yourself the resources serialized into JSON, check out the [Everest Servlet][3].

If you would like to create your own domain head out to [Hello Everest][4] tutorial.


[1]: ../downloads.html "Downloads"
[2]: http://en.wikipedia.org/wiki/Create,_read,_update_and_delete "CRUD"
[3]: ../everest-servlet/index.html "everest Servlet"
[4]: ../hello-everest.html "Hello Everest"
