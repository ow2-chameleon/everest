everest iPOJO Reference Card
============================

This documentation details the layout of the everest iPOJO domain and the different type of resources you may find inside.

In the following structured list, some resources are special (those with a link on it) : they contain metadata, relation and support operations that represent/impact an underlying iPOJO entity.

The other resources (w/out a link) have no special meaning, they just act as containers and defines relations to their child resources.

- **[/ipojo](Root.md "everest iPOJO domain")** - The root of the everest iPOJO domain.
    - /instance - The iPOJO component instances.
        - **[/$name](Instances.md "everest iPOJO Instance Resouces")** The component instance named *$name*.
    - /factory - The iPOJO component factories.
        - /$name - The iPOJO component factories named *$name*.
            - **[/$version](Factories.md "everest iPOJO Factory Resouces")** The component factory named *$name* with version *$version*.
    - /handler - The iPOJO handlers.
        - /$ns - The iPOJO handlers with namespace *$ns*.
            - **[/$name](Handlers.md "everest iPOJO Handler Resouces")** The handler named *$name* with namespace *$ns*.
    - /declaration - The iPOJO declarations.
        - /instance - The iPOJO instance declarations.
            - /$name - The iPOJO instance declarations named *$name*.
                - **[/$index](InstanceDeclarations.md "everest iPOJO Instance Declaration Resouces")** - The *$index* th instance declaration named *$name*. Several instance declarations can have the same name, so the everest iPOJO domain generates an *index* to distinguish them.
        - /type - The iPOJO type declarations.
            - /$name - The iPOJO type declarations named *$name*.
                - **[/$version](TypeDeclarations.md "everest iPOJO Type Declaration Resouces")** - The type declaration named *$name* with version *$version*.
        - /extension - The iPOJO extension declarations.
            - **[/$name](ExtensionDeclarations.md "everest iPOJO Extension Declaration Resouces")** The extension declaration named *$name*.
