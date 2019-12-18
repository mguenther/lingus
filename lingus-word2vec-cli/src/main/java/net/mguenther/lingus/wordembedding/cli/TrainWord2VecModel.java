package net.mguenther.lingus.wordembedding.cli;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.io.StringUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "train-word2vec-model",
        description = "Trains a word2vec model using Deeplearning4J based on a sentence-by-line textfile.",
        header = {
                "@|green  _      _____ _   _  _____ _    _  _____|@",
                "@|green | |    |_   _| \\ | |/ ____| |  | |/ ____||@",
                "@|green | |      | | |  \\| | |  __| |  | | (___  |@",
                "@|green | |      | | | . ` | | |_ | |  | |\\___ \\ |@",
                "@|green | |____ _| |_| |\\  | |__| | |__| |____) ||@",
                "@|green |______|_____|_| \\_|\\_____|\\____/|_____/ |@"
        }
)
public class TrainWord2VecModel implements Callable<Integer> {

    @CommandLine.Parameters(
            index = "0",
            description = "The training corpus for the word2vec model")
    private File inputFilename;

    @CommandLine.Option(
            names = {"-o", "--output"},
            description = "Sets the output filename")
    private File outputFilename;

    @CommandLine.Option(
            names = {"-f", "--force"},
            defaultValue = "false",
            description = "Override an already existing output file")
    private boolean overrideOutputIfExists;

    @CommandLine.Option(
            names = {"--minWordFrequency"},
            defaultValue = "5",
            description = "Sets the minimal element frequency for elements found in the training corpus. All elements below this threshold will be removed before training.")
    private Integer minWordFrequency;

    @CommandLine.Option(
            names = {"--iterations"},
            defaultValue = "1",
            description = "Sets how many iterations should be done over batched sequences.")
    private Integer iterations;

    @CommandLine.Option(
            names = {"--layerSize"},
            defaultValue = "100",
            description = "Sets the number of dimensions for outcome vectors")
    private Integer layerSize;

    @CommandLine.Option(
            names = {"--seed"},
            defaultValue = "42",
            description = "Sets the seed value for the internal random number generator")
    private Integer seed;

    @CommandLine.Option(
            names = {"--windowSize"},
            defaultValue = "5",
            description = "Sets the window size for Skip-Gram training")
    private Integer windowSize;

    @CommandLine.Option(
            names = {"-v", "--verbose"},
            defaultValue = "false",
            description = "Increases the amont of log output")
    private boolean verbose;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new TrainWord2VecModel()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {

        if (StringUtils.isEmpty(outputFilename)) {
            final String s = inputFilename.toPath().getFileName().toString();
            final String t = s.substring(0, s.lastIndexOf(".")) + ".bin";
            System.out.println("No output filename has been provided. Using '" + t + "'.");
            outputFilename = new File(t);
        }

        if (!isReadable(inputFilename)) {
            System.err.println("The source file '" + inputFilename.toString() + "' does not exist or is not readable.");
            return 1;
        }

        if (exists(outputFilename) && !overrideOutputIfExists) {
            System.err.println("Unable to write to output file '" + outputFilename.toString() + "'.");
            return 1;
        }

        int returnCode = 0;
        try {
            trainModel(inputFilename, outputFilename);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            if (verbose) {
                System.err.println(e.getStackTrace());
            }
            returnCode = 1;
        }

        return returnCode;
    }

    private void showBanner() {
        // this is required to also show the banner on normal execution
        final String[] banner = new CommandLine(new TrainWord2VecModel())
                .getCommandSpec()
                .usageMessage()
                .header();
        for (String line : banner) {
            System.out.println(CommandLine.Help.Ansi.AUTO.string(line));
        }
    }

    private boolean isReadable(final File f) {
        return f.exists() && f.canRead();
    }

    private boolean exists(final File f) {
        return f.exists();
    }

    private void trainModel(final File corpusLocation, final File trainedModelLocation) throws Exception {
        final SentenceIterator iter = new LineSentenceIterator(corpusLocation);
        iter.setPreProcessor(new SentencePreProcessor() {
            int i = 0;

            @Override
            public String preProcess(String sentence) {
                i++;
                if (i % 100_000 == 0) System.out.println("Processed '" + i + "' sentences.");
                return sentence.toLowerCase();
            }
        });

        final TokenizerFactory t = new DefaultTokenizerFactory();
        // CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
        // So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
        // Additionally it forces lower case for all tokens.
        t.setTokenPreProcessor(new CommonPreprocessor());

        System.out.println("Building model....");
        final Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(minWordFrequency)
                .iterations(iterations)
                .layerSize(layerSize)
                .seed(seed)
                .windowSize(windowSize)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        System.out.println("Fitting Word2Vec model....");
        vec.fit();

        System.out.println("Writing word vectors to text file....");

        WordVectorSerializer.writeWord2VecModel(vec, trainedModelLocation);
    }
}