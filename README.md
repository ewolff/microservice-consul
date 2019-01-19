Microservice Consul Sample
===================

[Deutsche Anleitung zum Starten des Beispiels](WIE-LAUFEN.md)


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
- `Order` to process orders. (http://localhost:8080 when started locally)
- `Customer` to handle customer data. (http://localhost:8080)
- `Catalog` to handle the items in the catalog. (http://localhost:8080)

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

Note that the demo uses the original
[Consul Docker image](https://hub.docker.com/_/consul/) provided
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
Consul Template. Consul Template starts Apache httpd and also restarts
Apache httpd when new services are registered in the Consul server.

Please refer to the subdirectory `apache` to see how this works.

Prometheus
----------

[Prometheus](https://prometheus.io/) is a monitoring system. The code
of the
[microservice-consul-demo-order](microservice-consul-demo/microservice-consul-demo-order) project
includes code to export metrics to Prometheus in
`com.ewolff.microservice.order.prometheus`. Also the docker-compose
configuration in `docker-compose-prometheus.yml` includes a Prometheus
instance. It will listen on port 9090 on the Docker host. That way you
can monitor the application. Run it with `docker-compose -f
docker-compose-prometheus.yml up -d`.

Elastic Stack
-----------

The [Elastic Stack](https://www.elastic.co/de/products) provides a set
of tools to handle log data. The projects contain a Logback
configuration in `logback-spring.xml` so that the services log JSON
formatted data.

The docker-compose
configuration in `docker-compose-elastic.yml` includes

* Filebeat to ship the log from a common volume to Elasticsearch.

* Elasticsearch to store and analyse the logs.

* Kibana to analyse the logs. You can access it on port 5601 e.g. at
<http://localhost:5601>. The indices are called `filebeat-*`.

You can run the configuration with `docker-compose -f
docker-compose-elastic.yml up -d`.

Zipkin
-----

[Zipkin](http://zipkin.io/) is a tool to trace calls across
microservices. The project includes all necessary libraries to provide
traces.

The docker-compose
configuration in `docker-compose-zipkin.yml` includes

* A Zipkin server to store and display the data.  You can access it on
port 9411 e.g. at <http://localhost:9411>.

* Microservices are configured to provide trace information.

You can run the configuration with `docker-compose -f
docker-compose-zipkin.yml up -d`.


Technologies
------------

- Consul for Lookup/ Discovery
- Apache as a reverse proxy to route calls to the appropriate SCS.
- [Ribbon](https://github.com/netflix/Ribbon) for client-side Load Balancing. See the classes `CatalogClient` and
  `CustomerClient` in the package `com.ewolff.microservice.order.clients` in the
  [microservice-demo-order](https://github.com/innoq/microservice-consul/tree/master/microservice-consul-demo/microservice-consul-demo-order) project.


How To Run
----------

The demo can be run with [Docker Machine and Docker
Compose](docker/README.md) via `docker-compose up`

See [How to run](HOW-TO-RUN.md) for details.

Remarks on the Code
-------------------

The servers for the infrastructure components are pretty simple thanks to Spring Cloud:

The microservices are:

- [microservice-consul-demo-catalog](microservice-consul-demo/microservice-consul-demo-catalog) is the application to take care of items.
- [microservice-consul-demo-customer](microservice-consul-demo/microservice-consul-demo-customer) is responsible for customers.
- [microservice-consul-demo-order](microservice-consul-demo/microservice-consul-demo-order) does order processing. It uses microservice-demo-catalog and microservice-demo-customer. Ribbon is used for load balancing.


The microservices have a Java main application in `src/test/java` to run them stand alone. `microservice-demo-order` uses a stub for the other services then. Also there are tests that use _consumer-driven contracts_. That is why it is ensured that the services provide the correct interface. These CDC tests are used in microservice-demo-order to verify the stubs. In `microservice-demo-customer` and `microserivce-demo-catalog` they are used to verify the implemented REST services.
