#!/bin/bash

cd output/secure
java -jar secure-server-monitoring-0.0.1-SNAPSHOT.jar &> secure-monitor.log &
securemonitorpid=$!

java -jar secure-server-0.0.1-SNAPSHOT.jar &> secure.log &
securepid=$!

cd ../insecure
java -jar insecure-server-0.0.1-SNAPSHOT.jar &> insecure.log &
insecurepid=$!

cd ..
java -jar api-gateway-0.0.1-SNAPSHOT.jar &> apigateway.log &
apigatewaypid=$!


stop_all() {
    kill $securemonitorpid
    kill $securepid
    kill $insecurepid
    kill $apigatewaypid
    echo "Stopped all BookShelf-Security servers. Press ENTER to finish, Goodbye!"
    
}
trap stop_all INT
echo "Started all servers!"
echo "You can check logs for each of the servers in their respective folders in output directory"
echo "Go to http://localhost:8080"
echo "Press CTRL+C to stop all servers"
read
