function sendMessage() {
  const messageInput = document.getElementById('messageInput').value;
  if (messageInput.trim() !== '') {
    const chatMessages = document.getElementById('chatMessages');
    const newMessage = document.createElement('div');
    newMessage.classList.add('message');
    newMessage.innerHTML = `
        <div class="username">나</div>
        <div class="text">${messageInput}</div>
        <div class="timestamp">지금</div>
      `;
    chatMessages.appendChild(newMessage);
    document.getElementById('messageInput').value = ''; // 입력창 초기화
    chatMessages.scrollTop = chatMessages.scrollHeight; // 스크롤 하단으로 이동
  }
}