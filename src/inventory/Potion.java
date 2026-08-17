package inventory;

import characters.Character;

public class Potion implements Item {

    private final String name;
    private final int healAmount;

    public Potion(String name, int healAmount) {
        this.name = name;
        this.healAmount = healAmount;
    }

    @Override
    public String getName() {
        return name;
    }

    public void useOn(Character target) {
        System.out.println(target.getName() + " drinks a " + name + "!");
        target.heal(healAmount);
    }
}
