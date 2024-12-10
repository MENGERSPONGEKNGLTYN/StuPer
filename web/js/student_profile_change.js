// Очистка сообщения об ошибке при вводе в любое поле
function clearErrorMessage() {
    document.getElementById('errorMessage').innerHTML = '';
}

// Функция для валидации формы профиля студента
function validateForm() {
    const firstName = document.getElementById("first_name").value;
    const lastName = document.getElementById("last_name").value;
    const birthdate = document.getElementById("birthdate").value;
    const phone = document.getElementById("phone").value;
    const email = document.getElementById("email").value;

    // Флаг для проверки, если все валидации пройдены
    let isValid = true;

    // Валидация имени (должно быть не пустым)
    if (firstName === "") {
        displayErrorMessage("Пожалуйста, введите ваше имя.");
        isValid = false;
    }

    // Валидация фамилии (должно быть не пустым)
    if (lastName === "") {
        displayErrorMessage("Пожалуйста, введите вашу фамилию.");
        isValid = false;
    }

    // Валидация даты рождения (студенту должно быть минимум 16 лет)
    if (birthdate === "") {
        displayErrorMessage("Пожалуйста, выберите вашу дату рождения.");
        isValid = false;
    } else {
        const today = new Date();
        const birthDate = new Date(birthdate);
        const age = today.getFullYear() - birthDate.getFullYear();
        const month = today.getMonth() - birthDate.getMonth();

        // Проверяем, чтобы возраст был не меньше 16 лет
        if (age < 16 || (age === 16 && month < 0) || age > 123) {
            displayErrorMessage("Некорректный возраст.");
            isValid = false;
        }
    }

    // Валидация телефона (формат: +X XXX XXX-XXXX)
    const phoneRegex = /^\+?[0-9]{10,15}$/;
    if (phone && !phoneRegex.test(phone)) {
        displayErrorMessage("Пожалуйста, введите корректный телефон.");
        isValid = false;
    }

    // Валидация email (формат email)
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    if (email && !emailRegex.test(email)) {
        displayErrorMessage("Пожалуйста, введите корректный email.");
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
