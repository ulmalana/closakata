;;;; source
;;;; https://kozieiev.com/blog/packaging-clojure-into-jar-uberjar-with-tools-build/

(ns build
  (:require [clojure.tools.build.api :as b]))

(def build-folder "jar")
(def jar-content (str build-folder "/classes"))

(def basis (b/create-basis {:project "deps.edn"}))
(def version "0.1.0")
(def app-name "closakata")
(def uber-file-name (format "%s/%s-%s.jar" build-folder app-name version))

(defn clean [_]
  (b/delete {:path build-folder})
  (println (format "Build folder \"%s\" removed" build-folder)))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["resources"]
               :target-dir jar-content})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir jar-content})
  (b/uber {:class-dir jar-content
           :uber-file uber-file-name
           :basis basis
           :main 'closakata.core})
  (println (format "Uber file created: \"%s\"" uber-file-name)))
