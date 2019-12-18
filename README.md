# Lingus

Lingus is a toolset for testing out AI-enabled NLP concepts. It currently supports word embeddings using the word2vec model implementation from DeepLearning4J.

This is first and foremost my personal playground, as I'm currently exploring the possibilities for AI-enabled NLP wrt. their application in enterprise search. I published this repository in case other people search for an easy entrance into this field and find the tools provided useful for their own experiments. But please note: While Lingus can surely be used for your personal experiments, the integration of Lingus into professional and / or commercial software is prohibited by its license (cf. the license information at the end of this document).

## Getting started

Lingus is a Quarkus application that expects a data directory with word2vec models in it. Using Maven, the solution can easily be compiled from its Maven module `lingus-webapp` and started locally:

```bash
$> mvn compile quarkus:dev -Dlingus.data.dir=/var/data/lingus
```

## Word embeddings

The word embedding API has both query and administration endpoints. After successfully starting up Lingus, go to [its Swagger UI](http://localhost:8080/swagger-ui) to see the different endpoints and interact with them. The following examples uses `curl` to interact with Lingus from the CLI.

Upon startup, Lingus treats all files at `lingus.data.dir` as binary word2vec models and lists them along with their active-inactive status when querying the `/wordembeddings/admin/models` endpoint. A model has to explicitly be activated before using it.
 
Suppose the query

```bash
$> curl -X GET http://localhost:8080/wordembedding/admin/models
```

yields the following result:

```json
{
  "availableModels": [
    {
      "active": false,
      "filename": "wikipedia-small.bin"
    },
    {
      "active": false,
      "filename": "wikipedia-large.bin"
    }
  ]
}
``` 

This shows that Lingus detected to files at `lingus.data.dir`. We're going to load a word2vec model from `wikipedia-small.bin` by issuing

```bash
$> curl -X PUT http://localhost:8080/wordembedding/admin/models/wikipedia-small.bin
```

Loading a model naturally depends heavily on its filesize. This process can easily take up to several minutes. The endpoint responds synchronously (as this suffices for my use cases), so don't be surprised about long response times. The response shows something like this:

```json
{
  "activated": "wikipedia-small.bin",
  "deactivated": "<<undefined>>"
}
```

The response tells us which model it loaded and which model - if any - got deactivated.

Now we are ready to query the model. For this example, we are interested for terms that the model deems similar for the input term "school".

```bash
$> curl http://localhost:8080/wordembeddings/query?term=school&maxSuggestions=5
```
```json
{
  "terms": [
    {
      "similar": [
        {
          "similarity": 0.9001061320304871,
          "word": "university"
        },
        {
          "similarity": 0.6805482506752014,
          "word": "academy"
        },
        {
          "similarity": 0.5688245296478271,
          "word": "sciences"
        },
        {
          "similarity": 0.6678423881530762,
          "word": "arts"
        }
      ],
      "term": "school"
    }
  ]
}
```

The response yields four similar terms for "school" (just four because the example is based on the German Wikipedia, so the model primarily learns contextual information for German words). Don't let the similarity metric fool you: The list is ordered by the closeness wrt. the word vector model of the underlying word2vec implementation. The similarity measure is another metric that the word2vec model is able to infer for pairs of words (e.g. the pair "school" and "university").

## Training a word2vec model

The Lingus toolset also comes with CLI applications to build models. Currently, there is a single CLI application in Maven module `lingus-word2vec-cli` which enables you to build your one models based on a given corpus. The corpus needs to be a plaintext file where each sentence is separated by a newline.

To give you a glimpse on the possible parameters: If you start the CLI tool `TrainWord2VecModel` without the required parameters, it prints the following help text.

```bash
 _      _____ _   _  _____ _    _  _____
| |    |_   _| \ | |/ ____| |  | |/ ____|
| |      | | |  \| | |  __| |  | | (___
| |      | | | . ` | | |_ | |  | |\___ \
| |____ _| |_| |\  | |__| | |__| |____) |
|______|_____|_| \_|\_____|\____/|_____/
Usage: train-word2vec-model [-fv] [--iterations=<iterations>]
                            [--layerSize=<layerSize>]
                            [--minWordFrequency=<minWordFrequency>]
                            [-o=<outputFilename>] [--seed=<seed>]
                            [--windowSize=<windowSize>] <inputFilename>
Trains a word2vec model using Deeplearning4J based on a sentence-by-line
textfile.
      <inputFilename>   The training corpus for the word2vec model
  -f, --force           Override an already existing output file
      --iterations=<iterations>
                        Sets how many iterations should be done over batched
                          sequences.
      --layerSize=<layerSize>
                        Sets the number of dimensions for outcome vectors
      --minWordFrequency=<minWordFrequency>
                        Sets the minimal element frequency for elements found
                          in the training corpus. All elements below this
                          threshold will be removed before training.
  -o, --output=<outputFilename>
                        Sets the output filename
      --seed=<seed>     Sets the seed value for the internal random number
                          generator
  -v, --verbose         Increases the amont of log output
      --windowSize=<windowSize>
                        Sets the window size for Skip-Gram training
```

## Native builds

Both the Lingus web application and the Lingus CLI tools can be compiled via GraalVM to native binaries. 

There are a dozen tutorials on the web on how to do this, so won't go into the details here. If you want to create an executable for the Lingus web application, you'll have to check the Quarkus documentation on how to do that. If you want to create an executable for the CLI tools, you'll have to check the Picocli documentation on how to do that.

## License

This work is released under the terms of the [LGPL v3](http://www.gnu.org/licenses/lgpl-3.0.html).