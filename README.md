Microservice Consul Sample
===================

This sample is like the sample for my Microservices Book
 ([English](http://microservices-book.com/) /
 [German](http://microservices-buch.de/)) that you can find at
 https://github.com/ewolff/microservice .

However, this demo uses Consul for service discovery and Apache httpd
as a reverse proxy to route the calls to the services and as a load balancer.

This project creates a complete micro service demo system in Docker
containers. The services are implemented in Java using Spring and
Spring Cloud.

It uses three microservices:
- Order to process orders.
- Customer to handle customer data.
- Catalog to handle the items in the catalog.

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

Note that the demo uses the original Consul Docker template provided
by Hashicorp. However, the demo does not use a Consul cluster and
stores the data only In-Memory i.e. it is certainly not fit for production.

The Spring Cloud Microservices register themselves on the Consul
server. An alternative is
[Registrator](https://github.com/gliderlabs/registrator). An
alternative load balancer is [Fabio](https://github.com/eBay/fabio).


Apache httpd Load Balancer
------------------------

Apache httpd is used to provide the web page of the demo at
port 8080. Also it forwards HTTP requests to the services. Apache
httpd is configured as a reverse proxy for this - and as a load
balancer. 

To configure this Apache httpd needs to get all registered services
from
Consul. [Consul Template](https://github.com/hashicorp/consul-template)
is used for this. It uses a template for the Apache httpd
configuration and fills in the IP adresses of the registered services.

The Docker container therefore runs two processes: Apache httpd and
Consul Template. To coordinate this supervisord is used. Running both
processes in one Docker container enables Consul Template to restart
Apache httpd if new services are registered.

Please refer to the subdirectory `apache` to see how this works.


Technologies
------------

- Consul for Lookup
- Apache as a reverse proxy to route calls to the appropriate SCS.
- Ribbon for Load Balancing. See the classes CatalogClient and
  CustomerClient in com.ewolff.microservice.order.clients in the
  microservice-demo-order project.
- Hystrix is used for resilience. See CatalogClient in
  com.ewolff.microservice.order.clients in the microservice-demo-order
  project . Note that the CustomerClient won't use Hystrix. This way
  you can see how a crash of the Customer microservices makes the
  Order microservice useless.
- Hystrix has a dashboard. Turbine can be used to combine the data
from multiple sources. However, this does not work at the moment.


How To Run
----------

The demo can be run with [Docker Machine and Docker
Compose](docker/README.md).

Remarks on the Code
-------------------

The server for the infrastruture are pretty simple thanks to Spring Cloud:

- microservice-consul-demo-turbine can be used to consolidate the Hystrix metrics and has a Hystrix dashboard.

The microservices are: 
- microservice-consul-demo-catalog is the application to take care of items.
- microserivce-consul-demo-customer is responsible for customers.
- microservice-consul-demo-order does order processing. It uses microservice-demo-catalog and microservice-demo-customer. Ribbon is used for load balancing and Hystrix for resilience.


The microservices have an Java main application in src/test/java to run them stand alone. microservice-demo-order uses a stub for the other services then. Also there are tests that use customer driven contracts. That is why it is ensured that the services provide the correct interface. These CDC tests are used in microservice-demo-order to verify the stubs. In microserivce-demo-customer and microserivce-demo-catalog they are used to verify the implemented REST services.
