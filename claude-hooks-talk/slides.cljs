(ns slides
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn slides []
      [:<>
       [:main
        [:section
         [:h1 "Não seja trip sitter de LLM"]
         [:h2 "Deixe o Claude alucinar sozinho"]
         [:footer
          [:small
           [:a {:href "https://github.com/chr15m/scittle-tiny-slides"}
            "Made with Scittle Tiny Slides"]]]]

        [:section
         [:h1 "O problema"]
         [:p "Você manda o Claude rodar uma task longa..."]
         [:p "...e fica olhando pra tela esperando."]]

        [:section
         [:h1 "Sonho de Princesa"]
         [:ul
          [:li "Mandar o Claude trabalhar"]
          [:li "Ir fazer outra coisa"]
          [:li "Ser avisado quando ele terminar"]
          [:li "Ser avisado quando ele precisar de você"]]]

        [:section
         [:h1 "O que são Hooks?"]
         [:p "Comandos shell que o Claude Code executa automaticamente em resposta a eventos."]
         [:p "Você define. O Claude executa."]]

        [:section
         [:h1 "Tipos de eventos"]
         [:ul
          [:li [:code "Stop"] " — Claude terminou a tarefa"]
          [:li [:code "Notification"] " — Claude está esperando input seu"]
          [:li [:code "PreToolUse"] " — antes de usar uma ferramenta"]
          [:li [:code "PostToolUse"] " — depois de usar uma ferramenta"]
          [:li [:code "SubagentStop"] " — subagente terminou"]]]

        [:section
         [:h1 "Como configurar"]
         [:p [:code "~/.claude/settings.json"]]
         [:pre [:code "{
  \"hooks\": {
    \"Stop\": [
      {
        \"hooks\": [
          {
            \"type\": \"command\",
            \"command\": \"seu-comando-aqui\"
          }
        ]
      }
    ]
  }
}"]]]

        [:section
         [:h1 "Exemplo prático: notificação no celular"]
         [:p "Usando " [:a {:href "https://ntfy.sh"} "ntfy.sh"] " — push notifications via HTTP"]
         [:pre [:code "{
  \"hooks\": {
    \"Stop\": [{
      \"hooks\": [{
        \"type\": \"command\",
        \"command\": \"curl -d 'Claude terminou!' ntfy.sh/meu-canal\"
      }]
    }],
    \"Notification\": [{
      \"hooks\": [{
        \"type\": \"command\",
        \"command\": \"curl -d 'Claude precisa de você!' ntfy.sh/meu-canal\"
      }]
    }]
  }
}"]]]

        [:section
         [:h1 "Resultado"]
         [:ul
          [:li "Você manda o Claude trabalhar"]
          [:li "Vai fazer outra task"]
          [:li "O celular vibra"]
          [:li "Você volta"]]
         [:p "Sem ficar olhando pra tela."]]

        [:section
         [:h1 "Outros casos de uso"]
         [:ul
          [:li "Bloquear comandos perigosos com " [:code "PreToolUse"]]
          [:li "Lint após edições de arquivo"]
          [:li "Rodar testes automaticamente após mudanças"]]]

        [:section
         [:h1 "Resumo"]
         [:p "Hooks = você e o Claude trabalhando em paralelo."]
         [:p "Configure uma vez. Funciona sempre."]
         [:p [:strong "Sem babá."]]]

        ]])

; *** implementation details *** ;

(defonce state (r/atom nil))

(defn get-slide-count []
      (aget
        (js/document.querySelectorAll "section")
        "length"))

(defn keydown
      [ev]
      (let [k (aget ev "keyCode")]
           (cond
             (contains? #{37 38 33} k)
             (swap! state update :slide dec)
             (contains? #{39 40 32 13 34} k)
             (swap! state update :slide inc)
             (contains? #{27 72 36} k)
             (swap! state assoc :slide 0)
             (contains? #{35} k)
             (swap! state assoc :slide (dec (get-slide-count))))))

(defn tap
      [ev]
      (when (= (aget ev "target") (aget js/document "body"))
            (let [x (aget ev "clientX")
                  w (aget js/window "innerWidth")]
                 (if (< x (/ w 2))
                   (swap! state update :slide dec)
                   (swap! state update :slide inc)))))

(defn component:show-slide [state]
      [:style (str "section:nth-child("
                   (inc (mod (:slide @state)
                             (get-slide-count)))
                   ") { display: block; }")])

(rdom/render [:<> [slides] [component:show-slide state]]
             (.getElementById js/document "app"))
(defonce keylistener (aset js/window "onkeydown" #(keydown %)))
(defonce taplistener (aset js/window "onclick" #(tap %)))
; trigger a second render so we get the sections count
(swap! state assoc :slide 0)
