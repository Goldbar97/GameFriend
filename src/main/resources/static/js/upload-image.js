document.addEventListener('DOMContentLoaded', function () {
  const token = sessionStorage.getItem('token');
  const imageInput = document.getElementById('imageUpload');
  const imagePreview = document.getElementById('imagePreview');

  if (!token) {
    alert('로그인이 필요한 서비스입니다.');
    window.location.href = 'signin.html';
  }
  imagePreview.src = sessionStorage.getItem('imageUrl');

  document.getElementById('imageUpload').addEventListener('change',
      function (event) {
        const file = event.target.files[0];
        if (file) {
          imagePreview.src = URL.createObjectURL(file); // URL.createObjectURL로 이미지 미리보기 설정
        }
      });

  // 업로드 폼 처리 (여기서는 기본적으로 제출 막기)
  document.getElementById('uploadForm').addEventListener('submit',
      function (event) {
        event.preventDefault(); // 폼의 기본 제출 동작을 막음

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
            alert('이미지가 성공적으로 업로드되었습니다.');
          } else {
            alert('이미지 업로드에 실패했습니다.');
          }
          return response.json();
        })
        .then(data => {
          const imageUrl = data.responseBody.imageUrl;
          sessionStorage.setItem('imageUrl', imageUrl);
        })
        .catch(error => {
          console.error('에러 발생:', error);
          alert('업로드 중 문제가 발생했습니다.');
        });
      });
});