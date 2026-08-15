package characters;

/*
 * THEORY - Inheritance:
 * "extends Character" means Warrior automatically inherits everything public/protected from
 * Character (getName(), takeDamage(), isAlive(), printStatus(), the private fields indirectly
 * through those methods, etc.) without us rewriting any of it. Warrior only needs to add what's
 * actually SPECIAL about being a Warrior: its starting stats and its own attack() behavior.
 */
public class Warrior extends Character {

    public Warrior(String name) {
        // "super(...)" calls Character's constructor to do the shared setup (store name, set
        // health = maxHealth, etc). We just decide what stats a Warrior starts with: tanky
        // (high health) and hard-hitting (high attack).
        super(name, 120, 15);
    }

    /*
     * THEORY - Polymorphism (method overriding):
     * "@Override" is not required by the compiler, but it's good practice — it tells Java
     * "I intend to replace the parent's version of this method", and Java will give you an
     * error if you typo the method signature, catching mistakes early.
     * This is Warrior's own version of the abstract attack() from Character.
     */
    @Override
    public void attack(Character target) {
        System.out.println(getName() + " swings a sword at " + target.getName() + "!");
        target.takeDamage(getAttackPower());
    }
}
