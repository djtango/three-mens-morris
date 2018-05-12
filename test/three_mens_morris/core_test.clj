(ns three-mens-morris.core-test
  (:require [clojure.test :refer :all]
            [three-mens-morris.core :refer :all]
            [three-mens-morris.core-spec :refer :all]
            [clojure.spec.alpha :as s]))

(deftest a-test
  (testing "A valid point in a board has neighbours and a value"
    (is (s/valid? :board/point {:point/neighbours 1,
                                :point/current-piece 2}))))
