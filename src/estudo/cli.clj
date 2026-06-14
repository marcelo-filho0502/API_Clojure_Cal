(ns estudo.cli
  (:require [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [estudo.core :as api]
            [estudo.db :as db]))

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

(defn iniciar-interface []
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
              resultado (db/registrar-usuario! {:altura alt :peso peso :idade idade :sexo sexo})]
          (println "\nPerfil cadastrado com sucesso! =>" resultado)
          (recur))

        (= opcao "B")
        (let [_ (println "\n--- REGISTRAR ALIMENTO ---")
              _ (print "Nome do alimento (em inglês, ex: apple): ") _ (flush) alim (read-line)
              _ (print "Quantidade : ") _ (flush) qtd (read-line)
              _ (print "Data (AAAA-MM-DD): ") _ (flush) data-alim (read-line)
              resultado (api/registrar-consumo! alim qtd data-alim)]
          (println "\nAlimento registrado com sucesso! =>" resultado)
          (recur))

        (= opcao "C")
        (let [_ (println "\n--- REGISTRAR EXERCÍCIO ---")
              _ (print "Nome da atividade (em inglês, ex: running): ") _ (flush) ex (read-line)
              _ (print "Duração em minutos: ") _ (flush) duracao (read-line)
              _ (print "Data (AAAA-MM-DD): ") _ (flush) data-ex (read-line)
              resultado (api/registrar-treino! ex duracao data-ex)]
          (println "\nExercício registrado com sucesso! =>" resultado)
          (recur))

        (= opcao "D")
        (let [_ (println "\n--- EXTRATO DE TRANSAÇÕES ---")
              _ (print "Data de início (AAAA-MM-DD): ") _ (flush) ini-ext (read-line)
              _ (print "Data de fim (AAAA-MM-DD): ") _ (flush) fim-ext (read-line)
              resultado (api/obter-extrato-periodo ini-ext fim-ext)]
          (println "\nTransações encontradas:")
          (pprint resultado)
          (recur))

        (= opcao "E")
        (let [_ (println "\n--- SALDO DE CALORIAS ---")
              _ (print "Data de início (AAAA-MM-DD): ") _ (flush) ini-sal (read-line)
              _ (print "Data de fim (AAAA-MM-DD): ") _ (flush) fim-sal (read-line)
              resultado (api/calcular-saldo-periodo ini-sal fim-sal)]
          (println "\nResultado do cálculo:")
          (pprint resultado)
          (recur))

        (= opcao "S")
        (println "\nEncerrando o programa. Até logo!")

        :else
        (let [_ (println "\nOpção inválida! Tente novamente.")]
          (recur))))))