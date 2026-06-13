(ns estudo.db)

;; O "banco de dados" em memória
(def estado-aplicacao (atom {:usuario nil
                             :transacoes '()}))

(defn registrar-usuario! [dados]
  (swap! estado-aplicacao assoc :usuario dados)
  (get @estado-aplicacao :usuario))

(defn obter-usuario []
  (:usuario @estado-aplicacao))

(defn adicionar-transacao! [nova-transacao]
  (swap! estado-aplicacao update :transacoes conj nova-transacao)
  nova-transacao)

;; VEJA SE ESTA FUNÇÃO ESTÁ EXATAMENTE ASSIM NO SEU ARQUIVO:
(defn obter-transacoes []
  (:transacoes @estado-aplicacao))