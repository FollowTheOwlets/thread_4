import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    static final int COUNT = 10_000;
    static final int LENGTH = 100_000;

    static BlockingQueue<String> fqueue = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> squeue = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> tqueue = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {

        Thread adder = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                String password = generateText("abc", LENGTH);
                try {
                    fqueue.put(password);
                    squeue.put(password);
                    tqueue.put(password);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        Thread th1 = buildThread("a", fqueue);
        Thread th2 = buildThread("b", squeue);
        Thread th3 = buildThread("c", tqueue);

        adder.start();
        th1.start();
        th2.start();
        th3.start();

        th1.join();
        th2.join();
        th3.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int count(char c, String s) {
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                counter++;
            }
        }
        return counter;
    }

    public static Thread buildThread(String c, BlockingQueue<String> queue) {
        return new Thread(() -> {
            String maxPassword = "";
            int maxCount = 0;
            for (int i = 0; i < COUNT; i++) {
                try {
                    String password = queue.take();
                    int newCount = count(c.charAt(0), password);
                    if (newCount > maxCount) {
                        maxPassword = password;
                        maxCount = newCount;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Максимальная строка с символом (" + c + ")  ->  " + maxPassword);
        });

    }
}
