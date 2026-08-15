package characters;

/*
 * Another subclass of Character — same idea as Warrior, different numbers and different
 * attack() behavior. Comparing this file side-by-side with Warrior.java is a good way to see
 * exactly what inheritance shares (structure) vs. what each subclass customizes (stats + attack).
 */
public class Mage extends Character {

    public Mage(String name) {
        // Glass cannon: low health, but a very strong attack.
        super(name, 80, 22);
    }

    @Override
    public void attack(Character target) {
        System.out.println(getName() + " casts a Fireball at " + target.getName() + "!");
        target.takeDamage(getAttackPower());
    }
}
