FROM java:8-jre
MAINTAINER David Bourasseau <dbourasseau@viotech.net>
RUN apt-get update && apt-get install -y mediainfo
RUN mkdir -p /var/www/dummy
COPY dummy-storage.jar dummy-storage.jar
CMD java -jar dummy-storage.jar --myPort $PORT --myHostName 0.0.0.0 --vanillaStorageHostName  $FRONTEND_HOST --vanillaStoragePort $FRONTEND_PORT
