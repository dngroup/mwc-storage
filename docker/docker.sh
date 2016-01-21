#/bin/bash
#docker run -it -e PORT=8078 -e FRONTEND_HOST=172.17.42.1 -e FRONTEND_PORT=8080  dngroup/mwc-storage bash
cd ../
mvn clean package
cd docker/
docker build -t dngroup/mwc-storage .
docker push dngroup/mwc-storage 
