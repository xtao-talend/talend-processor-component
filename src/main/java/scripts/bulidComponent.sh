#! /bin/bash

(cd /home/xtao/Projects/talend-uppercase-processor; mvn clean; mvn install; mvn talend-component:car)

rm /home/xtao/exchange/uppercase-processor-0.0.1-SNAPSHOT.car

cp /home/xtao/Projects/talend-uppercase-processor/target/uppercase-processor-0.0.1-SNAPSHOT.car /home/xtao/exchange/

docker run \
    -v /home/xtao/dev/remoteEngines/pipeline-remote-engine/:/home/xtao/dev/remoteEngines/pipeline-remote-engine/ \
    -v /home/xtao/exchange/:/home/xtao/exchange/ \
    -v /var/run/docker.sock:/var/run/docker.sock \
    tacokit/remote-engine-customizer:1.1.16_20191119111726 \
    register-component-archive \
    --remote-engine-dir=/home/xtao/dev/remoteEngines/pipeline-remote-engine/ \
    --component-archive=/home/xtao/exchange/uppercase-processor-0.0.1-SNAPSHOT.car

(cd /home/xtao/dev/remoteEngines/pipeline-remote-engine; ./launchLocalRE.sh restart)