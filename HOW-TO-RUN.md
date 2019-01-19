# How to Run

This is a step-by-step guide how to run the example:

## Installation

* The example is implemented in Java. See
   https://www.java.com/en/download/help/download_options.xml . The
   examples need to be compiled so you need to install a JDK (Java
   Development Kit). A JRE (Java Runtime Environment) is not
   sufficient. After the installation you should be able to execute
   `java` and `javac` on the command line.

* The example run in Docker Containers. You need to install Docker
  Community Edition, see https://www.docker.com/community-edition/
  . You should be able to run `docker` after the installation.

* The example need a lot of RAM. You should configure Docker to use 4
  GB of RAM. Otherwise Docker containers might be killed due to lack
  of RAM. On Windows and macOS you can find the RAM setting in the
  Docker application under Preferences/ Advanced.
  
* After installing Docker you should also be able to run
  `docker-compose`. If this is not possible, you might need to install
  it separately. See https://docs.docker.com/compose/install/ .

## Build

Change to the directory `microservice-consul-demo` and run `./mvnw clean
package` or `mvnw.cmd clean package` (Windows). This will take a while:

```
[~/microservice-consul/microservice-consul-demo]./mvnw clean package
....
[INFO] 
[INFO] --- maven-jar-plugin:2.6:jar (default-jar) @ microservice-consul-demo-order ---
[INFO] Building jar: /Users/wolff/microservice-consul/microservice-consul-demo/microservice-consul-demo-order/target/microservice-consul-demo-order-0.0.1-SNAPSHOT.jar
[INFO] 
[INFO] --- spring-boot-maven-plugin:1.4.5.RELEASE:repackage (default) @ microservice-consul-demo-order ---
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] microservice-consul-demo ........................... SUCCESS [  1.401 s]
[INFO] microservice-consul-demo-customer .................. SUCCESS [ 25.636 s]
[INFO] microservice-consul-demo-catalog ................... SUCCESS [ 36.618 s]
[INFO] microservice-consul-demo-order ..................... SUCCESS [ 27.781 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:35 min
[INFO] Finished at: 2017-09-07T18:08:13+02:00
[INFO] Final Memory: 52M/416M
[INFO] ------------------------------------------------------------------------
```

If this does not work:

* Ensure that `settings.xml` in the directory `.m2` in your home
directory contains no configuration for a specific Maven repo. If in
doubt: delete the file.

* The tests use some ports on the local machine. Make sure that no
server runs in the background.

* Skip the tests: `./mvnw clean package -Dmaven.test.skip=true` or `mvnw.cmd clean package -Dmaven.test.skip=true`.

* In rare cases dependencies might not be downloaded correctly. In
  that case: Remove the directory `repository` in the directory `.m2`
  in your home directory. Note that this means all dependencies will
  be downloaded again.

## Run the containers

First you need to build the Docker images. Change to the directory
`docker` and run `docker-compose build`. This will download some base
images, install software into Docker images and will therefore take
its time:

```
[~/microservice-consul/docker]docker-compose build 
....
Removing intermediate container 1d59f8227b12
Step 4/4 : EXPOSE 8989
 ---> Running in 11e7fbacfa01
 ---> 9cfa7772986f
Removing intermediate container 11e7fbacfa01
Successfully built 9cfa7772986f
```

Afterwards the Docker images should have been created. They have the prefix
`msconsul`:

```
[~/microservice-consul/docker]docker images
REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
msconsul_order               latest              12b279e78975        52 seconds ago      225MB
msconsul_apache              latest              22fac099ba93        55 seconds ago      255MB
msconsul_catalog             latest              c23c535ecaf6        2 minutes ago       225MB
msconsul_customer            latest              a780e4f49bac        2 minutes ago       225MB
```

Now you can start the containers using `docker-compose up -d`. The
`-d` option means that the containers will be started in the
background and won't output their stdout to the command line:

```
[~/microservice-consul/docker]docker-compose up -d
Creating network "msconsul_default" with the default driver
Pulling consul (consul:0.7.2)...
0.7.2: Pulling from library/consul
b7f33cc0b48e: Already exists
a4ca795d20eb: Pull complete
76bc5ef06918: Pull complete
965e633cb8c2: Pull complete
64e424fcbe65: Pull complete
Digest: sha256:ce15f85417a0cf121d943563dedb873c7d6c26e9b1e8b47bc2f1b5a3e27498e1
Status: Downloaded newer image for consul:0.7.2
Creating msconsul_consul_1 ... 
Creating msconsul_consul_1 ... done
Creating msconsul_order_1 ... 
Creating msconsul_catalog_1 ... 
Creating msconsul_customer_1 ... 
Creating msconsul_apache_1 ... 
Creating msconsul_order_1
Creating msconsul_catalog_1
Creating msconsul_apache_1
Creating msconsul_apache_1 ... done
```

