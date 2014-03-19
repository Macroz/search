(ns search.core-test
  (:use clojure.test
        search.core))

;;    a - 2 - b - 3 - c - 1 - j
;;    |       |       |       |
;;    1       2       1       |
;;    |       |       |       |
;;    d - 1 - e - 1 - f       1
;;    |               |       |
;;    2               4       |
;;    |               |       |
;;    g - 1 - h - 2 - i - 2 - k

(def paths-in-one-direction
  {"ab" 2
   "bc" 3
   "cj" 1
   "ad" 1
   "dg" 2
   "de" 1
   "be" 2
   "ef" 1
   "cf" 1
   "gh" 1
   "hi" 2
   "fi" 4
   "ik" 2
   "jk" 1})

(defn reverse-path [[k v]] [(apply str (reverse k)) v])
(def paths (into paths-in-one-direction (map reverse-path paths-in-one-direction)))
(def neighbors (apply merge-with concat
                      (map (fn [[[from to] distance]]
                             {(str from) [(str to)]})
                           paths)))
(defn goal= [n] (partial = n))
(def distance (comp paths str))

(deftest dijkstra-tests
  (is (= ["a" "b"] (dijkstra "a" (goal= "b") distance neighbors 20)))
  (is (= ["b" "a"] (dijkstra "b" (goal= "a") distance neighbors 20)))
  (is (= ["a" "d" "e" "f" "c" "j" "k"] (dijkstra "a" (goal= "k") distance neighbors 20)))
  (is (= (reverse ["a" "d" "e" "f" "c" "j" "k"]) (dijkstra "k" (goal= "a") distance neighbors 20))))