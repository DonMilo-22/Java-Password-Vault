import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

public class VaultBackup {
    public static void main(String[] args) {
        Path source = Path.of(args.length > 0 ? args[0] : "vault.dat").toAbsolutePath();
        if (!Files.isRegularFile(source)) {
            System.err.println("Vault file not found: " + source);
            System.exit(1);
        }
        Path folder = source.getParent().resolve("vault-backups");
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path destination = folder.resolve("vault-" + stamp + ".dat");
        try {
            Files.createDirectories(folder);
            Files.copy(source, destination);
            System.out.println("Encrypted vault backup created: " + destination);
            try (Stream<Path> backups = Files.list(folder)) {
                long count = backups.filter(Files::isRegularFile).count();
                System.out.println("Backups in folder: " + count);
            }
        } catch (IOException error) {
            System.err.println("Backup failed: " + error.getMessage());
            System.exit(1);
        }
    }
}
