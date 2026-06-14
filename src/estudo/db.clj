(ns estudo.db)

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

(defn obter-transacoes []
  (:transacoes @estado-aplicacao))