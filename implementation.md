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

## Day 2 (done) — Interfaces, Composition, Exceptions, Generics/Collections

### What exists after today
```
src/
  skills/
    Skill.java               - interface: getName(), getManaCost(), use(user, target)
    Fireball.java             - damage skill
    Heal.java                 - support skill (heals whoever is passed as target)
    PowerStrike.java          - melee skill, damage scales off the user's attackPower
  exceptions/
    OutOfManaException.java   - checked exception: not enough mana to use a skill
    InvalidTargetException.java - checked exception: target is null/already defeated/invalid index
  inventory/
    Item.java                 - interface: getName()
    Potion.java                - implements Item, heals whoever it's used on
    Inventory.java             - generic container, Inventory<T extends Item>
  characters/
    Character.java            - (extended, see below) now also holds mana + a List<Skill>
  Main.java                   - Day 1 demo, unchanged, still compiles and runs exactly as before
  Day2Demo.java               - NEW, separate entry point for the Day 2 demo
```
Everything from Day 1 (`Character`, `Warrior`, `Mage`, `Enemy`, `Goblin`, `Skeleton`, `Main.java`)
is untouched except for one additive change described below — same fields, same methods, same
`Main.java` output as Day 1. Day 2's demo lives in its own `Day2Demo.java` so the two days can be
run and compared side by side.

### The one change to `Character.java`, and why it's still "additive"
`Character` gained two new private fields (`mana`, `maxMana`, both defaulting to 50) and a
`List<Skill> skills`, plus new public methods (`getMana()`, `getMaxMana()`, `addSkill()`,
`getSkills()`, `useSkill()`). Every method and field from Day 1 — `takeDamage()`, `heal()`,
`attack()`, `printStatus()`, the constructor signature — is byte-for-byte the same as before.
Nothing needed to change in `Warrior.java`, `Mage.java`, `Enemy.java`, `Goblin.java`,
`Skeleton.java`, or `Main.java` for this to work.

### Concept 5 — Interfaces (a second way to share behavior)
**What:** `Skill` is an `interface`, not a class. `Fireball`, `Heal`, and `PowerStrike` each
`implements Skill` and provide their own `use(user, target)`.

