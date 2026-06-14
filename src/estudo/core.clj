(ns estudo.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [estudo.db :as db]))

(def usda-key "XfG4dBxN5Wxmc8JzvHZmKovAuUu3ag4cS5FghCeI")
(def link-caloria "https://api.nal.usda.gov/fdc/v1/foods/search")

(def ninjas-key "YWTkVcQgkAHfakrcF4YuZ49xCcN1l10pfgZsruf8")
(def link-exercicio "https://api.api-ninjas.com/v1/caloriesburned")


(defn buscar-calorias [item]
  (let [resposta (http/get link-caloria {:query-params {"query" item "api_key" usda-key}})
        dados-json (json/parse-string (:body resposta) true)
        primeiro-alimento (first (:foods dados-json))
        nutrientes (:foodNutrients primeiro-alimento)]
    (->> nutrientes
         (filter (fn [n] (and (= (:nutrientName n) "Energy") (= (:unitName n) "KCAL"))))
         (map #(select-keys % [:nutrientName :value :unitName]))
         first)))

(defn buscar-exercicio [item]
  (let [resposta (http/get link-exercicio {:headers
                                           {"X-Api-Key" ninjas-key}
                                           :query-params
                                           {"activity" item}})

        dados-json (json/parse-string (:body resposta) true)
        primeiro-exercicio (first dados-json)]
    (select-keys primeiro-exercicio [:name :calories_per_hour :duration_minutes])))


(defn registrar-consumo! [item quantidade data]
  (let [dados (buscar-calorias item)
        calorias-unitarias (get dados :value 0)
        calorias-totais (* calorias-unitarias (Float/parseFloat quantidade))
        nova-transacao {:tipo "ganho"
                        :item item
                        :data data
                        :quantidade quantidade
                        :calorias calorias-totais}]
    (db/adicionar-transacao! nova-transacao)))

(defn registrar-treino! [item duracao data]
  (let [dados (buscar-exercicio item)
        cph (get dados :calories_per_hour 0)
        calorias-perdidas (* cph (/ (Float/parseFloat duracao) 60.0))
        nova-transacao {:tipo "perda"
                        :item item
                        :data data
                        :duracao duracao
                        :calorias calorias-perdidas}]
    (db/adicionar-transacao! nova-transacao)))

;; Função auxiliar para filtrar transações por período (sem usar LOOPS imperativos) [cite: 44, 66]
(defn- filtrar-por-periodo [transacoes data-inicio data-fim]
  (filter (fn [t]
            (and (>= (compare (:data t) data-inicio) 0)
                 (<= (compare (:data t) data-fim) 0)))
          transacoes))


(defn obter-extrato-periodo [data-inicio data-fim]
  (let [todas (db/obter-transacoes)]
    (filtrar-por-periodo todas data-inicio data-fim)))

(defn calcular-saldo-periodo [data-inicio data-fim]
  (let [transacoes-filtradas (filtrar-por-periodo (db/obter-transacoes) data-inicio data-fim)
        saldo (reduce (fn [acumulado t]
                        (if (= (:tipo t) "ganho")
                          (+ acumulado (:calorias t))
                          (- acumulado (:calorias t))))
                      0
                      transacoes-filtradas)]
    {:data-inicio data-inicio
     :data-fim data-fim
     :saldo-calorias saldo}))