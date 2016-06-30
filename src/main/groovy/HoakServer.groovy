import io.vertx.lang.groovy.GroovyVerticle

class HoakServer extends GroovyVerticle {

    @Override
    def void start() {

        def eventBus = vertx.eventBus()
//        vertx.deployVerticle('chat_consumer.rb')

        vertx.deployVerticle('SseConsumer.groovy', [ worker: true ])
        vertx.createHttpServer().requestHandler({ req ->

            eventBus.send('news.uk.sport', 'Hello!')

            req.response()
                    .putHeader("content-type", "text/html")
                    .end("<html><head><title>Hey</title></head><body><h1>Hello from vert.x!</h1></body></html>")
        }).listen(8080)
    }

    @Override
    def void stop() {
        println("Done.")
    }

}
