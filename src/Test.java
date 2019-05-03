import java.math.BigInteger;
import java.util.concurrent.*;

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
public class Test {
    public static void main(String[] args) throws Exception {
        Integer[] someNumbers = {3, 5, 7, 9};
        Future<BigInteger>[] resultCalculation;
        FactorialCalc factorialCalc = new FactorialCalc();

        resultCalculation = factorialCalc.calcArraysFactorials(someNumbers);
        for (int indx = 0; indx < someNumbers.length; indx++) {
            System.out.println(resultCalculation[indx].get());
        }
    }
}
