# Monitora Grama — MVP Android

MVP do **Monitoramento Inteligente de Grama** (CCR Motiva). Um carro com câmera de visão computacional mede a altura da grama nos trechos de rodovia a cada 2 semanas. O app mostra os trechos, prevê a data do próximo corte (limite de **30 cm**) e permite registrar novas medições.

Feito em **Kotlin + Jetpack Compose**, com arquitetura em camadas (`ui` / `domain` / `data`) e MVVM.

## Fluxo do MVP

```
Login ──► Dashboard (trechos) ──► Detalhe do trecho ──► Nova medição
  ▲              │                        ▲                   │
  └── sair ──────┘                        └──── salvar ───────┘
```

| 1. Login | 2. Dashboard | 3. Detalhe | 4. Nova medição | 5. Após salvar |
|---|---|---|---|---|
| ![](docs/prints/1_login.png) | ![](docs/prints/2_dashboard.png) | ![](docs/prints/3_detalhe.png) | ![](docs/prints/4_nova_medicao.png) | ![](docs/prints/5_detalhe_atualizado.png) |

Wireframe usado como especificação: [`docs/wireframe.svg`](docs/wireframe.svg)

## Arquitetura (app/src/main/)

```
manifests/AndroidManifest.xml       Application + MainActivity (launcher)
java/br/com/monitoragrama/
├── MainActivity.kt                 Entrada: aplica tema e navegação
├── MonitoraGramaApp.kt             Application: cria o AppContainer
├── di/AppContainer.kt              Injeção de dependências manual
├── domain/                         Regras de negócio (Kotlin puro, sem Android)
│   ├── model/                      Trecho, Medicao, PrevisaoCorte, RegrasCorte
│   ├── repository/                 Interface TrechoRepository
│   └── usecase/                    PreverCorte, ObservarTrechos, ObservarTrecho,
│                                   RegistrarMedicao, ValidarLogin
├── data/                           Acesso a dados
│   ├── local/TrechoLocalDataSource Simula o banco que recebe as medições da câmera
│   └── repository/TrechoRepositoryImpl  StateFlow em memória
└── ui/                             Interface (Compose)
    ├── theme/                      Tokens: Color, Dimens, Type, Theme
    ├── components/                 AppTopBar, StatusBadge, AlertaStatus, GraficoCrescimento
    ├── navigation/AppNavHost.kt    Rotas e NavHost
    ├── login/  dashboard/  detalhe/  medicao/   Screen + ViewModel de cada tela
    └── common/                     Utilitários de ViewModel
res/
├── drawable/                       Ícones vetoriais extraídos do wireframe
├── mipmap-anydpi/                  Ícone adaptativo do app
└── values/                         strings.xml (+ plurals), colors.xml, themes.xml
```

**Regra de dependência:** `ui → domain ← data`. O `domain` não conhece Android nem a camada `data`; as telas só falam com ViewModels, que chamam casos de uso.

### Regra de previsão (domain/usecase/PreverCorteUseCase.kt)

- Taxa de crescimento = taxa da espécie (Braquiária 1,2 · Batatais 0,8 · Esmeralda 0,5 cm/dia) × fator do clima (Chuvoso 1,4 · Temperado 1,0 · Seco 0,6).
- Altura estimada hoje = última medição + taxa × dias desde a medição.
- **Crítico:** já passou de 30 cm · **Atenção:** passa de 30 cm antes da próxima vistoria (14 dias) · **Em dia:** caso contrário.

## Boas práticas aplicadas

- Nenhum texto fixo no código: tudo em `strings.xml` (com `plurals`); cores e espaçamentos em tokens (`Color.kt`, `Dimens.kt`).
- Estado com `StateFlow` + `collectAsStateWithLifecycle` (respeita o ciclo de vida); ViewModels sobrevivem à rotação.
- Argumentos de navegação lidos via `SavedStateHandle`.
- Eventos de navegação consumidos uma única vez; cliques repetidos ignorados enquanto a tela sai (`estaAtiva()`).
- Validação de entradas no domínio (login e altura entre 0,1 e 200 cm).
- Testes unitários da regra de negócio em `app/src/test`.

## Como executar

1. Abra a pasta no **Android Studio** e aguarde o Gradle sincronizar.
2. Selecione um emulador (API 26+) e clique em **Run ▶**.
3. Login: qualquer matrícula e senha com 4+ caracteres (ex.: `rm12345` / `senha123`).

Testes: `./gradlew testDebugUnitTest`

## Sprint (4 semanas)

| Semana | Entrega |
|---|---|
| 1 — Esboço e arquitetura | Wireframe das 4 telas e mapa de navegação (`docs/wireframe.svg`); definição das camadas ui/domain/data |
| 2 — Setup e recursos | Projeto Gradle (version catalog), ícones em `res/drawable`, `strings.xml`, `colors.xml`, `themes.xml`, tokens Compose |
| 3 — Layouts e regras | Telas em Compose, ViewModels, casos de uso e repositório separados por pacote |
| 4 — Fluxo e validação | Navigation Compose, eventos, `AndroidManifest.xml`, testes unitários e execução no emulador |

**Stack:** Kotlin 2.2 · AGP 8.13 · Compose BOM 2025.08 · Material 3 · Navigation Compose · Lifecycle ViewModel · minSdk 26 / targetSdk 36
