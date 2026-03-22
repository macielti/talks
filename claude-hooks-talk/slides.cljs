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
         [:h1 "Modo focado vs. modo difuso"]
         [:p "Ficar olhando pro Claude pode estar te prejudicando."]
         [:ul
          [:li [:strong "Modo focado"] ": concentração direta num problema — você está travado nele"]
          ; Nesse modo o cérebro é como um feixe de laser, focado numa coisa só. Ótimo para tarefas diretas,
          ; mas não para criatividade, ele foca em um único caminho de pensamento, e se esse caminho não levar a uma solução, você fica preso.
          ; Um exemplo mais clássico é quando deixamos passar um erro de digitação no código, e o cérebro fica preso
          ; tentando entender o que o código faz, sem perceber que o problema é um erro de digitação.
          [:li [:strong "Modo difuso"] ": pensamento relaxado — o cérebro conecta ideias livremente"]]
         ; Nesse modo o cérebro é como um céu estrelado, onde as ideias estão conectadas de forma mais livre,
         ; e isso é ótimo para criatividade, porque permite que o cérebro encontre conexões inesperadas entre ideias.
         ; Um exemplo clássico é quando estamos tomando banho ou fazendo uma caminhada, e de repente temos uma ideia brilhante,
         ; porque o cérebro está relaxado e conectando ideias de forma mais livre.
         [:p "Quando você se desconecta, seu cérebro trabalha em modo difuso."]
         [:p "É aí que surgem as melhores ideias."]
         [:footer [:small "Conceito do curso " [:em "Learning How to Learn"] " — Barbara Oakley (Coursera)"]]]

        [:section
         [:h1 "Sonho de Princesa"]
         [:ul
          [:li "Mandar o Claude trabalhar"]
          [:li "Ir fazer outra coisa (Colocar o cérebro em modo difuso, ou dar atenção a outras tarefas)"]
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
         [:h1 "Ou simplesmente..."]
         [:p "Peça pro Claude configurar pra você"]
         [:blockquote
          [:p "\"Adicione hooks no meu ~/.claude/settings.json para me
notificar via ntfy.sh no canal 'meu-canal' quando o Claude
terminar uma tarefa e quando precisar de input meu. Use
mensagens genéricas para não vazar informações sobre o que
estou trabalhando.\""]]]

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
