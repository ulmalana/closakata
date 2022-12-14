(ns closakata.helper
  (:gen-class))

;; for coloring the text
(def console-fg-color
  {
   :grey    "\u001b[90m"
   :black   "\u001b[30m"
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


(defn play-again?
  "This function prompts the user for a new session
  return true as long as the user's answer is either y or Y"
  []
  (print "Main lagi? y/n: ")
  (flush)
  (let [respon (read-line)]
    (or (= respon "y") (= respon "Y"))))

(defn score
  "This function compare guess and secret"
  [guess secret]
  (let [just (map #(= %1 %2) guess secret)
        almost (map #(and (false? %1) (true? %2))
                    just
                    (map #(some (partial = %1) secret) guess))]
    (map #(array-map :char %1 :just %2 :almost %3)
         guess just almost)))

(defn word-valid?
  "This function checks if the guess is in the dictionary"
  [word words]
  (some (partial = word) words))

(defn available-keys
  "This function checks if the chars have been used based on the history"
  [history]
  (let [hist (group-by :char (flatten history))
        keys (map char (range (int \A) (inc (int \Z))))]
    (map #(cond
            (some :just (hist %)) (array-map :char % :just true :almost false :used false)
            (some :almost (hist %)) (array-map :char % :just false :almost true :used false)
            (some :char (hist %)) (array-map :char % :just false :almost false :used true)
            :else (array-map :char % :just false :almost false :used false))
         keys)))
