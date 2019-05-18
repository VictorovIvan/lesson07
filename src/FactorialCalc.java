import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Class FactorialCalc
 */
class FactorialCalc {
    private static ConcurrentMap<Integer, BigInteger> mapFactorials = new ConcurrentHashMap<>();
    private static Integer lastKey;
    private static final int MAX_NUMBER_IN_ARRAY = 500;
    private static final int MAX_SIZE_OF_ARRAY = 10;

    /**
     * Class putValueMapRunnable (Runnable)
     */
    static class putValueMapRunnable implements Runnable {
        Integer number;
        BigInteger factorial;

        /**
         * Constructor of the putValueMapRunnable
         *
         * @param inputNumber    Input number
         * @param inputFactorial Input factorial
         */
        public putValueMapRunnable(Integer inputNumber, BigInteger inputFactorial) {
            this.number = inputNumber;
            this.factorial = inputFactorial;
        }

        /**
         * Run Thread for put factorial in map
         */
        @Override
        public void run() {
            mapFactorials.putIfAbsent(number, factorial);
        }
    }

    /**
     * Class getValueMapCallable (Callable)
     */
    static class getValueMapCallable implements Callable {
        Integer number;

        /**
         * Constructor of the getValueMapCallable
         *
         * @param number Input number
         */
        public getValueMapCallable(Integer number) {
            this.number = number;
        }

        /**
         * Call of the get factorial
         *
         * @return BigInteger Factorial of the number
         */
        @Override
        public BigInteger call() {
            return mapFactorials.get(number);
        }
    }

    /**
     * Calculate factorial with previous value
     *
     * @param sortInputArray Sorted array of the number
     * @param lengthOfArray  Length array of the array
     */
    private void curFactorials(Integer[] sortInputArray, int lengthOfArray) {
        ExecutorService exsService = Executors.newFixedThreadPool(lengthOfArray);
        Future[] resultCalculation = new Future[lengthOfArray];
        BigInteger result = BigInteger.ONE;
        for (int indexValue = 0; indexValue < lengthOfArray; indexValue++) {
            if (sortInputArray[indexValue] >= 2) {
                if (mapFactorials.isEmpty() & lastKey < 2) {
                    for (int index = 2; index <= sortInputArray[indexValue]; index++) {
                        result = result.multiply(BigInteger.valueOf(index));
                    }
                } else {
                    try {
                        resultCalculation[indexValue - 1].get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    result = mapFactorials.get(lastKey);
                    for (int index = lastKey + 1; index <= sortInputArray[indexValue]; index++) {
                        result = result.multiply(BigInteger.valueOf(index));
                    }
                }
            }
            lastKey = sortInputArray[indexValue];
            resultCalculation[indexValue] = exsService.submit(new putValueMapRunnable(lastKey, result));
        }
        exsService.shutdown();
    }

    /**
     * Put of the series value
     *
     * @param inputResultMaxValue       Input number of the result max value
     * @param inputExsecutorPutMaxValue Max value with type of the exsecutor service for put max value
     * @param inputIndex                Input index
     */
    private void putValueSeries(BigInteger inputResultMaxValue, ExecutorService inputExsecutorPutMaxValue, int inputIndex) {
        for (int indexValue = 0; indexValue < inputIndex + 1; indexValue++) {
            if (indexValue >= 2) {
                inputResultMaxValue = inputResultMaxValue.multiply(BigInteger.valueOf(indexValue));
            }
            inputExsecutorPutMaxValue.submit(new putValueMapRunnable(indexValue, inputResultMaxValue));
        }
        inputExsecutorPutMaxValue.shutdown();
        while (!inputExsecutorPutMaxValue.isTerminated()) {
        }
    }

    /**
     * Calculation array of the number. And return array of the factorials
     *
     * @param inputArray Input array of the number
     */
    Future[] calcArraysFactorials(Integer[] inputArray) {
        Integer[] sortInputArray = Arrays.copyOf(inputArray, inputArray.length);
        Arrays.sort(sortInputArray);
        lastKey = sortInputArray[0];
        Future[] resultCalculation = new Future[inputArray.length];
        ExecutorService exsServicePutMaxValue = Executors.newFixedThreadPool(sortInputArray[inputArray.length - 1]);
        ExecutorService exsServiceGetMaxValue = Executors.newFixedThreadPool(sortInputArray[inputArray.length - 1]);
        ExecutorService exsSeriesServiceGetValue = Executors.newFixedThreadPool(inputArray.length);
        BigInteger resultMaxValue = BigInteger.ONE;

        if ((sortInputArray[inputArray.length - 1] > MAX_NUMBER_IN_ARRAY) | inputArray.length > MAX_SIZE_OF_ARRAY) {
            curFactorials(sortInputArray, inputArray.length);
            for (int indexValue = 0; indexValue < inputArray.length; indexValue++) {
                resultCalculation[indexValue] = exsSeriesServiceGetValue.submit(new getValueMapCallable(inputArray[indexValue]));
            }
            exsSeriesServiceGetValue.shutdown();
            while (!exsSeriesServiceGetValue.isTerminated()) {
            }
        } else {
            putValueSeries(resultMaxValue, exsServicePutMaxValue, sortInputArray[inputArray.length - 1]);
            for (int indexValue = 0; indexValue < inputArray.length; indexValue++) {
                resultCalculation[indexValue] = exsServiceGetMaxValue.submit(new getValueMapCallable(inputArray[indexValue]));
            }
            exsServiceGetMaxValue.shutdown();
            while (!exsServiceGetMaxValue.isTerminated()) {
            }
        }
        return resultCalculation;
    }
}
