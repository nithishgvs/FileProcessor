# FileProcessor

## Task Description
Write a program that reads a file and finds matches against a predefined set of words. The set of predefined words can contain up to 10,000 entries.

## Requirements
1) Input file: Plain text (ASCII) file where each record is separated by a new line.
2) Assumption: Only English words are considered for this exercise.
3) File size: Up to 20 MB.
4) Predefined words: Defined in a text file where each word is separated by a newline. You can use a sample file of your choice for the set of predefined keywords.

## Approach
- 
- ConcurrentFileProcessor is the main class for my solution
- This has 2 separate classes FileChunkProcessor and FileMatchingProcessor which process the chunks of files and build the functionality
- RandomAccessFile to read the file from a specific byte position and each thread processes certain number of lines concurrently
- I have used Synchronized Set to add the unique words ensuring thread safety
- Locking mechanism is added using Reentrant lock for updating the matching strings