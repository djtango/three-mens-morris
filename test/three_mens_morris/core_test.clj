(ns three-mens-morris.core-test
  (:require [clojure.test :refer :all]
            [three-mens-morris.core :as sut]
            [three-mens-morris.core-spec :as sut-spec]
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

(deftest place-piece
  (testing "a piece should be placed into the right position"
    (let [point #:point{:neighbours #{} :value nil}
          board (into [] (repeat 9 point))
          result (sut/place-piece board 0 :white)]
      (is (= #:point{:neighbours #{} :value :white}
             (nth result 0)))
      (is (= (subvec board 1)
             (subvec result 1))))))
