let socket;
let clickCount = 0;

document.getElementById('connect').addEventListener('click', () => {
  socket = new WebSocket('ws://localhost:8080');
  socket.onopen = () => {
    document.getElementById('info').innerText = 'Sunucuya bağlanıldı.';
    document.getElementById('start').disabled = false;
  };

  socket.onmessage = (event) => {
    const data = JSON.parse(event.data);
    if (data.type === 'start') {
      document.getElementById('click').disabled = false;
      document.getElementById('info').innerText = 'Oyun başladı!';
    } else if (data.type === 'result') {
      document.getElementById('click').disabled = true;
      document.getElementById('result').innerText = `Kazanan: ${data.winner}, Tıklamalar: ${data.clicks}`;
    }
  };
});

document.getElementById('start').addEventListener('click', () => {
  socket.send(JSON.stringify({ type: 'start' }));
});

document.getElementById('click').addEventListener('click', () => {
  clickCount++;
  socket.send(JSON.stringify({ type: 'click', clicks: clickCount }));
});
