async function searchImages() {
    const searchInput = document.getElementById('searchInput');
    const keyword = searchInput.value;

    // 假设的API端点，你需要替换为实际的API端点
    const apiUrl = '/api/images?keyword=' + keyword;

    try {
        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error('请求失败');
        }
        const resp = await response.json();
        displayImages(resp.result);
    } catch (error) {
        console.error('搜索图片时出错:', error);
        alert('搜索图片时出错，请稍后再试。');
    }
}

function displayImages(images) {
    const imageGallery = document.getElementById('imageGallery');
    imageGallery.innerHTML = ''; // 清空之前的内容

    images.forEach(image => {
        const imgElement = document.createElement('img');
        imgElement.src = image.viewUrl;
        imgElement.alt = image.prompt;
        imgElement.addEventListener('click', function() {
            alert(image.flowName + ": " + image.prompt);
        });
        imageGallery.appendChild(imgElement);
    });
}