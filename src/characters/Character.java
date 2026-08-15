package characters;
// ^ A "package" is just Java's way of grouping related classes into a folder, like a namespace.
//   This file lives in src/characters/, so it MUST start with "package characters;".
//   Any class that wants to use Character from another folder will need to "import" it.

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
}
