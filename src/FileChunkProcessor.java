import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

public class FileChunkProcessor implements Runnable {
    private String filePath;
    private long startByte;
    private long endByte;
    private Set<String> concurrentSet;


    //Constructor
    public FileChunkProcessor(String filePath, long startByte, long endByte, Set<String> concurrentSet) {
        this.startByte = startByte;
        this.endByte = endByte;
        this.concurrentSet = concurrentSet;
        this.filePath = filePath;
    }

    //Run method which reads the file from startByte to endByte and build the uniqueWords Set
    @Override
    public void run() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
            // Seek to the start byte
            randomAccessFile.seek(startByte);

            // Read and process lines until end byte
            long currentByte = startByte;
            while (currentByte < endByte) {
                String line = randomAccessFile.readLine();
                if (line != null && !line.isBlank()) {
                    concurrentSet.add(line); // Add word to concurrent set
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


}