As you can see the Consul Docker image is downloaded now.

Check wether all containers are running:

```
[~/microservice-consul/docker]docker ps
CONTAINER ID        IMAGE                        COMMAND                  CREATED             STATUS              PORTS                                                                                              NAMES
2697aa91700b        msconsul_apache              "/bin/sh -c '/usr/..."   4 minutes ago       Up 4 minutes        0.0.0.0:8080->80/tcp                                                                               msconsul_apache_1
948f2576b0b0        msconsul_customer            "/bin/sh -c '/usr/..."   4 minutes ago       Up 4 minutes        8080/tcp                                                                                           msconsul_customer_1
0574e8dc5b11        msconsul_order               "/bin/sh -c '/usr/..."   4 minutes ago       Up 4 minutes        8080/tcp                                                                                           msconsul_order_1
144542583a05        msconsul_catalog             "/bin/sh -c '/usr/..."   4 minutes ago       Up 4 minutes        8080/tcp                                                                                           msconsul_catalog_1
c28d2a38f657        consul:0.7.2                 "docker-entrypoint..."   4 minutes ago       Up 4 minutes        8300-8302/tcp, 8400/tcp, 8600/tcp, 8301-8302/udp, 0.0.0.0:8500->8500/tcp, 0.0.0.0:8600->8600/udp   msconsul_consul_1
```
`docker ps -a`  also shows the terminated Docker containers. That is
useful to see Docker containers that crashed rigth after they started.

If one of the containers is not running, you can look at its logs using
e.g.  `docker logs msconsul_apache_1`. The name of the container is
given in the last column of the output of `docker ps`. Looking at the
logs even works after the container has been
terminated. If the log says that the container has been `killed`, you
need to increase the RAM assigned to Docker to e.g. 4GB. On Windows
and macOS you can find the RAM setting in the Docker application under
Preferences/ Advanced.
  
If you need to do more trouble shooting open a shell in the container
using e.g. `docker exec -it msconsul_catalog_1 /bin/sh` or execute
command using `docker exec msconsul_catalog_1 /bin/ls`.

You can access the microservices at http://localhost:8080/ 
 and the Consul dashboard
at http://localhost:8500 .

You can terminate all containers using `docker-compose down`.

## Run the Elastic example

There is also a configuration of the example that includes an Elastic
stack to do log analysis. To build the images, use `docker-compose
-f docker-compose-elastic.yml build`. This should result in seven
images:

```
[~/microservice-consul/docker]docker-compose -f docker-compose-elastic.yml build
....
[~/microservice-consul/docker]docker images
REPOSITORY                                      TAG                 IMAGE ID            CREATED             SIZE
msconsul_filebeat                               latest              fbf4012cc9f8        8 seconds ago       271MB
msconsul_elasticsearch                          latest              616ec15fe41c        22 seconds ago      510MB
msconsul_order                                  latest              12b279e78975        24 minutes ago      225MB
msconsul_apache                                 latest              22fac099ba93        24 minutes ago      255MB
msconsul_catalog                                latest              c23c535ecaf6        26 minutes ago      225MB
msconsul_customer                               latest              a780e4f49bac        26 minutes ago      225MB
```

You can then run the example using `docker-compose -f
docker-compose-elastic.yml up -d`. This will pull an extra Docker
image for Kibana:

```
[~/microservice-consul/docker]docker-compose -fdocker-compose-elastic.yml up -d
Creating volume "msconsul_log" with default driver
Pulling kibana (docker.elastic.co/kibana/kibana:5.5.0)...
5.5.0: Pulling from kibana/kibana
e6e5bfbc38e5: Already exists
3dae5b623dcc: Pull complete
3bfa3fa49f04: Pull complete
0d6d7b0307bc: Pull complete
00f298438720: Pull complete
dd0a3fa6a4e6: Pull complete
009015d27eea: Pull complete
33caa3f3ca53: Pull complete
6e9d15756da6: Pull complete
Digest: sha256:ad076204edd4834ab78b219414e694cc4ca1bd9956dcfe21c73deee7d727da2e
Status: Downloaded newer image for docker.elastic.co/kibana/kibana:5.5.0
msconsul_consul_1 is up-to-date
Recreating msconsul_catalog_1 ... 
Recreating msconsul_customer_1 ... 
Recreating msconsul_catalog_1
Recreating msconsul_customer_1
msconsul_apache_1 is up-to-date
Recreating msconsul_order_1 ... 
Recreating msconsul_order_1
Creating msconsul_elasticsearch_1 ... 
Creating msconsul_elasticsearch_1 ... done
Creating msconsul_kibana_1 ... 
Creating msconsul_filebeat_1 ... 
Creating msconsul_kibana_1
Recreating msconsul_catalog_1 ... done
```

