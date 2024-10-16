let urlParam = null;
let categoryId = null;
let chatroomId = null;
let token = null;
let chatBox = null;
let stompClient = null;
let listGroup = null;
let socket = null;

function wsConnect() {
  socket = new SockJS('/ws/chat');  // WebSocket 엔드포인트
  stompClient = Stomp.over(socket);

  // WebSocket 연결 설정
  stompClient.connect(
      {Authorization: `Bearer ${token}`},  // JWT 토큰을 Authorization 헤더에 포함
      (frame) => {
        console.log('STOMP 연결 성공');
        // 채팅방 구독 (서버에서 인증이 완료된 후 메시지를 받을 수 있음)
        stompClient.subscribe(
            `/topic/categories/${categoryId}/chatrooms/${chatroomId}`,
            (message) => {
              const messageBody = JSON.parse(message.body);

              if (messageBody.type === 'chat') {
                const responseBody = messageBody.responseBody;
                const nickname = responseBody.nickname;
                const imageUrl = responseBody.imageUrl;
                const chatMessage = responseBody.message;
                const createdAt = responseBody.createdAt;
                const messageElement = document.createElement('div');
                messageElement.classList.add('message');
                messageElement.innerHTML = `
              <div class="d-flex align-items-start mb-3">
                <img src="${imageUrl}" alt="Profile Image" class="rounded-circle me-3 profile-image">
                <div class="flex-grow-1">
                  <div class="d-flex justify-content-between">
                    <strong>${nickname}</strong>
                    <small class="text-muted">${new Date(
                    createdAt).toLocaleString()}</small>
                  </div>
                  <div class="bg-light p-2 rounded border mt-1">
                    ${chatMessage}
                  </div>
                </div>
              </div>
            `;
                chatBox.appendChild(messageElement);
                chatBox.scrollTop = chatBox.scrollHeight; // 스크롤 하단으로 이동

              } else if (messageBody.type === 'user') {
                const responseBody = messageBody.responseBody;
                const id = responseBody.id;
                const nickname = responseBody.nickname;
                const imageUrl = responseBody.imageUrl;
                addParticipantToList(id, nickname, imageUrl);

              } else if (messageBody.type === 'delete') {
                alert('방장이 나갔습니다. 방을 나갑니다.');
                window.location.href = 'index.html';

              } else if (messageBody.type === 'leave') {
                const responseBody = messageBody.responseBody;
                const id = responseBody.id;
                removeParticipantToList(id);
              }
            }
        );
        sendParticipant();
      },
      (error) => {
        console.error('STOMP 연결 실패', error);
      }
  );
}

function handleKeyDown(event) {
  if (event.key === 'Enter') {
    sendMessage();
  }
}

function sendMessage() {
  const message = document.getElementById('messageInput').value;
  if (message.trim()) {
    stompClient.send(
        `/app/categories/${categoryId}/chatrooms/${chatroomId}/chat`,
        {}, message);
    messageInput.value = ""; // 입력란 초기화
  }
}

function sendParticipant() {
  stompClient.send(
      `/app/categories/${categoryId}/chatrooms/${chatroomId}/user`, {});
}

function sendLeave() {
  stompClient.send(
      `/app/categories/${categoryId}/chatrooms/${chatroomId}/leave`, {});
}

function confirmLeave() {
  if (confirm('정말 채팅방에서 나가시겠습니까?')) {
    leaveChatroom();
  }
}

function disconnectWebSocket() {
  if (stompClient !== null) {
    stompClient.disconnect(() => {
      console.log('WebSocket Disconnected');
    });
  }
}

function leaveChatroom() {
  sendLeave();
  disconnectWebSocket();
  window.location.href = 'index.html';
}

function addParticipantToList(id, nickname, imageUrl) {
  const listItems = document.querySelectorAll('.list-group-item');

  const isDuplicate = Array.from(listItems).some(
      item => item.getAttribute('data-id') === String(id));

  if (isDuplicate) {
    return;  // 중복된 경우 함수 종료
  }

  const groupItem = document.createElement('li');
  groupItem.classList.add('list-group-item');
  groupItem.setAttribute('data-id', id);
  groupItem.innerHTML = `
    <div class="d-flex align-items-center">
      <img src="${imageUrl}" alt="${nickname}'s profile image" class="rounded-circle small-profile-image me-2">
      ${nickname}
    </div>
  `;
  listGroup.appendChild(groupItem);
}

function removeParticipantToList(id) {
  const listItems = document.querySelectorAll('.list-group-item');

  listItems.forEach(item => {
    if (item.getAttribute('data-id') === String(id)) {
      item.remove();
    }
  });
}

document.addEventListener('DOMContentLoaded', function () {
  urlParam = new URLSearchParams(window.location.search);
  categoryId = urlParam.get('categoryId');
  chatroomId = urlParam.get('chatroomId');
  token = sessionStorage.getItem('token');
  chatBox = document.getElementById("chatMessages");

  if (!token) {
    alert('로그인이 필요한 서비스입니다.');
    window.location.href = 'signin.html';
  }

  fetch(`/api/categories/${categoryId}/chatrooms/${chatroomId}/details`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  .then(response => {
    if (!response.ok) {
      alert('잘못된 접근입니다.');
      window.location.href = 'index.html';
    }
    return response.json();
  })
  .then(data => {
    const details = data.responseBody;
    const chatroomName = details.title;
    const entranceMessage = details.entranceMessage;
    const participants = details.users;
    const chats = details.chats;
    document.getElementById('chatroomName').textContent = chatroomName;
    document.getElementById('entranceMessage').textContent = entranceMessage;
    listGroup = document.getElementsByClassName('list-group').item(0);
    participants.forEach(participant => {
      addParticipantToList(participant.id, participant.nickname,
          participant.imageUrl);
    });
    chats.forEach(chat => {
      const nickname = chat.nickname;
      const imageUrl = chat.imageUrl;
      const message = chat.message;
      const createdAt = chat.createdAt;
      const messageElement = document.createElement('div');
      messageElement.classList.add('message');
      messageElement.innerHTML = `
              <div class="d-flex align-items-start mb-3">
                <img src="${imageUrl}" alt="Profile Image" class="rounded-circle me-3 profile-image">
                <div class="flex-grow-1">
                  <div class="d-flex justify-content-between">
                    <strong>${nickname}</strong>
                    <small class="text-muted">${new Date(
          createdAt).toLocaleString()}</small>
                  </div>
                  <div class="bg-light p-2 rounded border mt-1">
                    ${message}
                  </div>
                </div>
              </div>
            `;
      chatBox.appendChild(messageElement);
    })
    chatBox.scrollTop = chatBox.scrollHeight; // 스크롤 하단으로 이동
    wsConnect();
  })
});