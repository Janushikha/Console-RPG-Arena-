// Main.java has no "package" line, which puts it in Java's "default package" — the root of src.
// That's fine for a small learning project; we'll only need imports for classes that live inside
// a named package (like our characters.* classes below).

import characters.Character;
import characters.Warrior;
import characters.Mage;
import characters.enemies.Goblin;
import characters.enemies.Skeleton;

/*
 * This class exists purely to DEMONSTRATE the concepts built so far:
 *   - Encapsulation (Character's private fields, accessed only via its methods)
 *   - Abstraction   (Character/Enemy are abstract, attack() has no body until a subclass gives one)
 *   - Inheritance   (Warrior/Mage extend Character; Goblin/Skeleton extend Enemy extends Character)
 *   - Polymorphism  (one line of code, heroes[i].attack(...), behaves differently per object)
 *
 * Every Java program needs exactly one class with a "main" method — that's the entry point the
 * JVM (Java Virtual Machine) calls first when you run the program.
 */
public class Main {

    // "public static void main(String[] args)" is the exact signature Java looks for to start
    // your program. You don't need to fully understand "static" yet — just know this line is
    // boilerplate every runnable Java program needs.
    public static void main(String[] args) {
        System.out.println("=== Console RPG Arena (Day 1 demo) ===\n");

        // Create two different KINDS of hero. Even though Warrior and Mage are different
        // classes, both are also "a Character" (because both extend Character).
        Warrior warrior = new Warrior("Conan");
        Mage mage = new Mage("Merlin");

        Goblin goblin = new Goblin();
        Skeleton skeleton = new Skeleton();

        // THEORY - Polymorphism in action:
        // This array is declared as Character[], NOT Warrior[] or Mage[]. Because Warrior and
        // Mage both extend Character, we're allowed to store either kind in a Character-typed
        // box. This is what lets us treat a whole mixed group of different classes uniformly.
        Character[] heroes = { warrior, mage };
        Character[] enemies = { goblin, skeleton };

        System.out.println("--- Starting stats ---");
        for (Character c : heroes) {
            c.printStatus();
        }
        for (Character c : enemies) {
            c.printStatus();
        }

        System.out.println("\n--- Round 1: heroes attack enemies ---");
        // This loop is the heart of the polymorphism demo. The code "heroes[i].attack(...)"
        // never mentions Warrior or Mage by name, yet it runs Warrior's sword attack for Conan
        // and Mage's fireball attack for Merlin. Java looks at the REAL type of the object at
        // runtime (not the declared array type) to decide which attack() to actually run.
        // This mechanism is called "dynamic dispatch", and it's what makes polymorphism useful:
        // we can add a Healer or Archer class later and this loop won't need to change at all.
        for (int i = 0; i < heroes.length; i++) {
            heroes[i].attack(enemies[i]);
        }

        System.out.println("\n--- After round 1 ---");
        for (Character c : enemies) {
            c.printStatus();
        }

        System.out.println("\n--- Round 2: enemies attack heroes ---");
        for (int i = 0; i < enemies.length; i++) {
            // isAlive() reads the encapsulated health field through a safe public method —
            // we never check enemies[i].health directly, because it's private.
            if (enemies[i].isAlive()) {
                enemies[i].attack(heroes[i]);
            } else {
                System.out.println(enemies[i].getName() + " is defeated and cannot attack.");
            }
        }

        System.out.println("\n--- Final stats ---");
        for (Character c : heroes) {
            c.printStatus();
        }
        for (Character c : enemies) {
            c.printStatus();
        }
    }
}
