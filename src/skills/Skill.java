package skills;

import characters.Character;

/*
 * THEORY - Interfaces (a second way to share behavior, contrasted with Day 1's "extends"):
 * Day 1 used INHERITANCE ("is-a") to share behavior: Warrior IS-A Character, so it got
 * takeDamage()/heal()/etc for free. But inheritance only works along a single chain — a class
 * can extend just one other class.
 *
 * An INTERFACE instead describes a CAPABILITY ("can-do") with no shared implementation and no
 * fields. Any class, anywhere in the hierarchy, can implement it as long as it provides the
 * methods listed here. That's why a Skill can be handed to a Warrior, a Mage, or even a Goblin
 * (see Enemy in Day 1) — Skill doesn't care what a class extends, only that it fulfils this
 * contract. This is looser and more flexible than inheritance, at the cost of not sharing any
 * code between implementations (every class below still writes its own use() from scratch).
 */
public interface Skill {

    String getName();

    int getManaCost();

    /*
     * "user" is the Character performing the skill, "target" is who it affects. A Heal skill
     * might pass the user as their own target (self-heal); an attack skill passes an enemy.
     */
    void use(Character user, Character target);
}
