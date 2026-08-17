package observer;

/*
 * THEORY - Observer pattern:
 * This is the "subscriber" contract. Anything that wants to react to battle events (right now,
 * just printing them; later maybe a UI update, a sound effect, a stats tracker) implements this
 * interface and registers itself with a publisher (see game.GameManager). The publisher doesn't
 * need to know or care how many observers exist or what they do with the message — it just calls
 * onEvent() on each one. That's the decoupling: "something happened" (published once) is
 * separated from "here's what to do about it" (decided independently by each observer).
 */
public interface BattleObserver {
    void onEvent(String message);
}
