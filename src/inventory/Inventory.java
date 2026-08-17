package inventory;

import java.util.ArrayList;
import java.util.List;

/*
 * THEORY - Generics:
 * "<T extends Item>" makes Inventory reusable for ANY item type, while "extends Item" still
 * guarantees every T has getName() to call. Without generics we'd either write a separate
 * PotionInventory/ScrollInventory class per item type (duplication), or store plain Object and
 * cast everywhere (loses compile-time type safety — a cast mistake would only blow up at
 * runtime). With Inventory<Potion>, the compiler itself rejects trying to add() a non-Potion.
 */
public class Inventory<T extends Item> {

    private final List<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(item);
    }

    public T get(int index) {
        return items.get(index);
    }

    // Day 3 addition: Day3Demo's playable loop needs potions to actually get used up.
    public T remove(int index) {
        return items.remove(index);
    }

    public int size() {
        return items.size();
    }

    public List<T> getAll() {
        return items;
    }
}
