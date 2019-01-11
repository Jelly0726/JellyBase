package org.apache.http.conn.routing;


/** @deprecated */
@Deprecated
public class BasicRouteDirector implements HttpRouteDirector {
    public BasicRouteDirector() {
        throw new RuntimeException("Stub!");
    }

    public int nextStep(RouteInfo plan, RouteInfo fact) {
        throw new RuntimeException("Stub!");
    }

    protected int firstStep(RouteInfo plan) {
        throw new RuntimeException("Stub!");
    }

    protected int directStep(RouteInfo plan, RouteInfo fact) {
        throw new RuntimeException("Stub!");
    }

    protected int proxiedStep(RouteInfo plan, RouteInfo fact) {
        throw new RuntimeException("Stub!");
    }
}
