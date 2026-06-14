(ns estudo.handler
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
    [estudo.core :as api]
    [estudo.db :as db]))

(defroutes app-routes

           (GET "/" []
             "Calculadora de Calorias API Pronta!")

           (POST "/usuario" req
             (let [{:keys [altura peso idade sexo]}
                   (:body req)]
               {:status 200
                :body (db/registrar-usuario!
                        {:altura altura
                         :peso peso
                         :idade idade
                         :sexo sexo})}))

           (GET "/usuario" []
             {:status 200
              :body (db/obter-usuario)})

           (POST "/comer" req
             (let [{:keys [item quantidade data]}
                   (:body req)]
               {:status 200
                :body (api/registrar-consumo!
                        item
                        (str quantidade)
                        data)}))

           (POST "/treinar" req
             (let [{:keys [item duracao data]}
                   (:body req)]
               {:status 200
                :body (api/registrar-treino!
                        item
                        (str duracao)
                        data)}))

           (GET "/extrato" [inicio fim]
             {:status 200
              :body (api/obter-extrato-periodo inicio fim)})

           (GET "/saldo" [inicio fim]
             {:status 200
              :body (api/calcular-saldo-periodo inicio fim)})

           (route/not-found
             {:status 404
              :body "Rota não encontrada"}))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-defaults api-defaults)))