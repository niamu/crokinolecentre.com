(ns com.crokinolecentre.style
  (:require
   [binnacle.core :as binnacle]
   [garden.core :as garden]
   [garden.color :as color]
   [garden.selectors :as selectors]
   [garden.stylesheet :as stylesheet]
   [garden.units :as units]
   [normalize.core :as normalize]))

(def assets
  (get-in (binnacle/assets "resources/templates/themes/crokinolecentre/assets/")
          [:resources :templates :themes :crokinolecentre :assets]))

(def theme
  #_{:name "Matu Poliak 03",
     :author "Alexandre Matuszczak",
     :id 11855956,
     :href "https://color.adobe.com/Matu-Poliak-03-color-theme-11855956/",
     :swatches
     {"Mine Shaft" "#2B3333",
      "Roman" "#D56753",
      "Pampas" "#F3F1EB",
      "Anzac" "#E3B047",
      "Matisse" "#1973AE"}}
  {0 "#F3F1EB"
   1 "#E3B047"
   2 "#2B3333"
   3 "#D56753"
   4 "#1973AE"})

(def attr (stylesheet/cssfn :attr))
(def blur (stylesheet/cssfn :blur))
(def calc (stylesheet/cssfn :calc))
(def rotate (stylesheet/cssfn :rotate))
(def url (stylesheet/cssfn :url))

(defn linear-gradient
  [c1 c2]
  (apply (stylesheet/cssfn :linear-gradient) [c1 c2]))

(defn radial-gradient
  [c1 c2]
  (apply (stylesheet/cssfn :radial-gradient)
         [[:circle :at 0 0] c1 c2]))

