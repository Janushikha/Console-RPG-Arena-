// Day2Demo.java has no "package" line (default package), same as Main.java.
// This is a SEPARATE entry point from Main.java on purpose: Main.java stays exactly as it was
// for the Day 1 demo, and this file shows off everything added on Day 2 without touching it.

import characters.Character;
import characters.Warrior;
import characters.Mage;
import characters.enemies.Goblin;
import characters.enemies.Skeleton;
import skills.Skill;
import skills.Fireball;
import skills.Heal;
import skills.PowerStrike;
import inventory.Inventory;
import inventory.Potion;
import exceptions.OutOfManaException;
import exceptions.InvalidTargetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class DEMONSTRATES everything added on Day 2, on top of the Day 1 hierarchy:
 *   - Interfaces   (Skill, implemented by Fireball/Heal/PowerStrike — a "can-do" contract that
 *                    doesn't care what class you extend)
 *   - Composition  (Character now HAS-A List<Skill>, filled in at runtime via addSkill())
 *   - Exceptions   (OutOfManaException / InvalidTargetException, both checked, caught around
 *                    useSkill() calls so bad actions don't crash the program)
 *   - Generics /
 *     Collections  (Inventory<Potion>, a List<Character> turn order, a Map<String, Character>
 *                    party lookup)
 *
 * All of Warrior/Mage/Goblin/Skeleton/Main from Day 1 are reused completely unchanged.
 */
public class Day2Demo {

    public static void main(String[] args) {
        System.out.println("=== Console RPG Arena (Day 2 demo) ===\n");

        Warrior warrior = new Warrior("Conan");
        Mage mage = new Mage("Merlin");
        Goblin goblin = new Goblin();
        Skeleton skeleton = new Skeleton();

        // ----- Composition + Interfaces -----
        // Skills are handed out at runtime with addSkill(), not baked in via a constructor like
        // Day 1's attack(). The SAME Fireball class could just as easily be given to the Goblin
        // below — Skill doesn't know or care about the Character hierarchy, only that whoever
        // calls use() passes a Character. That's the payoff of an interface over inheritance.
        warrior.addSkill(new PowerStrike());
        mage.addSkill(new Fireball());
        mage.addSkill(new Heal());
        goblin.addSkill(new Fireball()); // proof a Skill isn't tied to "hero" classes at all

        System.out.println("--- Skills learned ---");
        printSkills(warrior);
        printSkills(mage);
        printSkills(goblin);

        // ----- Generics & Collections -----
        // Inventory<Potion> only accepts Potion objects — the compiler enforces that, unlike a
        // raw List that would happily mix in unrelated types.
        Inventory<Potion> potions = new Inventory<>();
        potions.add(new Potion("Minor Health Potion", 15));
        potions.add(new Potion("Greater Health Potion", 40));

        // A List keeps whose-turn-is-next in order; a Map gives O(1) lookup of a party member by
        // name. Both are declared with the Character supertype, so — same as Day 1's arrays —
        // they can mix Warrior/Mage/Goblin/Skeleton objects freely.
        List<Character> turnOrder = new ArrayList<>(List.of(warrior, mage, goblin, skeleton));
        Map<String, Character> party = new HashMap<>();
        party.put(warrior.getName(), warrior);
        party.put(mage.getName(), mage);

        System.out.println("\n--- Turn order ---");
        for (Character c : turnOrder) {
            System.out.println("  " + c.getName());
        }

        System.out.println("\n--- Party lookup by name (HashMap) ---");
        System.out.println("  party.get(\"Conan\") -> " + party.get("Conan").getName());

        // ----- Exceptions -----
        System.out.println("\n--- Round 1: Mage casts Fireball at Goblin ---");
        castSkill(mage, 0, goblin); // index 0 = Fireball

        System.out.println("\n--- Round 2: Warrior Power Strikes the Goblin ---");
        castSkill(warrior, 0, goblin);

        System.out.println("\n--- Round 2b: Warrior tries to strike the Goblin again ---");
        // The Power Strike above should have finished the Goblin off. Attacking a defeated
        // target is exactly the case useSkill() rejects, so this deliberately triggers
        // InvalidTargetException instead of letting the program act on a dead target.
        castSkill(warrior, 0, goblin);

        System.out.println("\n--- Round 3: Mage casts Fireball at Skeleton until out of mana ---");
        // Each Fireball costs 15 MP out of a starting pool of 50, so the 4th cast should fail —
        // this deliberately drives useSkill() into throwing OutOfManaException.
        for (int i = 1; i <= 4; i++) {
            System.out.println("Cast attempt #" + i + " (Mage MP: " + mage.getMana() + "/" + mage.getMaxMana() + ")");
            castSkill(mage, 0, skeleton);
            if (!skeleton.isAlive()) {
                System.out.println(skeleton.getName() + " is already defeated, stopping early.");
                break;
            }
        }

        System.out.println("\n--- Mage heals themself ---");
        castSkill(mage, 1, mage); // index 1 = Heal, target = self

        System.out.println("\n--- Using a potion from the generic Inventory ---");
        Potion healthPotion = potions.get(0);
        healthPotion.useOn(warrior);

        System.out.println("\n--- Final stats ---");
        for (Character c : turnOrder) {
            c.printStatus();
            System.out.println("  MP: " + c.getMana() + "/" + c.getMaxMana());
        }
    }

    private static void printSkills(Character c) {
        System.out.print(c.getName() + "'s skills: ");
        List<Skill> skills = c.getSkills();
        if (skills.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        for (int i = 0; i < skills.size(); i++) {
            System.out.print(skills.get(i).getName());
            if (i < skills.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    /*
     * A small helper so every call site doesn't repeat the same try/catch. This is exactly the
     * kind of place exceptions earn their keep: no matter which of the two checked exceptions
     * useSkill() throws, the battle loop reports it and keeps running instead of crashing.
     */
    private static void castSkill(Character caster, int skillIndex, Character target) {
        try {
            caster.useSkill(skillIndex, target);
        } catch (OutOfManaException | InvalidTargetException e) {
            System.out.println("  [Action failed] " + e.getMessage());
        }
    }
}
