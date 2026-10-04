# 🔐 Java Password Vault

> Local encrypted credential vault built with Java.

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk&logoColor=white)
![AES](https://img.shields.io/badge/encryption-AES--GCM-blue)
![License](https://img.shields.io/badge/license-MIT-green)

A small terminal password vault that encrypts credentials before writing them to disk. It uses a master password to derive an encryption key.

## ✨ Features

- Add credentials locally
- List saved service names and usernames
- Reveal one credential on demand
- AES-GCM authenticated encryption
- PBKDF2 key derivation
- No cloud service or external dependency

## 🚀 Run

```bash
javac -d out src/Vault.java
java -cp out Vault
```

The app asks for a master password and presents an interactive menu.

## 🔒 Security model

The vault file is encrypted using AES-GCM. The key is derived from the master password using PBKDF2 with a random salt.

> This project is educational. For high-value real-world credentials, use a professionally audited password manager with secure backup and recovery features.

## 🧠 What it demonstrates

Java cryptography APIs, binary file I/O, records, collections and interactive console applications.

## 📄 License

MIT.

## 🆕 Recent changes

### 2026-10-04

- Added a menu option to search encrypted vault entries by service name without revealing passwords.

### Previous update

- Added an encrypted-vault delete option so individual saved credentials can be removed.
