Microservice Consul Sample
===================

This sample is like the sample for my Microservices Book
 ([English](http://microservices-book.com/) /
 [German](http://microservices-buch.de/)) that you can find at
 https://github.com/ewolff/microservice .

However, this demo uses [Hashicorp Consul](https://www.consul.io) for service discovery and Apache httpd
as a reverse proxy to route the calls to the services and as a load balancer.

This project creates a complete micro service demo system in Docker
containers. The services are implemented in Java using Spring and
Spring Cloud.

It uses three microservices:
- `Order` to process orders. (http://localhost:8380 when started locally)
- `Customer` to handle customer data. (http://localhost:8280)
- `Catalog` to handle the items in the catalog. (http://localhost:8180)

Consul
------

Consul has a Web UI. You can access it at port 8500 of your Docker
host. Also the homepage at port 8080 contains a link to the Consul UI

Also you can use Consul's DNS interface with e.g. dig:

```
dig @192.168.99.100 -p 8600  order.service.consul.
dig @192.168.99.100 -p 8600  order.service.consul. ANY
dig @192.168.99.100 -p 8600  order.service.consul. SRV
```

Note that the demo uses the original []Consul Docker template](https://hub.docker.com/_/consul/) provided
by Hashicorp. However, the demo does not use a Consul cluster and
only stores the data in memory i.e. it is certainly not fit for production.

All the Spring Cloud microservices (customer, catalog and order) register
themselves in the Consul server. An alternative approach to register the services is
[Registrator](https://github.com/gliderlabs/registrator).
An alternative to using Apache HTTP as a load balancer would e.g. be [Fabio](https://github.com/eBay/fabio).


Apache HTTP Load Balancer
------------------------

Apache HTTP is used to provide the web page of the demo at
port 8080. It also forwards HTTP requests to the microservices whose ports
are not exposed! Apache HTTP is configured as a reverse proxy for this - and
as a load balancer i.e. if you start multiple instances of a microservices
e.g. via `docker-compose scale catalog=2`, Apache will recognize the new instance.

To configure this Apache HTTP needs to get all registered services from
Consul. [Consul Template](https://github.com/hashicorp/consul-template)
is used for this. It uses a template for the Apache HTTP
configuration and fills in the IP addresses of the registered services.

The Docker container therefore runs two processes: Apache HTTP and
Consul Template. To coordinate these [supervisord](http://supervisord.org) is
used. Running both processes in one Docker container enables Consul Template
to restart Apache HTTP when new services are registered in the Consul server.

Please refer to the subdirectory `apache` to see how this works.


Technologies
------------

- Consul for Lookup/ Discovery
- Apache as a reverse proxy to route calls to the appropriate SCS.
- [Ribbon](https://github.com/netflix/Ribbon) for client-side Load Balancing. See the classes `CatalogClient` and
  `CustomerClient` in the package `com.ewolff.microservice.order.clients` in the
  [microservice-demo-order](https://github.com/innoq/microservice-consul/tree/master/microservice-consul-demo/microservice-consul-demo-order) project.
- [Hystrix](https://github.com/netflix/hystrix) is used for resilience. See `CatalogClient` in
  `com.ewolff.microservice.order.clients` in the microservice-demo-order
  project . Note that the CustomerClient won't use Hystrix. This way
  you can see how a crash of the Customer microservices makes the
  Order microservice useless.
- Hystrix has a dashboard. [Turbine](https://github.com/netflix/Turbine) can be used to combine the data
from multiple sources i.e. `hystrix.stream`s. **Unfortunately this does not work at the moment.**


How To Run
----------

The demo can be run with [Docker Machine and Docker
Compose](docker/README.md) via `docker-compose up`

Remarks on the Code
-------------------

The servers for the infrastructure components are pretty simple thanks to Spring Cloud:

- microservice-consul-demo-turbine can be used to consolidate the Hystrix metrics and has a Hystrix dashboard.

The microservices are:

- microservice-consul-demo-catalog is the application to take care of items.
- microserivce-consul-demo-customer is responsible for customers.
- microservice-consul-demo-order does order processing. It uses microservice-demo-catalog and microservice-demo-customer. Ribbon is used for load balancing and Hystrix for resilience.


The microservices have a Java main application in `src/test/java` to run them stand alone. `microservice-demo-order` uses a stub for the other services then. Also there are tests that use _consumer-driven contracts_. That is why it is ensured that the services provide the correct interface. These CDC tests are used in microservice-demo-order to verify the stubs. In `microservice-demo-customer` and `microserivce-demo-catalog` they are used to verify the implemented REST services.
