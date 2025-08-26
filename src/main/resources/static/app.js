// Utilidades
const get = (id) => document.getElementById(id);
const show = (id, data) => get(id).textContent = (typeof data === 'string') ? data : JSON.stringify(data, null, 2);

// GET /app/hello?name=...
get('btnHelloGet').addEventListener('click', async () => {
  const name = encodeURIComponent(get('name').value || '');
  const res = await fetch(`/app/hello?name=${name}`);
  const json = await res.json();
  show('respHelloGet', json);
});

// POST /app/hello (name en query o body)
get('btnHelloPost').addEventListener('click', async () => {
  const name = encodeURIComponent(get('namePost').value || '');
  // Enviamos en la query (el backend también soporta leer del body x-www-form-urlencoded)
  const res = await fetch(`/app/hello?name=${name}`, { method: 'POST' });
  const json = await res.json();
  show('respHelloPost', json);
});

// GET /app/time
get('btnTime').addEventListener('click', async () => {
  const res = await fetch('/app/time');
  const json = await res.json();
  show('respTime', json);
});

// GET /app/sum?a=...&b=...
get('btnSum').addEventListener('click', async () => {
  const a = encodeURIComponent(get('a').value || '0');
  const b = encodeURIComponent(get('b').value || '0');
  const res = await fetch(`/app/sum?a=${a}&b=${b}`);
  const json = await res.json();
  show('respSum', json);
});
