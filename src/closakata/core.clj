(ns closakata.core
  (:require [clojure.string :as str]
            [closakata.helper :as h])
  (:gen-class))

;; TODO: histories are printed using the length of chosen word.
;; if the chosen word is adan, then adalah wil be adal.

(def max-length 8)
(def min-length 4)

(def console-fg-color
  {
   :grey    "\u001b[90m"
   :black   "\u001b[30m"
   :red     "\u001b[31m"
   :green   "\u001b[32m"
   :yellow  "\u001b[33m"
   :blue    "\u001b[34m"
   :magenta "\u001b[35m"
   :cyan    "\u001b[36m"
   :white   "\u001b[37m"
   :reset   "\u001b[0m"
   })

(def console-bg-color
  {
   :grey    "\u001b[100m"
   :black   "\u001b[40m"
   :red     "\u001b[41m"
   :green   "\u001b[42m"
   :yellow  "\u001b[43m"
   :blue    "\u001b[44m"
   :magenta "\u001b[45m"
   :cyan    "\u001b[46m"
   :white   "\u001b[47m"
   :reset   "\u001b[49m"
   })


(defn get-words
  []
  (-> (slurp "resources/daftar-kata.txt")
      str/split-lines))

(defn game-words
  [words]
  (filter #(and
            (>= (count %) min-length)
            (<= (count %) max-length)
            (not (str/includes? % "-")))
          words))

(def all-words (map str/upper-case (game-words (get-words))))
(def random-word (rand-nth all-words))

(defn print-with-color
  "Print characters with color"
  [c]
  (cond
    (:just c) (print (format "%s%s%s" (:green console-fg-color) (:char c) (:reset console-fg-color)))
    (:almost c) (print (format "%s%s%s" (:yellow console-fg-color) (:char c) (:reset console-fg-color)))
    (:used c) (print (format "%s%s%s" (:grey console-fg-color) (:char c) (:reset console-fg-color)))
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
    (println)
    ))

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

(defonce history (atom '[]))
(defonce valid-keys (atom '[]))
(defonce secret (atom nil))
(defonce win (atom false))

(defn judge
  "judge the input"
  [line]
  (cond
    (< (count line) min-length) (println "Kata pilihan terlalu pendek. Coba lagi")
    (> (count line) max-length) (println "Kata pilihan terlalu panjang. Coba lagi")
    (not (h/word-valid? line all-words)) (println "Kata pilihan tidak dikenali. Coba lagi")
    :else
    (do
      (swap! history conj (h/score line @secret))
      (reset! valid-keys (h/available-keys @history))
      (print-keyboard @valid-keys)
      (println)
      (print-history @history)
      (if (= line @secret)
        (do
          (reset! win true)
          (println "Kamu menang!!!!"))))))

(defn game-continue?
  []
  (and (< (count @history) 6) (not @win)))

(defn game-cli
  []
  (reset! history '[])
  (reset! valid-keys '[])
  (reset! secret random-word)

  (println "--- Selamat datang di Closakata ---")
  (println "Tebak kata rahasia yang saya punya sekarang!")
  (println "Keterangan:\nBenar=Hijau Hampir=Kuning Terpakai=Abu-abu")
  (println "Panjang karakter kata rahasia:" (count @secret))
  (println "Kamu punya 6 kesempatan untuk menebak.")

  (while (game-continue?)
    (println "###############################################")
    (print (format "Percobaan ke-%d> " (inc (count @history))))
    (flush)
    (judge (str/upper-case (read-line))))

  (if (not @win) (println "Kamu kalah!!!"))
  (println "Jawabannya adalah" @secret))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (game-cli))
