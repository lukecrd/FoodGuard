# FoodGuard 🛡️

App Android (Kotlin + Jetpack Compose) per gestire una lista personale di **cibi vietati / consentiti**
e per **scansionare il codice a barre** dei prodotti alimentari, verificando automaticamente se contengono
allergeni o ingredienti che hai segnato come vietati.

## Funzionalità

- **Lista personale**: aggiungi cibi con stato "Vietato" o "Consentito", e marca quelli che sono allergeni.
- **Scanner con fotocamera**: usa CameraX + ML Kit per leggere i codici a barre (EAN/UPC) in tempo reale.
- **Analisi automatica**: il codice viene inviato al database pubblico [Open Food Facts](https://world.openfoodfacts.org/)
  per recuperare ingredienti e allergeni ufficiali del prodotto, poi confrontati con la tua lista.
- **Verdetto immediato**: SICURO / VIETATO / DATI INSUFFICIENTI, con evidenziati gli ingredienti che hanno
  fatto scattare l'allarme.
- **Grafica hightech**: tema scuro con accenti neon (cyan, viola, verde, rosso), card con effetto glow,
  font monospace per titoli.

## Come aprire il progetto

1. Apri **Android Studio** (versione Koala/2024.1 o successiva consigliata).
2. `File → Open` e seleziona la cartella `FoodGuard` (questa cartella).
3. Lascia che Gradle sincronizzi le dipendenze (serve connessione internet la prima volta, per scaricare
   le librerie: Compose, Room, CameraX, ML Kit, Retrofit).
4. Collega un dispositivo/emulatore Android **con fotocamera funzionante** (minSdk 26, Android 8.0+).
5. Premi ▶️ Run.

> Nota: gli emulatori senza fotocamera virtuale configurata non permettono di testare lo scanner:
> in AVD Manager imposta "Front/Back Camera" su "Webcam0" o "Emulated".

## Struttura del progetto

```
app/src/main/java/com/hightech/foodguard/
├── data/          → Entity, DAO e Database Room (lista cibi)
├── network/       → Retrofit + modelli per Open Food Facts
├── analysis/       → FoodAnalyzer: confronta prodotto scansionato vs lista utente
├── scanner/       → BarcodeAnalyzer (ML Kit) + ScannerScreen (CameraX + UI)
├── ui/
│   ├── theme/     → Palette colori neon, tipografia, Material3 theme
│   ├── components/→ GlowCard (componente riutilizzabile con bordo luminoso)
│   ├── screens/   → HomeScreen, FoodListScreen
│   └── navigation/→ Navigazione Compose tra le schermate
├── viewmodel/     → FoodViewModel, ScanViewModel, Factory
└── MainActivity.kt
```

## Come funziona l'analisi

1. Scansioni un codice a barre → `BarcodeAnalyzer` (ML Kit) lo intercetta dal flusso della fotocamera.
2. `ScanViewModel` chiama l'API Open Food Facts (`GET /api/v2/product/{barcode}.json`).
3. `FoodAnalyzer` normalizza il testo (minuscolo, senza accenti) e confronta:
   - ogni voce "Vietato" della tua lista con il testo `ingredients_text` del prodotto;
   - le voci marcate come "allergene" anche con i tag ufficiali `allergens_tags` / `traces_tags`.
4. Se trova corrispondenze → verdetto **VIETATO**, con l'elenco degli ingredienti incriminati.
   Se il prodotto non ha dati sufficienti → **ATTENZIONE**. Altrimenti → **SICURO**.

## Possibili estensioni future

- Cache locale dei prodotti già scansionati (per funzionare offline sui prodotti già visti).
- Storico delle scansioni.
- Condivisione della lista tra dispositivi (es. per famiglie con più allergie da gestire).
- Riconoscimento fuzzy/multilingua degli ingredienti (es. sinonimi "latte" / "milk" / "lactose").
- Widget home screen con scansione rapida.

## Dipendenze principali

- Jetpack Compose + Material 3
- Room (persistenza locale)
- CameraX + ML Kit Barcode Scanning
- Retrofit + Gson (chiamate a Open Food Facts, API pubblica e gratuita, nessuna API key richiesta)
- Accompanist Permissions (gestione permesso fotocamera)
