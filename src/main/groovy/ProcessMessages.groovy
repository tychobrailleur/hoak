import groovy.json.JsonSlurper
import io.vertx.lang.groovy.GroovyVerticle

import java.util.regex.Matcher

class ProcessMessages extends GroovyVerticle {

    def final JsonSlurper slurper = new JsonSlurper()

    @Override
    def void start() {
        def eventBus = vertx.eventBus()

        eventBus.consumer('hoak.message.process', { message ->

            def msg = message?.body()

            if (msg) {
                switch (msg) {
                    case ~/^data\:(.*)/:
                        def json = Matcher.lastMatcher[0][1]
                        def parsedObject = slurper.parseText(json)

                        if (parsedObject.event == 'message') {
                            eventBus.send('hoak.message.process.message', parsedObject)
                        }
                }
            }
        })

        eventBus.consumer('hoak.message.process.message', { message ->
            println("Message – ${message?.body()?.content}")
        })
    }
}