(defmacro defbreakpoint [name media-params]
  `(defn ~name [& rules#]
     (stylesheet/at-media ~media-params [:& rules#])))

(defbreakpoint non-mobile-screen
  {:screen true
   :min-width (units/px 1000)})

(def fonts
  (stylesheet/at-font-face
   {:font-family "Aleo"
    :src (url (binnacle/data-url assets [:aleo.woff]))
    :font-weight :normal
    :font-style :normal}))

(def typography
  [[:body {:font [["15px/18px" "Helvetica"] "Arial" "sans-serif"]}]
   [:h1 :h2 :h3 :h4 {:line-height (units/em 1.2)
                     :font-family "Aleo"
                     :margin [[0 0 (units/em 0.5)]]}
    [:a {:color (theme 2)}]]
   [:h1 {:position :relative
         :text-shadow [[(units/px 1) (units/px 1) (units/px 1)
                        (assoc (color/as-rgb (theme 2))
                               :alpha 0.4)]]
         :color (theme 0)}]
   [:article [:h2 {:font-size (units/percent 250)
                   :margin 0}]]
   [:h3 :h4 {:margin-top (units/em 2)}]
   [:h3 {:font-size (units/percent 150)
         :text-align :center}]
   [:h4 {:font-size (units/percent 120)
         :color (color/lighten (theme 2) 30)}]
   [:a {:text-decoration :none
        :color (theme 4)}
    [:&:hover {:color (color/lighten (theme 4) 15)}]]])

(def table
  [[:table
    {:display :block
     :width (units/percent 100)
     :overflow-x :scroll}]
   [:tbody
    [:tr
     [(selectors/& (selectors/nth-child :odd))
      {:background (color/lighten (theme 0) 2)}]]]
   [:th {:white-space :nowrap
         :padding (units/em 1)}]
   [:td {:padding [[(units/em 0.25) (units/em 1)]]}]])

(def header
  [:header
   {:position :relative
    :padding (units/em 0.75)
    :text-align :center
    :color (theme 0)}
   [:&:before
    {:content (attr :nil)
     :display :block
     :width (units/percent 100)
     :height (units/vh 50)
     :padding-bottom (units/vh 15)
     :background (radial-gradient (theme 1)
                                  (theme 3))
     :z-index -1
     :position :absolute
     :top 0
     :left 0}]
   [:h1
    {:font-size (units/vmin 8)
     :margin-bottom (units/em 0.2)
     :color (theme 1)
     :transition [[(units/s 0.15) :linear :color]]}
    [:a {:color (color/lighten (theme 0) 15)}
     [:&:hover {:color (theme 0)}]]]
   [:span.tagline
    {:text-transform :uppercase
     :font-size (units/em 0.9)
     :color (assoc (color/as-rgb (theme 0))
                   :alpha 0.75)}]
   [:nav
    {:margin-top (units/em 2)}
    [:ul
     {:list-style :none
      :padding 0
      :margin 0}
     [:li
      {:display :inline-block
       :margin (units/px 2)}
      [:&.active
       {:background (linear-gradient (assoc (color/as-rgb (theme 0))
                                            :alpha 0)
                                     (assoc (color/as-rgb (theme 0))
                                            :alpha 0.4))
        :border-radius (units/px 5)}]
      [:a
       {:display :block
        :border [[(units/px 2) :solid (assoc (color/as-rgb (theme 0))
                                             :alpha 0.4)]]
        :padding (units/px 6)
        :border-radius (units/px 5)
        :color (theme 0)
        :transition [[(units/s 0.15) :linear :border-color]]}
       [:&:hover {:color (theme 0)
                  :border-color (theme 0)}]]]]]])

(def media
  [[:img :iframe
    {:display :block
     :max-width (units/percent 100)
     :border :none}]
   [:article
    [:img :iframe
     {:max-width (units/percent 90)
      :margin [[0 :auto]]
      :background-color :black
      :box-shadow [[0 0 (units/px 10)
                    (assoc (color/as-rgb (theme 2))
                           :alpha 0.4)]]}]]])

(def posts
  [[:article
    {:width (calc [(units/percent 100) \- (units/px 20)])}
    (non-mobile-screen {:flex-grow 1
                        :width :auto})]
   [:.posts :.videos
    {:display :flex
     :flex-direction :column
     :flex-wrap :wrap}]
   [:.post :.video
    {:display :inline-block
     :width (units/percent 100)
     :margin (units/px 10)
     :background (color/lighten (theme 0) 15)
     :box-shadow [[0 0 (units/px 5)
                   (assoc (color/as-rgb (theme 2)) :alpha 0.2)]]}
    (non-mobile-screen {:max-width (units/px 600)})
    [:.content {:overflow :hidden}]
    [:&.preview
     {:width (units/percent 100)}
     (non-mobile-screen {:max-width (units/px 600)})
     [:div.content
      {:height (units/em 14)
       :position :relative}
      [:&:after
       {:content (attr :nil)
        :pointer-events :none
        :display :block
        :position :absolute
        :width (units/percent 100)
        :height (units/percent 100)
        :top 0
        :left 0
        :background (linear-gradient (assoc (color/lighten (theme 0) 15)
                                            :alpha 0)
                                     (color/lighten (theme 0) 15))}]]
     [:a.read-more
      {:position :relative
       :display :block
       :margin [[0 :auto]]
       :width (units/em 12)
       :text-align :center
       :padding (units/px 6)
       :border-radius (units/px 5)
       :z-index 0
       :color (theme 3)
       :transition [[(units/s 0.1) :linear :opacity]]}
      [:&:hover {:opacity 0.6}]
      [:&:before :&:after
       {:content (attr :nil)
        :position :absolute
        :top 0
        :left 0
        :display :block
        :width :inherit
        :height (units/percent 100)
        :z-index -1}]
      [:&:before
       {:top (units/px -2)
        :left (units/px -2)
        :padding (units/px 2)
        :border-radius (units/px 5)
        :background (radial-gradient (theme 1)
                                     (theme 3))}]
      [:&:after
       {:background (color/lighten (theme 0) 15)
        :border-radius (units/px 3)}]]]
    [:span.author
     {:font-weight 600
      :color (color/lighten (theme 2) 30)}]
    [:span.date
     {:text-transform :uppercase
      :font-size :smaller
      :font-weight 300}]
    [:.video-wrapper
     {:position :relative
      :max-width (units/px 640)
      :width (units/percent 100)
      :margin [[0 :auto (units/em 2)]]
      :z-index 0}
     [:&:before
      {:content (attr :nil)
       :display :block
       :width (units/percent 90)
       :height (units/percent 100)
       :position :absolute
       :top (units/px 20)
       :left (units/percent 5)
       :background-image :inherit
       :background-size :cover
       :background-position :center
       :filter (blur (units/px 10))
       :z-index -1}]
     [:iframe {:max-width (units/percent 100)}]]]
   [:.video.preview
    (non-mobile-screen {:max-width (units/px 300)})
    [:img {:width (units/percent 100)
           :transition [[(units/s 0.1) :linear :opacity]]}]
    [:a
     [:&:hover
      [:img {:opacity 0.85}]]]]
   [:div.prev-next
    {:display :flex
     :flex-direction :row
     :text-align :center
     :margin [[(units/em 1) 0 (units/em 2)]]
     :justify-content :space-evenly}
    (non-mobile-screen {:max-width (units/px 600)})]
   [:div.sidebar
    {:width (calc [(units/percent 100) \- (units/px 20)])}
    (non-mobile-screen {:max-width (units/px 300)})]])

(def footer
  [:footer
   {:margin [[(units/em 2) :auto]]
    :max-width (units/px 600)
    :text-align :center
    :color (color/darken (theme 0) 60)
    :font-size :small}
   [:p.description]])

(defn style
  [theme]
  [normalize/normalize
   [:* {:box-sizing :border-box}]
   [:body
    {:margin 0
     :background (theme 0)
     :color (theme 2)}]
   [:.container
    {:display :flex
     :flex-direction :row
     :flex-wrap :wrap
     :max-width (units/px 920)
     :margin [[0 :auto]]}]
   [:.element {:padding (units/em 1)}]
   media
   fonts
   typography
   table
   header
   posts
   footer])

(defn -main
  []
  (spit "resources/templates/themes/crokinolecentre/css/style.css"
        (garden/css {:pretty-print? false}
                    (style theme))))

(-main)
