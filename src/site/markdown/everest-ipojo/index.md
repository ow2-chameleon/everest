everest iPOJO
=============

This is the documentation of the everest iPOJO domain. This domain is a resource-base representation of Apache Felix iPOJO entities, including:
- Component instances
- Component factories
- Handlers
- Declarations

## Requirements
In order to use the everest iPOJO resource domain, you need an [OSGi™](http://www.osgi.org "OSGi™ Alliance") r4.3 compliant framework with the following bundles:
- [Apache Felix iPOJO](http://www.ipojo.org "iPOJO web site"), version **1.10.1** or above
- everest-core, version ${everest.core.version}

**WARNING:** The everest iPOJO domains uses the latest bug fixes and improvements of Apache Felix iPOJO, so it *won't work at all* on previous releases (\<=1.10.0).

Optional dependencies include:
- everest-servlet, version ${everest.servlet.version} if you want to use HTTP binding on the iPOJO resources.
- everest-osgi, version ${everest.osgi.version} because iPOJO resources are very often related to resources of the everest OSGi domain.

## Installation

The everest iPOJO domain has no need for specific configuration : it uses the same configuration as the **everest-core** bundle.

To install the everest iPOJO domain on your OSGi framework, you just have to install the **everest-ipojo** bundle.

## Use

To use the everest iPOJO domain, you need to get the *Everest* service and make requests on the "/ipojo" resources.

Here is a quick programmatic usage example:
```java
  package example.everest.ipojo;
  
  import org.osgi.framework.BundleContext;
  import org.osgi.framework.ServiceReference;
  
  import org.apache.felix.ipojo.everest.services.EverestService;
  import org.apache.felix.ipojo.everest.services.Resource;
  import org.apache.felix.ipojo.everest.impl.DefaultRequest;
  import org.apache.felix.ipojo.everest.services.Action;
  import org.apache.felix.ipojo.everest.services.Path;
  
  public class EverestIpojoExample {
    
    public Resource getIpojoResource(BundleContext bc) throws Exception {
        
        // This is how you can get the Everest service.
        // Beware that the EverestService may not be present (i.e. ref == null)
        ServiceReference\<Everest\> ref = bc.getServiceReference(EverestService.class);
        EverestService everest = bc.getService(ref);
        
        // This is how you can access the everest iPOJO domain.
        // The process() method throws a ResourceNotFoundException exception
        // if the iPOJO domain is not active.
        Resource r = everest.process(new DefaultRequest(Action.READ, Path.from("/ipojo"), null));
        
        return r;
    }
  
  }
```

If you have chosen to install the optional **everest-servlet** dependency (which needs an HTTP service), then you can access to the everest iPOJO domain from your browser.

Considering that the framework is running on *localhost*, and that the HTTP service listens to the *8080* port, you just have to enter the following URL in your favorite browser:

    http://localhost:8080/everest/ipojo

You should obtain something like this:
```json
{
  "name":"ipojo",
  "description":"The iPOJO domain",
  "version":"1.10.1.SNAPSHOT",
  ...
}
```

You should consider using a REST client that can send different type of requests (GET, POST, ...), so you can have (almost) total control on the everest iPOJO resources.

The [everest iPOJO Reference Card](ReferenceCard.md "Reference Card") details the global layout of the everest iPOJO domain and the resources you can found inside. 


## Copyright and License

everest iPOJO is part of the Apache Felix project, and thus is licensed to the Apache Software foundation with the following license information.
```
    Copyright 2006-2013 The Apache Software Foundation
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```

You can get a complete reader-friendly copy of the license at the following location: [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html "Apache License, Version 2.0").

## Changelog

- **v1.0.0**: Initial release
