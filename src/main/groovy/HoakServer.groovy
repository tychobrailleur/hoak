import groovy.json.JsonSlurper
import io.vertx.lang.groovy.GroovyVerticle

class HoakServer extends GroovyVerticle {

    def logger = io.vertx.core.logging.LoggerFactory.getLogger(HoakServer)
    def final jsonSlurper = new JsonSlurper()

    @Override
    def void start() {
        def conf = jsonSlurper.parse(JsonSlurper.getResource('/conf.json'))

        def token = System.getProperty('flowdock.access.token')
        def organization = System.getProperty('flowdock.organization', conf.flowdock.organization)
        def flow = System.getProperty('flowdock.flow', conf.flowdock.flow)

        if (!token) {
            throw new Exception('A token is required')
        }

        logger.info("Start Hoak for ${organization}/${flow}")

        def endpoint = "https://${token}:@stream.flowdock.com/flows/${organization}/${flow}"

        vertx.deployVerticle('ProcessMessages.groovy')
        vertx.deployVerticle('SseConsumer.groovy', [worker: true, config: [
                endpoint: endpoint
        ]])


        vertx.createHttpServer().requestHandler({ req ->
            req.response()
                    .putHeader("content-type", "text/html")
                    .end("<html><head><title>Hey</title></head><body><h1>Hello from vert.x!</h1></body></html>")
        }).listen(8085)
    }

    @Override
    def void stop() {
        logger.info("Done.")
    }

}