Afterwards, nine containers should be running:

```
[~/microservice-consul/docker]docker ps
CONTAINER ID        IMAGE                                   COMMAND                  CREATED             STATUS              PORTS                                                                                              NAMES
b7c76ac04b96        msconsul_customer                       "/bin/sh -c '/usr/..."   14 seconds ago      Up 9 seconds        8080/tcp                                                                                           msconsul_customer_1
64de7b6b6e75        msconsul_catalog                        "/bin/sh -c '/usr/..."   14 seconds ago      Up 9 seconds        8080/tcp                                                                                           msconsul_catalog_1
e39819aa47f2        msconsul_order                          "/bin/sh -c '/usr/..."   14 seconds ago      Up 9 seconds        8080/tcp                                                                                           msconsul_order_1
a2e12faa921a        msconsul_filebeat                       "filebeat -e"            24 seconds ago      Up 22 seconds                                                                                                          msconsul_filebeat_1
2133f927417a        docker.elastic.co/kibana/kibana:5.5.0   "/bin/sh -c /usr/l..."   24 seconds ago      Up 22 seconds       0.0.0.0:5601->5601/tcp                                                                             msconsul_kibana_1
de65adc63bc4        msconsul_elasticsearch                  "/bin/bash bin/es-..."   25 seconds ago      Up 23 seconds       9200/tcp, 9300/tcp                                                                                 msconsul_elasticsearch_1
2697aa91700b        msconsul_apache                         "/bin/sh -c '/usr/..."   24 minutes ago      Up 24 minutes       0.0.0.0:8080->80/tcp                                                                               msconsul_apache_1
c28d2a38f657        consul:0.7.2                            "docker-entrypoint..."   24 minutes ago      Up 24 minutes       8300-8302/tcp, 8400/tcp, 8600/tcp, 8301-8302/udp, 0.0.0.0:8500->8500/tcp, 0.0.0.0:8600->8600/udp   msconsul_consul_1
```

See above for tips how to trouble shoot the containers if needed.

In addition to the URL mentioned above you can access Kibana at
http://localhost:5601 .

You can stop the containers using `docker-compose -f
docker-compose-elastic.yml down`.

## Run the Prometheus example

Prometheus is a tool to monitor applications. There is a separate
Docker Compose configuration for a microservices setup that includes
Prometheus.
To build the images  use `docker-compose
-f docker-compose-prometheus.yml build`. This should result in five
images:

```
[~/microservice-consul/docker]docker-compose -f docker-compose-prometheus.yml build
...
[~/microservice-consul/docker]docker images
REPOSITORY                                      TAG                 IMAGE ID            CREATED              SIZE
msconsul_prometheus                             latest              20eec31db30c        About a minute ago   74.5MB
msconsul_order                                  latest              12b279e78975        34 minutes ago       225MB
msconsul_apache                                 latest              22fac099ba93        34 minutes ago       255MB
msconsul_catalog                                latest              c23c535ecaf6        36 minutes ago       225MB
msconsul_customer                               latest              a780e4f49bac        36 minutes ago       225MB
``` 

You can run the containers then:

```
[~/microservice-consul/docker]docker-compose -f docker-compose-prometheus.yml up -d
Creating msconsul_consul_1 ... 
Creating msconsul_consul_1 ... done
Creating msconsul_catalog_1 ... 
Creating msconsul_apache_1 ... 
Creating msconsul_order_1 ... 
Creating msconsul_customer_1 ... 
Creating msconsul_apache_1
Creating msconsul_customer_1
Creating msconsul_order_1
Creating msconsul_order_1 ... done
Recreating msconsul_prometheus_1 ... 
Recreating msconsul_prometheus_1 ... done
```
Seven containers should be running then:

