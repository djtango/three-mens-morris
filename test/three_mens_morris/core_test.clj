(ns three-mens-morris.core-test
  (:require [clojure.test :refer :all]
            [three-mens-morris.core :as sut]
            [three-mens-morris.core-spec :as sut-spec]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as stest]
            [three-mens-morris.board :as board]))

(stest/instrument)

(deftest points
  (testing "A valid point in a board has neighbours and a value"
    (is (s/valid? :board/point {:point/neighbours #{},
                                :point/value nil}))))

(deftest board
  (testing "A valid board must have 9 points"
    (let [point #:point{:neighbours #{} :value nil}]
      (is (not (s/valid? :game/board (into [] (repeat 7 point)))))
      (is (not (s/valid? :game/board (into [] (repeat 8 point)))))
      (is (not (s/valid? :game/board (into [] (repeat 10 point)))))

      (is (s/valid? :game/board (into [] (repeat 9 point)))))))

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

(deftest three-in-a-diagonal?
  (testing "should return true if there are three pieces in a diagonal"
    (let [blank #:point{:neighbours #{} :value nil}
          white (assoc blank :point/value :white)]
      (testing "checking the \\ diagonal"
        (let [left-column-white [white blank blank,
                                 blank white blank,
                                 blank blank white]]
          (is (sut/three-in-a-diagonal? left-column-white))))
      (testing "checking the / diagonal"
        (let [middle-column-white [blank blank white,
                                   blank white blank,
                                   white blank blank]]
          (is (sut/three-in-a-diagonal? middle-column-white)))))))

(deftest winner?
  (testing "should return true for three in any line"
    (let [blank #:point{:neighbours #{} :value nil}
          white (assoc blank :point/value :white)
          black (assoc blank :point/value :black)]
      (testing "checking the \\ diagonal"
        (let [left-column-white [white blank blank,
                                 blank white blank,
                                 blank blank white]]
          (is (sut/winner? left-column-white))))
      (testing "checking the top-row"
        (let [top-row-black [black black black,
                             blank white blank,
                             white blank blank]]
          (is (sut/winner? top-row-black)))))))

(deftest use-available-pieces?
  (is (sut/use-available-pieces? {:game/pieces [:black]} [nil 1]))
  (is (not (sut/use-available-pieces? {:game/pieces [:black]} [1 1])))
  (is (not (sut/use-available-pieces? {:game/pieces []} [nil 1]))))

(deftest use-pieces-from-board?
  (is (sut/use-pieces-from-board? {:game/pieces []} [0 1]))
  (is (not (sut/use-pieces-from-board? {:game/pieces []} [nil 1])))
  (is (not (sut/use-pieces-from-board? {:game/pieces [:black]} [nil 1]))))

(deftest take-turn
  (testing "when making the first move"
    (let [new-game #:game{:player :white,
                          :board board/empty-board
                          :pieces [:white :black :white :black :white :black]}
          result (sut/take-turn new-game [nil 0])]
      (testing "next player should be black"
        (is (= :black
               (:game/player result))))
      (testing "white should have 2 pieces left"
        (is (= [:black :white :black :white :black]
               (:game/pieces result))))
      (testing "white should be placed at 0"
        (is (= :white
               (-> result
                   :game/board
                   (nth 0)
                   :point/value))))))

  (testing "after six turns:"
    (let [new-game #:game{:player :white,
                          :board board/empty-board
                          :pieces [:white :black :white :black :white :black]}
          mid-game (reduce (fn [acc i]
                             (sut/take-turn acc [nil i])) new-game (range 6))]
      (testing "the number of pieces should be empty"
        (is (empty? (:game/pieces mid-game))))
      (testing "the current player should be white"
        (is (= :white (:game/player mid-game))))
      (testing "the game should crash if nil is supplied as from for the next move"
        (is (thrown? Exception (sut/take-turn mid-game [nil 6]))))
      (testing "the game should move a piece if an origin is given"
        (let [reposition-white (sut/take-turn mid-game [0 6])
              point #(get-in reposition-white [:game/board % :point/value])]
          (testing "there should be 3 white pieces on the board"
            (is (= 3 (->> reposition-white :game/board (map :point/value) (filter #{:white}) count))))
          (testing "there should be 3 black pieces on the board"
            (is (= 3 (->> reposition-white :game/board (map :point/value) (filter #{:black}) count))))
          (testing "point 0 should be white for mid-game"
            (is (= :white (get-in mid-game [:game/board 0 :point/value]))))
          (testing "point 0 should be nil after moving this piece"
            (is (= nil (point 0))))
          (testing "point 6 should be white after using the piece from point 0"
            (is (= :white (point 6)))))))))
