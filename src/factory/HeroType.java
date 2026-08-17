package factory;

// A closed set of hero kinds the factory knows how to build. Using an enum instead of a raw
// String means a typo like "Warroir" is a compile error, not a runtime surprise.
public enum HeroType {
    WARRIOR,
    MAGE
}
