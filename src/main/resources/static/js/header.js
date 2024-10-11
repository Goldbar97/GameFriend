function isLoggedIn() {
  return sessionStorage.getItem('token') !== null;
}

function signOut() {
  sessionStorage.removeItem('token');
}

function setNavbar() {
  const navbar = document.getElementsByClassName('navbar').item(0);

  if (isLoggedIn()) {
    fetch('header-logged-in.html')
      .then(response => response.text())
      .then(navbarHtml => {
        navbar.innerHTML = navbarHtml;
        const imageUrl = sessionStorage.getItem('imageUrl');

        // 프로필 이미지 요소를 선택하고 src 속성 변경
        const profileImage = navbar.querySelector('.profile-image');
        if (profileImage && imageUrl) {
          profileImage.src = imageUrl;
        }
      })
      .catch(error => console.error('Error loading header:', error))
  } else {
    fetch('header-logged-out.html')
    .then(response => response.text())
    .then(data => {
      navbar.innerHTML = data;
    })
    .catch(error => console.error('Error loading header:', error))
  }
}

document.addEventListener('DOMContentLoaded', setNavbar);