package skills;

import characters.Character;

// A support skill: notice "target" here is expected to be whoever receives the healing, which
// might be the caster themself (self-heal) — the caller decides who to pass as target.
public class Heal implements Skill {

    private static final int MANA_COST = 10;
    private static final int HEAL_AMOUNT = 20;

    @Override
    public String getName() {
        return "Heal";
    }

    @Override
    public int getManaCost() {
        return MANA_COST;
    }

    @Override
    public void use(Character user, Character target) {
        System.out.println(user.getName() + " channels healing energy into " + target.getName() + "!");
        target.heal(HEAL_AMOUNT);
    }
}
