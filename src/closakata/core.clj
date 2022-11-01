(ns closakata.core
  (:require [clojure.string :as str]
            [closakata.helper :as h])
  (:gen-class))

(def max-length 8)
(def min-length 4)

(defonce history (atom '[]))
(defonce valid-keys (atom '[]))
(def secret (atom nil))
(defonce win (atom false))

;; word dictionary
(def get-words
  (-> (slurp "resources/daftar-kata-bahasa-indonesia.txt")
      str/split-lines))


(defn game-words
  "This function extracts all qualified words from the dictionary"
  [words]
  (filter #(and
            (>= (count %) min-length) ; word should be geq than min-length
            (<= (count %) max-length) ; word should be leq than max-length
            (not (str/includes? % "-"))) ; word should not contain - character, which is normal in indonesian
          words))

;; uppercased qualified words
(def all-words (map str/upper-case (game-words get-words)))

(defn random-word
  "Pick a random word from qualified words"
  []
  (rand-nth all-words))

(defn print-with-color
  "Print characters with color"
  [c]
  (cond
    (:just c) (print (format "%s%s%s" (:green h/console-fg-color) (:char c) (:reset h/console-fg-color)))
    (:almost c) (print (format "%s%s%s" (:yellow h/console-fg-color) (:char c) (:reset h/console-fg-color)))
    (:used c) (print (format "%s%s%s" (:grey h/console-fg-color) (:char c) (:reset h/console-fg-color)))
    :else (print (format "%s" (:char c)))))

(defn print-keyboard
  "Print valid keys which formatted by order"
  [valid-keys]
  (let [print-seq
        (fn [seq]
          (dorun (for [c seq]
                   (print-with-color
                    (first (filter #(= (:char %) c) valid-keys))))))]
    (print-seq '[\Q \W \E \R \T \Y \U \I \O \P])
    (println)
    (print " ")
    (print-seq '[\A \S \D \F \G \H \J \K \L])
    (println)
    (print "  ")
    (print-seq '[\Z \X \C \V \B \N \M])
    (println)))

(defn print-history
  "print history with color"
  [history]
  (let [print-seq
        (fn [seq]
          (dorun (for [c seq]
                   (print-with-color c))))]
    (dorun
     (map #(do
             (print-seq %)
             (println))
          history))))


(defn check-guess
  "This function validates and checks the user's guess."
  [line]
  (cond
    ;; check the guess word length and whether it is a recognized word
    (< (count line) min-length) (println "Kata pilihan terlalu pendek. Coba lagi")
    (> (count line) max-length) (println "Kata pilihan terlalu panjang. Coba lagi")
    (not (h/word-valid? line all-words)) (println "Kata pilihan tidak dikenali. Coba lagi")

    ;; if valid and recognized
    :else
    (do
      (swap! history conj (h/score line @secret)) ; compare guess to secret and put guess in history
      (reset! valid-keys (h/available-keys @history)) ; compute un-guessed characters based on the history
      (print-keyboard @valid-keys)
      (println)
      (print-history @history)
      (if (= line @secret) ; if guess = secret
        (do
          (reset! win true)
          (println "Kamu menang!!!!"))))))

(defn play-again?
  "This function prompts the user for a new session
  return true as long as the user's answer is either y or Y"
  []
  (print "Main lagi? y/n: ")
  (flush)
  (let [respon (read-line)]
    (or (= respon "y") (= respon "Y"))))


(defn has-chance?
  "This function checks if the user still has chance to guess"
  []
  (and (< (count @history) (dec (count @secret))) (not @win)))

(defn game
  "game's backbone"
  []
  (reset! history '[])
  (reset! valid-keys '[])
  (reset! secret (random-word)) ; pick a different secret word for every game session
  (def secret-length (count @secret))

  (println "Panjang karakter kata rahasia:" secret-length)
  (println (str "Kamu punya " (dec secret-length) " kesempatan untuk menebak."))

  (while (has-chance?)
    (println "###############################################")
    (print (format "Percobaan ke-%d> " (inc (count @history))))
    (flush)
    (check-guess (str/upper-case (read-line)))) ; check the input, is it valid? is it correct?

  (when (not @win) (println "Kamu kalah!!!"))
  (println "Jawabannya adalah" @secret)

  (when (play-again?)
    (reset! win false) ; if the user want to play again, set win to false
    (recur)))

(defn -main
  "Main entry"
  [& args]
  (println "--- Selamat datang di Closakata ---")
  (println (str "Aku akan memilih kata dalam Bahasa Indonesia yang panjangnya " min-length " sampai " max-length " karakter."))
  (println "Kamu punya n-1 kesempatan untuk menebak, yang mana n adalah panjang kata rahasia.")
  (println "Tebak kata rahasia yang aku punya sekarang!")
  (println)
  (println "Keterangan:\nBenar=Hijau Hampir=Kuning Terpakai=Abu-abu")
  (println "----------------------------------")
  (game)
  (println "Sampai ketemu lagi!."))
