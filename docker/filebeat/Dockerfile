FROM docker.elastic.co/beats/filebeat:5.5.0
ADD filebeat.yml /usr/share/filebeat/filebeat.yml
USER root
RUN chown filebeat /usr/share/filebeat/filebeat.yml
USER filebeat
