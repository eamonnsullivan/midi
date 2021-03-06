(ns eamonnsullivan.midi
  (:import [javax.sound.midi MidiSystem]))

(defrecord Note [pitch octave duration])

(def close-encounters [(->Note :D 3 1/2)
                       (->Note :E 3 1/2)
                       (->Note :C 2 1/2)
                       (->Note :G 2 1/2)])

(def jaws (for [duration [1/2 1/2 2/4 1/4 1/8 1/8 1/8 1/8] pitch [:E :F]]
            (Note. pitch 2 duration)))

(defprotocol MidiNote
  (to-msec [this tempo])
  (key-number [this])
  (play [this tempo midi-channel]))

(extend-type Note
  MidiNote
  (to-msec [this tempo]
    (let [duration-to-bpm {1 240, 1/2 120, 1/4 60, 1/8 30, 1/16 15}]
      (* 1000 (/ (duration-to-bpm (:duration this))
                 tempo))))

  (key-number [this]
    (let [scale {:C 0,  :C# 1, :Db 1,  :D 2,
                 :D# 3, :Eb 3, :E 4,   :F 5,
                 :F# 6, :Gb 6, :G 7,   :G# 8,
                 :Ab 8, :A 9,  :A# 10, :Bb 10,
                 :B 11}]
      (+ (* 12 (inc (:octave this)))
         (scale (:pitch this)))))

  (play [this tempo midi-channel]
    (let [velocity (or (:velocity this) 100)]
      (.noteOn midi-channel (key-number this) velocity)
      (Thread/sleep (to-msec this tempo)))))

(defn perform [notes & {:keys [tempo] :or {tempo 120}}]
  (with-open [synth (doto (MidiSystem/getSynthesizer) .open)]
    (let [channel (aget (.getChannels synth) 0)]
      (doseq [note notes]
        (play note tempo channel)))))

;; (defn john-cage
;;   "A simulated John Cage"
;;   []
;;   (let [min-duration 250
;;         min-velocity 120
;;         rand-note (reify
;;                     MidiNote
;;                     (to-msec [this tempo] (+ (rand-int 1000) min-duration))
;;                     (key-number [this] (rand-int 100))
;;                     (play [this tempo midi-channel]
;;                       (let [velocity (+ (rand-int 100) min-velocity)]
;;                         (.noteOn midi-channel (key-number this) velocity)
;;                         (Thread/sleep (to-msec this tempo)))))]
;;     (perform (repeat 15 rand-note))))
