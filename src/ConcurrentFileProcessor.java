import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentFileProcessor {

    //Total number of threads used
    private final int threadsCount;

    //Ensuring thread safe operations by using Synchronized Set
    private final Set<String> uniqueWords;

    //Lock for concurrency
    private final ReentrantLock reentrantLock;

    //To save the matching words in the file
    private final StringBuilder matchStringBuilder;

    public ConcurrentFileProcessor(int threadsCount) {
        this.threadsCount = threadsCount;
        this.uniqueWords = Collections.synchronizedSet(new HashSet<>());
        this.reentrantLock = new ReentrantLock();
        this.matchStringBuilder = new StringBuilder();
    }

    /**
     * Concurrently reads a large file and populates unique words into a set.
     * Uses multiple threads to read file chunks concurrently.
     *
     * @param wordsFilePath
     * @param inputFileToProcessMatches
     */
    public void processFilesToInitiateFileMatching(final String wordsFilePath, final String inputFileToProcessMatches) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(wordsFilePath, "r")) {
            long fileSize = randomAccessFile.length();
            long chunkSize = fileSize / threadsCount;

            for (int i = 0; i < threadsCount; i++) {
                long startByte = i * chunkSize;
                long endByte = (i == threadsCount - 1) ? fileSize : startByte + chunkSize;
                //Thread starts executing the unique logic
                executor.submit(new FileChunkProcessor(wordsFilePath, startByte, endByte, uniqueWords));
            }

        } catch (IOException e) {
            System.out.println("Exception in file reading: " + e);
        }

        // Shutdown executor
        executor.shutdown();

        // Wait for all submitted tasks to finish executing
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // Proceed to match words in the second file
        matchWordsInFile(inputFileToProcessMatches);
    }


    /**
     * Matching logic triggered here
     *
     * @param inputFilePath
     */
    private void matchWordsInFile(final String inputFilePath) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(inputFilePath, "r")) {
            long fileSize = randomAccessFile.length();
            long chunkSize = fileSize / threadsCount;

            for (int i = 0; i < threadsCount; i++) {
                long startByte = i * chunkSize;
                long endByte = (i == threadsCount - 1) ? fileSize : startByte + chunkSize;
                //Thread starts executing the matching logic
                executor.submit(new FileMatchingProcessor(inputFilePath, startByte, endByte, uniqueWords, reentrantLock, matchStringBuilder));
            }

        } catch (IOException e) {
            System.out.println("Exception in file reading: " + e);
        }

        // Shutdown executor
        executor.shutdown();

        // Wait for all submitted tasks to finish executing
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        System.out.println("Matching words size " + matchStringBuilder.toString().length());
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        ConcurrentFileProcessor concurrentFileProcessor = new ConcurrentFileProcessor(10);
        concurrentFileProcessor.processFilesToInitiateFileMatching("words.txt", "bible.txt");
    }
}

