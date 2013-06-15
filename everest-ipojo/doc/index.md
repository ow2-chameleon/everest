everest iPOJO
=============

This is the documentation of the everest iPOJO domain. This domain is a resource-base representation of Apache Felix iPOJO entities, including:
- Component instances
- Component factories
- Handlers
- Declarations

# Requirements
In order to use the everest iPOJO resource domain, you need an OSGi r4.3 compliant framework with the following bundles:
- [Apache Felix iPOJO](http://www.ipojo.org "iPOJO web site"), version 1.10.1 or above
- everest-core, version ${everest.core.version}

Optional dependencies include:
- everest-servlet, version ${everest.servlet.version} if you want to use HTTP binding on the iPOJO resources
- everest-osgi, version ${everest.osgi.version} because iPOJO resources are very often related to resources of the everest OSGi domain
