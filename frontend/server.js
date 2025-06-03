const express = require('express');
const path = require('path');
const app = express();

// Serve everything under /public as static files
app.use(express.static(path.join(__dirname, 'public')));

// No catch-all route needed anymore.

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Frontend server running at http://localhost:${PORT}`);
});
