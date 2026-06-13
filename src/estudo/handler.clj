(ns estudo.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as jetty]
            [estudo.core :as api]
            [estudo.db :as db]))

(defroutes app-routes
           (GET "/" [] "Calculadora de Calorias API Pronta!")

           (POST "/usuario" [altura peso idade sexo]
             (str (db/registrar-usuario! {:altura altura :peso peso :idade idade :sexo sexo})))

           (GET "/usuario" []
             (str (db/obter-usuario)))


           (POST "/comer" [item quantidade data]
             (str (api/registrar-consumo! item quantidade data)))


           (POST "/treinar" [item duracao data]
             (str (api/registrar-treino! item duracao data)))


           (GET "/extrato" [inicio fim]
             (str (api/obter-extrato-periodo inicio fim)))


           (GET "/saldo" [inicio fim]
             (str (api/calcular-saldo-periodo inicio fim)))

           (route/not-found "Not Found"))

(defn iniciar-servidor []
  (jetty/run-jetty (wrap-defaults app-routes api-defaults)
                   {:port 3000 :join? false}))