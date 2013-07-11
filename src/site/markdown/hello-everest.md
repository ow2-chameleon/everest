Hello Everest!
==============

Here goes the Everest, Aconcagua and other Seven Summits.

## Requirement

In order to follow the tutorial, you need to download :

* This OSGi framework ( based on Chameleon ) :
* A ReST plug-in for your browser to interact with your domain ( google chrome :

Additionally, you need to have read the part about **[concept](everest-core/concepts.html)** and **[getting started](everest-core/getting-started.html)**.

## General purpose about domain

### What is a domain ?

A domain is a set of things; more or less abstract; that you want to represent as resource.

### First step : identify your resources

In this tuto, the domain we will represent is the house.
So the first and the primordial step is to identify the several resource of the domains.

For this example we choose to represent the house like this :
!["Representation of the house"](everest-casa.png "The Everest Logo")

* **Device** : represent all the electronic equipment in the house (Binarylight,sensors,cooler...), each device is define by a serial number.
* **Person** : represent the inhabitant of the house.
* **Zone** : represent the different part of the house(kitchen,bathroom...).

The level of granularity is at the charge of the domain's creator. Indeed we can imagine to describe more the device resource
in sub-resources like :

* **Light** : every equipment in relation with luminosity (BinaryLight,photometer...).
* **Heat** : every equipment in relation with temperature (Heater,thermometer...).
* **Music** : every equipment in relation with sound (speaker, audio source...).


### Everest in all this

Everest is a resources manager which allowed to traduce the precedent representation in a ReST way.
In our case the ReST layout is :

- **/casa** - The root of the everest Casa domain.
    - **/devices**
        - **$serialNumber** - The device identified by *$serialNumber*.
    - **/person**
    - **/zone**
        - **/$nameZone** - The zone named  *$nameZone*.


## Build your domain

### The root resource

The start point of each domain is the root resource. It contains all the sub resource of the domain.
In our case the root resource is Casa. So let's implement your first resource !

```java
public class CasaRootResource extends DefaultReadOnlyResource {

    /*
    * Name of the resource
     */
    public static final String m_casaRoot = "casa";

    /*
     * Path of the resource : /casa
    */
    public static final Path m_casaRootPath = Path.from(Path.SEPARATOR + m_casaRoot);

    /*
     * Description of the resource
    */
    private static final String m_casaDescription = "casa resources";

    /*
     * List of the subResource : Contains the direct sub resource of root
    */
    private final List<Resource> m_casaResources = new ArrayList<Resource>();

    /*
     * Constructor of Casa
    */
    public CasaRootResource() {
        super(m_casaRootPath);
    }

    /*
     * Extract the sub resource of casa
    */
    @Override
    public List<Resource> getResources() {
        return m_casaResources;
    }
}

```

In order to create the rest architecture create 3 class on this model PersonManager,GenericDeviceManager and ZoneManager.



#### Add sub resource to root

In order to create the rest architecture create 3 class on the precedent model :

- **PersonManager** :
    - *name* : "person"
    - *path* : "/casa/person"
- **GenericDeviceManager** :
    - *name* : "devices"
    - *path* : "/casa/devices"
- **ZoneManager** :
    - *name* : "zone"
    - *path* : "/casa/zone"

We consider that by default this 3 resources are present.So to add this resources to the root we just have to modify the
 constructor  :

```java

  public CasaRootResource() {
        super(m_casaRootPath);
        m_casaResources.add(new GenericDeviceManager());
        m_casaResources.add(new PersonManager());
        m_casaResources.add(new ZoneManager());
    }

```

#### Relation with sub resource

So let's do your first request on your domain.
Send a get request on the following adress: [http://localhost:8080/everest/casa][1]
The response will be :

```json
{
    __relations: {
    }-
    __observable: false
}
```

But where are the sub resource that i just add ?
The fact is it haven't implicit relation between a resource and his sub resource. So let's declare this relation ! For this,
we have to implement a new method in the Root resource :

```java

   public List<Relation> getRelations() {
        List<Relation> relations = new ArrayList<Relation>();
        relations.addAll(super.getRelations());
        for (Resource resource : getResources()) {
            int size = getCanonicalPath().getCount();
            String name = resource.getCanonicalPath().getElements()[size];
            relations.add(new DefaultRelation(resource.getCanonicalPath(), Action.READ, getCanonicalPath().getLast() + ":" + name,
                    "Get " + name));
        }
        return relations;
    }

```

This method will add a relation between the root resource and his sub-resources.

So now if you re-do a get, you will obtain :

```json
{
    __relations: {
        casa:devices: {
            href: "http://localhost:8080/everest/casa/devices"
            action: "READ"
            name: "casa:devices"
            description: "Get devices"
            parameters: [0]
        }-
        casa:person: {
            href: "http://localhost:8080/everest/casa/person"
            action: "READ"
            name: "casa:person"
            description: "Get person"
            parameters: [0]
        }-
        casa:zone: {
            href: "http://localhost:8080/everest/casa/zone"
            action: "READ"
            name: "casa:zone"
            description: "Get zone"
            parameters: [0]
        }-
    }-
    __observable: false
}
```

We see that this method will be very efficient in every case where resource contains sub resource. So a good practice is
to create the following class :

```java
public abstract class AbstractResourceCollection extends DefaultReadOnlyResource {

     /**
      * Constructor, same as {@code DefaultReadOnlyResource}
      *
      * @param path path of the resource
      */
     public AbstractResourceCollection(Path path) {
         super(path);
     }

     /**
      * Extracts the direct children and add a {@literal READ} relation to them.
      *
      * @return list of relations
      */
     public List<Relation> getRelations() {
         List<Relation> relations = new ArrayList<Relation>();
         relations.addAll(super.getRelations());
         for (Resource resource : getResources()) {
             int size = getCanonicalPath().getCount();
             String name = resource.getCanonicalPath().getElements()[size];
             relations.add(new DefaultRelation(resource.getCanonicalPath(), Action.READ, getCanonicalPath().getLast() + ":" + name,
                     "Get " + name));
         }
         return relations;
     }
}
```

Every class which contains sub resource will be *extend* AbstractResourceCollection.

#### How everest run through the ReST architecture

If you send a GET request to ["http://localhost:8080/everest/casa/zone"][1]

```json
{
__relations: {}
__observable: false
}
```
But how the everest core can run through the architecture to */zone* ?
He start to the root and browse recursively the tree invoking the getResource method.
So it's very important to override the getResource method either the everest-core couldn't browse the domain.

### Manage your resource

#### Why manage your resource ?

#### Implement create relation

#### Implement delete relation





[1]: chrome-extension://hgmloofddffdnphfgcellkdfbfbjeloo/RestClient.html "http://localhost:8080/everest/casa"

