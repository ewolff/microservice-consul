FROM ubuntu:15.04

# Update Ubuntu
RUN apt-get update ; apt-get dist-upgrade -y -qq 

# Install Apache + modules
RUN apt-get install -y -qq libapache2-mod-proxy-html apache2 && \
    a2enmod proxy proxy_http proxy_ajp rewrite deflate headers proxy_balancer proxy_connect proxy_html lbmethod_byrequests && \
    mkdir /var/lock/apache2 && mkdir /var/run/apache2

# Install Consul Templates
RUN apt-get install -y -qq wget unzip && \
    wget -nv https://releases.hashicorp.com/consul-template/0.18.0/consul-template_0.18.0_linux_amd64.zip && \
    unzip consul-template_0.18.0_linux_amd64.zip && \
    chmod a+x consul-template && \
    mv consul-template /usr/bin/consul-template && \
    rm consul-template_0.18.0_linux_amd64.zip

# Install supervisor
RUN apt-get install -y -qq supervisor

# Config Apache
COPY index.html /var/www/html/index.html
COPY 000-default.conf  /etc/apache2/sites-enabled/000-default.conf

# Config Consul Templates
COPY 000-default.ctmpl /etc/apache2/sites-enabled/000-default.ctmpl

# Config supervisor
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 80
CMD /usr/bin/supervisord