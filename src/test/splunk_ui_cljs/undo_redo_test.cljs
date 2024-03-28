(ns splunk-ui-cljs.undo-redo-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [splunk-ui-cljs.undo-redo :as ur]))


(deftest undo-redo-basic-test
  (let [cache (ur/new-cache)]
    (testing "cache initialization"
      (is (instance? ur/UndoRedoCache cache))
      (is (satisfies? ur/UndoRedoProtocol cache))
      (is (= 10 (:cache-limit cache)))
      (is (= 0 (:current-index cache)))
      (is (nil? (ur/current cache))))

    (testing "too much undo's"
      (let [new-cache (-> cache
                          (ur/push :first-item)
                          (ur/undo)
                          (ur/undo)
                          (ur/undo))]
        (is (= 0 (:current-index new-cache)))
        (is (= nil (ur/current new-cache)))))

    (testing "basic undo/redo ops"
      (let [new-cache (-> cache
                          (ur/push :first-item)
                          (ur/push :second-item))]
        (is (= 2 (:current-index new-cache)))
        (is (= :second-item (ur/current new-cache)))

        (is (= :first-item (-> new-cache ur/undo ur/current)))
        (is (= :second-item (-> new-cache ur/undo ur/redo ur/current)))))

    (testing "push after undo overrides history items"
      (let [new-cache (-> cache
                          (ur/push :first-item)
                          (ur/push :second-item)
                          (ur/undo)
                          (ur/push :third-item))]
        (is (= 2 (:current-index new-cache)))
        (is (= :third-item (ur/current new-cache)))

        (is (= :first-item (-> new-cache ur/undo ur/current)))
        (is (= :third-item (-> new-cache ur/undo ur/redo ur/current)))
        (is (= :third-item (-> new-cache ur/undo ur/redo ur/redo ur/current)))))

    (testing "cache limits"
      (let [counter   (atom 0)
            new-cache (->> cache
                           (iterate #(ur/push % (swap! counter inc)))
                           (take 21) ;; 21 because iterate includes the first element as it is
                           last)]
        (is (= 9 (:current-index new-cache)))
        (is (= 10 (count (:history new-cache))))
        (is (= 20 (ur/current new-cache)))
        (is (= 19 (-> new-cache ur/undo ur/current)))))))