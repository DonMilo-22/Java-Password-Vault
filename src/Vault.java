import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

public class Vault {
  static final Path FILE = Path.of("vault.dat");
  static final SecureRandom RNG = new SecureRandom();
  record Entry(String service, String username, String password) {}

  static SecretKey derive(char[] password, byte[] salt) throws Exception {
    var spec = new PBEKeySpec(password, salt, 120000, 256);
    byte[] key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    return new SecretKeySpec(key, "AES");
  }

  static byte[] encrypt(String plain, char[] master) throws Exception {
    byte[] salt = new byte[16], iv = new byte[12]; RNG.nextBytes(salt); RNG.nextBytes(iv);
    Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
    c.init(Cipher.ENCRYPT_MODE, derive(master, salt), new GCMParameterSpec(128, iv));
    byte[] enc = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
    var out = new ByteArrayOutputStream(); out.write(salt); out.write(iv); out.write(enc); return out.toByteArray();
  }

  static String decrypt(byte[] blob, char[] master) throws Exception {
    byte[] salt = Arrays.copyOfRange(blob,0,16), iv = Arrays.copyOfRange(blob,16,28), enc = Arrays.copyOfRange(blob,28,blob.length);
    Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
    c.init(Cipher.DECRYPT_MODE, derive(master,salt), new GCMParameterSpec(128,iv));
    return new String(c.doFinal(enc), StandardCharsets.UTF_8);
  }

  static List<Entry> load(char[] master) throws Exception {
    if (!Files.exists(FILE)) return new ArrayList<>();
    String text = decrypt(Files.readAllBytes(FILE), master);
    var list = new ArrayList<Entry>();
    for (String line : text.split("\\R")) {
      if (line.isBlank()) continue;
      String[] p = line.split("\\t",3); if (p.length==3) list.add(new Entry(p[0],p[1],p[2]));
    }
    return list;
  }

  static void save(List<Entry> entries, char[] master) throws Exception {
    var text = new StringBuilder();
    for (Entry e: entries) text.append(e.service()).append("\t").append(e.username()).append("\t").append(e.password()).append("\n");
    Files.write(FILE, encrypt(text.toString(), master));
  }

  public static void main(String[] args) throws Exception {
    Console console = System.console();
    if (console == null) { System.err.println("Run this app from a real terminal."); return; }
    char[] master = console.readPassword("Master password: ");
    List<Entry> entries;
    try { entries = load(master); } catch (Exception e) { System.err.println("Could not unlock vault. Wrong password or damaged file."); return; }
    Scanner sc = new Scanner(System.in);
    while (true) {
      System.out.println("\n1) List  2) Add  3) Reveal  4) Delete  5) Search  0) Exit");
      String choice = sc.nextLine().trim();
      if (choice.equals("0")) break;
      if (choice.equals("1")) {
        if (entries.isEmpty()) System.out.println("Vault is empty.");
        for (int i=0;i<entries.size();i++) System.out.printf("%d. %s (%s)%n", i+1, entries.get(i).service(), entries.get(i).username());
      } else if (choice.equals("2")) {
        System.out.print("Service: "); String service=sc.nextLine();
        System.out.print("Username: "); String user=sc.nextLine();
        boolean duplicate=entries.stream().anyMatch(e -> e.service().equalsIgnoreCase(service) && e.username().equalsIgnoreCase(user));
        if (duplicate) System.out.println("Warning: an entry for this service and username already exists.");
        char[] pw=console.readPassword("Password: ");
        String rawPw=new String(pw);
        int score=0;
        if (rawPw.length() >= 12) score++;
        if (rawPw.matches(".*[A-Z].*") && rawPw.matches(".*[a-z].*")) score++;
        if (rawPw.matches(".*\\d.*") && rawPw.matches(".*[^A-Za-z0-9].*")) score++;
        System.out.println("Password strength: "+(score == 3 ? "strong" : score == 2 ? "medium" : "weak"));
        entries.add(new Entry(service,user,rawPw); Arrays.fill(pw,'\0'); save(entries,master); System.out.println("Saved.");
      } else if (choice.equals("3")) {
        System.out.print("Entry number: ");
        try { int i=Integer.parseInt(sc.nextLine())-1; Entry e=entries.get(i); System.out.println(e.service()+" -> "+e.username()+" / "+e.password()); }
        catch(Exception e){ System.out.println("Invalid entry."); }
      } else if (choice.equals("4")) {
        System.out.print("Entry number to delete: ");
        try {
          int i=Integer.parseInt(sc.nextLine())-1;
          Entry selected=entries.get(i);
          System.out.print("Delete "+selected.service()+"? Type yes to confirm: ");
          if (!sc.nextLine().trim().equalsIgnoreCase("yes")) { System.out.println("Cancelled."); continue; }
          entries.remove(i);
          save(entries,master);
          System.out.println("Deleted: "+selected.service());
        } catch(Exception e){ System.out.println("Invalid entry."); }
      } else if (choice.equals("5")) {
        System.out.print("Service search: "); String query=sc.nextLine().trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) { System.out.println("Enter a service name."); continue; }
        for (int i=0;i<entries.size();i++) {
          Entry e=entries.get(i);
          if (e.service().toLowerCase(Locale.ROOT).contains(query))
            System.out.printf("%d. %s (%s)%n",i+1,e.service(),e.username());
        }
      }
    }
    Arrays.fill(master,'\0');
  }
}
