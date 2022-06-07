package poll;

public enum ReactionNumbers {
    ZERO ("0️⃣"),
    ONE ("1️⃣"),
    TWO ("2️⃣"),
    THREE ("3️⃣"),
    FOUR ("4️⃣"),
    FIVE ("5️⃣"),
    SIX ("6️⃣"),
    SEVEN ("7️⃣"),
    EIGHT ("8️⃣"),
    NINE ("9️⃣"),
    TEN ("\uD83D\uDD1F");

    private final String reaction;

    private ReactionNumbers(String reaction) {
        this.reaction = reaction;
    }
}
