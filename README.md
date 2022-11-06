# Closakata

Closakata adalah permainan kata Bahasa Indonesia yang terinspirasi dari Katla dan Wordle. Permainan ini berbasis CLI dan ditulis menggunakan bahasa pemrograman Clojure. Kamu harus menebak kata acak yang panjangnya 4 sampai 8 karakter dengan kesempatan menebak n+1, di mana n adalah panjangnya karakter. Misalkan kata acak yang terpilih "Januari" (7 karakter), maka kamu punya 8 kesempatan menebak.

Closakata is inspired by Katla, which is Indonesian version of Wordle. This CLI-based game is written in Clojure. You have to guess a random word that is 4 to 8 characters long. You have n+1 chance of guessing, where n is the character length. For example, if the chosen random word is "January" (7 characters), then you have 8 chances to guess.

## Cara main / How to play
1. Dengan `lein`
   Pastikan [`lein`](https://leiningen.org/) sudah terinstal di komputermu.
   - Kloning repo ini: `git clone https://github.com/ulmalana/closakata`
   - Masuk ke direktori `closakata`: `$ cd closakata`
   - Jalankan `lein`: `$ lein run`
   
2. Dengan `java`
   Pastikan di komputermu sudah terinstal JDK. Cara cek: `$ java -version`
   - Kloning repo ini: `git clone https://github.com/ulmalana/closakata`
   - Masuk ke direktori `closakata`: `$ cd closakata`
   - Eksekusi file JAR: `$ java -jar jar/closakata-0.1.0.jar`
