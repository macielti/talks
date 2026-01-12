(ns slides
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn slides []
      [:<>
       [:main
        [:section
         [:h1 "Engenharia Reversa de aplicações web por diversão e lucro"]
         [:h2 "Automação de investimentos com Clojure"]
         [:footer
          [:small
           [:a {:href "https://github.com/chr15m/scittle-tiny-slides"}
            "Made with Scittle Tiny Slides"]]]]

        ;;https://investidorsardinha.r7.com/opiniao/como-encontrar-bons-cdbs/
        [:section
         [:h1 "Certificados de Depósito Bancário (CDB)"]]

        [:section
         [:h1 "Certificados de Depósito Bancário (CDB)"]
         [:img {:src "images/CDB.jpg"}]]

        [:section
         [:h1 "Certificados de Depósito Bancário (CDB)"]
         [:ul
          [:i "Todo CDB tem um prazo de vencimento"]]]

        [:section
         [:h1 "E se eu quiser resgatar o valor investido antes do prazo?"]
         [:p "É necessário encontrar alguém disposto a comprar esse título da sua mão."]]

        [:section
         [:h1 "Onde acontece a negociação desses CDBs resgatados antes do prazo?"]]

        [:section
         [:h1 "Mercado Secundário"]
         [:p "Os títulos vão para o mercado secundário quando um cliente quer vender antecipadamente (antes do prazo de vencimento), nesse caso, o ativo é devolvido para a corretora que acaba repassando para outro cliente."]]

        [:section
         [:h1 "Mercado Secundário - XP Investimentos"]
         [:p "Quando um cliente vende o seu título antes do vencimento, a XP coloca à disposição para outros investidores. Isso quer dizer que poucas quantidades são disponibilizadas, pois é um ativo que partiu de outro cliente."]
         [:p "O mercado secundário é bem limitado, pode ocorrer da oferta não atender a demanda, o que pode fazer com que essas cotas se encerrem em segundos."]]

        ; O primeiro ponto motivador para a automação do processo de investimento nos CDBs do mercado secundário é a sua
        ; escassez, onde é crucial que sejamos mais rápidos que os seres humanos no processo de tomada de decisão e também
        ; na execução da operação de compra do CDB. Colocando uma automação no processo conseguimos romper essa barreira da
        ; disputa pelos melhores títulos, onde quem executa a ordem primeiro é quem tem êxito.

        [:section
         [:h1 "\"Não há almoços grátis\""]
         [:p "A corretora não retorna o valor investido acrescido do rendimento proporcional até a data do resgate."]]

        ;Quem vende o CDB no mercado secundário assume um deságio (desconto sobre o rendimento) em prol de aumentar a liquidez do ativo que ele está se desfazendo.
        ;Uma parte do desconto sobre o rendimento é pago para a corretora, e a outra parte entra como um rendimento adicional ao CDB para tornar o título mais atrativo aumentando a liquidez.
        ;Por exemplo, o pessoal que estava vendendo os títulos do Banco Master não encontrariam compradores se não compensassem o risco renunciando a boa parte do rendimento.

        [:section
         [:img {:src "images/infochart_CDB.png"}]]

        [:section
         [:h1 "Análise de Rentabilidade por Prazo de Vencimento - Prefixado"]
         [:img {:src "images/secondary_market_moths_to_release_by_yield.png"}]]

        [:section
         [:h1 "Dito isso..."]
         [:ul
          [:li "Conseguimos encontrar no Mercado Secundário de CDBs ativos com taxas de rendimento acima da média do mercado."]
          [:li "Poucos ativos disponíveis, oportunidades se esgotam em segundos."]]]

        ; A barreira principal para acesso às oportunidades mais rentáveis do mercado secundário de CDBs é a velocidade com que eles se esgotam.

        [:section
         [:h1 "Solução"]
         [:img {:src "images/bot_illustration.png"}]]

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
