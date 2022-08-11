(ns closakata.core
  (:require [clojure.string :as str])
  (:gen-class))

(def max-length 8)
(def min-length 4)

(defn get-words
  []
  (-> (slurp "resources/daftar-kata.txt")
      str/split-lines))

(defn game-words
  [words]
  (filter #(and
            (>= (count %) min-length)
            (<= (count %) max-length))
          words))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (game))
