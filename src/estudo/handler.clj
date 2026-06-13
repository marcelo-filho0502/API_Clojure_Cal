(ns estudo.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as jetty]
            [estudo.core :as api]))


(defroutes app-routes
           (GET "/" [] "Hello World")

           (GET "/calorias" [item]
             (str(api/buscar-calorias item )))

           (GET "/exercicio" [item]
             (str(api/buscar-exercicio item )))

           (GET "/gasto" [alimento exercicio]
             (str (api/calcular-saldo alimento exercicio)))


           (route/not-found "Not Found"))



(defn iniciar-servidor []
  (jetty/run-jetty (wrap-defaults app-routes api-defaults)
                   {:port 3000 :join? false}))