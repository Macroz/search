(ns search.core
  (:use [clojure.data.priority-map]))

(defn a*
  "Calculate shortest path with A*.

  Note the heuristic function must be monotonous, increasing and never overestimate
  (i.e. it must be admissible) to guarantee an optimal result.

  start is the first node.
  goal?-fn is called for each expanded node to check whether it is a goal
  distance-fn is called with from and to nodes and should return a numeric distance.
  heuristic-fn is called with a node and should return a numeric estimated distance to the nearest goal.
  neighbors-fn is called with the node and should return all the neighbors
  max-depth is the maximum depth of the path in steps."
  [start goal?-fn distance-fn heuristic-fn neighbors-fn max-depth]
  (loop [open (priority-map [start [start] 0] (heuristic-fn start))
         depth 0]
    (let [[[node path distance] total] (first open)]
      (cond (> depth max-depth) false
            (goal?-fn node) path
            :else (recur (into (pop open)
                               (for [neighbor (neighbors-fn node)]
                                 (let [new-node neighbor
                                       new-path (conj path neighbor)
                                       new-distance (+ distance (distance-fn node neighbor))]
                                   [[new-node new-path new-distance]
                                    (+ new-distance (heuristic-fn neighbor))])))
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

(defn- ida
  "Helper function for IDA* search."
  [node path distance max-distance goal?-fn distance-fn heuristic-fn neighbors-fn max-depth]
  (let [new-distance (+ distance (heuristic-fn node))]
    (cond (> new-distance max-distance) new-distance
          (> (count path) max-depth) false
          (goal?-fn node) path
          :else (loop [neighbors (neighbors-fn node)
                       min-distance false]
                  (if (empty? neighbors)
                    min-distance
                    (let [neighbor (first neighbors)
                          result (ida neighbor (conj path neighbor) (+ distance (distance-fn node neighbor)) max-distance goal?-fn distance-fn heuristic-fn neighbors-fn max-depth)]
                      (cond (not result) result
                            (vector? result) result
                            (or (not min-distance)
                                (< result min-distance)) (recur (rest neighbors) result)
                                :else (recur (rest neighbors) min-distance))))))))

(defn ida*
  "Calculate shortest path using IDA*.

  Note the heuristic function must be monotonous, increasing and never overestimate
  (i.e. it must be admissible) to guarantee an optimal result.

  start is the first node.
  goal?-fn is called for each expanded node to check whether it is a goal
  distance-fn is called with from and to nodes and should return a numeric distance.
  heuristic-fn is called with a node and should return a numeric estimated distance to the nearest goal.
  neighbors-fn is called with the node and should return all the neighbors
  max-depth is the maximum depth of the path in steps."
  [start goal?-fn distance-fn heuristic-fn neighbors-fn max-depth]
  (loop [max-distance 1]
    (let [result (ida start [start] 0 max-distance goal?-fn distance-fn heuristic-fn neighbors-fn max-depth)]
      (cond (not result) result
            (vector? result) result
            :else (recur result)))))
