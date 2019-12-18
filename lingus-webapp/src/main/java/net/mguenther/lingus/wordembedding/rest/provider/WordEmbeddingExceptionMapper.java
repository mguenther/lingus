package net.mguenther.lingus.wordembedding.rest.provider;

import net.mguenther.lingus.common.api.Error;
import net.mguenther.lingus.wordembedding.service.NoModelLoadedException;
import net.mguenther.lingus.wordembedding.service.UnableToActivateModelException;
import net.mguenther.lingus.wordembedding.service.UnavailableModelException;
import net.mguenther.lingus.wordembedding.service.UnknownTermException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
@ApplicationScoped
public class WordEmbeddingExceptionMapper implements ExceptionMapper<Exception> {

    private static final Map<Class<? extends Exception>, Integer> EXCEPTION_TO_STATUS_CODE;

    static {
        EXCEPTION_TO_STATUS_CODE = new HashMap<>();
        EXCEPTION_TO_STATUS_CODE.put(UnavailableModelException.class, 404);
        EXCEPTION_TO_STATUS_CODE.put(UnknownTermException.class, 404);
        EXCEPTION_TO_STATUS_CODE.put(NoModelLoadedException.class, 409);
        EXCEPTION_TO_STATUS_CODE.put(UnableToActivateModelException.class, 500);
    }

    @Override
    public Response toResponse(final Exception e) {
        final int httpStatus = EXCEPTION_TO_STATUS_CODE.getOrDefault(e.getClass(), 500);
        final Error error = new Error(e.getMessage(), httpStatus);
        return Response.status(httpStatus).entity(error).build();
    }
}
