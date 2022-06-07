#!/bin/bash

cd security-ui
npm install

node --max_old_space_size=8192 ./node_modules/@angular/cli/bin/ng build --prod

cp -r dist/security-ui/* ../api-gateway/src/main/resources/public

cd ../api-gateway
./gradlew build

cd ../insecure-server
./gradlew build

cd ../secure-server
./gradlew build

cd ../secure-server-monitoring
./gradlew build

mkdir ../output
cd ..
cp ./api-gateway/build/libs/* output
mkdir output/insecure
cp ./insecure-server/build/libs/* output/insecure
mkdir output/secure
cp ./secure-server/build/libs/* output/secure
cp ./secure-server-monitoring/build/libs/* output/secure
echo "build script completed."

