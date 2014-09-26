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

import static com.softinstigate.restheart.db.CollectionDAO.checkCollectionExists;
import static com.softinstigate.restheart.db.DBDAO.checkDbExists;
import com.softinstigate.restheart.handlers.root.DeleteRootHandler;
import com.softinstigate.restheart.handlers.root.GetRootHandler;
import com.softinstigate.restheart.handlers.root.PatchRootHandler;
import com.softinstigate.restheart.handlers.root.PostRootHandler;
import com.softinstigate.restheart.handlers.root.PutRootHandler;
import com.softinstigate.restheart.handlers.collection.DeleteCollectionHandler;
import com.softinstigate.restheart.handlers.collection.GetCollectionHandler;
import com.softinstigate.restheart.handlers.collection.PatchCollectionHandler;
import com.softinstigate.restheart.handlers.collection.PostCollectionHandler;
import com.softinstigate.restheart.handlers.collection.PutCollectionHandler;
import com.softinstigate.restheart.handlers.database.DeleteDBHandler;
import com.softinstigate.restheart.handlers.database.GetDBHandler;
import com.softinstigate.restheart.handlers.database.PatchDBHandler;
import com.softinstigate.restheart.handlers.database.PostDBHandler;
import com.softinstigate.restheart.handlers.database.PutDBHandler;
import com.softinstigate.restheart.handlers.document.DeleteDocumentHandler;
import com.softinstigate.restheart.handlers.document.GetDocumentHandler;
import com.softinstigate.restheart.handlers.document.PatchDocumentHandler;
import com.softinstigate.restheart.handlers.document.PostDocumentHandler;
import com.softinstigate.restheart.handlers.document.PutDocumentHandler;
import com.softinstigate.restheart.utils.HttpStatus;
import com.softinstigate.restheart.utils.RequestContext;
import io.undertow.server.HttpServerExchange;
import static com.softinstigate.restheart.utils.RequestContext.METHOD;
import static com.softinstigate.restheart.utils.RequestContext.TYPE;
import com.softinstigate.restheart.utils.ResponseHelper;

/**
 *
 * @author uji
 */
public class RequestDispacherHandler extends PipedHttpHandler
{
    private final GetRootHandler rootGet;
    private final PostRootHandler rootPost;
    private final PutRootHandler rootPut;
    private final DeleteRootHandler rootDelete;
    private final PatchRootHandler rootPatch;
    private final GetDBHandler dbGet;
    private final PostDBHandler dbPost;
    private final PutDBHandler dbPut;
    private final DeleteDBHandler dbDelete;
    private final PatchDBHandler dbPatch;
    private final GetCollectionHandler collectionGet;
    private final PostCollectionHandler collectionPost;
    private final PutCollectionHandler collectionPut;
    private final DeleteCollectionHandler collectionDelete;
    private final PatchCollectionHandler collectionPatch;
    private final GetDocumentHandler documentGet;
    private final PostDocumentHandler documentPost;
    private final PutDocumentHandler documentPut;
    private final DeleteDocumentHandler documentDelete;
    private final PatchDocumentHandler documentPatch;

    /**
     * Creates a new instance of EntityResource
     *
     * @param rootGet
     * @param rootPost
     * @param rootPut
     * @param rootDelete
     * @param rootPatch
     * @param dbGet
     * @param dbPost
     * @param dbPut
     * @param dbDelete
     * @param dbPatch
     * @param collectionGet
     * @param collectionPost
     * @param collectionPut
     * @param collectionDelete
     * @param collectionPatch
     * @param documentGet
     * @param documentPost
     * @param documentPut
     * @param documentDelete
     * @param documentPatch
     */
    public RequestDispacherHandler(
            GetRootHandler rootGet,
            PostRootHandler rootPost,
            PutRootHandler rootPut,
            DeleteRootHandler rootDelete,
            PatchRootHandler rootPatch,
            GetDBHandler dbGet,
            PostDBHandler dbPost,
            PutDBHandler dbPut,
            DeleteDBHandler dbDelete,
            PatchDBHandler dbPatch,
            GetCollectionHandler collectionGet,
            PostCollectionHandler collectionPost,
            PutCollectionHandler collectionPut,
            DeleteCollectionHandler collectionDelete,
            PatchCollectionHandler collectionPatch,
            GetDocumentHandler documentGet,
            PostDocumentHandler documentPost,
            PutDocumentHandler documentPut,
            DeleteDocumentHandler documentDelete,
            PatchDocumentHandler documentPatch
    )
    {
        super(null);
        this.rootGet = rootGet;
        this.rootPost = rootPost;
        this.rootPut = rootPut;
        this.rootDelete = rootDelete;
        this.rootPatch = rootPatch;
        this.dbGet = dbGet;
        this.dbPost = dbPost;
        this.dbPut = dbPut;
        this.dbDelete = dbDelete;
        this.dbPatch = dbPatch;
        this.collectionGet = collectionGet;
        this.collectionPost = collectionPost;
        this.collectionPut = collectionPut;
        this.collectionDelete = collectionDelete;
        this.collectionPatch = collectionPatch;
        this.documentGet = documentGet;
        this.documentPost = documentPost;
        this.documentPut = documentPut;
        this.documentDelete = documentDelete;
        this.documentPatch = documentPatch;

    }

