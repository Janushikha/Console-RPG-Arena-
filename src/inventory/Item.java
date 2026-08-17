package inventory;

// A minimal capability every item needs. Kept as an interface (not a class) since items may
// otherwise have nothing in common — same "can-do, not is-a" idea as skills.Skill.
public interface Item {
    String getName();
}
