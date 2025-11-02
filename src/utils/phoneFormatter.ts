// src/utils/phoneFormatter.ts

/** Ожидаемая длина чистого номера (7XXXXXXXXXX) */
export const EXPECTED_PHONE_LENGTH = 11; // 7 и 10 цифр (для сервера)

/**
 * Очищает номер телефона от форматирования, оставляя только цифры.
 * Приводит к формату 7XXXXXXXXXX (11 цифр).
 * @param phone Строка с номером телефона (например, '+7 (910) 123-45-67').
 * @returns Очищенный номер без '+' (например, '79101234567').
 */
export const parsePhoneNumber = (phone: string): string => {
  // 1. Удаляем все, кроме цифр
  let cleaned = phone.replace(/[^\d]/g, '');

  // 2. Обрабатываем российский префикс
  if (cleaned.length === EXPECTED_PHONE_LENGTH) {
    // Если начинается с '8', заменяем на '7' (частая ошибка ввода)
    if (cleaned.startsWith('8')) {
      return '7' + cleaned.substring(1);
    }
    // Если начинается с '7', возвращаем как есть
    if (cleaned.startsWith('7')) {
      return cleaned;
    }
  }

  // 3. Обрабатываем случай, когда ввели только 10 цифр (без кода страны)
  if (cleaned.length === 10) {
    return '7' + cleaned;
  }

  // 4. Если ввели больше 11 цифр, обрезаем, если начинается с 7/8
  if (cleaned.length > EXPECTED_PHONE_LENGTH) {
    if (cleaned.startsWith('8')) {
      // '8' + 10 цифр + лишнее -> '7' + 10 цифр
      return '7' + cleaned.substring(1, EXPECTED_PHONE_LENGTH);
    }
    if (cleaned.startsWith('7')) {
      // '7' + 10 цифр + лишнее -> '7' + 10 цифр
      return cleaned.substring(0, EXPECTED_PHONE_LENGTH);
    }
  }

  // В остальных случаях (например, не российский номер, или не 10/11 цифр)
  return cleaned;
};

/**
 * Проверяет правильность номера телефона.
 * Возвращает true, если номер валиден (российский мобильный: 7 + 10 цифр).
 * @param phone Строка с номером телефона.
 * @returns true, если номер правильный.
 */
export const isValidPhoneNumber = (phone: string): boolean => {
  const parsed = parsePhoneNumber(phone);

  // Проверяем: длина 11, начинается с 7, и состоит только из цифр
  return (
    parsed.length === EXPECTED_PHONE_LENGTH &&
    parsed.startsWith('7') &&
    /^\d{11}$/.test(parsed)
  );
};

/**
 * Валидирует номер телефона и возвращает сообщение об ошибке, если невалиден.
 * @param phone Строка с номером телефона.
 * @returns null, если валиден, иначе строку с ошибкой.
 */
export const validatePhoneNumber = (phone: string): string | null => {
  if (!phone || phone.trim() === '') {
    return 'Номер телефона обязателен.';
  }
  if (!isValidPhoneNumber(phone)) {
    return 'Некорректный формат номера. Ожидается российский мобильный номер (+7 XXX XXX XX XX).';
  }
  return null;
};

/**
 * Форматирует номер телефона в читаемый вид: +7 (XXX) XXX-XX-XX.
 * Если номер невалиден, возвращает исходную строку.
 * @param phone Строка с номером телефона.
 * @returns Отформатированный номер.
 */
export const formatPhoneNumber = (value: string): string => {
  // Очищаем ввод: оставляем только цифры, кроме первого знака '+'
  const cleaned = value.replace(/\D/g, '');

  if (cleaned.length === 0) {
    return '';
  }

  // Начинаем форматирование с '+7', если не начинается с него.
  // Это важно, так как у нас русский/казахский формат +7 (XXX)...
  let result = '+7';
  let phoneDigits = cleaned;

  // Если строка уже начинается с 7, 8 или +, обрезаем их для форматирования
  if (phoneDigits.startsWith('7') || phoneDigits.startsWith('8')) {
    phoneDigits = phoneDigits.substring(1);
  } else if (phoneDigits.startsWith('+7')) {
    phoneDigits = phoneDigits.substring(2);
  } else if (phoneDigits.startsWith('+')) {
    // Пользователь ввел '+', но не 7. Оставляем только цифры.
    phoneDigits = phoneDigits.substring(1);
  }

  // Обрезаем, чтобы не превысить 10 цифр после +7
  phoneDigits = phoneDigits.substring(0, 10);

  if (phoneDigits.length > 0) {
    result += ` (${phoneDigits.substring(0, 3)}`; // +7 (XXX
  }
  if (phoneDigits.length > 3) {
    result += `) ${phoneDigits.substring(3, 6)}`; // +7 (XXX) XXX
  }
  if (phoneDigits.length > 6) {
    result += ` - ${phoneDigits.substring(6, 8)}`; // +7 (XXX) XXX - XX
  }
  if (phoneDigits.length > 8) {
    result += ` - ${phoneDigits.substring(8, 10)}`; // +7 (XXX) XXX - XX - XX
  }

  return result;
};
