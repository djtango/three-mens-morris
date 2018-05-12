(ns three-mens-morris.core
  (:require [clojure.spec.alpha :as s]
            [three-mens-morris.core-spec :as specs]))

(s/fdef place-piece
        :args (s/cat :board :board/board,
                     :position :board/position,
                     :piece :point/value)
        :ret :board/board
        :fn #(= (get-in (:ret %) [(-> % :args :position) :point/value])
                (-> % :args :piece)))

(defn place-piece [board position piece]
  (assoc-in board [position :point/value] piece))

(s/fdef three-in-a-row?
        :args (s/cat :board :board/board)
        :ret boolean?)

(s/fdef same-colour?
        :args (s/cat :points (s/coll-of :point/value))
        :ret boolean?)

(defn same-colour? [points]
  (let [all-same? (fn [colour] (apply = colour points))]
    (or (all-same? :white)
        (all-same? :black))))

(defn group-horizontally [board]
  (partition 3 board))

(defn three-in-a-row? [board]
  (->> board
       (map :point/value)
       group-horizontally
       (map same-colour?)
       (some true?)))

(defn three-in-a-column? [board]
  (let [transpose (partial apply map vector)]
    (->> board
         (map :point/value)
         group-horizontally
         transpose
         (map same-colour?)
         (some true?))))

(defn three-in-a-diagonal? [board]
  "[0 1 2
    3 4 5
    6 7 8]"
  (let [top-left->bottom-right [0 4 8]
        bottom-left->top-right [6 4 2]

        get-points (fn [points] (->> points
                                     (mapv #(nth board %))
                                     (map :point/value)))]
    (->> [(get-points top-left->bottom-right),
          (get-points bottom-left->top-right)]
         (map same-colour?)
         (some true?))))
