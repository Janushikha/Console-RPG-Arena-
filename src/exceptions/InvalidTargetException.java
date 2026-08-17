package exceptions;

// A second checked exception, thrown when a skill is used on a target that can't legally
// receive it right now (e.g. the target is already defeated, or no such skill exists).
public class InvalidTargetException extends Exception {

    public InvalidTargetException(String message) {
        super(message);
    }
}
