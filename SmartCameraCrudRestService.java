
package com.tymoshenko.controller.verticle;

import com.tymoshenko.controller.repository.CrudService;
import com.tymoshenko.model.SmartCamera;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.shiro.ShiroAuth;
import io.vertx.ext.auth.shiro.ShiroAuthRealmType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Creates a Router object which routes HTTP requests to first matching URL.
 * REST methods for CRUD API are mapped to URLs inside the only public method of this class: createHttpRequestRouter(Vertx vertx).
 *
 * @author Yakiv Tymoshenko
 * @since 15.03.2016
 */
@Service
public class WhiskyCrudRestService {

    // REST endpoint URLs
    public static final String REST_SMARTCAMERA_URL = "/rest/SmartCamera";
    public static final String REST_SMARTCAMERA_URL_WITH_ID = REST_SMARTCAMERA_URL + "/:id";

    // HTTP req/res constants
    public static final String CONTENT_TYPE = "content-type";
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
    public static final String TEXT_HTML = "text/html";

    // HTTP status codes
    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_OK_CREATED = 201;
    public static final int STATUS_CODE_OK_NO_CONTENT = 204;
    public static final int STATUS_CODE_BAD_REQUEST = 400;
    public static final int STATUS_CODE_NOT_FOUND = 404;

    private static final Logger LOG = LoggerFactory.getLogger(SmartCameraCrudRestService.class);

    @Autowired
    private CrudService<SmartCamera> smartcameraCrudService;

    /**
     * Creates a Router which routs REST (HTTP) requests to the first matching URL.
     *
     * @param vertx HttpServer Vertex
     * @return a REST request router.
     * The router receives request from an HttpServer and routes it to the first matching Route that it contains.
     * A router can contain many routes.
     * Routers are also used for routing failures.
     */
    public Router createHttpRequestRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        // We need cookies, sessions and request bodies
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        // Bind "/" to our hello message.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            final String _helloMessage = "<h1>Hello from my first Vert.x 3 application</h1>";
            response
                    .putHeader(CONTENT_TYPE, TEXT_HTML)
                    .end(_helloMessage);
        });

        // Simple auth service which uses a properties file for user/role info
        AuthProvider authProvider = ShiroAuth.create(vertx, ShiroAuthRealmType.PROPERTIES, new JsonObject());
        // protect the API
        router.route(REST_SMARTCAMERA_URL + "/*").handler(BasicAuthHandler.create(authProvider).addAuthority("role:admin"));
        // We need a user session handler too to make sure the user is stored in the session between requests
        router.route().handler(UserSessionHandler.create(authProvider));


        // Register REST methods for CRUD operations
        // Create
        router.post(REST_SMARTCAMERA_URL).handler(this::addOne);
        // Read one
        router.get(REST_SMARTCAMERA_URL_WITH_ID).handler(this::getOne);
        // Read all
        router.get(REST_SMARTCAMERA_URL).handler(this::getAll);
        // Update
        router.put(REST_SMARTCAMERA_URL_WITH_ID).handler(this::updateOne);
        // Delete
        router.delete(REST_SMARTCAMERA_URL_WITH_ID).handler(this::deleteOne);

        return router;
    }

    /**
     * Create an Whisky entity.
     *
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web.
     */
    private void addOne(RoutingContext routingContext) {
        final Whisky whisky;
        try {
            smartcamera = Json.decodeValue(routingContext.getBodyAsString(), SmartCamera.class);

            smartcameraCrudService.save(whisky);

            routingContext.response()
                    .setStatusCode(STATUS_CODE_OK_CREATED)
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
                    .end(Json.encodePrettily(whisky));
        } catch (DecodeException e) {
            LOG.error(e.getLocalizedMessage());
            routingContext.response()
                    .setChunked(true)
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
                    .write("Malformed SmartCamera object")
                    .setStatusCode(STATUS_CODE_BAD_REQUEST)
                    .end();
        }
    }

    /**
     * Get a Whisky by ID.
     * Should provide an ID in the request URL.
     *
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web.
     */
    private void getOne(RoutingContext routingContext) {
        Long id = getSmartCameraId(routingContext);
        if (id != null) {
            SmartCamera smartcamera = smartcameraCrudService.readOne(id);
            if (smartcamera == null) {
                routingContext.response().setStatusCode(STATUS_CODE_NOT_FOUND).end("SmartcCamera not found for id=" + id);
            } else {
                routingContext.response()
                        .putHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
                        .end(Json.encodePrettily(whisky));
            }
        }
    }

    /**
     * Get all Whisky instances from the DB.
     *
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web.
     */
    private void getAll(RoutingContext routingContext) {
        List<SmartCamera> smartCameraList = smartCameraCrudService.readAll();
        routingContext.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
                .end(Json.encodePrettily(whiskyList));
    }

    /**
     * Update a Whisky instance.
     * Should provide an ID in the request URL and
     * new values for Whisky.name and Whisky.origin in the request body (json).
     *
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web.
     */
    private void updateOne(RoutingContext routingContext) {
        Long id = getSmartCameraId(routingContext);
        if (id == null) {
            return;
        }
        try {
            JsonObject json = routingContext.getBodyAsJson();
            if (json == null) {
                routingContext.response().setStatusCode(STATUS_CODE_BAD_REQUEST).end("Malformed Whisky object.");
            } else {
                SmartCamera smartcamera = smartcameraCrudService.readOne(id);
                if (smartcamera == null) {
                    routingContext.response().setStatusCode(STATUS_CODE_NOT_FOUND).end();
                } else {
                    smartcamera.setName(json.getString("name"));
                    smartcamera.setOrigin(json.getString("origin"));

                    smartcamera = smartcameraCrudService.save(smartcamera);

                    routingContext.response()
                            .putHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
                            .end(Json.encodePrettily(smartcamera));
                }
            }
        } catch (DecodeException e) {
            routingContext.response().setStatusCode(STATUS_CODE_BAD_REQUEST).end("Malformed Smart Camera object.");
        }
    }

    /**
     * Deletes a Whisky instance by ID.
     * Should provide an ID in the request URL.
     *
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web.
     */
    private void deleteOne(RoutingContext routingContext) {
        Long id = getSmartCameraId(routingContext);
        if (id != null) {
            try {
                smartcameraCrudService.delete(id);
            } catch (EmptyResultDataAccessException e) {
                // Trying to delete an entity which not exists
                routingContext.response().setStatusCode(STATUS_CODE_NOT_FOUND).end("Can not delete Whisky because it does not exist. ID=" + id);
                return;
            }
            routingContext.response().setStatusCode(STATUS_CODE_OK_NO_CONTENT).end("Deleted.");
        }
    }

    private Long getSmartCameraId(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        Long idAsLong = null;
        try {
            idAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            LOG.error(e.getLocalizedMessage());

            routingContext.response()
                    .setChunked(true)
                    .putHeader(CONTENT_TYPE, TEXT_HTML)
                    .write(String.format("Bad ID. ID=\"%s\"", id))
                    .setStatusCode(STATUS_CODE_BAD_REQUEST)
                    .end();
        }
        return idAsLong;
    }
}

