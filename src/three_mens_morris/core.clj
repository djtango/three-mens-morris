(ns three-mens-morris.core
  (:require [clojure.spec.alpha :as s]
            [three-mens-morris.core-spec :as specs]))

(s/fdef place-piece
        :args (s/cat :board :game/board,
                     :position :board/position,
                     :piece :point/value)
        :ret :game/board
        :fn #(= (get-in (:ret %) [(-> % :args :position) :point/value])
                (-> % :args :piece)))

(defn place-piece [board position piece]
  (assoc-in board [position :point/value] piece))

(s/fdef three-in-a-row?
        :args (s/cat :board :game/board)
        :ret (s/nilable boolean?))

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

(s/fdef three-in-a-column?
        :args (s/cat :board :game/board)
        :ret (s/nilable boolean?))

(defn three-in-a-column? [board]
  (let [transpose (partial apply map vector)]
    (->> board
         (map :point/value)
         group-horizontally
         transpose
         (map same-colour?)
         (some true?))))

(s/fdef three-in-a-diagonal?
        :args (s/cat :board :game/board)
        :ret (s/nilable boolean?))

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

(s/fdef winner?
        :args (s/cat :board :game/board)
        :ret (s/nilable boolean?))

(defn winner? [board]
  (some true?
        ((juxt three-in-a-row?
               three-in-a-column?
               three-in-a-diagonal?) board)))

(defn switch-player [{:as state :game/keys [player]}]
  (->> (if (= :white player)
         :black
         :white)
       (assoc state :game/player)))

(s/fdef use-piece-from-outside-board
        :args (s/cat :state :game/state))

(defn use-piece-from-outside-board [state]
  (if (seq (:game/pieces state))
    (update state :game/pieces subvec 1)
    state))

(defn make-move [state move]
  (let [{:game/keys [board player]} state
        [from to] move]
    (-> state
        use-piece-from-outside-board
        (assoc :game/board
               (place-piece board to player)))))

(s/fdef take-turn
        :args (s/cat :state :game/state
                     :move :game/move)
        :ret :game/state)
(defn take-turn [state move]
  (-> state
      (make-move move)
      switch-player))

(s/fdef next-turn
        :args (s/cat :state :game/state
                     :move :game/move))
(defn next-turn [state move]
  (let [next-state (make-move state move)]
    (if (winner? (:game/board next-state))
      (str (:game/player state) " wins")
      next-state)))
