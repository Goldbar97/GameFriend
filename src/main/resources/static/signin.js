function signIn(event) {
  event.preventDefault();

  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value.trim();

  if (!username || !password) {
    alert('아이디와 비밀번호를 입력하세요.');
    return;
  }

  fetch('http://localhost:8080/api/users/signin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      username: username,
      password: password
    })
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('로그인 실패');
    }
    return response.json();
  })
  .then(data => {
    const token = data.responseBody;

    sessionStorage.setItem('token', token);

    const expirationTime = Date.now() + 3600000;
    sessionStorage.setItem('expirationTime', expirationTime);

    window.location.href = 'index.html';
  })
  .catch(error => {
    console.error('Error:', error);
    alert('로그인에 실패했습니다.');
  })
}