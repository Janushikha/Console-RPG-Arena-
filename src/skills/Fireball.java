package skills;

import characters.Character;

// One concrete "capability". Notice this class doesn't extend anything from the Character
// hierarchy at all — it only promises to fulfil the Skill interface.
public class Fireball implements Skill {

    private static final int MANA_COST = 15;
    private static final int DAMAGE = 25;

    @Override
    public String getName() {
        return "Fireball";
    }

    @Override
    public int getManaCost() {
        return MANA_COST;
    }

    @Override
    public void use(Character user, Character target) {
        System.out.println(user.getName() + " hurls a Fireball at " + target.getName() + "!");
        target.takeDamage(DAMAGE);
    }
}
