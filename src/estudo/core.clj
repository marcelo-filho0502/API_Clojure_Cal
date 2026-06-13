(ns estudo.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def usda-key "XfG4dBxN5Wxmc8JzvHZmKovAuUu3ag4cS5FghCeI")
(def link-caloria "https://api.nal.usda.gov/fdc/v1/foods/search")

(defn buscar-calorias [item]
  (let [resposta (http/get link-caloria
                           {:query-params {"query" item
                                           "api_key" usda-key}})
        dados-json (json/parse-string (:body resposta) true)

        primeiro-alimento (first (:foods dados-json))
        nutrientes (:foodNutrients primeiro-alimento)]

    (->> nutrientes
         (filter (fn [n]
                   (and (= (:nutrientName n) "Energy")
                        (= (:unitName n) "KCAL"))))

         ;; Limpa o resultado deixando apenas o essencial
         (map #(select-keys % [:nutrientName :value :unitName]))

         first)))


(def ninjas-key  "YWTkVcQgkAHfakrcF4YuZ49xCcN1l10pfgZsruf8" )
(def link-exercicio "https://api.api-ninjas.com/v1/caloriesburned")


(defn buscar-exercicio [item]
  (let [resposta (http/get link-exercicio
                           {
                            :headers {"X-Api-Key" ninjas-key}
                            :query-params {"activity" item}})
        dados-json (json/parse-string (:body resposta) true)

        primeiro-exercicio (first dados-json)]
        (select-keys primeiro-exercicio [:name :calories_per_hour :duration_minutes])
 ))


(defn calcular-saldo [alimento exercicio]
  (let [dados-alimento (buscar-calorias alimento)
        dados-exercicio (buscar-exercicio exercicio)

        calorias-ingeridas (get dados-alimento :value 0)

        calorias-queimadas (get dados-exercicio :calories_per_hour 0)

        saldo (- calorias-ingeridas calorias-queimadas)]

    {:alimento alimento
     :calorias-ingeridas calorias-ingeridas
     :exercicio exercicio
     :calorias-queimadas calorias-queimadas
     :saldo-final saldo}))


(defn -main [& args]
  (println "Para testar a API, chame a função no REPL!"))