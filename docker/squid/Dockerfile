FROM ubuntu:23.10

RUN apt -y update
RUN apt -y upgrade
RUN apt -y install apache2-utils squid

COPY squid.conf /etc/squid/squid.conf
COPY --chmod=755  init.sh /init.sh
RUN mkdir /usr/etc
EXPOSE 3128
CMD ["/init.sh"]
