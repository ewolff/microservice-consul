# Beispiel starten

Die ist eine Schritt-für-Schritt-Anleitung zum Starten der Beispiele.
Informationen zu Maven und Docker finden sich im
[Cheatsheet-Projekt](https://github.com/ewolff/cheatsheets-DE).

## Installation

* Die Beispiele sind in Java implementiert. Daher muss Java
  installiert werden. Die Anleitung findet sich unter
  https://www.java.com/en/download/help/download_options.xml . Da die
  Beispiele kompiliert werden müssen, muss ein JDK (Java Development
  Kit) installiert werden. Das JRE (Java Runtime Environment) reicht
  nicht aus. Nach der Installation sollte sowohl `java` und `javac` in
  der Eingabeaufforderung möglich sein.

* Die Beispiele laufen in Docker Containern. Dazu ist eine
  Installation von Docker Community Edition notwendig, siehe
  https://www.docker.com/community-edition/ . Docker kann mit
  `docker` aufgerufen werden. Das sollte nach der Installation ohne
  Fehler möglich sein.

* Die Beispiele benötigen zum Teil sehr viel Speicher. Daher sollte
  Docker ca. 4 GB zur Verfügung haben. Sonst kann es vorkommen, dass
  Docker Container aus Speichermangel beendet werden. Unter Windows
  und macOS findet sich die Einstellung dafür in der Docker-Anwendung
  unter Preferences/ Advanced.

* Nach der Installation von Docker sollte `docker-compose` aufrufbar
  sein. Wenn Docker Compose nicht aufgerufen werden kann, ist es nicht
  als Teil der Docker Community Edition installiert worden. Dann ist
  eine separate Installation notwendig, siehe
  https://docs.docker.com/compose/install/ .

## Build

Wechsel in das Verzeichnis `microservice-consul-demo` und starte `./mvnw clean
package` bzw. `mvnw.cmd clean package` (Windows). Das wird einige Zeit dauern:

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
[INFO] ------------------------------------------------------- -----------------
[INFO] Total time: 01:35 min
[INFO] Finished at: 2017-09-07T18:08:13+02:00
[INFO] Final Memory: 52M/416M
[INFO] ------------------------------------------------------------------------
```

Weitere Information zu Maven gibt es im
[Maven Cheatsheet](https://github.com/ewolff/cheatsheets-DE/blob/master/MavenCheatSheet.md).

Falls es dabei zu Fehlern kommt:

* Stelle sicher, dass die Datei `settings.xml` im Verzeichnis  `.m2`
in deinem Heimatverzeichnis keine Konfiguration für ein spezielles
Maven Repository enthalten. Im Zweifelsfall kannst du die Datei
einfach löschen.

* Die Tests nutzen einige Ports auf dem Rechner. Stelle sicher, dass
  im Hintergrund keine Server laufen.

* Führe die Tests beim Build nicht aus: `./mvnw clean package
  -Dmaven.test.skip=true` bzw. `mvnw.cmd clean package
  -Dmaven.test.skip=true`.

* In einigen selten Fällen kann es vorkommen, dass die Abhängigkeiten
  nicht korrekt heruntergeladen werden. Wenn du das Verzeichnis
  `repository` im Verzeichnis `.m2` löscht, werden alle Abhängigkeiten
  erneut heruntergeladen.

## Docker Container starten

Weitere Information zu Docker gibt es im
[Docker Cheatsheet](https://github.com/ewolff/cheatsheets-DE/blob/master/DockerCheatSheet.md).

Zunächst musst du die Docker Images bauen. Wechsel in das Verzeichnis 
`docker` und starte `docker-compose build`. Das lädt die Basis-Images
herunter und installiert die Software in die Docker Images:

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

Danach sollten die Docker Images erzeugt worden sein. Sie haben das
Präfix `msconsul`:

```
[~/microservice-consul/docker]docker images
REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
msconsul_order               latest              12b279e78975        52 seconds ago      225MB
msconsul_apache              latest              22fac099ba93        55 seconds ago      255MB
msconsul_catalog             latest              c23c535ecaf6        2 minutes ago       225MB
msconsul_customer            latest              a780e4f49bac        2 minutes ago       225MB
```

Nun kannst Du die Container mit `docker-compose up -d` starten. Die
Option `-d` bedeutet, dass die Container im Hintergrund gestartet
werden und keine Ausgabe auf der Kommandozeile erzeugen.

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

Wie man sieht, werden nun auch noch Docker Images heruntergeladen.

Du kannst nun überprüfen, ob alle Docker Container laufen:

```
[~/microservice-consul/docker]docker ps
CONTAINER ID        IMAGE                        COMMAND                  CREATED             STATUS              PORTS                                                                                              NAMES
2697aa91700b        msconsul_apache              "/bin/sh -c '/usr/..."   4 minutes ago       Up 4 minutes        0.0.0.0:8080->80/tcp                                                                               msconsul_apache_1
948f2576b0b0        msconsul_customer            "/bin/sh -c '/usr/..."   4 minutes ago       Up 4 minutes        8080/tcp                                                                                           msconsul_customer_1
0574e8dc5b11        msconsul_order               "/bin/sh -c '/usr/..."   4 minutes ago       Up 4 minutes        8080/tcp                                                                                           msconsul_order_1
144542583a05        msconsul_catalog             "/bin/sh -c '/usr/..."   4 minutes ago       Up 4 minutes        8080/tcp                                                                                           msconsul_catalog_1
c28d2a38f657        consul:0.7.2                 "docker-entrypoint..."   4 minutes ago       Up 4 minutes        8300-8302/tcp, 8400/tcp, 8600/tcp, 8301-8302/udp, 0.0.0.0:8500->8500/tcp, 0.0.0.0:8600->8600/udp   msconsul_consul_1
```
`docker ps -a`  zeigt auch die terminierten Docker Container an. Das
ist nützlich, wenn ein Docker Container sich sofort nach dem Start
wieder beendet..

Wenn einer der Docker Container nicht läuft, kannst du dir die Logs
beispielsweise mit `docker logs msconsul_apache_1` anschauen. Der Name
der Container steht in der letzten Spalte der Ausgabe von `docker
ps`. Das Anzeigen der Logs funktioniert auch dann, wenn der Container
bereits beendet worden ist. Falls im Log steht, dass der Container
`killed` ist, dann hat Docker den Container wegen Speichermangel
beendet. Du solltest Docker mehr RAM zuweisen z.B. 4GB. Unter Windows
und macOS findet sich die RAM-Einstellung in der Docker application
unter Preferences/ Advanced.

Um einen Container genauer zu untersuchen, kannst du eine Shell in dem
Container starten. Beispielsweise mit `docker exec -it
msconsul_catalog_1 /bin/sh` oder du kannst in dem Container ein
Kommando mit `docker exec msconsul_catalog_1 /bin/ls` ausführen.

Du kannst auf die Microservices unter http://localhost:8080/
zugreigen und
auf das Consul Dashboard unter http://localhost:8500 .

Mit `docker-compose down` kannst Du alle Container beenden.

## Elastic-Beispiel ausführen

Es gibt ebenenfalls eine Docker-Konfiguration mit einem 
 Elastic
Stack zur Analyse der Logs. Mit `docker-compose
-f docker-compose-elastic.yml build` kannst Du die Docker Images
 bauen. Das sollte sieben Docker Images erzeugen:

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

Du kannst das Beispiel dann mit `docker-compose -f
docker-compose-elastic.yml up -d` starten. Dabei wird auch das Docker
Image für Kibana heruntergeladen::

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

Danach sollten neun Docker Container laufen::

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

Bei Problemen können die Tipps weiter oben hilfreich sein.

Zusätzlich zu den oben erwähnten URLs kannst du unter
http://localhost:5601 auf Kibana zugreifen.


Du kannst die Container mit `docker-compose -f
docker-compose-elastic.yml down` wieder herunterfahren.

## Prometheus-Beispiel ausführen

Prometheus ist ein Werkzeug zum Monitoring von Anwendungen. Für das
Setup der Microservices mit Prometheus gibt es eine eigene
Docker-Compose-Konfiguration.  Nutze `docker-compose -f
docker-compose-prometheus.yml build`, um die Images zu bauen. Das
Ergebnis sollten fünf Images sein:

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

Danach kannst du die Container starten:

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

Danach sollten sieben Container laufen:

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

Weiter oben stehen einige Tipps, um Probleme mit den Container zu
identifizieren und zu begeben.

Zusätzlich zu den oben genannten URLs steht nun unter
http://localhost:9090 Prometheus zur Verfügung.

Du kannst die Container mit `docker-compose -f
docker-compose-prometheus.yml down` wieder stopppen.
 
## Zipkin-Beispiel ausführen

Zipkin ist ein Werkzeug, um die Aufrufe zwischen mehreren Microservies
zu verfolgen. Um die Images für dieses Beispiel zu bauen, gib
folgendes ein:

```
[~/microservice-consul/docker]docker-compose -f docker-compose-zipkin.yml build
...
```

Das Ergebnis sollten fünf Images sein:

```
[~/microservice-consul/docker]docker images
REPOSITORY                                      TAG                 IMAGE ID            CREATED             SIZE
msconsul_order                                  latest              12b279e78975        About an hour ago   225MB
msconsul_apache                                 latest              22fac099ba93        About an hour ago   255MB
msconsul_catalog                                latest              c23c535ecaf6        About an hour ago   225MB
msconsul_customer                               latest              a780e4f49bac        About an hour ago   225MB
```

Du kannst die Container nun starten. Dabei werden noch fehlende Docker
Images heruntergeladen:

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

Überprüfe dann, ob alle sieben Docker Container laufen. Oben finden
sich Hinweise, wie man Probleme mit den Containern analysieren kann:

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

Zusätzlich zu den oben genannten URLs steht nun unter
http://localhost:9411 Zipkin zur Verfügung.

Mit `docker-compose -f docker-compose-zipkin.yml down` kannst Du die
Container wieder stoppen.
