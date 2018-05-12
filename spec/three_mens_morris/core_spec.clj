(ns three-mens-morris.core-spec
  (:require [clojure.spec.alpha :as s]))

(s/def :board/point (s/keys :req [:point/neighbours :point/current-piece]))
