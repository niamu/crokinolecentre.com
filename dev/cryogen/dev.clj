(ns cryogen.dev
  (:require
   [ring.server.standalone :as ring]
   [cryogen.server :as server]))

(defn -main
  [& args]
  (ring/serve server/handler {:init server/init}))
