import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class TestingThreadPool {

  public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

    Path path = Paths.get(TestingFileOrder.SAMPLE_FILE_NAME);
    List<String> writes = TestingFileOrder.generateSampleFile(path);

    doNothingStream(path);
    List<String> reads = doSomethingThreads(path);

    // check individual records
    // for (int i = 0; i < writes.size(); i++) {
    // String readAtI = reads.get(i);
    // String writeAtI = writes.get(i);
    // System.out.println("check @" + i + " = " + readAtI.equals(writeAtI));
    // }

    // boolean allThere = reads.containsAll(writes);
    // System.out.println("all there?" + allThere);
  }

  static List<String> doSomethingThreads(Path path) throws IOException {
    List<String> reads = Collections.synchronizedList(new ArrayList<>());

    ExecutorService threadPoolForStream = Executors.newCachedThreadPool();

    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(line -> {
        TestingThreadPool.ThreadedRunner runner = new ThreadedRunner(line, reads);
        threadPoolForStream.submit(runner);
      });
    }
    threadPoolForStream.shutdown();

    System.out.println("Finished reading file");

    System.out.println(reads.size());

    return reads;
  }

  static void doNothingStream(Path path) throws IOException {
    List<String> reads = Collections.synchronizedList(new ArrayList<>());

    ExecutorService threadPoolForStream = Executors.newCachedThreadPool();

    try (Stream<String> lines = Files.lines(path)) {
      threadPoolForStream.submit(
          () -> lines.forEach(line -> {
            reads.add(line);
            System.out.println("reading file");
          }));
    }
    threadPoolForStream.shutdown();

    System.out.println("Finished doing nothing with file");

    System.out.println(reads.size());

  }

  private static class ThreadedRunner implements Runnable {

    private String line;
    private List<String> reads;

    public ThreadedRunner(String line, List<String> reads) {
      this.line = line;
      this.reads = reads;
    }

    @Override
    public void run() {
      reads.add(line);
    }

  }

  void addRead(List<String> reads, String line) {
    reads.add(line);
  }

}