```
[~/microservice-consul/docker]docker ps
CONTAINER ID        IMAGE                        COMMAND                  CREATED             STATUS              PORTS                                                                                              NAMES
ef4283b672a1        msconsul_prometheus          "/bin/prometheus -..."   7 minutes ago       Up 7 minutes        0.0.0.0:9090->9090/tcp                                                                             msconsul_prometheus_1
4e4f6fcf27aa        msconsul_catalog             "/bin/sh -c '/usr/..."   7 minutes ago       Up 7 minutes        8080/tcp                                                                                           msconsul_catalog_1
a51a85f2bd95        msconsul_order               "/bin/sh -c '/usr/..."   7 minutes ago       Up 7 minutes        8080/tcp                                                                                           msconsul_order_1
de6443eb8885        msconsul_customer            "/bin/sh -c '/usr/..."   7 minutes ago       Up 7 minutes        8080/tcp                                                                                           msconsul_customer_1
f18f472e052e        msconsul_apache              "/bin/sh -c '/usr/..."   7 minutes ago       Up 7 minutes        0.0.0.0:8080->80/tcp                                                                               msconsul_apache_1
818e33f41c43        consul:0.7.2                 "docker-entrypoint..."   7 minutes ago       Up 7 minutes        8300-8302/tcp, 8400/tcp, 8600/tcp, 8301-8302/udp, 0.0.0.0:8500->8500/tcp, 0.0.0.0:8600->8600/udp   msconsul_consul_1
```

See above for tips how to trouble shoot the setup if needed.

In addition to the URL mentioned above you can access Prometheus at
http://localhost:9090 .

You can stop the containers using `docker-compose -f
docker-compose-prometheus.yml down`.
 
## Run the Zipkin example

Zipkin is a tool to trace calls across multiple microservices. To
build the images:

```
[~/microservice-consul/docker]docker-compose -f docker-compose-zipkin.yml build
...
```

The result will be five Docker images:

```
[~/microservice-consul/docker]docker images
REPOSITORY                                      TAG                 IMAGE ID            CREATED             SIZE
msconsul_order                                  latest              12b279e78975        About an hour ago   225MB
msconsul_apache                                 latest              22fac099ba93        About an hour ago   255MB
msconsul_catalog                                latest              c23c535ecaf6        About an hour ago   225MB
msconsul_customer                               latest              a780e4f49bac        About an hour ago   225MB
```

You can now start the setup. This will download the missing Docker
images:

```
[~/microservice-consul/docker]docker-compose -fdocker-compose-zipkin.yml up -d
Creating network "msconsul_default" with the default driver
Pulling zipkin (openzipkin/zipkin:1.28.1)...
1.28.1: Pulling from openzipkin/zipkin
57e29adcf254: Pull complete
a3ed95caeb02: Pull complete
d66aff79ffd6: Pull complete
aa976c4a3a76: Pull complete
Digest: sha256:c7c96285dada7f1e3a041fca914e34d2eeabc31efe9c771f324a0b5d50114091
Status: Downloaded newer image for openzipkin/zipkin:1.28.1
Creating msconsul_zipkin_1 ... 
Creating msconsul_consul_1 ... 
Creating msconsul_zipkin_1
Creating msconsul_consul_1 ... done
Creating msconsul_zipkin_1 ... done
Creating msconsul_catalog_1 ... 
Creating msconsul_order_1 ... 
Creating msconsul_customer_1 ... 
Creating msconsul_apache_1
Creating msconsul_customer_1
Creating msconsul_order_1
Creating msconsul_customer_1 ... done
```

Check to see whether all seven containers are running and see above
for some possible ways to trouble shoot the setup:

```
[~/microservice-consul/docker]docker ps
CONTAINER ID        IMAGE                        COMMAND                  CREATED             STATUS              PORTS                                                                                              NAMES
4cc5728d5e10        msconsul_catalog             "/bin/sh -c '/usr/..."   32 seconds ago      Up 28 seconds       8080/tcp                                                                                           msconsul_catalog_1
4193b99d3e04        msconsul_customer            "/bin/sh -c '/usr/..."   32 seconds ago      Up 27 seconds       8080/tcp                                                                                           msconsul_customer_1
1e8fb642a9cb        msconsul_order               "/bin/sh -c '/usr/..."   32 seconds ago      Up 28 seconds       8080/tcp                                                                                           msconsul_order_1
a131a0b394f7        msconsul_apache              "/bin/sh -c '/usr/..."   32 seconds ago      Up 30 seconds       0.0.0.0:8080->80/tcp                                                                               msconsul_apache_1
aa45e781dfe2        openzipkin/zipkin:1.28.1     "/bin/sh -c 'test ..."   35 seconds ago      Up 32 seconds       9410/tcp, 0.0.0.0:9411->9411/tcp                                                                   msconsul_zipkin_1
00b331eb1d22        consul:0.7.2                 "docker-entrypoint..."   35 seconds ago      Up 33 seconds       8300-8302/tcp, 8400/tcp, 8600/tcp, 8301-8302/udp, 0.0.0.0:8500->8500/tcp, 0.0.0.0:8600->8600/udp   msconsul_consul_1
```

In addition to the URL mentioned above you can access Prometheus at
http://localhost:9411 .

You can stop the containers using `docker-compose -f
docker-compose-zipkin.yml down`.
