(defproject estudo "0.1.0-SNAPSHOT"
  :description "Calculadora de Calorias - API & CLI"

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clj-http "3.12.3"]
                 [cheshire "5.11.0"]
                 [compojure "1.6.3"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [ring/ring-defaults "0.3.4"]
                 [ring/ring-json "0.5.1"]]

  :plugins [[lein-ring "0.12.5"]]

  :source-paths ["src" "frontend"]

  :main frontend.core

  :ring {:handler estudo.handler/app}

  :repl-options {:init-ns estudo.core})