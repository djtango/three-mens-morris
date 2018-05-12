(ns three-mens-morris.board
  (:require [three-mens-morris.core-spec :as specs]
            [clojure.spec.alpha :as s]))

(def indexes
  {:ul 0, :u 1, :ur 2,
   :l  3, :m 4, :r  5,
   :dl 6, :d 7, :dr 8})

(defn ->point [& neighbours]
  #:point{:neighbours (into #{} (map indexes neighbours)),
          :value nil})

(def top-left      (->point :u :l :m))
(def top-middle    (->point :ul :ur :l :m :r))
(def top-right     (->point :u :r :m))

(def middle-left   (->point :ul :u :m :dl :d))
(def middle        (->point :ul :u :ur :l :r :dl :d :dr))
(def middle-right  (->point :ur :u :m :d :dr))

(def bottom-left   (->point :d :l :m))
(def bottom-middle (->point :dl :dr :l :m :r))
(def bottom-right  (->point :d :r :m))

(def empty-board
  [top-left     top-middle     top-right
   middle-left  middle         middle-right
   bottom-left  bottom-middle  bottom-right])

(assert (s/valid? :game/board empty-board))
