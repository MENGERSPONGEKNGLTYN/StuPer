// Очистка сообщения об ошибке при вводе в любое поле
function clearErrorMessage() {
    document.getElementById('errorMessage').innerHTML = '';
}

// Функция для переключения видимости поля выбора группы
function toggleGroupSelection() {
    const userType = document.getElementById('userType').value;
    const groupSelection = document.getElementById('groupSelection');

    // Если выбран студент, показываем поле выбора группы
    if (userType === 'student') {
        groupSelection.style.display = 'block';
    } else {
        groupSelection.style.display = 'none';
    }
}

// Функция для валидации формы регистрации
function validateForm() {
    console.log("mda")
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const userType = document.getElementById('userType').value;
    const group = document.getElementById('group');

    // Флаг для проверки, если все валидации пройдены
    let isValid = true;

    if (username === "") {
        displayErrorMessage("Пожалуйста, введите логин.");
        isValid = false;
    }

    if (password === "" || confirmPassword === "") {
        displayErrorMessage("Пожалуйста, введите пароль.");
        isValid = false;
    }

    if (password !== confirmPassword) {
        displayErrorMessage("Пароли не совпадают!");
        isValid = false;
    }

    // Проверка для студентов: выбрана ли группа
    if (userType === 'student' && !group.value) {
        displayErrorMessage("Пожалуйста, выберите группу.");
        isValid = false;
    }

    // Если все проверки пройдены, форма отправляется на сервер
    return isValid;
}

// Функция для отображения сообщений об ошибке
function displayErrorMessage(message) {
    const errorMessageContainer = document.getElementById("errorMessage");
    const errorMessageElement = document.createElement("div");
    errorMessageElement.classList.add("error");
    errorMessageElement.textContent = message;
    errorMessageContainer.appendChild(errorMessageElement);
}
