function submitChatroom() {
  // URL에서 categoryId 가져오기
  const urlParams = new URLSearchParams(window.location.search);
  const categoryId = urlParams.get('categoryId');
  const categoryName = urlParams.get('categoryName');
  const title = document.getElementById('chatroomTitle').value;
  const entranceMessage = document.getElementById('entranceMessage').value;
  const capacity = document.getElementById('chatroomCapacity').value;
  const token = sessionStorage.getItem('token');

  if (!token) {
    alert('로그인이 필요한 서비스입니다.');
    window.location.href = 'signin.html';
    return;
  }

  if (!categoryId || !title || !capacity) {
    alert("제목, 인원 필드를 입력하세요.");
    return;
  }

  // 채팅방 생성 API 요청 보내기
  fetch(`http://localhost:8080/api/categories/${categoryId}/chatrooms`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      title: title,
      capacity: parseInt(capacity),
      entranceMessage: entranceMessage
    })
  })
  .then(response => response.json())
  .then(data => {
    alert("채팅방이 생성되었습니다!");
    window.location.href = `chatroom.html?categoryId=${categoryId}&categoryName=${categoryName}`; // 다시 채팅방 목록으로 리디렉션
  })
  .catch(error => console.error('Error:', error));
}