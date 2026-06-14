(ns frontend.core
  (:require [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [clj-http.client :as http]
            [cheshire.core :as json])
  (:gen-class))

(def url-back "http://localhost:3000")

(defn para-int [texto]
  (try (Integer/parseInt (string/trim texto))
       (catch Exception _ 0)))

(defn para-double [texto]
  (try (Double/parseDouble (string/trim texto))
       (catch Exception _ 0.0)))

(defn exibir-menu []
  (println "\n=========================================")
  (println "      CALCULADORA DE CALORIAS (AV3)      ")
  (println "=========================================")
  (println " [A] Cadastrar Perfil do Usuário")
  (println " [B] Registrar Consumo de Alimento (Ganho)")
  (println " [C] Registrar Exercício Físico (Perda)")
  (println " [D] Ver Extrato de Transações")
  (println " [E] Ver Saldo de Calorias do Período")
  (println " [S] Sair do Programa")
  (println "=========================================")
  (print   "Escolha uma opção: ")
  (flush))

(defn -main [& args]
  (println "Iniciando a interface da Calculadora de Calorias...")

  (loop []
    (exibir-menu)
    (let [opcao (string/upper-case (read-line))]
      (cond
        (= opcao "A")
        (let [_ (println "\n--- CADASTRAR PERFIL ---")
              _ (print "Digite sua altura (ex: 1.75): ") _ (flush) alt (read-line)
              _ (print "Digite seu peso em kg (ex: 70): ") _ (flush) peso (read-line)
              _ (print "Digite sua idade: ") _ (flush) idade (read-line)
              _ (print "Digite seu sexo (M/F): ") _ (flush) sexo (read-line)
              ;; 🛠️ Mudamos para :content-type :json e enviamos a string JSON gerada pelo cheshire
              resposta (http/post (str url-back "/usuario")
                                  {:content-type :json
                                   :body (json/generate-string
                                           {:altura (para-double alt)
                                            :peso (para-double peso)
                                            :idade (para-int idade)
                                            :sexo sexo})})]
          (println "\nResposta do servidor Back-End =>" (:body resposta))
          (recur))

        (= opcao "B")
        (let [_ (println "\n--- REGISTRAR ALIMENTO ---")
              _ (print "Nome do alimento (em inglês, ex: apple): ") _ (flush) alim (read-line)
              _ (print "Quantidade: ") _ (flush) qtd (read-line)
              _ (print "Data (AAAA-MM-DD): ") _ (flush) data-alim (read-line)
              ;; 🛠️ Transmitindo como JSON correto para o Back-end receber os números
              resposta (http/post (str url-back "/comer")
                                  {:content-type :json
                                   :body (json/generate-string
                                           {:item alim
                                            :quantidade (para-int qtd)
                                            :data data-alim})})]
          (println "\nAlimento registrado no Back-End! =>" (:body resposta))
          (recur))

        (= opcao "C")
        (let [_ (println "\n--- REGISTRAR EXERCÍCIO ---")
              _ (print "Nome da atividade (em inglês, ex: running): ") _ (flush) ex (read-line)
              _ (print "Duração em minutos: ") _ (flush) duracao (read-line)
              _ (print "Data (AAAA-MM-DD): ") _ (flush) data-ex (read-line)
              ;; 🛠️ Transmitindo como JSON correto
              resposta (http/post (str url-back "/treinar")
                                  {:content-type :json
                                   :body (json/generate-string
                                           {:item ex
                                            :duracao (para-int duracao)
                                            :data data-ex})})]
          (println "\nExercício registrado no Back-End! =>" (:body resposta))
          (recur))

        (= opcao "D")
        (let [_ (println "\n--- EXTRATO DE TRANSAÇÕES ---")
              _ (print "Data de início (AAAA-MM-DD): ") _ (flush) ini-ext (read-line)
              _ (print "Data de fim (AAAA-MM-DD): ") _ (flush) fim-ext (read-line)
              resposta (http/get (str url-back "/extrato")
                                 {:query-params {:inicio ini-ext :fim fim-ext}})]
          (println "\nTransações encontradas no servidor:")
          (println (:body resposta))
          (recur))

        (= opcao "E")
        (let [_ (println "\n--- SALDO DE CALORIAS ---")
              _ (print "Data de início (AAAA-MM-DD): ") _ (flush) ini-sal (read-line)
              _ (print "Data de fim (AAAA-MM-DD): ") _ (flush) fim-sal (read-line)
              resposta (http/get (str url-back "/saldo")
                                 {:query-params {:inicio ini-sal :fim fim-sal}})]
          (println "\nResultado calculado pelo Back-End:")
          (println (:body resposta))
          (recur))

        (= opcao "S")
        (println "\nEncerrando o programa. Até logo!")

        :else
        (let [_ (println "\nOpção inválida! Tente novamente.")]
          (recur)))))
  )