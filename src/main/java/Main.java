import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Main {
    //private static final AtomicInteger beautifulWords3 = new AtomicInteger(0);
    //private static final AtomicInteger beautifulWords4 = new AtomicInteger(0);
    //private static final AtomicInteger beautifulWords5 = new AtomicInteger(0);

    private static final int SIZE = 100_000;
    private static final AtomicIntegerArray atomicArray = new AtomicIntegerArray(SIZE);

    public static void main(String[] args) {
        Random random = new Random();
        String[] texts = new String[SIZE];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        try (ExecutorService service = Executors.newWorkStealingPool()) {
            //EcxecutorService executorService = Ecxecutors.newFixedThreadPool(3);

            Future<Boolean> isPalindromeFuture = service.submit(() -> {
                for (int i = 0; i < texts.length; i++) {
                    boolean palindrome = isPalindrome(texts[i]);
                    if (palindrome) {
                        atomicArray.getAndIncrement(i);
                    }
                }
                return true;
            });

            Future<Boolean> isAscendingFuture = service.submit(() -> {
                for (int i = 0; i < texts.length; i++) {
                    boolean ascending = isAscending(texts[i]);
                    if (ascending) {
                        atomicArray.getAndIncrement(i);
                    }
                }
                return true;
            });

            Future<Boolean> isOneLetterFuture = service.submit(() -> {
                for (int i = 0; i < texts.length; i++) {
                    boolean oneLetter = isOneLetter(texts[i]);
                    if (oneLetter) {
                        atomicArray.getAndIncrement(i);
                    }
                }
                return true;
            });

            try {
                isPalindromeFuture.get();
                isAscendingFuture.get();
                isOneLetterFuture.get();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                return;
            } catch (ExecutionException e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        int beautifulWords3 = 0;
        int beautifulWords4 = 0;
        int beautifulWords5 = 0;

        for (int i = 0; i < SIZE; i++) {

            final int length = texts[i].length();
            int result = atomicArray.get(i);

            if (result > 0) {
                switch (length) {
                    case 3:
                        beautifulWords3++;
                        break;
                    case 4:
                        beautifulWords4++;
                        break;
                    case 5:
                        beautifulWords5++;
                        break;
                    default:
                        System.out.println("Непредвиденная длина слова");
                        break;
                }
            }
        }

        System.out.println(String.format("Красивых слов с длиной 3: %d шт", beautifulWords3) );
        System.out.println(String.format("Красивых слов с длиной 4: %d шт", beautifulWords4) );
        System.out.println(String.format("Красивых слов с длиной 5: %d шт", beautifulWords5) );
    }

    // палиндром
    private static boolean isPalindrome(final String text) {
        if (text.length() <= 1) {
            return true;
        }
        int left = 0;
        int right = text.length() - 1;
        while (left < right) {
            if (text.charAt(left) != text.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    // по возрастанию
    private static boolean isAscending(final String text) {
        char prev = text.charAt(0);
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) < prev) {
                return false;
            }
            prev = text.charAt(i);
        }
        return true;
    }

    // из одного символа
    private static boolean isOneLetter(final String text) {
        char ch = text.charAt(0);
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) != ch) {
                return false;
            }
        }
        return true;
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