    @Override
    public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception
    {
        if (context.getType() == TYPE.ERROR)
        {
            ResponseHelper.endExchange(exchange, HttpStatus.SC_NOT_FOUND);
            return;
        }
        
        if (context.getMethod() == METHOD.OTHER)
        {
            ResponseHelper.endExchange(exchange, HttpStatus.SC_NOT_IMPLEMENTED);
            return;
        }
        
        if (context.isReservedResource())
        {
            ResponseHelper.endExchange(exchange, HttpStatus.SC_NOT_FOUND);
            return;
        }
        
        // TODO: use AllowedMethodsHandler to limit methods
        
        switch (context.getMethod())
        {
            case GET:
                switch (context.getType())
                {
                    case ROOT:
                        rootGet.handleRequest(exchange, context);
                        return;
                    case DB:
                        if (checkDbExists(exchange, context.getDBName()))
                            dbGet.handleRequest(exchange, context);
                        return;
                    case COLLECTION:
                        if (checkDbExists(exchange, context.getDBName()) && checkCollectionExists(exchange, context.getDBName(), context.getCollectionName()))
                            collectionGet.handleRequest(exchange, context);
                        return;
                    case DOCUMENT:
                        documentGet.handleRequest(exchange, context);
                        return;
                }
            
            case POST:
                switch (context.getType())
                {
                    case ROOT:
                        rootPost.handleRequest(exchange, context);
                        return;
                    case DB:
                        if (checkDbExists(exchange, context.getDBName()))
                            dbPost.handleRequest(exchange, context);
                        return;
                    case COLLECTION:
                        if (checkDbExists(exchange, context.getDBName()) && checkCollectionExists(exchange, context.getDBName(), context.getCollectionName()))
                            collectionPost.handleRequest(exchange, context);
                        return;
                    case DOCUMENT:
                        documentPost.handleRequest(exchange, context);
                        return;
                }
            
            case PUT:
                switch (context.getType())
                {
                    case ROOT:
                        rootPut.handleRequest(exchange, context);
                        return;
                    case DB:
                            dbPut.handleRequest(exchange, context);
                        return;
                    case COLLECTION:
                        if (checkDbExists(exchange, context.getDBName()))
                            collectionPut.handleRequest(exchange, context);
                        return;
                    case DOCUMENT:
                        if (checkDbExists(exchange, context.getDBName()) && checkCollectionExists(exchange, context.getDBName(), context.getCollectionName()))
                            documentPut.handleRequest(exchange, context);
                        return;
                }
            
            case DELETE:
                switch (context.getType())
                {
                    case ROOT:
                        rootDelete.handleRequest(exchange, context);
                        return;
                    case DB:
                        if (checkDbExists(exchange, context.getDBName()))
                            dbDelete.handleRequest(exchange, context);
                        return;
                    case COLLECTION:
                        if (checkDbExists(exchange, context.getDBName()) && checkCollectionExists(exchange, context.getDBName(), context.getCollectionName()))
                            collectionDelete.handleRequest(exchange, context);
                        return;
                    case DOCUMENT:
                        if (checkDbExists(exchange, context.getDBName()) && checkCollectionExists(exchange, context.getDBName(), context.getCollectionName()))
                            documentDelete.handleRequest(exchange, context);
                        return;
                }
            
            case PATCH:
                switch (context.getType())
                {
                    case ROOT:
                        rootPatch.handleRequest(exchange, context);
                        return;
                    case DB:
                        if (checkDbExists(exchange, context.getDBName()))
                            dbPatch.handleRequest(exchange, context);
                        return;
                    case COLLECTION:
                        if (checkDbExists(exchange, context.getDBName()) && checkCollectionExists(exchange, context.getDBName(), context.getCollectionName()))
                            collectionPatch.handleRequest(exchange, context);
                        return;
                    case DOCUMENT:
                        documentPatch.handleRequest(exchange, context);
                }
        }
    }
    
    
}