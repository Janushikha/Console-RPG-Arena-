package game;

import observer.BattleObserver;

import java.util.ArrayList;
import java.util.List;

/*
 * THEORY - Singleton pattern:
 * There should only ever be ONE game/turn state for a running battle — it wouldn't make sense
 * to have two different "current turn numbers" floating around different parts of the program.
 * The private constructor stops anyone from writing "new GameManager()" anywhere else; the only
 * way to get one is GameManager.getInstance(), which creates it once and hands back that same
 * object every time after. Contrast this with CharacterFactory, which deliberately creates a
 * NEW object every call — Singleton is for exactly the opposite situation, where sharing one
 * instance is the whole point.
 *
 * GameManager also doubles as the Observer pattern's "publisher": it holds the list of
 * subscribed BattleObservers and is the one place that calls notifyObservers().
 */
public class GameManager {

    private static GameManager instance;

    private final List<BattleObserver> observers = new ArrayList<>();
    private int turnNumber = 1;

    // Private constructor: the only way to get a GameManager is through getInstance() below.
    private GameManager() {
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void addObserver(BattleObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (BattleObserver observer : observers) {
            observer.onEvent(message);
        }
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void nextTurn() {
        turnNumber++;
    }
}
