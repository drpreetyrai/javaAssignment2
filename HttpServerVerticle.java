package com.tymoshenko.controller.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import org.springframework.context.ApplicationContext;

/**
 * The Verticle which runs HttpServer for our application.
 *
 * @author Yakiv Tymoshenko
 * @since 15.03.2016
 */
public class HttpServerVerticle extends AbstractVerticle {

    private final WhiskyCrudRestService _smartcameraCrudRestService;

    public HttpServerVerticle(final ApplicationContext applicationContext) {
        this._SmartCameraCrudRestService = (SmartCameraCrudRestService) applicationContext.getBean("smartcameraCrudRestService");
    }

    /**
     * Starts an HTTP server against which we can execute REST (HTTP) requests.
     *
     * @throws Exception
     */
    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();
        launchHttpServer(future);
    }

    private void launchHttpServer(Future<Void> fut) {
        Router httpRequestRouter = _smartcameraCrudRestService.createHttpRequestRouter(vertx);

        vertx
                .createHttpServer()
                // Register REST (HTTP) request handler
                .requestHandler(httpRequestRouter::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }
}