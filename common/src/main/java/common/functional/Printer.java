package common.functional;

/**
 * Class for printing to console
 */
public class Printer {
        /**
         * Prints toOut.toString() to Console
         *
         * @param toOut Object to print
         */
        public static void print(Object toOut) {
            System.out.print(toOut);
        }

        /**
         * Prints \n to Console
         */
        public static void println() {
            System.out.println();
        }

        /**
         * Prints toOut.toString() + \n to Console
         *
         * @param toOut Object to print
         */
        public static void println(Object toOut) {
            System.out.println(toOut);
        }

        /**
         * Prints error: toOut.toString() to Console
         *
         * @param toOut Error to print
         */
        public static void printerror(Object toOut) {
            System.out.println("error: " + toOut);
        }

}
