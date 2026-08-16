# Implementation Log — Console RPG Arena

This file tracks what's been built, why, and the theory behind it, day by day. It's meant to be
read alongside the code — check it whenever a comment in the code says "see implementation.md"
or when you want the bigger picture instead of line-by-line detail.

Timeline note: originally scoped as a 2-week project, now compressed to **3 days**. Each day
below covers roughly one third of the original plan.

---

## Day 1 (done) — Encapsulation, Abstraction, Inheritance, Polymorphism

### What exists after today
```
src/
  characters/
    Character.java          - abstract base class for every fighter in the game
    Warrior.java             - hero subclass
    Mage.java                 - hero subclass
    enemies/
      Enemy.java             - abstract base class for enemies (extends Character)
      Goblin.java            - enemy subclass
      Skeleton.java          - enemy subclass
  Main.java                  - runnable demo that ties everything together
```

### The class hierarchy

```
              Character (abstract)
              /              \
        Warrior, Mage      Enemy (abstract)
                             /        \
                        Goblin      Skeleton
```

Everything below `Character` "is-a" Character. That single sentence is the theory behind
inheritance: if B extends A, every B object can be used anywhere an A is expected.

### Concept 1 — Encapsulation
**What:** `Character`'s fields (`name`, `health`, `maxHealth`, `attackPower`) are `private`.
Nothing outside the class can read or write them directly — you're forced to go through public
methods like `getHealth()`, `takeDamage()`, `heal()`.

**Why it matters:** `takeDamage()` clamps health so it never goes below 0. If the field were
public, any other code in the program could do `character.health = -500;` and break that
guarantee. Encapsulation means the object is always responsible for keeping itself valid — no
one else can corrupt its internal state.

**Where to look:** `Character.java`, the `private` fields at the top and `takeDamage()`/`heal()`.

### Concept 2 — Abstraction
**What:** `Character` and `Enemy` are declared `abstract`, and `attack(Character target)` has no
body in `Character` — just a signature ending in `;`.

**Why it matters:** It doesn't make sense to create a plain, generic `Character` (what would it
even attack with?). Marking the class `abstract` makes that a *compiler error* if anyone tries
`new Character(...)`, not just a convention. The abstract `attack()` method is a contract: "every
concrete subclass MUST provide its own attack behavior." This forces consistency across the whole
hierarchy without dictating what each attack actually does.

**Where to look:** `Character.java` — the `public abstract class` line and `public abstract void
attack(Character target);`.

### Concept 3 — Inheritance
**What:** `Warrior extends Character`, `Mage extends Character`, `Enemy extends Character`,
`Goblin extends Enemy`, `Skeleton extends Enemy`.

**Why it matters:** All the shared logic (health tracking, damage clamping, `isAlive()`,
`printStatus()`) is written exactly once, in `Character`. Every subclass gets it for free via
`extends`, and only writes the code that's actually different about it (starting stats, attack
flavor text). `Enemy extends Character` but stays abstract — it's a shared ancestor for enemy
types without being a "finished" class itself.

**Where to look:** Compare `Warrior.java` and `Mage.java` side by side — same shape, different
numbers/text. `super(...)` in each constructor is what invokes `Character`'s constructor to do
the shared setup.

### Concept 4 — Polymorphism
**What:** In `Main.java`, `Character[] heroes = { warrior, mage };` stores two *different*
classes in one `Character`-typed array. The loop `heroes[i].attack(enemies[i]);` runs Warrior's
sword attack for Conan and Mage's fireball attack for Merlin — same line of code, different
behavior, decided automatically at runtime based on the object's real type.

**Why it matters:** This is the payoff of the whole hierarchy. Code that works with `Character`
references doesn't need `if (x instanceof Warrior) ... else if (x instanceof Mage) ...` — it
just calls `attack()` and trusts each object to know how to attack. Adding an `Archer` or
`Healer` class later means the `Main.java` loop needs **zero changes**.

**Where to look:** `Main.java`, the `for (int i = 0; i < heroes.length; i++)` loop and the
comment above it about "dynamic dispatch."

### How to compile & run
From the project root (`d:\Projects_CSE\OOP project`):
```
javac -d out src/Main.java src/characters/*.java src/characters/enemies/*.java
java -cp out Main
```
(`javac` compiles `.java` source files into `.class` bytecode, placed in `out/`; `java -cp out
Main` runs the compiled `Main` class using `out` as the classpath.)

### A note on packages
`package characters;` at the top of a file just means "this class lives in the `characters`
folder," matching Java's rule that folder structure = package structure. Any file that wants to
use a class from a different package needs an `import` line (see the top of `Main.java` and
`Enemy.java`). `Main.java` has no package line, so it lives in Java's unnamed "default package"
at the root of `src/`.

---

## Day 2 (planned) — Interfaces, Composition, Exceptions, Generics/Collections
- `Skill` interface (`Fireball`, `Heal`, `PowerStrike`, ...) — a *second*, more flexible way to
  share behavior across unrelated classes, contrasted with the `extends` inheritance from Day 1.
- Give `Character` a `List<Skill>` — composition ("has-a") vs. inheritance ("is-a").
- Custom exceptions (`OutOfManaException`, `InvalidTargetException`) and `try`/`catch` around the
  battle loop, so bad input/actions don't crash the program.
- Generic `Inventory<T extends Item>` plus `ArrayList`/`HashMap` for party and turn order.

## Day 3 (planned) — Design Patterns, Integration, Polish
- Factory pattern (`CharacterFactory`) to centralize object creation.
- Observer pattern (`BattleLogger`) to decouple "things happened" from "print a log."
- Singleton pattern (`GameManager`) to own the overall game/turn state.
- Wire everything into one playable console loop; add colored/formatted output (ANSI health
  bars, boxed menus) as a polish pass.
- *Optional stretch, after the 3 days:* a JavaFX front-end reusing the same model classes.
