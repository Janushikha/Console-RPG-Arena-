package observer;

// One concrete subscriber: reacts to a battle event by printing it. A different observer
// (e.g. a BattleStatsTracker counting defeats) could listen to the exact same events without
// this class or the publisher changing at all.
public class BattleLogger implements BattleObserver {

    @Override
    public void onEvent(String message) {
        System.out.println("[Log] " + message);
    }
}
