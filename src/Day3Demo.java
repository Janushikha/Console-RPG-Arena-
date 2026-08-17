// Day3Demo.java has no "package" line (default package), same as Main.java and Day2Demo.java.
// This is a THIRD, separate entry point: Main.java (Day 1) and Day2Demo.java (Day 2) are both
// left completely unchanged. This file wires everything from all three days into one actually
// playable console battle.

import characters.Character;
import factory.CharacterFactory;
import factory.HeroType;
import factory.EnemyType;
import skills.Fireball;
import skills.Heal;
import skills.PowerStrike;
import exceptions.OutOfManaException;
import exceptions.InvalidTargetException;
import inventory.Inventory;
import inventory.Potion;
import observer.BattleLogger;
import game.GameManager;
import ui.ConsoleUI;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/*
 * This class DEMONSTRATES everything added on Day 3, on top of Days 1 and 2:
 *   - Factory     (CharacterFactory builds every hero/enemy instead of scattering "new X()" calls)
 *   - Observer    (GameManager publishes battle events; BattleLogger subscribes and prints them)
 *   - Singleton   (GameManager.getInstance() — exactly one shared game/turn state)
 *   - Polish      (ConsoleUI health bars + boxed status panel)
 * ...wired into one real turn-based loop you can actually play from the console, alternating
 * between a human-controlled party and simple enemy AI, using Days 1-2's Character/Skill/
 * Inventory classes completely unchanged.
 */
public class Day3Demo {

    public static void main(String[] args) {
        System.out.println(ConsoleUI.BOLD + "=== Console RPG Arena (Day 3 - Playable) ===" + ConsoleUI.RESET);
        System.out.println("Choose actions by typing a number and pressing Enter each turn.\n");

        // ----- Singleton + Observer -----
        // Exactly one GameManager exists for the whole program (getInstance() never "new"s a
        // second one). It publishes events; BattleLogger is the subscriber that prints them —
        // a second observer could log to a file instead and GameManager wouldn't need to change.
        GameManager game = GameManager.getInstance();
        game.addObserver(new BattleLogger());

        // ----- Factory -----
        // Every hero/enemy is built through CharacterFactory instead of "new Warrior(...)"
        // directly, so this file never names a concrete hero/enemy class at construction time.
        Character warrior = CharacterFactory.createHero(HeroType.WARRIOR, "Conan");
        Character mage = CharacterFactory.createHero(HeroType.MAGE, "Merlin");
        warrior.addSkill(new PowerStrike());
        mage.addSkill(new Fireball());
        mage.addSkill(new Heal());

        List<Character> heroes = new ArrayList<>(List.of(warrior, mage));
        List<Character> enemies = new ArrayList<>(List.of(
                CharacterFactory.createEnemy(EnemyType.GOBLIN),
                CharacterFactory.createEnemy(EnemyType.SKELETON)));

        Inventory<Potion> potions = new Inventory<>();
        potions.add(new Potion("Health Potion", 25));
        potions.add(new Potion("Health Potion", 25));

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        game.notifyObservers("The battle begins! " + heroes.size() + " heroes vs " + enemies.size() + " enemies.");

        boolean inputEnded = false;
        while (anyAlive(heroes) && anyAlive(enemies) && !inputEnded) {
            printStatusPanel(game.getTurnNumber(), heroes, enemies);

            for (Character hero : heroes) {
                if (inputEnded || !hero.isAlive() || !anyAlive(enemies)) {
                    continue;
                }
                try {
                    heroTurn(hero, enemies, potions, scanner, game);
                } catch (NoSuchElementException e) {
                    // Scanner ran out of input (e.g. piped input exhausted) — end the game
                    // gracefully instead of crashing or looping forever asking for input.
                    inputEnded = true;
                }
            }

            if (!anyAlive(enemies) || inputEnded) {
                break;
            }

            for (Character enemy : enemies) {
                if (enemy.isAlive() && anyAlive(heroes)) {
                    enemyTurn(enemy, heroes, random, game);
                }
            }

            game.nextTurn();
        }

        System.out.println();
        if (!anyAlive(enemies)) {
            game.notifyObservers("All enemies defeated! Victory!");
        } else if (!anyAlive(heroes)) {
            game.notifyObservers("The party has fallen. Game over.");
        } else {
            game.notifyObservers("No more input — battle stopped early.");
        }

        scanner.close();
    }

