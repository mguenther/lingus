package net.mguenther.lingus.wordembedding.rest;

import io.quarkus.test.junit.QuarkusTest;
import net.mguenther.lingus.wordembedding.api.FindSimilarWordsResult;
import net.mguenther.lingus.wordembedding.api.SimilarWord;
import net.mguenther.lingus.wordembedding.api.Term;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@QuarkusTest
class WordEmbeddingResourceTest {

    @Test
    void queryEndpointShouldReturnSuggestionsForRecognizedWords() {
        final FindSimilarWordsResult result = given()
                .when().get("/wordembedding/query?term=das")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response().as(FindSimilarWordsResult.class);
        assertThat(result.getTerms()).hasSize(1);
        for (Term term : result.getTerms()) {
            assertThat(term.getSimilar()).hasSize(5);
            assertThat(term.getTerm()).isNotEmpty();
            for (SimilarWord similarWord : term.getSimilar()) {
                assertThat(similarWord.getSimilarity()).isGreaterThan(0.0);
                assertThat(similarWord.getWord()).isNotNull();
                assertThat(similarWord.getWord()).isNotEqualTo("das");
            }
        }
    }

    @Test
    void queryEndpointShouldReturnAnEmptySimilarWordsListIfTheInputTermIsNotRecognizedByTheModel() {
        final FindSimilarWordsResult result = given()
                .when().get("/wordembedding/query?term=schule")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response().as(FindSimilarWordsResult.class);
        assertThat(result.getTerms()).hasSize(1);
        for (Term term : result.getTerms()) {
            assertThat(term.getSimilar()).isEmpty();
            assertThat(term.getTerm()).isNotEmpty();
        }
    }
}
