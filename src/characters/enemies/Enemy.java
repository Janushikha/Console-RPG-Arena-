package characters.enemies;

// We're in a different folder/package now (characters/enemies), so to use the Character class
// from the "characters" package, we must import it explicitly.
import characters.Character;

/*
 * THEORY - Multi-level inheritance:
 * Enemy extends Character, and in a moment Goblin/Skeleton will extend Enemy. That gives us a
 * chain: Character -> Enemy -> Goblin. This mirrors plain English: "a Goblin IS-A Enemy, and
 * an Enemy IS-A Character." Enemy itself stays abstract — we still never want a bare "Enemy"
 * object, only concrete kinds like Goblin or Skeleton.
 *
 * Right now Enemy doesn't add any new fields or methods of its own — it exists as a place to
 * put behavior that's shared by ALL enemies but NOT by heroes (for example, simple AI logic),
 * which we can add later without touching Character or the hero classes.
 */
public abstract class Enemy extends Character {

    public Enemy(String name, int maxHealth, int attackPower) {
        super(name, maxHealth, attackPower);
    }
}
