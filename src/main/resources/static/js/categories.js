// 카테고리 리스트를 렌더링하는 공통 함수
function renderCategoryList(categories) {
  const categoryList = document.getElementById('categoryList');
  categoryList.innerHTML = '';  // 기존 리스트 초기화

  // 카테고리 리스트 생성
  categories.forEach(category => {
    const listItem = document.createElement('li');
    listItem.classList.add('list-group-item', 'd-flex',
        'justify-content-between', 'align-items-center');
    listItem.innerHTML = `
      <a href="chatrooms.html?categoryId=${category.id}&categoryName=${category.name}" class="category-link text-decoration-none">
        <h5>${category.name}</h5>
        <small>채팅방 개수: ${category.rooms}, 참여자 수: ${category.participants}명</small>
        <div class="badge-container d-flex">
          <span class="badge bg-secondary rounded-pill me-2">${category.rooms}개</span> <!-- 채팅방 개수 배지 -->
          <span class="badge bg-primary rounded-pill">${category.participants}명</span> <!-- 참여자 수 배지 -->
        </div>
      </a>
    `;
    categoryList.appendChild(listItem);
  });
}

// 전체 카테고리 가져오기
function fetchCategories() {
  fetch('http://localhost:8080/api/categories', {
    method: 'GET'
  })
  .then(response => response.json())
  .then(data => {
    const categories = data.responseBody;  // API 응답의 responseBody 리스트로 접근
    renderCategoryList(categories);  // 카테고리 리스트 렌더링
  })
  .catch(error => console.error('Error fetching categories:', error));
}

// 검색된 카테고리 가져오기
function searchCategory() {
  const searchTerm = document.getElementById('categorySearch').value;

  // 서버로 검색 요청
  fetch(`http://localhost:8080/api/categories/search?query=${encodeURIComponent(
      searchTerm)}`)
  .then(response => response.json())
  .then(data => {
    const categories = data.responseBody;
    renderCategoryList(categories);  // 검색된 카테고리 리스트 렌더링
  })
  .catch(error => console.error('Error fetching categories:', error));
}

// 페이지 로드 시 카테고리 리스트를 가져옴
document.addEventListener('DOMContentLoaded', fetchCategories);