package exceptions;

/*
 * THEORY - Custom checked exceptions:
 * Extending "Exception" (not "RuntimeException") makes this a CHECKED exception — Java forces
 * any method that can throw it to either "throws OutOfManaException" in its own signature or
 * wrap the call in a try/catch. That's intentional here: running out of mana is an expected,
 * recoverable game event (not a bug), so the compiler makes sure we never forget to handle it.
 */
public class OutOfManaException extends Exception {

    public OutOfManaException(String message) {
        super(message);
    }
}
