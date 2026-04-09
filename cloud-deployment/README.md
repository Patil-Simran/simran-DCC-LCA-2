# Task 3 — Cloud web app

## Steps — run locally

1. Install [Node.js LTS](https://nodejs.org/).
2. In terminal:
   ```powershell
   cd cloud-deployment
   npm install
   npm start
   ```
3. Browser: `http://localhost:3000` → click **Ping API** (see JSON).

## Steps — deploy (Render)

1. Push repo to **your** GitHub.
2. [render.com](https://render.com) → sign up → connect GitHub → **New Web Service**.
3. Select repo → **Root directory:** `cloud-deployment` → **Build:** `npm install` → **Start:** `npm start`.
4. Wait for deploy → open the `https://....onrender.com` URL in browser → **Ping API** again.
5. Screenshot URL + JSON → `screenshots/`.

*(Free tier may sleep; first load can be slow.)*

## Other hosts

Same: Node 18+, `npm install`, `npm start`, platform sets `PORT`.
