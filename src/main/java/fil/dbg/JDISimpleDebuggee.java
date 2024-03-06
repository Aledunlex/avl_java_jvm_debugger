package fil.dbg;

/**
 * Simple power printer
 * An example of debugee for our debugger
 */
public class JDISimpleDebuggee {

    public static void main(String[] args) {
        String description = "Simple power printer";
        System.out.println(description + " -- starting");
        int x = 40;
        int power = 2;
        printPower(x, power);
        System.out.println(getMagicNumber());
        anyLoop();
        System.out.println(description + " -- done");
    }

    /**
     * Calculate the power of x
     *
     * @param x     the number
     * @param power the power
     * @return x^power
     */
    public static double power(int x, int power) {
        return Math.pow(x, power);
    }

    /**
     * Print the power of x
     *
     * @param x     the number
     * @param power the power
     */
    public static void printPower(int x, int power) {
        double powerX = power(x, power);
        System.out.println(powerX);
    }

    /**
     * Return the magic number
     *
     * @return the magic number
     */
    private static int getMagicNumber() {
        return 42;
    }

    /**
     * A loop
     */
    private static void anyLoop() {
        for (int i = 0; i < 20; i++) {
            if (i % 5 == 0)
                System.out.println("i = " + i);
        }
    }

}