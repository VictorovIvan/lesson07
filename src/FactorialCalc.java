import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Дан массив случайных чисел. Написать программу для вычисления факториалов всех элементов массива.
 * Использовать пул потоков для решения задачи.</p>
 * <p>Особенности выполнения:</p>
 * <p>Для данного примера использовать рекурсию - не очень хороший вариант, т.к. происходит большое выделение памяти,
 * очень вероятен StackOverFlow. Лучше перемножать числа в простом цикле при этом создавать объект типа BigInteger
 * По сути, есть несколько способа решения задания:</p>
 * <p>1) распараллеливать вычисление факториала для одного числа
 * 2) распараллеливать вычисления для разных чисел
 * 3) комбинированный</p>
 * <p>При чем вычислив факториал для одного числа, можно запомнить эти данные и использовать их для вычисления другого,
 * что будет гораздо быстрее</p>
 */

/**
 * Class FactorialCalc
 */
public class FactorialCalc {
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
        public FactorialCalcCallable(int numberOfFactorial) {
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
    public Future<BigInteger>[] calcArraysFactorials(Integer[] inputArray) {
        Integer[] sortInputArray = Arrays.copyOf(inputArray,inputArray.length);
        Arrays.sort(sortInputArray);
        lastKey = sortInputArray[0];
        Future<BigInteger>[] resultCalculation = new Future[inputArray.length];
        ExecutorService exsService = Executors.newFixedThreadPool(inputArray.length);
        for (int index = 0; index < inputArray.length; index++) {
            resultCalculation[index] = exsService.submit(new FactorialCalc.FactorialCalcCallable(sortInputArray[index]));
            try {
                resultCalculation[index].get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        for (int index = 0; index < inputArray.length; index++) {
            System.out.println("Число: " + (inputArray[index]));
            System.out.println("Факториал: " + mapFactorials.get(inputArray[index]) + "\n");
        }
        exsService.shutdown();
        return resultCalculation;
    }
}