    private static boolean anyAlive(List<Character> group) {
        for (Character c : group) {
            if (c.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private static void printStatusPanel(int turnNumber, List<Character> heroes, List<Character> enemies) {
        List<String> lines = new ArrayList<>();
        lines.add("Heroes:");
        for (Character c : heroes) {
            lines.add("  " + c.getName() + "  " + ConsoleUI.healthBar(c.getHealth(), c.getMaxHealth(), 12));
        }
        lines.add("Enemies:");
        for (Character c : enemies) {
            lines.add(c.isAlive()
                    ? "  " + c.getName() + "  " + ConsoleUI.healthBar(c.getHealth(), c.getMaxHealth(), 12)
                    : "  " + c.getName() + "  (defeated)");
        }
        ConsoleUI.printBox("Turn " + turnNumber, lines.toArray(new String[0]));
    }

    // Throws NoSuchElementException (via Scanner) if input has run out — the caller catches it.
    private static void heroTurn(Character hero, List<Character> enemies, Inventory<Potion> potions,
                                  Scanner scanner, GameManager game) {
        Character firstEnemy = firstAlive(enemies);

        System.out.println("\n" + hero.getName() + "'s turn  (HP " + hero.getHealth() + "/" + hero.getMaxHealth()
                + ", MP " + hero.getMana() + "/" + hero.getMaxMana() + ")");
        System.out.println("  1) Attack  2) Use skill  3) Use potion  4) Skip turn");
        System.out.print("> ");

        int choice = readInt(scanner);

        switch (choice) {
            case 1:
                if (firstEnemy != null) {
                    hero.attack(firstEnemy);
                    announceIfDefeated(firstEnemy, game);
                }
                break;

            case 2:
                if (hero.getSkills().isEmpty()) {
                    System.out.println("  " + hero.getName() + " has no skills learned.");
                    break;
                }
                for (int i = 0; i < hero.getSkills().size(); i++) {
                    System.out.println("    " + i + ") " + hero.getSkills().get(i).getName());
                }
                System.out.print("  skill> ");
                int skillIndex = readInt(scanner);
                try {
                    // Simplification for this demo: every skill targets the first living enemy.
                    // A fuller game would let Heal target a party member instead.
                    hero.useSkill(skillIndex, firstEnemy);
                    announceIfDefeated(firstEnemy, game);
                } catch (OutOfManaException | InvalidTargetException e) {
                    System.out.println("  [Action failed] " + e.getMessage());
                }
                break;

            case 3:
                if (potions.size() == 0) {
                    System.out.println("  No potions left.");
                } else {
                    potions.remove(0).useOn(hero);
                }
                break;

            default:
                System.out.println("  " + hero.getName() + " skips their turn.");
        }
    }

    private static void enemyTurn(Character enemy, List<Character> heroes, Random random, GameManager game) {
        Character target = randomAlive(heroes, random);
        if (target == null) {
            return;
        }

        if (!enemy.getSkills().isEmpty() && random.nextBoolean()) {
            int skillIndex = random.nextInt(enemy.getSkills().size());
            try {
                enemy.useSkill(skillIndex, target);
            } catch (OutOfManaException | InvalidTargetException e) {
                enemy.attack(target); // fall back to a plain attack if the skill can't be used
            }
        } else {
            enemy.attack(target);
        }

        announceIfDefeated(target, game);
    }

    private static void announceIfDefeated(Character character, GameManager game) {
        if (character != null && !character.isAlive()) {
            game.notifyObservers(character.getName() + " was defeated!");
        }
    }

    private static Character firstAlive(List<Character> group) {
        for (Character c : group) {
            if (c.isAlive()) {
                return c;
            }
        }
        return null;
    }

    private static Character randomAlive(List<Character> group, Random random) {
        List<Character> alive = new ArrayList<>();
        for (Character c : group) {
            if (c.isAlive()) {
                alive.add(c);
            }
        }
        return alive.isEmpty() ? null : alive.get(random.nextInt(alive.size()));
    }

    // Throws NoSuchElementException (propagated to the caller) if there's no more input to read.
    private static int readInt(Scanner scanner) {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1; // any unhandled choice number just falls through to "skip turn"/no-op
        }
    }
}
