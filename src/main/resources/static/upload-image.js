document.addEventListener('DOMContentLoaded', function() {
  document.getElementById('imageUpload').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
      const imagePreview = document.getElementById('imagePreview');
      imagePreview.src = URL.createObjectURL(file); // URL.createObjectURL로 이미지 미리보기 설정
    }
  });

  // 업로드 폼 처리 (여기서는 기본적으로 제출 막기)
  document.getElementById('uploadForm').addEventListener('submit', function(event) {
    event.preventDefault();
    alert('이미지 업로드 기능이 아직 구현되지 않았습니다.');
  });
});