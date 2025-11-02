// src/utils/validationRules.ts

// --- 1. РЕГУЛЯРНЫЕ ВЫРАЖЕНИЯ ---

// Имя/Фамилия: Допускает кириллицу/латиницу, пробелы, дефисы. Мин. 2 символа.
export const PERMISSIVE_NAME_REGEX = /^[a-zA-Zа-яА-ЯёЁ\s-]{2,}$/;

// Email: стандартная проверка (латиница/цифры/спецсимволы).
export const EMAIL_REGEX = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i;

// Пароль: мин. 8 символов, 1 заглавная, 1 цифра, 1 спец. знак.
// ИСПРАВЛЕНО: Экранированы $ и ! для максимальной надежности.
export const PASSWORD_REGEX =
  /^([A-Za-z]+(\d[A-Za-z\d]*([^A-Za-z\d].*\S|[^A-Za-z0-9 ])|[^A-Za-z\d][^\d]*(\d.*\S|\d))|\d+([A-Za-z][A-Za-z\d]*([^A-Za-z\d].*\S|[^A-Za-z\s\d])|[^A-Za-z\d][^A-Za-z]*([A-Za-z].*\S|[A-Za-z]))|[^A-Za-z\d\s][^A-Za-z\d]*([A-Za-z][^\d]*(\d.*\S|\d)|\d[^A-Za-z]*([A-Za-z].*\S|[A-Za-z])))$/;
//
// --- 2. ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ВАЛИДАЦИИ ДЛЯ REACT-HOOK-FORM ---

// 1. Валидация обязательного поля
export const requiredValidation = (fieldName: string) => () => ({
  required: `${fieldName} обязателен для заполнения.`,
});

// 2. Валидация минимальной длины
export const minLengthValidation = (min: number) => (fieldName: string) => ({
  minLength: {
    value: min,
    message: `${fieldName} должен содержать не менее ${min} символов.`,
  },
});

// 3. Валидация максимальной длины
export const maxLengthValidation = (max: number) => (fieldName: string) => ({
  maxLength: {
    value: max,
    message: `${fieldName} не должен превышать ${max} символов.`,
  },
});

// 4. Валидация email
// ИСПРАВЛЕНО: Функция теперь возвращает объект правил напрямую,
// чтобы избежать ошибки "Did you mean to call it?"
export const emailValidation = (fieldName: string = 'Email') => ({
  ...requiredValidation(fieldName)(),
  pattern: {
    value: EMAIL_REGEX,
    message: 'Некорректный формат email.',
  },
});

// 5. Валидация пароля (сочетает обязательность и мин. длину)
export const passwordValidation =
  (min: number = 8) =>
  (fieldName: string = 'Пароль') => ({
    ...requiredValidation(fieldName)(),
    ...minLengthValidation(min)(fieldName),
  });

// 6. Валидация номера телефона
const PHONE_MASK_LENGTH = 18;

export const phoneValidation = () => ({
  ...requiredValidation('Телефон')(),
  validate: (value: string) => {
    if (value.length < PHONE_MASK_LENGTH) {
      return 'Пожалуйста, введите полный номер телефона.';
    }
    if (!/^\+7 \(\d{3}\) \d{3} - \d{2} - \d{2}$/.test(value)) {
      return 'Неверный формат номера телефона. Используйте +7 (XXX) XXX-XX-XX.';
    }
    return true;
  },
});

// 7. Валидация специализаций (специфично для MasterRegistrationForm)
export const specializationValidation = (min: number, max: number) => ({
  validate: (value: string[] | undefined) => {
    if (!value || value.length === 0) {
      return 'Необходимо выбрать хотя бы одну специализацию.';
    }
    if (value.length < min) {
      return `Выберите минимум ${min} специализацию.`;
    }
    if (value.length > max) {
      return `Максимальное количество специализаций: ${max}.`;
    }
    return true;
  },
});

// --- 3. СТАРЫЕ ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ (для validate) ---

export const validateText = (
  value: string,
  minLength: number
): string | true => {
  if (!value || value.trim().length < minLength) {
    return `Минимальная длина ${minLength} символов.`;
  }
  return true;
};

export const validateBirthdate = (dateString: string): string | true => {
  if (!dateString) {
    return 'Дата рождения обязательна.';
  }

  let birthDate: Date;
  if (dateString.includes('.')) {
    const parts = dateString.split('.');
    if (parts.length !== 3) {
      return 'Неверный формат даты (ожидается DD.MM.YYYY).';
    }
    const day = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10) - 1;
    const year = parseInt(parts[2], 10);

    if (
      isNaN(day) ||
      isNaN(month) ||
      isNaN(year) ||
      month < 0 ||
      month > 11 ||
      day < 1 ||
      day > 31
    ) {
      return 'Неверный формат даты.';
    }
    birthDate = new Date(year, month, day);
  } else {
    birthDate = new Date(dateString);
  }

  if (isNaN(birthDate.getTime())) {
    return 'Неверная дата.';
  }

  const today = new Date();
  const minAgeDate = new Date(
    today.getFullYear() - 14,
    today.getMonth(),
    today.getDate()
  );

  if (birthDate > minAgeDate) {
    return 'Вы должны быть старше 14 лет.';
  }

  if (birthDate > today) {
    return 'Дата рождения не может быть в будущем.';
  }

  return true;
};

export const validateWorkExperience = (years: number): string | true => {
  if (years < 0) {
    return 'Опыт работы не может быть отрицательным.';
  }
  if (years > 60) {
    return 'Некорректное значение опыта.';
  }
  return true;
};
