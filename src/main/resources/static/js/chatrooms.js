function fetchCategoryAndChatrooms(categoryId) {
  // 카테고리 정보와 채팅방 목록을 가져오기
  fetch(`http://localhost:8080/api/categories/${categoryId}/chatrooms`)
  .then(response => response.json())
  .then(data => {
    const chatrooms = data.responseBody; // 채팅방 리스트

    // 채팅방 목록 렌더링
    renderChatrooms(categoryId, chatrooms);
  })
  .catch(
      error => console.error('Error fetching category and chatrooms:', error));
}

function createChatroom() {
  const urlParams = new URLSearchParams(window.location.search);
  const categoryId = urlParams.get('categoryId');
  const token = sessionStorage.getItem('token');

  if (!token) {
    alert('로그인이 필요한 서비스입니다.')
    window.location.href = 'signin.html';
  } else if (categoryId) {
    window.location.href = `create-chatroom.html?categoryId=${categoryId}`;
  } else {
    alert("카테고리 ID를 찾을 수 없습니다.");
  }
}

// 검색된 채팅방 가져오기
function searchChatroom() {
  const urlParams = new URLSearchParams(window.location.search);
  const categoryId = urlParams.get('categoryId');
  const searchTerm = document.getElementById('chatroomSearch').value;

  // 서버로 검색 요청
  fetch(
      `/api/categories/${categoryId}/chatrooms/search?query=${encodeURIComponent(
          searchTerm)}`)
  .then(response => response.json())
  .then(data => {
    const chatrooms = data.responseBody;
    renderChatrooms(categoryId, chatrooms);  // 검색된 카테고리 리스트 렌더링
  })
  .catch(error => console.error('Error fetching categories:', error));
}

function renderChatrooms(categoryId, chatrooms) {
  const chatroomList = document.getElementById('chatroomList');
  chatroomList.innerHTML = '';  // 기존 목록 초기화

  chatrooms.forEach(chatroom => {
    const listItem = document.createElement('li');
    listItem.classList.add('list-group-item', 'd-flex',
        'justify-content-between', 'align-items-center');
    listItem.innerHTML = `
      <div>
        <h5>${chatroom.title}</h5>
        <small>생성자: ${chatroom.createdBy}</small></br>
        <small>참여자 수: ${chatroom.present}명</small></br>
        <small>인원 제한: ${chatroom.capacity}명</small>
      </div>
          <!-- 배지와 버튼을 감싸는 컨테이너 -->
      <div class="d-flex align-items-center">
        <span class="badge bg-primary rounded-pill me-3">${chatroom.present}/${chatroom.capacity}</span>
        <a href="chatroom.html?categoryId=${categoryId}&chatroomId=${chatroom.id}" class="btn btn-outline-primary">입장</a>
      </div>
    `;
    chatroomList.appendChild(listItem);
  });
}

document.addEventListener('DOMContentLoaded', function () {
  const urlParams = new URLSearchParams(window.location.search);
  const categoryName = decodeURIComponent(urlParams.get('categoryName'));
  const categoryId = urlParams.get('categoryId');
  const categoryNameDisplay = document.getElementById('categoryNameDisplay');

  // 카테고리 이름이 있을 때, 타이틀에 반영
  if (categoryName) {
    document.title = `Game Friend Chatrooms - ${categoryName}`;
    categoryNameDisplay.textContent = decodeURIComponent(categoryName);
  }

  // 카테고리 ID가 있으면, 해당 ID로 채팅방 목록을 불러옴
  if (categoryId) {
    fetchCategoryAndChatrooms(categoryId);
  }
});