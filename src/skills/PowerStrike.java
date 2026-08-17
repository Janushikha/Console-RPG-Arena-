package skills;

import characters.Character;

// A melee skill whose damage scales off the user's own attackPower (read through the public
// getAttackPower() getter — still respecting Day 1's encapsulation, even from a totally
// unrelated package).
public class PowerStrike implements Skill {

    private static final int MANA_COST = 12;
    private static final int DAMAGE_MULTIPLIER = 2;

    @Override
    public String getName() {
        return "Power Strike";
    }

    @Override
    public int getManaCost() {
        return MANA_COST;
    }

    @Override
    public void use(Character user, Character target) {
        int damage = user.getAttackPower() * DAMAGE_MULTIPLIER;
        System.out.println(user.getName() + " unleashes a Power Strike on " + target.getName() + "!");
        target.takeDamage(damage);
    }
}
