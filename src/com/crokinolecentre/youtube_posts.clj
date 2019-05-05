(ns com.crokinolecentre.youtube-posts
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.pprint :as pprint]
   [clojure.string :as string])
  (:import
   [java.time Duration]))

(def key-fn
  (comp #(keyword %) #(string/lower-case %) #(string/replace % "_" "-")))

(defn format-duration
  [seconds]
  (let [duration (Duration/ofSeconds seconds)
        hours (.toHours duration)
        minutes (.toMinutes (.minusHours duration hours))
        seconds(.getSeconds (.minusMinutes duration minutes))]
    (format "%02d:%02d:%02d" hours minutes seconds)))

(defn parse-post
  [file]
  (-> (json/read (io/reader file) :key-fn key-fn)
      (select-keys [:id :title :thumbnail :tags :duration :upload-date
                    :uploader :uploader-url :channel-url :description])
      (update :upload-date
              #(string/replace % #"^([0-9]{4})([0-9]{2})" "$1-$2-"))
      (update :duration #(format-duration %))
      (update :thumbnail #(-> (string/replace % #"maxresdefault" "mqdefault")
                              (string/replace #"hqdefault" "mqdefault")))
      (assoc :filename (-> (.getName file)
                           (string/replace ".info.json" ".md")
                           string/lower-case
                           (string/replace " " "-")
                           (string/replace "---" "-")
                           (string/replace #"^([0-9]{4})([0-9]{2})" "$1-$2-"))
             :author "Nathan Walsh"
             :layout :youtube)))

(defn write-post
  [post]
  (spit (str "resources/templates/md/posts/" (:filename post))
        (str (-> (select-keys post [:id :title :author :layout :tags
                                    :thumbnail :duration])
                 (dissoc post :upload-date :tags)
                 (assoc :date (:upload-date post) :youtube-tags (:tags post))
                 pprint/pprint with-out-str)
             "\n\n"
             (string/replace (:description post) #"(^|[^\n])\n(?!\n)" "\n\n")
             "\n")))

(defn -main
  []
  (doseq [post (->> (file-seq (io/file "resources/youtube-dl"))
                    (filter #(and (.isFile %) (string/ends-with? % ".info.json"))))]
    ((comp write-post parse-post) post)))
