package factory;

import characters.Character;
import characters.Warrior;
import characters.Mage;
import characters.enemies.Goblin;
import characters.enemies.Skeleton;

/*
 * THEORY - Factory pattern:
 * Without this class, every piece of code that wants a new Warrior has to know the concrete
 * class name and constructor signature directly ("new Warrior(name)"). That's fine in a small
 * demo, but it means the *decision* of which concrete class to build is scattered across the
 * whole codebase. CharacterFactory centralizes that decision behind one method per role
 * (createHero/createEnemy) that takes a simple enum and hands back a Character. Callers
 * (Day3Demo, or a future save/load system, or a level-up screen) never need to say "new Warrior"
 * or "new Goblin" themselves — only the factory does, in one place. Adding a new hero type later
 * means touching HeroType + this switch, not every call site that creates heroes.
 */
public class CharacterFactory {

    private CharacterFactory() {
        // Not meant to be instantiated — every method here is static.
    }

    public static Character createHero(HeroType type, String name) {
        switch (type) {
            case WARRIOR:
                return new Warrior(name);
            case MAGE:
                return new Mage(name);
            default:
                throw new IllegalArgumentException("Unknown hero type: " + type);
        }
    }

    public static Character createEnemy(EnemyType type) {
        switch (type) {
            case GOBLIN:
                return new Goblin();
            case SKELETON:
                return new Skeleton();
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
    }
}
