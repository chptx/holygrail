(set-env!
 :source-paths   #{"src/cljs" "src/clj"}
 :resource-paths #{"resources"}
 :dependencies '[[adzerk/boot-cljs      "1.7.170-3" :scope "test"]
                 [adzerk/boot-reload    "0.4.2"      :scope "test"]
		 [boot-deps "0.1.6" :scope "test"]
                 [environ"1.0.1"]
                 [danielsz/boot-environ "0.0.5"]
                 ; server
                 [org.danielsz/system "0.2.0"]
                 [ring/ring-defaults "0.1.5"]
                 [http-kit "2.1.19"]
                 [compojure "1.4.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 ; client
                 [org.omcljs/om "0.9.0" :exclusions [cljsjs/react]]
                 [cljsjs/react "0.14.3-0"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-reload    :refer [reload]]
 '[reloaded.repl :refer [init start stop go reset]]
 '[holy-grail.systems :refer [dev-system prod-system]]
 '[danielsz.boot-environ :refer [environ]]
 '[system.boot :refer [system run]])

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port 3000})
   (watch :verbose true)
   (system :sys #'dev-system :auto-start true :hot-reload true :files ["handler.clj"])
   (reload)
   (cljs :source-map true)
   (repl :server true)))

(deftask dev-run
  "Run a dev system from the command line"
  []
  (comp
   (environ :env {:http-port 3000})
   (cljs)
   (run :main-namespace "holy-grail.core" :arguments [#'dev-system])
   (wait)))

(deftask prod-run
  "Run a prod system from the command line"
  []
  (comp
   (environ :env {:http-port 8008
                  :repl-port 8009})
   (cljs :optimizations :advanced)
   (run :main-namespace "holy-grail.core" :arguments [#'prod-system])
   (wait)))

(deftask build
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (aot :namespace '#{holy-grail.core})
   (pom :project 'holyuber
        :version "1.0.1")
   (cljs :optimizations :advanced)
   (uber)
   (jar :main 'holy-grail.core)))
