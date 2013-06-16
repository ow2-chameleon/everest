everest iPOJO Reference Card
============================

This documentation details the layout of the everest iPOJO domain and the different type of resources you may find inside.

In the following structured list, some resources are special (those with a link on it) : they contain metadata, relation and support operations that represent/impact an underlying iPOJO entity.

The other resources (w/out a link) have no special meaning, they just act as containers and defines relations to their child resources.

- **[/ipojo](Root.md "everest iPOJO domain")** - The root of the everest iPOJO domain.
    - /instance - The iPOJO component instances.
        - **[/$name](Instances.md "everest iPOJO Instance Resources")** The component instance named *$name*.
            - /dependency - The service dependencies of the component instance.
                - **[/id](ServiceDependencies.md "everest iPOJO Service Dependency Resources")** - The service dependency with the *$id* identifier.
            - /providing - The service providings of the component instance.
                - **[/index](ServiceProvidings.md "everest iPOJO Service Providing Resources")** - The service providing with index *$index*.
    - /factory - The iPOJO component factories.
        - /$name - The iPOJO component factories named *$name*.
            - **[/$version](Factories.md "everest iPOJO Factory Resources")** The component factory named *$name* with version *$version*.
    - /handler - The iPOJO handlers.
        - /$ns - The iPOJO handlers with namespace *$ns*.
            - **[/$name](Handlers.md "everest iPOJO Handler Resources")** The handler named *$name* with namespace *$ns*.
    - /declaration - The iPOJO declarations.
        - /instance - The iPOJO instance declarations.
            - /$name - The iPOJO instance declarations named *$name*.
                - **[/$index](InstanceDeclarations.md "everest iPOJO Instance Declaration Resources")** - The *$index* th instance declaration named *$name*. Several instance declarations can have the same name, so the everest iPOJO domain generates an *index* to distinguish them.
        - /type - The iPOJO type declarations.
            - /$name - The iPOJO type declarations named *$name*.
                - **[/$version](TypeDeclarations.md "everest iPOJO Type Declaration Resources")** - The type declaration named *$name* with version *$version*.
        - /extension - The iPOJO extension declarations.
            - **[/$name](ExtensionDeclarations.md "everest iPOJO Extension Declaration Resources")** The extension declaration named *$name*.
