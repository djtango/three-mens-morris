(ns three-mens-morris.core
  (:require [clojure.spec.alpha :as s]
            [three-mens-morris.core-spec :as specs]))

(s/fdef place-piece
        :args (s/cat :board :board/board,
                     :position :board/position,
                     :piece :point/value)
        :ret :board/board
        :fn #(= (get-in (:ret %) [(:position %) :point/value])
                (:piece %)))

(defn place-piece [board position piece]
  (assoc-in board [position :point/value] piece))
