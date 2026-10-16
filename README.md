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
