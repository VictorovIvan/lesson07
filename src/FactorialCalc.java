import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    /**
     *  Class FactorialCalcCallable
     */
    static class FactorialCalcCallable implements Callable<BigInteger> {
        int numberOfFactorial;

        /**
         * Constructor FactorialCalcCallable
         * @param numberOfFactorial  Input number for calculation of the factorial
         */
        public FactorialCalcCallable(int numberOfFactorial) {
            this.numberOfFactorial = numberOfFactorial;
        }

        /**
         * Calculation factorial
         * @return result Result calculation of the factorial
         */
        @Override
        public BigInteger call() {
            BigInteger result = BigInteger.ONE;
            for (int indx = 2; indx <= numberOfFactorial; indx++) {
                result = result.multiply(BigInteger.valueOf(indx));
            }
            return result;
        }
    }

    /**
     * Calculation array of the number. And return array of the factorials
     * @param inputArray Input array of the number
     * @return resultCalculation Result calculates the array of factorials
     */
    public Future<BigInteger>[] calcArraysFactorials(Integer[] inputArray) {
        Future<BigInteger>[] resultCalculation = new Future[inputArray.length];
        ExecutorService exsService = Executors.newFixedThreadPool(inputArray.length);
        for (int index = 0; index < inputArray.length; index++) {
            resultCalculation[index] = exsService.submit(new FactorialCalc.FactorialCalcCallable(inputArray[index]));
        }
        exsService.shutdown();
        return resultCalculation;
    }
}
