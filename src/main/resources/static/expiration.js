document.addEventListener('DOMContentLoaded', function() {
  const token = sessionStorage.getItem('token');
  const expirationTime = sessionStorage.getItem('expirationTime');

  if (token && expirationTime) {
    if (Date.now() > expirationTime) {
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('expirationTime');
      alert('세션이 만료되었습니다. 다시 로그인 해주세요.');
      window.location.href = 'signin.html'; // 로그인 페이지로 이동
    }
  }
});