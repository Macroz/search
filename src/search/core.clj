(ns search.core
  (:use [clojure.data.priority-map]))

(defn a*
  "Calculate shortest path with A*.

  Note the heuristic function must be monotonous, increasing and never overestimate 
  (i.e. it must be admissible) to guarantee an optimal result.

  start is the first node.
  goal?-fn is called for each expanded node to check whether it is a goal
  distance-fn is called with from and to nodes and should return a numeric distance.
  heuristic-fn is called with a node should return a numeric estimated distance to the nearest goal.
  neighbors-fn is called with the node and should return all the neighbors
  max-depth is the maximum depth of the path in steps."
  [start goal?-fn distance-fn heuristic-fn neighbors-fn max-depth]
  (loop [open (priority-map [start [start]] 0)
         depth 0]
    (let [[[node path] distance] (first open)]
      (cond (> depth max-depth) false
            (goal?-fn node) path
            :else (recur (into (pop open)
                               (for [neighbor (neighbors-fn node)]
                                 [[neighbor
                                   (conj path neighbor)] (+ distance (distance-fn node neighbor) (heuristic-fn neighbor))]))
                         (count path))))))

(defn dijkstra
  "Calculate shortest path with Dijkstra's algorithm.

  start is the first node.
  goal?-fn is called for each expanded node to check whether it is a goal
  distance-fn is called with from and to nodes and should return a numeric distance.
  neighbors-fn is called with the node and should return all the neighbors
  max-depth is the maximum depth of the path in steps."
  [start goal?-fn distance-fn neighbors-fn max-depth]
  ;; Dijkstra is a special case of A* when the heuristic is zero
  (a* start goal?-fn distance-fn (constantly 0) neighbors-fn max-depth))
