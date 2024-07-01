import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class FileMatchingProcessor implements Runnable {
    private String filePath;
    private long startByte;
    private long endByte;
    private Set<String> uniqueWords;
    private ReentrantLock reentrantLock;
    private StringBuilder matchStringBuilder;

    //Constructor initialization
    public FileMatchingProcessor(final String filePath, final long startByte, final long endByte, final Set<String> uniqueWords, final ReentrantLock reentrantLock, final StringBuilder matchStringBuilder) {
        this.filePath = filePath;
        this.startByte = startByte;
        this.endByte = endByte;
        this.uniqueWords = uniqueWords;
        this.reentrantLock = reentrantLock;
        this.matchStringBuilder = matchStringBuilder;
    }

    //Run method which reads the file from startByte to endByte and finds the matches
    @Override
    public void run() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
            // Seek to the start byte
            randomAccessFile.seek(startByte);

            // Read and process lines until end byte is reached by the thread
            long currentByte = startByte;
            while (currentByte < endByte) {
                String line = randomAccessFile.readLine();
                if (line != null) {
                    processLine(line.trim());//Process line
                    currentByte = randomAccessFile.getFilePointer(); // Update current byte position
                } else {
                    //Breaking if we reach the end of the file
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error encountered while processing the file: " + e);
        }
    }

    /**
     * Processes lines to match the words from the uniqueSet
     *
     * @param line
     */
    private void processLine(String line) {
        if (line.isEmpty()) {
            return;
        }

        // Split the line into words using whitespace as delimiter
        String[] words = line.split("\\s+");

        for (String word : words) {
            //Changing to lower case for my simplicity
            if (uniqueWords.contains(line.toLowerCase())) {
                //Obtaining lock to update matchStringBuilder
                reentrantLock.lock();
                try {
                    matchStringBuilder.append("Match found for word: ").append(word).append("\n");
                } finally {
                    //Releasing lock
                    reentrantLock.unlock();
                }
            }
        }
    }
}