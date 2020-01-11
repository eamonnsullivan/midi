(ns eamonnsullivan.generator
  (:require [eamonnsullivan.midi :as midi])
  (:import [eamonnsullivan.midi MidiNote]
           [javax.sound.midi MidiSystem]))

(defn john-cage
  "A simulated John Cage"
  []
  (let [min-duration 250
        min-velocity 64
        rand-note (reify
                    MidiNote
                    (to-msec [this tempo] (+ (rand-int 1000) min-duration))
                    (key-number [this] (rand-int 100))
                    (play [this tempo midi-channel]
                      (let [velocity (+ (rand-int 100) min-velocity)]
                        (.noteOn midi-channel (midi/key-number this) velocity)
                        (Thread/sleep (midi/to-msec this tempo)))))]
    (midi/perform (repeat 15 rand-note))))
