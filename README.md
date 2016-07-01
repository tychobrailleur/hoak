App connecting to Flowdock, using Vert.x

# Build

```
gradle shadowJar
```

# Running

```
java -jar build/libs/hoak-vertx-3.3.0-fat.jar \
    -Dflowdock.access.token=<flowdock_secret_token> \
    -Dflowdock.organization=<flowdock_org> \
    -Dflowdock.flow=<flowdock_flow>
```
