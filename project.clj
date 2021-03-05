(defproject patients "0.1.0-SNAPSHOT"
  :description "Patients management"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.2.19"]
                 ;; [org.clojure/java.jdbc "0.6.1"]
                 ;; [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [compojure "1.6.2"]
                 [environ "1.2.0"]
                 [hiccup "1.0.5"]
                 [ragtime "0.8.0"]
                 [bouncer "1.0.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-jetty-adapter "1.9.1"]
                 [ring/ring-anti-forgery "1.3.0"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.30"]
                 [org.slf4j/jcl-over-slf4j "1.7.30"]
                 [org.slf4j/log4j-over-slf4j "1.7.30"]
                 [com.mchange/c3p0 "0.9.5.5"]
                 [com.stuartsierra/component "1.0.0"]]
  :plugins [[lein-environ "1.2.0"]]
  :aliases {"migrate"  ["run" "-m" "user/migrate"]
            "rollback" ["run" "-m" "user/rollback"]}
  :main ^:skip-aot patients.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}

             :production {:ring {:open-browser? false
                                 :stacktraces? false
                                 :auto-reload? false}}

             :test {:env {:database-username "postgres"
                          :database-password "postgres"
                          :database-subname "//pg:5432/patients_test"
                          :database-initial-pool-size "3"
                          :database-min-pool-size "3"
                          :database-max-pool-size "5"}
                    :dependencies [[reloaded.repl "0.2.4"]
                                   [ring/ring-mock "0.4.0"]]
                    :source-paths ["dev"]
                    :repl-options {:init-ns user}}

             :dev {:env {:database-username "postgres"
                         :database-password "postgres"
                         :database-subname "//pg:5432/patients_dev"
                         :database-initial-pool-size "3"
                         :database-min-pool-size "3"
                         :database-max-pool-size "5"}
                   :dependencies [[reloaded.repl "0.2.4"]
                                  [ring/ring-mock "0.4.0"]]
                              :source-paths ["dev"]
                              :resource-paths ["target" "resources"]
                              :clean-targets ^{:protect false} ["target"]
                              :repl-options {:init-ns user}}})
