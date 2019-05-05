(ns cryogen.server
  (:require
   [compojure.core :refer [GET defroutes]]
   [compojure.route :as route]
   [ring.util.response :refer [redirect resource-response]]
   [ring.util.codec :refer [url-decode]]
   [cryogen-core.watcher :refer [start-watcher!]]
   [cryogen-core.plugins :refer [load-plugins]]
   [cryogen-core.compiler :as compiler]
   [cryogen-core.io :as cryogen-io]
   [cryogen.core]
   [com.crokinolecentre.style :as style]))

(defn init
  []
  (style/-main)
  (load-plugins)
  (compiler/compile-assets-timed)
  (let [ignored-files (-> (compiler/read-config) :ignored-files)]
    (start-watcher! "resources/templates"
                    ignored-files
                    (fn [] (style/-main) (compiler/compile-assets-timed)))))

(defn wrap-subdirectories
  [handler]
  (fn [request]
    (let [req-uri (.substring (url-decode (:uri request)) 1)
          res-path (if (or (= req-uri "") (= req-uri "/"))
                     (cryogen-io/path "/index.html")
                     (cryogen-io/path (str req-uri ".html")))]
      (or (resource-response res-path {:root "public"})
          (handler request)))))

(defroutes routes
  (GET "/" []
       (redirect (let [config (compiler/read-config)]
                   (cryogen-io/path (:blog-prefix config)
                                    (when-not (:clean-urls? config) "index.html")))))
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler (wrap-subdirectories routes))