**Why it matters:** Day 1's inheritance (`extends`) only works along a single chain — a class can
extend just one other class, and the relationship is permanent ("a Warrior IS-A Character, always
will be"). An interface instead describes a *capability* with no shared fields or implementation:
"anything that can `use()` itself." Because `Skill` doesn't care what a class extends, the exact
same `Fireball` object can be handed to a `Mage`, a `Warrior`, or even a `Goblin` — proven in
`Day2Demo.java`, where the Goblin is given a Fireball too. Contrast this with `attack()`, which is
permanently fixed per subclass from the moment the object is constructed.

**Where to look:** `skills/Skill.java` for the contract, `skills/Fireball.java` for one
implementation, and `Day2Demo.java`'s `goblin.addSkill(new Fireball())` line for the proof that it
crosses the hero/enemy boundary freely.

### Concept 6 — Composition ("has-a") vs. Inheritance ("is-a")
**What:** `Character` now holds `private final List<Skill> skills`, filled in later via
`addSkill()` — not passed into the constructor, not fixed at compile time.

**Why it matters:** This is the classic OOP contrast. Inheritance answers "what IS this object,
permanently, from creation?" (a Warrior is-and-always-will-be a Character). Composition answers
"what does this object HAVE, right now, that could change?" (a Character has whatever skills were
added to it, and more could be added — or in a fuller game, removed — later). Using a `List<Skill>`
field instead of subclassing per skill combination avoids a combinatorial explosion of classes
(`FireballMage`, `HealingMage`, `FireballAndHealingMage`, ...).

**Where to look:** `Character.java`'s `addSkill()`/`getSkills()`/`useSkill()`, and
`Day2Demo.java`'s `warrior.addSkill(new PowerStrike())` / `mage.addSkill(new Fireball())` lines.

### Concept 7 — Custom Exceptions
**What:** `OutOfManaException` and `InvalidTargetException` both `extends Exception` (not
`RuntimeException`), making them *checked* exceptions. `Character.useSkill()` declares
`throws OutOfManaException, InvalidTargetException` and validates before acting: not enough mana,
or a null/already-defeated/out-of-range target, throws instead of silently doing the wrong thing.
`Day2Demo.java`'s `castSkill()` helper wraps every call in `try { ... } catch (OutOfManaException |
InvalidTargetException e) { ... }` (a multi-catch, since both are handled the same way here).

**Why it matters:** Because they're checked, the compiler *forces* every caller of `useSkill()` to
deal with the possibility of failure — you cannot forget to handle "player tried an illegal move"
the way you could with an unchecked exception or a silently-ignored error code. This is the same
philosophy as Day 1's encapsulation (`takeDamage()` clamping health): the object refuses to enter
or cause an invalid state, it just does so here by throwing rather than clamping, because "you
don't have enough mana" isn't something that can be silently corrected the way negative HP is.

**Where to look:** `exceptions/OutOfManaException.java`, `exceptions/InvalidTargetException.java`,
`Character.java`'s `useSkill()`, and `Day2Demo.java`'s `castSkill()` — run it and see "Round 2b"
and "Mage heals themself" print `[Action failed] ...` instead of crashing the program.

### Concept 8 — Generics & Collections
**What:** `Inventory<T extends Item>` (in `inventory/Inventory.java`) is a generic class — instead
of writing a separate `PotionInventory`, `ScrollInventory`, etc., one class works for any item
type, and `<T extends Item>` still guarantees the compiler that every `T` has `getName()`.
`Day2Demo.java` also uses `List<Character>` for turn order and `HashMap<String, Character>` for a
name-keyed party lookup.

**Why it matters:** Without generics, a reusable container would either need one class per item
type (duplicated code) or store plain `Object` and cast on every read (compiles fine, but a wrong
cast only blows up at runtime). `Inventory<Potion>` gets compile-time safety — the compiler itself
rejects `potions.add(someNonPotionItem)` — with zero code duplication. `List` and `HashMap` are
the standard-library generic collections doing the same job for ordering and lookup.

**Where to look:** `inventory/Inventory.java`, `inventory/Potion.java`, and the
`Inventory<Potion> potions = new Inventory<>();` / `Map<String, Character> party = new
HashMap<>();` lines in `Day2Demo.java`.

### How to compile & run
From the project root (`d:\Projects_CSE\OOP project`):
```
javac -d out src/Main.java src/Day2Demo.java src/characters/*.java src/characters/enemies/*.java src/skills/*.java src/exceptions/*.java src/inventory/*.java
java -cp out Main        # Day 1 demo — output identical to before
java -cp out Day2Demo    # Day 2 demo — skills, exceptions, inventory, collections
```

## Day 3 (done) — Design Patterns, Integration, Polish

### What exists after today
```
src/
  factory/
    HeroType.java              - enum: WARRIOR, MAGE
    EnemyType.java              - enum: GOBLIN, SKELETON
    CharacterFactory.java       - createHero(type, name) / createEnemy(type)
  observer/
    BattleObserver.java         - interface: onEvent(message)
    BattleLogger.java            - implements BattleObserver, prints "[Log] ..."
  game/
    GameManager.java            - Singleton: getInstance(), turn counter, observer registry
  ui/
    ConsoleUI.java               - ANSI health bars + boxed status panels (polish, not a pattern)
  inventory/
    Inventory.java              - (extended, see below) gained a remove(index) method
  Main.java                     - Day 1 demo, unchanged
  Day2Demo.java                 - Day 2 demo, unchanged
  Day3Demo.java                 - NEW, playable turn-based console loop tying everything together
```
As with Day 2, everything from Days 1-2 is untouched except one small additive method. `Day3Demo`
is a third, separate entry point — `Main.java` and `Day2Demo.java` still compile and run exactly
as they did before.

### The one change outside new files: `Inventory.remove(int index)`
Day 2's `Inventory<T extends Item>` only had `add`/`get`/`size`/`getAll` — nothing consumed an
item. `Day3Demo`'s playable loop needed potions to actually run out, so `remove(int index)` was
added. Every Day 2 method and behavior is unchanged; this is a new method, not a modified one.

### Concept 9 — Factory pattern
**What:** `CharacterFactory.createHero(HeroType.WARRIOR, "Conan")` and
`CharacterFactory.createEnemy(EnemyType.GOBLIN)` are the only places in `Day3Demo.java` that
mention which concrete class gets built.

**Why it matters:** Without a factory, every call site that wants a new Warrior has to say
`new Warrior(name)` directly, so the *decision* of which concrete class to instantiate is
scattered across the codebase. Centralizing it means adding a new hero type later means touching
`HeroType` + one `switch` in `CharacterFactory` — not every place a hero gets created. It also
means calling code only ever depends on the shape (`Character`) and a simple enum, not on
`Warrior`/`Mage`/`Goblin`/`Skeleton` by name.

**Where to look:** `factory/CharacterFactory.java`, and the two `CharacterFactory.create...` calls
near the top of `Day3Demo.java`'s `main()`.

### Concept 10 — Observer pattern
**What:** `GameManager` keeps a `List<BattleObserver>` and calls `onEvent(message)` on each one
via `notifyObservers()`. `BattleLogger` is the one observer registered in `Day3Demo`, and it just
prints the message with a `[Log]` prefix.

**Why it matters:** This decouples "something happened" (a defeat, a victory, the battle
starting) from "what to do about it" (print it). `GameManager` never calls `System.out.println`
itself — it has no idea what its observers do with a message. A second observer (e.g. one that
counts defeats, or writes to a file) could be added with `game.addObserver(...)` and
`GameManager` wouldn't need a single line changed. Note this is deliberately layered *alongside*
Day 1/2's existing direct `System.out.println` calls inside `attack()`/`Skill.use()` (per-move
flavor text stays as immediate feedback, unchanged) — the Observer pattern is used specifically
for game-level events (defeats, victory/defeat) raised by the new Day 3 battle loop.

**Where to look:** `observer/BattleObserver.java`, `observer/BattleLogger.java`,
`GameManager.notifyObservers()`, and the `game.notifyObservers(...)` calls scattered through
`Day3Demo.java` (e.g. inside `announceIfDefeated()`).

### Concept 11 — Singleton pattern
**What:** `GameManager`'s constructor is `private`; the only way to get one is
`GameManager.getInstance()`, which creates it once and returns that same object on every later
call.

**Why it matters:** There should only be one "current turn number" / one shared observer registry
for a running battle — two different `GameManager` objects drifting out of sync would be a bug,
not a feature. Contrast this deliberately with `CharacterFactory`, which creates a *new* object
every call: Factory and Singleton solve opposite problems ("give me a fresh one" vs. "give me the
one shared one").

**Where to look:** `game/GameManager.java` — the `private static GameManager instance`, the
`private GameManager()` constructor, and `getInstance()`'s lazy-init `if (instance == null)`.

### Concept 12 — Integration: one playable console loop
**What:** `Day3Demo.java` wires Day 1's `Character`/`Warrior`/`Mage`/`Goblin`/`Skeleton`, Day 2's
`Skill`/exceptions/`Inventory`, and Day 3's Factory/Observer/Singleton into a real `while` loop:
heroes act on player input (read via `Scanner`), enemies act via simple AI (`Random` picks attack
vs. skill, and a target), turns alternate until one whole side is defeated.

**Why it matters:** Days 1-2 were demos that ran the same scripted sequence every time. This is
the payoff of building everything as reusable, decoupled pieces: the loop itself doesn't know or
care that a Warrior swings a sword and a Mage casts Fireball (polymorphism from Day 1), doesn't
care how a skill decides what it costs (composition from Day 2), and doesn't crash on a bad
action (exceptions from Day 2) — it just calls `hero.attack(...)` / `hero.useSkill(...)` and
reacts to the result. If the Scanner runs out of input (e.g. piped input in a test run), the loop
ends gracefully instead of crashing with an unhandled exception.

**Where to look:** `Day3Demo.java`'s `main()` loop, `heroTurn()`, and `enemyTurn()`.

### Concept 13 — Polish: ANSI health bars & boxed panels (not a design pattern)
**What:** `ConsoleUI.healthBar(current, max, width)` renders a colored `[####--------] 30/100`
style bar (red/yellow/green by how full it is); `ConsoleUI.printBox(title, lines)` renders a
boxed status panel. Both are plain static helper methods with ANSI escape codes, not tied to any
other class.

**Why it matters:** This is explicitly *not* a GoF pattern — it's listed separately in the plan as
a polish pass. It's included here because it surfaces a real, easy-to-miss bug class: ANSI escape
codes are invisible on screen but still count as characters in the raw `String`. `printBox()`
originally measured line width including those invisible codes, which misaligned the box's right
border on any colored line. The fix (`stripAnsi()`/`visibleLength()`) measures and pads based on
*visible* width only — worth knowing any time you mix formatting codes with fixed-width text
layout, in any language.

**Where to look:** `ui/ConsoleUI.java` — `healthBar()`, and `printBox()`'s `visibleLength()` /
`stripAnsi()` helpers.

### How to compile & run
From the project root (`d:\Projects_CSE\OOP project`):
```
javac -d out src/Main.java src/Day2Demo.java src/Day3Demo.java src/characters/*.java src/characters/enemies/*.java src/skills/*.java src/exceptions/*.java src/inventory/*.java src/factory/*.java src/observer/*.java src/game/*.java src/ui/*.java
java -cp out Main        # Day 1 demo — unchanged
java -cp out Day2Demo    # Day 2 demo — unchanged
java -cp out Day3Demo    # Day 3 — actually playable; type a number + Enter each turn
```

### Optional stretch (not implemented)
A JavaFX front-end reusing the same model classes was scoped as an optional stretch *after* the
3-day plan, not part of Day 3 itself — skipped here to stay within the compressed 3-day scope.
