(ns three-mens-morris.core-test
  (:require [clojure.test :refer :all]
            [three-mens-morris.core :refer :all]
            [three-mens-morris.core-spec :refer :all]
            [clojure.spec.alpha :as s]))

(deftest points
  (testing "A valid point in a board has neighbours and a value"
    (is (s/valid? :board/point {:point/neighbours #{},
                                :point/value nil}))))

(deftest board
  (testing "A valid board must have 9 points"
    (let [point #:point{:neighbours #{} :value nil}]
      (is (not (s/valid? :board/board (into [] (repeat 7 point)))))
      (is (not (s/valid? :board/board (into [] (repeat 8 point)))))
      (is (not (s/valid? :board/board (into [] (repeat 10 point)))))

      (is (s/valid? :board/board (into [] (repeat 9 point)))))))
