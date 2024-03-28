(ns splunk-ui-cljs.undo-redo)


(declare
 ->UndoRedoCache)


(defprotocol UndoRedoProtocol
  (undo [this])
  (redo [this])
  (push [this value])
  (current [this])
  (has-next? [this])
  (reset [this] [this initial-value]))


(defrecord UndoRedoCache [history current-index cache-limit]
  UndoRedoProtocol
  (undo [this]
    (if (pos? (:current-index this))
      (update this :current-index dec)
      this))

  (redo [this]
    (if (< (inc (:current-index this)) (count (:history this)))
      (update this :current-index inc)
      this))

  (push [this value]
    (let [new-index   (inc (:current-index this))
          new-history (subvec (:history this) 0 new-index)
          new-history (->> (conj new-history value)
                           (take-last cache-limit))]
      (->UndoRedoCache (vec new-history)
                       (min (dec cache-limit) new-index)
                       cache-limit)))

  (reset [_]
    (->UndoRedoCache [nil] 0 cache-limit))

  (reset [_ initial-value]
    (->UndoRedoCache [initial-value] 0 cache-limit))

  (current [this]
    (if (empty? (:history this))
      nil
      (nth (:history this) (:current-index this))))

  (has-next? [this]
    (< (inc (:current-index this)) (count (:history this)))))


(defn new-cache [& {:keys [initial cache-limit]
                    :or   {initial     nil
                           cache-limit 10}}]
  (->UndoRedoCache [initial] 0 cache-limit))