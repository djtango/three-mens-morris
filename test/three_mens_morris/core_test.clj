(ns three-mens-morris.core-test
  (:require [clojure.test :refer :all]
            [three-mens-morris.core :as sut]
            [three-mens-morris.core-spec :as sut-spec]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as stest]))

(stest/instrument)
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

(deftest three-in-a-row?
  (testing "should return true if there are three pieces in a row"
    (let [blank #:point{:neighbours #{} :value nil}
          white (assoc blank :point/value :white)]
      (testing "checking the top row:"
        (let [top-row-white [white white white,
                             blank blank blank,
                             blank blank blank]]
          (is (sut/three-in-a-row? top-row-white))))
      (testing "checking the middle row:"
        (let [middle-row-white [blank blank blank,
                                white white white,
                                blank blank blank]]
          (is (sut/three-in-a-row? middle-row-white))))
      (testing "checking the bottom row:"
        (let [bottom-row-white [blank blank blank,
                                blank blank blank,
                                white white white]]
          (is (sut/three-in-a-row? bottom-row-white)))))))

(deftest three-in-a-column?
  (testing "should return true if there are three pieces in a column"
    (let [blank #:point{:neighbours #{} :value nil}
          white (assoc blank :point/value :white)]
      (testing "checking the left column:"
        (let [left-column-white [white blank blank,
                                 white blank blank,
                                 white blank blank]]
          (is (sut/three-in-a-column? left-column-white))))
      (testing "checking the middle row:"
        (let [middle-column-white [blank white blank,
                                   blank white blank,
                                   blank white blank]]
          (is (sut/three-in-a-column? middle-column-white))))
      (testing "checking the bottom row:"
        (let [right-column-white [blank blank white,
                                  blank blank white,
                                  blank blank white]]
          (is (sut/three-in-a-column? right-column-white)))))))
