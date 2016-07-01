import io.vertx.lang.groovy.GroovyVerticle
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.message.BasicHeader
import org.apache.http.nio.client.methods.HttpAsyncMethods
import rx.apache.http.ObservableHttp
import rx.apache.http.ObservableHttpResponse


class SseConsumer extends GroovyVerticle {

    def logger = io.vertx.core.logging.LoggerFactory.getLogger(SseConsumer)

    @Override
    def void start() {

        def eventBus = vertx.eventBus()

        final String endpoint = vertx.getOrCreateContext().config()['endpoint']
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(500).build()
        final CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultHeaders([new BasicHeader('Accept', 'text/event-stream')])
                .setMaxConnPerRoute(20)
                .setMaxConnTotal(50)
                .build()

        httpClient.start()

        ObservableHttp.createRequest(HttpAsyncMethods.createGet(endpoint), httpClient)
                .toObservable()
                .flatMap({ ObservableHttpResponse response ->
            return response.getContent().map({ byte[] bb ->
                return new String(bb)
            })
        }).forEach({ String resp ->
            logger.debug("Received message: ${resp}")
            eventBus.send('hoak.message.process', resp)
        })

    }
}
