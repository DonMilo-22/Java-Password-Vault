public final class PasswordStrength {
    private PasswordStrength() {}
    public static String assess(String password) {
        if (password == null || password.isEmpty()) return "Empty";
        int score = 0;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(c -> !Character.isLetterOrDigit(c))) score++;
        if (password.length() < 8) return "Very weak";
        if (score <= 2) return "Weak";
        if (score <= 4) return "Moderate";
        return "Stronger";
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java PasswordStrength <password>");
            System.exit(2);
        }
        System.out.println("Estimated strength: " + assess(args[0]));
        System.out.println("Basic estimate only, not a security guarantee.");
    }
}
