(ns closakata.helper
  (:gen-class))

(defn score
  "score guess word"
  [guess secret]
  (let [just (map #(= %1 %2) guess secret)
        almost (map #(and (false? %1) (true? %2))
                    just
                    (map #(some (partial = %1) secret) guess))]
    (map #(array-map :char %1 :just %2 :almost %3)
         guess just almost)))

(defn word-valid?
  "validate word"
  [word words]
  (some (partial = word) words))

(defn available-keys
  "return available keys"
  [history]
  (let [hist (group-by :char (flatten history))
        keys (map char (range (int \a) (inc (int \z))))]
    (map #(cond
            (some :just (hist %)) (array-map :char % :just true :almost false :used false)
            (some :almost (hist %)) (array-map :char % :just false :almost true :used false)
            (some :char (hist %)) (array-map :char % :just false :almost false :used true)
            :else (array-map :char % :just false :almost false :used false))
         keys)))
