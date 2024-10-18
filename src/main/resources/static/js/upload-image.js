function resetProfileImage() {
  const imageUrl = sessionStorage.getItem('imageUrl');

  if (imageUrl !== 'src/default-profile-image.png') {
    fetchDeleteRequest(imageUrl);
    alert('프로필 이미지를 초기화했습니다.');
    location.reload();
  } else {
    alert('이미 기본 프로필 이미지를 사용중입니다.');
  }
}

function updateNickname() {
  const nickname = sessionStorage.getItem('nickname');
  const nicknameValue = document.getElementById('nicknameInput').value;
  const token = sessionStorage.getItem('token');

  if (nickname === nicknameValue) {
    alert('이미 사용중인 닉네임입니다.');
    return;
  }

  fetch('/api/users/nickname', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({nickname: nicknameValue})
  })
  .then(response => {
    if (!response.ok) {
      alert('닉네임 변경에 실패했습니다.');
      return;
    }
    return response.json();
  })
  .then(data => {
    const responseBody = data.responseBody;
    const nickname = responseBody.nickname;

    alert('닉네임을 성공적으로 변경했습니다.');
    sessionStorage.setItem('nickname', nickname);
    location.reload();
  })
}

function fetchDeleteRequest(imageUrl) {
  const token = sessionStorage.getItem('token');

  fetch('/api/users/profile-image', {
    method: 'DELETE',
    body: JSON.stringify({
      imageUrl: imageUrl
    }),
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  })
  .then(response => {
    if (!response.ok) {
      alert('이미지 삭제에 실패했습니다.');
      return;
    }

    sessionStorage.setItem('imageUrl', 'src/default-profile-image.png');
  })
}

document.addEventListener('DOMContentLoaded', function () {
  const token = sessionStorage.getItem('token');
  const imageInput = document.getElementById('imageUpload');
  const imagePreview = document.getElementById('imagePreview');
  const nicknameInput = document.getElementById('nicknameInput');

  if (!token) {
    alert('로그인이 필요한 서비스입니다.');
    window.location.href = 'signin.html';
  }

  const nickname = sessionStorage.getItem('nickname');
  const imageUrl = sessionStorage.getItem('imageUrl');
  nicknameInput.value = nickname;
  imagePreview.src = imageUrl;

  document.getElementById('imageUpload').addEventListener('change',
      function (event) {
        const file = event.target.files[0];
        if (file) {
          imagePreview.src = URL.createObjectURL(file); // URL.createObjectURL로 이미지 미리보기 설정
        }
      }
  );

  // 업로드 폼 처리 (여기서는 기본적으로 제출 막기)
  document.getElementById('uploadForm').addEventListener('submit',
      function (event) {
        event.preventDefault(); // 폼의 기본 제출 동작을 막음

        if (!imageInput.files || imageInput.files.length === 0) {
          alert('파일을 선택하세요.');
          return;
        }

        if (imageUrl !== 'src/default-profile-image.png') {
          fetchDeleteRequest(imageUrl);
        }

        const formData = new FormData();
        formData.append('file', imageInput.files[0]);

        fetch('/api/users/profile-image', {
          method: 'POST',
          body: formData,
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        .then(response => {
          if (response.ok) {
            return response.json();
          } else {
            alert('이미지 업로드에 실패했습니다.');
            throw new Error('Upload failed');
          }
        })
        .then(data => {
          const imageUrl = data.responseBody.imageUrl;
          sessionStorage.setItem('imageUrl', imageUrl);
          alert('이미지가 성공적으로 업로드 되었습니다.');
          location.reload();
        })
        .catch(error => {
          console.error('에러 발생:', error);
          alert('업로드 중 문제가 발생했습니다.');
        });
      }
  );
});