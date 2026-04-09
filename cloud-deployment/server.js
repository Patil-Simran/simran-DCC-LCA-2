const express = require("express");
const path = require("path");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.static(path.join(__dirname, "public")));

app.get("/api/health", (_req, res) => {
  res.json({
    ok: true,
    service: "DCC-LCA Cloud Demo",
    time: new Date().toISOString(),
  });
});

app.listen(PORT, () => {
  console.log(`Listening on port ${PORT}`);
});
