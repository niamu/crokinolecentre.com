(ns cryogen.core
  (:require
   [cryogen-core.compiler :refer [compile-assets-timed]]
   [cryogen-core.plugins :refer [load-plugins]]
   [cryogen-core.compiler :as compiler]
   [cryogen-core.io :as cryogen-io]
   [net.cgrand.enlive-html :as enlive]))

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
                     (compiler/page-uri (cryogen-io/path "p"
                                                         (str index ".html"))
                                        params))
                 params
                 (selmer.parser/render-file
                  "/html/previews.html"
                  (merge params
                         {:active-page     "preview"
                          :home            (when index-page? true)
                          :selmer/context  (cryogen-io/path "/" blog-prefix "/")
                          :posts           posts
                          :videos          videos}))))))))

(defn -main
  []
  (load-plugins)
  (compile-assets-timed)
  (System/exit 0))
