const express = require('express');
const http = require('node:http');
const path = require('node:path');

const app = express();
const port = Number(process.env.PORT || 3000);
const backend = new URL(process.env.API_BASE_URL || 'http://127.0.0.1:8080');
const publicDirectory = path.join(__dirname, 'public');

function proxyToSpring(req, res) {
  const headers = { ...req.headers };
  delete headers.connection;
  delete headers['content-length'];
  headers.host = backend.host;
  // Replace any client-supplied forwarding value with the socket peer address.
  headers['x-forwarded-for'] = req.socket.remoteAddress || '';

  const upstream = http.request({
    protocol: backend.protocol,
    hostname: backend.hostname,
    port: backend.port,
    method: req.method,
    path: req.originalUrl,
    headers
  }, response => {
    res.writeHead(response.statusCode || 502, response.headers);
    response.pipe(res);
  });

  upstream.on('error', () => {
    if (!res.headersSent) res.status(502).json({ message: 'Backend service is unavailable' });
    else res.destroy();
  });
  req.pipe(upstream);
}

app.use(['/api', '/swagger-ui', '/v3/api-docs'], proxyToSpring);
app.get('/swagger-ui.html', proxyToSpring);
app.use(express.static(publicDirectory, { extensions: ['html'] }));
app.listen(port, () => console.log(`Frontend available at http://localhost:${port}`));
