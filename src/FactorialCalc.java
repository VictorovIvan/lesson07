import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Class FactorialCalc
 */
class FactorialCalc {
    private static ConcurrentMap<Integer, BigInteger> mapFactorials = new ConcurrentHashMap<>();
    private static Integer lastKey;


    /**
     * Class FactorialCalcCallable
     */
    static class FactorialCalcCallable implements Callable<BigInteger> {
        final int numberOfFactorial;
        final private Lock lock = new ReentrantLock();


        /**
         * Constructor FactorialCalcCallable
         *
         * @param numberOfFactorial Input number for calculation of the factorial
         */
        FactorialCalcCallable(int numberOfFactorial) {
            this.numberOfFactorial = numberOfFactorial;
        }

        /**
         * Calculation factorial
         *
         * @return result Result calculation of the factorial
         */
        @Override
        public BigInteger call() {
            BigInteger result = BigInteger.ONE;
            lock.lock();
            try {
                if (numberOfFactorial >= 2) {
                    if (mapFactorials.isEmpty() & lastKey < 2) {
                        for (int index = 2; index <= numberOfFactorial; index++) {
                            result = result.multiply(BigInteger.valueOf(index));
                        }
                    } else {
                        result = mapFactorials.get(lastKey);
                        for (int index = lastKey + 1; index <= numberOfFactorial; index++) {
                            result = result.multiply(BigInteger.valueOf(index));
                        }
                    }
                }
                lastKey = numberOfFactorial;
                mapFactorials.putIfAbsent(numberOfFactorial, result);
            } finally {
                lock.unlock();
            }
            return result;
        }
    }

    /**
     * Calculation array of the number. And return array of the factorials
     *
     * @param inputArray Input array of the number
     * @return resultCalculation Result calculates the array of factorials
     */
    Future[] calcArraysFactorials(Integer[] inputArray) {
        Integer[] sortInputArray = Arrays.copyOf(inputArray,inputArray.length);
        Arrays.sort(sortInputArray);
        lastKey = sortInputArray[0];
        Future[] resultCalculation = new Future[inputArray.length];
        ExecutorService exsService = Executors.newFixedThreadPool(inputArray.length);
        IntStream.range(0, inputArray.length).forEach(index -> {
            resultCalculation[index] = exsService.submit(new FactorialCalcCallable(sortInputArray[index]));
            try {
                resultCalculation[index].get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        IntStream.range(0, inputArray.length).forEach(index -> {
            System.out.println("Число: " + (inputArray[index]));
            System.out.println("Факториал: " + mapFactorials.get(inputArray[index]) + "\n");
        });
        exsService.shutdown();
        return resultCalculation;
    }
}
