// Очистка сообщения об ошибке при вводе в любое поле
function clearErrorMessage() {
    document.getElementById('errorMessage').innerHTML = '';
}


// Функция для валидации формы входа
function validateForm() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    let isValid = true;

    // Проверка на пустые поля
    if (!username || !password) {
        displayErrorMessage("Пожалуйста, заполните все поля.");
        isValid = false;
    }

    // Если все проверки пройдены, форма отправляется на сервер
    return isValid;
}

// Функция для отображения ошибок
function displayErrorMessage(message) {
    const errorMessageContainer = document.getElementById("errorMessage");
    const errorMessageElement = document.createElement("div");
    errorMessageElement.classList.add("error");
    errorMessageElement.textContent = message;
    errorMessageContainer.appendChild(errorMessageElement);
}
