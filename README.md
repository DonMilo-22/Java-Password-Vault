# Java Password Vault

Bóveda educativa de credenciales locales, desarrollada con Java.

Cifra el archivo con AES-GCM y deriva la clave desde una contraseña maestra mediante PBKDF2.

## Funciones actuales

- Agregar, listar, buscar, revelar, actualizar y eliminar credenciales.
- Confirmación de borrado y aviso de entradas repetidas.
- Indicador simple de longitud/variedad de la contraseña al agregarla.
- Estadísticas de entradas y servicios únicos sin revelar contraseñas.
- Archivo cifrado sin servicios externos.

## Requisitos

JDK 17 o superior y una terminal interactiva real. La aplicación usa `System.console()`; ejecutar desde una consola de IDE sin ese soporte o con entrada redirigida no funciona.

## Instalación y uso

```bash
git clone https://github.com/DonMilo-22/Java-Password-Vault.git
cd Java-Password-Vault
javac -d out src/Vault.java
java -cp out Vault
```

La aplicación solicita la contraseña maestra y muestra un menú.

## Menú

| Opción | Acción |
|---|---|
| `1` | Listar servicios y usuarios. |
| `2` | Agregar credencial. |
| `3` | Revelar una credencial por número. |
| `4` | Eliminar; requiere escribir `yes`. |
| `5` | Buscar por servicio. |
| `6` | Actualizar la contraseña de una entrada. |
| `7` | Consultar estadísticas. |
| `8` | Mostrar entradas ordenadas por nombre del servicio, sin revelar contraseñas. |
| `0` | Salir. |

## Almacenamiento y seguridad

`vault.dat` se guarda en el directorio de trabajo. Cada guardado genera una sal aleatoria de 16 bytes y un nonce de 12 bytes. La derivación usa PBKDF2-HMAC-SHA256 con 120 000 iteraciones y clave de 256 bits; AES-GCM utiliza una etiqueta de 128 bits.

El proyecto es educativo y no ha sido auditado profesionalmente. No incorpora recuperación de contraseña maestra ni copias automáticas. El indicador de fortaleza es una heurística simple. Usa un gestor auditado para credenciales reales de alto valor.

## Comprobaciones

`javac -d out src/Vault.java` comprueba la compilación. Para una revisión manual, utiliza credenciales ficticias, guarda, cierra y vuelve a abrir la misma bóveda desde la misma carpeta.

## Autor y licencia

Desarrollado por [DonMilo-22](https://github.com/DonMilo-22).

Licencia: MIT.

## Actualización 2026-10-12

- Vault search now matches both service names and usernames.

## Actualización adicional 2026-10-12

- Searches now show a clear message when no saved service or username matches the query.

## Historial

Consulta [CHANGELOG.md](CHANGELOG.md) para ver las actualizaciones.

## Actualización 2026-10-14

- Search now reports the number of matching saved entries.

## Actualización 2026-10-15

- Vault statistics now count stored passwords shorter than 12 characters, without displaying the passwords themselves.


## Actualización 2026-10-16

- Added menu option 8 to display saved entries alphabetically by service name without exposing their passwords or changing the stored order.


## New utility: encrypted vault backup

Create a timestamped copy of the encrypted vault file before testing changes or moving the project:

```bash
javac -d out src/VaultBackup.java
java -cp out VaultBackup
# Or provide a vault file in another location:
java -cp out VaultBackup /path/to/vault.dat
```

The helper copies the encrypted file as-is into `vault-backups/`; it never asks for or prints the master password. Keep backup files protected like the original vault.


## New utility: basic password strength estimate

Compile and run the standalone estimator:

```bash
javac -d out src/PasswordStrength.java
java -cp out PasswordStrength 'Example-password-123'
```

It checks length and character variety and prints a rough category. This is educational guidance only, not a replacement for a password-strength library. Avoid using real passwords as command-line arguments on shared systems.
