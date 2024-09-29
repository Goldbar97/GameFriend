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
      .then(data => {
        navbar.innerHTML = data;
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