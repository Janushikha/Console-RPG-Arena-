package characters.enemies;

import characters.Character;

public class Skeleton extends Enemy {

    public Skeleton() {
        // Tougher than a Goblin, hits harder too.
        super("Skeleton", 40, 10);
    }

    @Override
    public void attack(Character target) {
        System.out.println(getName() + " bashes " + target.getName() + " with a bone club!");
        target.takeDamage(getAttackPower());
    }
}
