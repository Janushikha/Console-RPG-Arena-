# Console RPG Arena

A turn-based, console-only RPG battle game written in Java — built as a 2-week
learning project to practice core object-oriented programming concepts and classic
design patterns by hand, without a framework hiding the mechanics.

## Concept

Build a small party of hero classes, fight a sequence of enemies with different
behaviors, use skills and items, and watch a live battle log — all through a text
menu in the terminal (`Scanner` for input, `System.out` for output).

## OOP Concepts Covered

| Concept | Where it shows up |
|---|---|
| Encapsulation | Private fields on `Character` (`health`, `mana`, `attackPower`) exposed only via getters/validated setters |
| Abstraction | `Character` / `Enemy` are abstract classes with abstract methods subclasses must implement |
| Inheritance | `Warrior`, `Mage`, `Archer`, `Healer` extend `Character`; `Goblin`, `Skeleton`, `Dragon` extend `Enemy` |
| Polymorphism | A `List<Character>` of mixed subclasses calling `attack()` triggers different overridden behavior per type |
| Interfaces & composition | `Skill` interface implemented by `Fireball`, `Heal`, etc.; each `Character` *has-a* `List<Skill>` |
| Exception handling | Custom exceptions (`InvalidTargetException`, `OutOfManaException`, `CharacterDeadException`) validate illegal actions |
| Generics & collections | Generic `Inventory<T extends Item>`; `ArrayList`/`HashMap` for party members and turn order |
| Design patterns | Factory (`CharacterFactory`), Strategy (`Skill` implementations), Observer (`BattleLogger`), Singleton (`GameManager`) |

## Project Structure

```
src/
  characters/
    Character.java        (abstract base)
    Warrior.java, Mage.java, Archer.java, Healer.java
    enemies/
      Enemy.java           (abstract)
      Goblin.java, Skeleton.java, Dragon.java
  skills/
    Skill.java             (interface)
    Slash.java, Fireball.java, Heal.java, PowerStrike.java
  items/
    Item.java               (abstract)
    Weapon.java, Armor.java, Potion.java
    Inventory.java           (generic container)
  exceptions/
    InvalidTargetException.java
    OutOfManaException.java
    CharacterDeadException.java
  engine/
    CharacterFactory.java   (Factory pattern)
    BattleLogger.java       (Observer pattern)
    GameManager.java        (Singleton pattern)
  Main.java
```

## Tools

Java, JDK, ANSI escape sequences (for colored/formatted console output).

## How to Run

```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

## Roadmap

- [ ] Week 1: `Character` hierarchy, inheritance, polymorphism, `Skill` interface, basic turn loop
- [ ] Week 2: Exceptions, generics/collections, Factory, Observer, Singleton, full integration
- [ ] Day 14 polish: ANSI-colored output, formatted menus, text health bars
- [ ] Stretch goal: JavaFX front-end reusing the same model classes

## Status

In progress — 2-week build.


