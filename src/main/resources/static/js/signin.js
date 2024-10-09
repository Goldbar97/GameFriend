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
    if (response.status === 400) {
      alert('비밀번호를 틀렸습니다.');
      throw new Error('틀린 비밀번호');
    } else if (response.status === 404) {
      alert('존재하지 않는 아이디입니다.');
      throw new Error('없는 아이디');
    } else if (response.status === 423) {
      alert('연속 로그인 실패로 계정이 일시적으로 잠겼습니다.');
      throw new Error('잠긴 아이디');
    } else if (!response.ok) {
      alert('서버 오류로 로그인에 실패했습니다.');
      throw new Error('로그인 실패');
    }
    return response.json();
  })
  .then(data => {
    const responseBody = data.responseBody;
    const userDTO = responseBody.userDTO;
    const imageUrl = userDTO.imageUrl;
    const token = responseBody.token;
    const expirationTime = Date.now() + 3600000;

    sessionStorage.setItem('token', token);
    sessionStorage.setItem('expirationTime', expirationTime);
    sessionStorage.setItem('imageUrl', imageUrl);

    window.location.href = 'index.html';
  })
  .catch(error => {
    console.error('Error:', error);
  })
}