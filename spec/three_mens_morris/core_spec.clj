(ns three-mens-morris.core-spec
  (:require [clojure.spec.alpha :as s]))

(s/def :board/position (into #{} (range 9)))
(s/def :point/neighbours (s/coll-of :board/position :into #{}))

(def point-values #{nil :white :black})
(s/def :point/value (s/with-gen (partial contains? point-values)
                      #(s/gen point-values)))

(s/def :board/point (s/keys :req [:point/neighbours :point/value]))

(s/def :board/board (s/coll-of :board/point :count 9 :into []))

(s/def :game/player #{:white :black})

(s/def :game/state (s/keys :req [:game/player :board/board]))

(s/def :game/move :board/position)
