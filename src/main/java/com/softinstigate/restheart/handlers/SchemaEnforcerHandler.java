/*
 * Copyright SoftInstigate srl. All Rights Reserved.
 *
 *
 * The copyright to the computer program(s) herein is the property of
 * SoftInstigate srl, Italy. The program(s) may be used and/or copied only
 * with the written permission of SoftInstigate srl or in accordance with the
 * terms and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied. This copyright notice must not be removed.
 */
package com.softinstigate.restheart.handlers;

import com.softinstigate.restheart.utils.HttpStatus;
import com.softinstigate.restheart.utils.JSONHelper;
import com.softinstigate.restheart.utils.RequestContext;
import com.softinstigate.restheart.utils.ResponseHelper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;

/**
 *
 * @author uji
 */
public class SchemaEnforcerHandler extends PipedHttpHandler
{
    /**
     * Creates a new instance of EntityResource
     *
     * @param next
     */
    public SchemaEnforcerHandler(PipedHttpHandler next)
    {
        super(next);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception
    {
        if (context.getMethod() == RequestContext.METHOD.POST || context.getMethod() == RequestContext.METHOD.PUT)
        {
            HeaderValues contentTypes = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE);

            if (contentTypes == null || contentTypes.isEmpty() || !contentTypes.contains(JSONHelper.JSON_MEDIA_TYPE) )
            {
                ResponseHelper.endExchangeWithError(exchange, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, new IllegalArgumentException("Contet-Type must be " + JSONHelper.JSON_MEDIA_TYPE));
                return;
            }
        }
        
        next.handleRequest(exchange);
    }
}