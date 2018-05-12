(ns three-mens-morris.core-spec
  (:require [clojure.spec.alpha :as s]))

(s/def :point/neighbours set?)
(s/def :point/value #(contains? #{nil :white :black} %))

(s/def :board/point (s/keys :req [:point/neighbours :point/value]))
