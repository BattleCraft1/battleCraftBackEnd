package pl.edu.pollub.battleCraft.additionalClasses;


public class Factorial {
    public static Long factorial(Long number) {
        if (number <= 1) return 1L;
        else return number * factorial(number - 1);
    }
}
