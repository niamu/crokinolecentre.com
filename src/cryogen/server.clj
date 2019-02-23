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
   [net.cgrand.enlive-html :as enlive]
   [com.crokinolecentre.style :as style]))

;; Ensure archive page has all post metadata passed through
(intern 'cryogen-core.compiler
        'group-for-archive
        (fn [posts]
          (->> posts
               (group-by :formatted-archive-group)
               (map (fn [[group posts]]
                      {:group        group
                       :parsed-group (:parsed-archive-group (get posts 0))
                       :posts        posts}))
               (sort-by :parsed-group)
               reverse)))

;; text only previews
(intern 'cryogen-core.compiler
        'create-preview
        (fn [blocks-per-preview post]
          (update post :content
                  (fn [c]
                    (->> (enlive/html-snippet c)
                         (filter #(string? (first (:content %))))
                         (take blocks-per-preview)
                         enlive/emit*
                         (apply str))))))

(intern 'cryogen-core.compiler
        'compile-preview-pages
        (fn [{:keys [blog-prefix posts-per-page blocks-per-preview]
             :as params} posts]
          (when-not (empty? posts)
            (let [previews (-> (remove #(= (:layout %) "youtube.html") posts)
                               (compiler/create-previews posts-per-page
                                                         blocks-per-preview))
                  videos (-> (filter #(= (:layout %) "youtube.html") posts)
                             (compiler/create-previews posts-per-page
                                                       blocks-per-preview)
                             first :posts)]
              (cryogen-io/create-folder (cryogen-io/path "/" blog-prefix "p"))
              (doseq [{:keys [index posts]} previews
                      :let [index-page? (= 1 index)]]
                (compiler/write-html
                 (if index-page? (compiler/page-uri "index.html" params)
                     (compiler/page-uri (cryogen-io/path "p" (str index ".html"))
                                        params))
                 params
                 (selmer.parser/render-file
                  "/html/previews.html"
                  (merge params
                         {:active-page     "preview"
                          :home            (when index-page? true)
                          :selmer/context  (cryogen-io/path "/"
                                                            blog-prefix
                                                            "/")
                          :posts           posts
                          :videos          videos}))))))))

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
