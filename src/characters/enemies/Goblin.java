package characters.enemies;

import characters.Character;

public class Goblin extends Enemy {

    public Goblin() {
        // Weak but fast enemy: low health, low-ish attack.
        super("Goblin", 30, 8);
    }

    @Override
    public void attack(Character target) {
        System.out.println(getName() + " scratches " + target.getName() + " with its claws!");
        target.takeDamage(getAttackPower());
    }
}
