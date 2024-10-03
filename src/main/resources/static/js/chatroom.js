let urlParam = null;
let categoryId = null;
let chatroomId = null;
let token = null;
let chatBox = null;
let stompClient = null;

function wsConnect() {
  const socket = new SockJS('/ws/chat');  // WebSocket 엔드포인트
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

              const nickname = messageBody.nickname;
              const imageUrl = messageBody.imageUrl
                  ?? 'src/default-profile-image.png';
              const chatMessage = messageBody.message;
              const createdAt = messageBody.createdAt;

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
            });
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
    stompClient.send(`/app/categories/${categoryId}/chatrooms/${chatroomId}`,
        {}, message);
    messageInput.value = ""; // 입력란 초기화
  }
}

function sendParticipant() {

}

function confirmLeave() {
  if (confirm('정말 채팅방에서 나가시겠습니까?')) {
    leaveChatroom();
  }
}

function leaveChatroom() {
  const xhr = new XMLHttpRequest();
  xhr.open('DELETE',
      `/api/categories/${categoryId}/chatrooms/${chatroomId}/leave`, false);
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.setRequestHeader('Authorization', `Bearer ${token}`)
  xhr.send();

  window.location.href = 'index.html';
}

function addParticipantToList(listGroup, nickname) {
  const groupItem = document.createElement('li');
  groupItem.classList.add('list-group-item');
  groupItem.textContent = nickname;
  listGroup.appendChild(groupItem);
}

document.addEventListener('DOMContentLoaded', function () {
  // 입장 메시지 설정 (백엔드에서 데이터를 받아와 설정)
  urlParam = new URLSearchParams(window.location.search);
  categoryId = urlParam.get('categoryId');
  chatroomId = urlParam.get('chatroomId');
  token = sessionStorage.getItem('token');
  chatBox = document.getElementById("chatMessages");

  if (!token) {
    alert('로그인이 필요한 서비스입니다.');
    window.location.href = 'signin.html';
  }

  fetch(
      `http://localhost:8080/api/categories/${categoryId}/chatrooms/${chatroomId}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      })
  .then(response => {
    if (response.status === 404) {
      alert('존재하지 않는 채팅방입니다.');
      window.location.href = 'index.html';
    }

    return response.json();
  })
  .then(data => {
    const chatroom = data.responseBody;
    const chatroomName = chatroom.title;
    const entranceMessage = chatroom.entranceMessage;

    document.getElementById('chatroomName').textContent = chatroomName;
    document.getElementById('entranceMessage').textContent = entranceMessage;
  })

  fetch(
      `http://localhost:8080/api/categories/${categoryId}/chatrooms/${chatroomId}/users`,
      {
        method: 'GET'
      })
  .then(response => response.json())
  .then(data => {
    const participants = data.responseBody;
    const listGroup = document.getElementsByClassName('list-group').item(0);
    participants.forEach(participant => {
      addParticipantToList(listGroup, participant.nickname);
    })
  })

  wsConnect();
});