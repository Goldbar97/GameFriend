function isCheckedDupe() {

}

function checkDupe() {
  const username = document.getElementById('username').value.trim();

  if (!username) {
    alert('아이디를 입력하세요.');
    return;
  }

  fetch('http://141.164.45.30:8080/api/users/check-duplication', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({username: username})
  })
  .then(response => {
    if (response.status === 409) {
      alert("이미 존재하는 아이디입니다.")
    } else if (response.status === 200) {
      alert("사용 가능한 아이디입니다.")
    } else {
      throw new Error('Unexpected response');
    }
  })
  .catch(error => {
    console.error('Error checking duplication:', error);
    alert('중복 확인 중 오류가 발생했습니다.');
  });
}

function signUp() {
  event.preventDefault();

  const username = document.getElementById('username').value.trim();
  const nickname = document.getElementById('nickname').value.trim();
  const password = document.getElementById('password').value.trim();
  const passwordVerify = document.getElementById('passwordVerify').value.trim();

  if (!username || !nickname || !password || !passwordVerify) {
    alert('입력하지 않은 정보가 있습니다.');
    return;
  }

  fetch('http://141.164.45.30:8080/api/users/signup', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      username: username,
      nickname: nickname,
      password: password,
      passwordVerify: passwordVerify
    })
  })
  .then(response => {
    if (!response.ok) {
      // response.json() 자체가 비동기 작업이므로 .then()으로 처리해야 함
      return response.json().then(body => {
        const responseBody = body.responseBody;
        let alertMessage = '';
        for (const [key, value] of Object.entries(responseBody)) {
          alertMessage += `${key}: ${value}\n`;
        }
        alert(alertMessage);
        throw new Error('회원가입 실패');
      });
    } else {
      alert('회원가입을 축하드립니다.');
      window.location.href = 'signin.html';
    }
  })
  .catch(error => {
    console.error('Error:', error);
  });
}

function validatePassword(password, passwordVerify, passwordHelp) {
  if (password.value === '' || passwordVerify.value === '') {
    passwordHelp.classList.remove('text-success');
    passwordHelp.classList.add('text-danger');
    passwordHelp.textContent = '비밀번호를 입력하세요.';
  } else if (password.value !== passwordVerify.value) {
    passwordHelp.classList.remove('text-success');
    passwordHelp.classList.add('text-danger');
    passwordHelp.textContent = '비밀번호가 일치하지 않습니다.';
  } else {
    passwordHelp.classList.remove('text-danger');
    passwordHelp.classList.add('text-success');
    passwordHelp.textContent = '비밀번호가 일치합니다.';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const password = document.getElementById('password');
  const passwordVerify = document.getElementById('passwordVerify');
  const passwordHelp = document.getElementById('passwordHelp');

  // 매개변수로 password, passwordVerify, passwordHelp를 전달
  password.addEventListener('input',
      () => validatePassword(password, passwordVerify, passwordHelp));
  passwordVerify.addEventListener('input',
      () => validatePassword(password, passwordVerify, passwordHelp));
});