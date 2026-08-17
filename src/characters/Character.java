package characters;
// ^ A "package" is just Java's way of grouping related classes into a folder, like a namespace.
//   This file lives in src/characters/, so it MUST start with "package characters;".
//   Any class that wants to use Character from another folder will need to "import" it.

// Day 2 imports: List/ArrayList for the composed skill list, and our own exception types and
// Skill interface, all of which live in other top-level packages.
import java.util.ArrayList;
import java.util.List;
import skills.Skill;
import exceptions.OutOfManaException;
import exceptions.InvalidTargetException;

/*
 * Character is the "blueprint" that every fighter in our game (heroes AND enemies) is built from.
 *
 * THEORY - Abstraction & Encapsulation:
 * - It is declared "abstract" because we never want to create a plain, generic "Character" object
 *   directly (what would that even look like — no special attack style?). We only ever want
 *   specific kinds, like Warrior or Goblin. An abstract class can hold shared data/behavior while
 *   still forcing its subclasses to fill in the parts that must differ (see attack() below).
 * - All the fields below are "private". This is ENCAPSULATION: no other class (not even a
 *   subclass) can read or change them directly. The only way to interact with a Character's stats
 *   is through the public methods we write, so we can guarantee the object never enters an invalid
 *   state (e.g. health can never go negative, see takeDamage()).
 */
public abstract class Character {

    private String name;
    private int maxHealth;
    private int health;
    private int attackPower;

    // ----- Day 2 additions below: composition (a Character HAS-A list of skills) -----
    // Every Character starts with the same mana pool and an empty skill list. Nothing here
    // changes what was already true in Day 1 — these are new fields/methods, not replacements.
    private int maxMana = 50;
    private int mana = 50;
    private final List<Skill> skills = new ArrayList<>();

    /*
     * Constructor: special method that runs once, automatically, when a new object is created
     * with "new". It has no return type and shares its name with the class.
     * Subclasses (Warrior, Goblin, ...) will call this via "super(...)" to fill in these shared
     * fields, instead of repeating the same setup code in every subclass.
     */
    public Character(String name, int maxHealth, int attackPower) {
        // "this.name" means "the field belonging to THIS object". Without "this.", writing
        // "name = name;" would just assign the parameter to itself and do nothing useful —
        // "this." is what tells Java to look at the object's field instead of the parameter.
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth; // everyone starts at full health
        this.attackPower = attackPower;
    }

    // ----- Getters: controlled READ access to the private fields above -----
    // Notice there are no public setters for these — once created, a Character's name/maxHealth/
    // attackPower can't be changed from outside at all. That's an intentional encapsulation choice.
    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public boolean isAlive() {
        return health > 0;
    }

    /*
     * This is a WRITE operation, but a validated one — the whole point of encapsulation.
     * Outside code cannot do "someCharacter.health = -999;" (health is private), it can only
     * call takeDamage(amount), and this method guarantees health never drops below 0.
     */
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0; // clamp so we never show/store negative HP
        }
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth; // clamp so we never exceed max HP
        }
    }

    /*
     * THEORY - Abstraction (the method) leading to Polymorphism (how it's used):
     * There is no "{ ... }" body here — just a semicolon. This is an ABSTRACT METHOD.
     * It forces every subclass of Character to write its OWN attack() method.
     * Because every subclass is required to have SOME attack() method, we can later write code
     * that calls character.attack(target) on ANY Character — Warrior, Mage, Goblin, whatever —
     * without needing to know which one it actually is. Java figures out at runtime which
     * version to run. That mechanism is called POLYMORPHISM, and we demonstrate it in Main.java.
     */
    public abstract void attack(Character target);

    // A normal (non-abstract) method — every subclass gets this exact behavior for free,
    // with no need to override it, because it doesn't need to differ per subclass.
    public void printStatus() {
        System.out.println(name + " - HP: " + health + "/" + maxHealth);
    }

    // ----- Day 2 additions: mana + composition (a list of Skill objects) -----

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    /*
     * THEORY - Composition ("has-a") vs. Inheritance ("is-a"):
     * Day 1's attack() is baked into each subclass at compile time via inheritance/overriding —
     * a Warrior can NEVER stop having a sword attack. addSkill() instead lets us plug a Skill
     * object INTO a Character at runtime. A Character "has-a" list of skills; it isn't required
     * to "be" any particular skill-granting subclass. This is more flexible: the same Fireball
     * object could be handed to a Mage, a Warrior, or even a Goblin, and skills can be added or
     * swapped after the object already exists.
     */
    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public List<Skill> getSkills() {
        return skills;
    }

    /*
     * THEORY - Exceptions:
     * "throws OutOfManaException, InvalidTargetException" in the signature is what makes these
     * CHECKED exceptions — any caller of useSkill() is forced by the compiler to either catch
     * them or declare them too. We validate here instead of trusting the caller, for the same
     * reason takeDamage() clamps health: an object should keep itself in a valid state and
     * refuse to do something impossible (cast a skill you can't afford, target someone already
     * defeated) rather than silently doing the wrong thing or crashing.
     */
    public void useSkill(int skillIndex, Character target) throws OutOfManaException, InvalidTargetException {
        if (skillIndex < 0 || skillIndex >= skills.size()) {
            throw new InvalidTargetException(name + " has no skill at index " + skillIndex + ".");
        }
        if (target == null || !target.isAlive()) {
            throw new InvalidTargetException(name + " cannot target a defeated or missing target.");
        }

        Skill skill = skills.get(skillIndex);
        if (mana < skill.getManaCost()) {
            throw new OutOfManaException(
                    name + " needs " + skill.getManaCost() + " MP for " + skill.getName()
                            + " but only has " + mana + ".");
        }

        mana -= skill.getManaCost();
        skill.use(this, target);
    }
}
