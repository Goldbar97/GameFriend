document.addEventListener('DOMContentLoaded', () => {
  const password = document.getElementById('InputPassword');
  const passwordVerify = document.getElementById('InputPasswordVerify');
  const passwordHelp = document.getElementById('passwordHelp');

  passwordVerify.addEventListener('input', () => {
    if (password.value !== passwordVerify.value) {
      passwordHelp.classList.remove('text-success');
      passwordHelp.classList.add('text-danger');
      passwordHelp.textContent = '비밀번호가 일치하지 않습니다.';
    } else {
      passwordHelp.classList.remove('text-danger');
      passwordHelp.classList.add('text-success');
      passwordHelp.textContent = '비밀번호가 일치합니다.';
    }
  });
});