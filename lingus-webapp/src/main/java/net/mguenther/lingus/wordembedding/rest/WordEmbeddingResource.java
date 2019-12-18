package net.mguenther.lingus.wordembedding.rest;

import net.mguenther.lingus.wordembedding.api.AvailableModelsResult;
import net.mguenther.lingus.wordembedding.api.FindSimilarWordsRequest;
import net.mguenther.lingus.wordembedding.api.FindSimilarWordsResult;
import net.mguenther.lingus.wordembedding.api.ActivateModelRequest;
import net.mguenther.lingus.wordembedding.api.ActivateModelResult;
import net.mguenther.lingus.wordembedding.api.MeasureSimilarityRequest;
import net.mguenther.lingus.wordembedding.api.MeasureSimilarityResult;
import net.mguenther.lingus.wordembedding.service.WordEmbeddingService;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@ApplicationScoped
@Path("/wordembedding")
@OpenAPIDefinition(
        info = @Info(
                title = "Word Embedding API",
                version = "0.1.0",
                description = "This API provides an interface for word embeddings, e.g. finding similar words or measuring the similarity of pairs of words.",
                license = @License(name = "LGPL", url = "http://www.gnu.org/licenses/lgpl-3.0.html"),
                contact = @Contact(url = "https://www.mguenther.net", name = "Markus Günther")
        )
)
public class WordEmbeddingResource {

    private WordEmbeddingService service;

    @Inject
    public WordEmbeddingResource(final WordEmbeddingService service) {
        this.service = service;
    }

    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Finds words that are similar to the given words.")
    @APIResponse(
            description = "A result containing the set of original words, each enriched by a list of words that are similar",
            content = @Content(mediaType = "application/json"),
            responseCode = "200"
    )
    @Schema(implementation = FindSimilarWordsResult.class)
    public FindSimilarWordsResult findSimilarities(@Parameter(description = "One or multiple words for which the client seeks to retrieve similar words.") @QueryParam("term") Set<String> terms,
                                                   @Parameter(description = "The maximum number of suggested similar words per input word.") @QueryParam("maxSuggestions") int maxSuggestions) {
        final FindSimilarWordsRequest request = FindSimilarWordsRequest
                .create()
                .forTerm(terms)
                .limit(maxSuggestions)
                .build();
        return service.similar(request);
    }

    @GET
    @Path("/similarity")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Measures the similarity between a pair of words.")
    @APIResponses({
            @APIResponse(
                    description = "A result containing the pair of original words, enriched with a similarity measurement",
                    content = @Content(mediaType = "application/json"),
                    responseCode = "200"
            ),
            @APIResponse(
                    description = "The current model does not recognize at least one of the given words",
                    content = @Content(mediaType = "application/json"),
                    responseCode = "500"
            )}
    )
    @Schema(implementation = MeasureSimilarityResult.class)
    public MeasureSimilarityResult measureSimilarity(@Parameter(description = "The LHS of the similarity comparison.") @QueryParam("term1") String term1,
                                                     @Parameter(description = "The RHS of the similarity comparison.") @QueryParam("term2") String term2) {
        final MeasureSimilarityRequest request = new MeasureSimilarityRequest(term1, term2);
        return service.measureSimilarity(request);
    }

    @GET
    @Path("/admin/models")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Shows all available Word2Vec models.")
    @APIResponse(
            description = "A list of all available models that Lingus is able to load. Shows also which of these model is currently active (if any).",
            content = @Content(mediaType = "application/json"),
            responseCode = "200"
    )
    @Schema(implementation = AvailableModelsResult.class)
    public AvailableModelsResult availableModels() {
        return service.availableModels();
    }

    @PUT
    @Path("/admin/models/{filename}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Activates the model identified by its filename. Deactivates the current model, if any.")
    @APIResponse(
            description = "Shows which model has been activated and which one has been deactivated.",
            content = @Content(mediaType = "application/json"),
            responseCode = "200"
    )
    @Schema(implementation = ActivateModelResult.class)
    public ActivateModelResult loadModel(@PathParam("filename") String filename) {
        return service.activate(new ActivateModelRequest(filename));
    }
}
