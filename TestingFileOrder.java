import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class TestingFileOrder {

  private static final String SAMPLE_FILE_NAME = "sample_file.csv";
  private static final int NUMBER_OF_RECORDS = 1_000_000;

  public static void main(String[] args) throws IOException {
    System.out.println("Babaganoush");
    generateSampleFile();
  }

  static void generateSampleFile() throws IOException {
    Path path = Paths.get(SAMPLE_FILE_NAME);
    Files.deleteIfExists(path);
    Files.createFile(path);
    List<String> writes = new ArrayList<>();
    // this writes slowly
    // for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
    // String lineContents = "some-key-" + i + ",some-value-" + i + "\n";
    // // System.out.println("\t"+lineContents);
    // writes.add(lineContents.trim());

    // Files.writeString(path, lineContents, StandardOpenOption.APPEND);
    // }

    // this writes quickly
    BufferedWriter writer = new BufferedWriter(new FileWriter(SAMPLE_FILE_NAME));

    for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
      String lineContents = "some-key-" + i + ",some-value-" + i + "\n";
      // System.out.println("\t"+lineContents);

      writer.write(lineContents);
      writes.add(lineContents.trim());
    }
    writer.close();
    System.out.println("Finished writing file");

    List<String> reads = new ArrayList<>();
    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(line -> reads.add(line)); // with parallel, it breaks
    }
    System.out.println("Finished reading file");
    // by default, it looks ordered

    System.out.println(reads.size());
    System.out.println(writes.size());

    // System.out.println(reads);

    // check individual records
    // for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
    // String readAtI = reads.get(i);
    // String writeAtI = writes.get(i);
    // System.out.println("check @" + i + " = " + readAtI.equals(writeAtI));
    // }

    System.out.println(reads.equals(writes));
  }

}