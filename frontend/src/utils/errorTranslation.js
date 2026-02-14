const HTTP_STATUS_MESSAGES = {
  400: 'Неверный запрос',
  401: 'Требуется авторизация',
  403: 'Доступ запрещён',
  404: 'Ресурс не найден',
  405: 'Метод не поддерживается',
  408: 'Время ожидания запроса истекло',
  409: 'Конфликт данных',
  413: 'Слишком большой запрос',
  415: 'Неподдерживаемый тип данных',
  422: 'Ошибка валидации данных',
  429: 'Слишком много запросов',
  500: 'Внутренняя ошибка сервера',
  502: 'Ошибка шлюза',
  503: 'Сервис временно недоступен',
  504: 'Время ожидания ответа от сервера истекло',
};

const SERVER_MESSAGE_TRANSLATIONS = {
  'not found': 'Не найдено',
  'city not found': 'Город не найден',
  'resource not found': 'Ресурс не найден',
  'invalid request': 'Неверный запрос',
  'bad request': 'Неверный запрос',
  'validation failed': 'Ошибка валидации',
  'validation error': 'Ошибка валидации',
  'internal server error': 'Внутренняя ошибка сервера',
  'service unavailable': 'Сервис недоступен',
  'connection refused': 'Соединение отклонено',
  'connection failed': 'Ошибка соединения',
  'timeout': 'Время ожидания истекло',
  'request timeout': 'Время ожидания запроса истекло',
  'unauthorized': 'Требуется авторизация',
  'forbidden': 'Доступ запрещён',
  'access denied': 'Доступ запрещён',
  
  'duplicate entry': 'Запись уже существует',
  'already exists': 'Уже существует',
  'constraint violation': 'Нарушение ограничений данных',
  'invalid data': 'Некорректные данные',
  'invalid format': 'Неверный формат',
  'invalid value': 'Недопустимое значение',
  'required field': 'Обязательное поле',
  'field is required': 'Поле обязательно для заполнения',
  'cannot be null': 'Поле не может быть пустым',
  'cannot be empty': 'Поле не может быть пустым',
  'must not be null': 'Поле обязательно для заполнения',
  'must not be empty': 'Поле не может быть пустым',
  'must not be blank': 'Поле не может быть пустым',
  
  'must be greater than': 'Должно быть больше',
  'must be less than': 'Должно быть меньше',
  'must be positive': 'Должно быть положительным числом',
  'must be greater than 0': 'Должно быть больше 0',
  'must be at least': 'Минимальное значение:',
  'must be at most': 'Максимальное значение:',
  'out of range': 'Значение вне допустимого диапазона',
  
  'too long': 'Слишком длинное значение',
  'too short': 'Слишком короткое значение',
  'length must be': 'Длина должна быть',
  'size must be': 'Размер должен быть',
  
  'no cities found': 'Города не найдены',
  'city service unavailable': 'Сервис городов недоступен',
  'population cannot be zero': 'Население не может быть нулевым',
  'invalid coordinates': 'Некорректные координаты',
  'invalid climate': 'Недопустимый тип климата',
  'invalid standard of living': 'Недопустимый уровень жизни',
  'governor age must be': 'Возраст губернатора должен быть',
  'area must be positive': 'Площадь должна быть положительной',
  'no suitable city found': 'Подходящий город не найден',
  'cannot relocate to same city': 'Нельзя переселить в тот же город',
  'relocation not possible': 'Переселение невозможно',
  'failed to connect': 'Не удалось подключиться',
  'network error': 'Ошибка сети',
  'failed to fetch': 'Ошибка получения данных. Проверьте соединение с сервером',
  'failed to convert value': 'Ошибка преобразования значения. Проверьте введённые данные',
  'for input string': 'Некорректное значение',
  'method parameter': 'Ошибка параметра запроса',
  
  'name is required': 'Название обязательно',
  'name is required and cannot be blank': 'Название обязательно и не может быть пустым',
  'coordinates are required': 'Координаты обязательны',
  'area is required': 'Площадь обязательна',
  'area must be greater than 0': 'Площадь должна быть больше 0',
  'population is required': 'Население обязательно',
  'population must be greater than 0': 'Население должно быть больше 0',
  'capital is required': 'Поле "Столица" обязательно',
  'standard of living is required': 'Уровень жизни обязателен',
  'governor is required': 'Губернатор обязателен',
  'must be greater than or equal to': 'Должно быть больше или равно',
  'must be less than or equal to': 'Должно быть меньше или равно',
  
  'governor.age': 'Возраст губернатора',
  'coordinates.x': 'Координата X',
  'coordinates.y': 'Координата Y',
  'name': 'Название',
  'area': 'Площадь',
  'population': 'Население',
  'capital': 'Столица',
  'climate': 'Климат',
  'standardofliving': 'Уровень жизни',
  'governor': 'Губернатор',
  'metersabovesealevel': 'Высота над уровнем моря',
};

const FIELD_TRANSLATIONS = {
  'name': 'Название',
  'area': 'Площадь',
  'population': 'Население',
  'capital': 'Столица',
  'climate': 'Климат',
  'standardofliving': 'Уровень жизни',
  'governor': 'Губернатор',
  'metersabovesealevel': 'Высота над уровнем моря',
  'governor.age': 'Возраст губернатора',
  'coordinates.x': 'Координата X',
  'coordinates.y': 'Координата Y',
  'age': 'Возраст',
  'x': 'X',
  'y': 'Y',
  'coordinates': 'Координаты',
};

function translateFieldMessage(message) {
  if (!message) return '';
  
  const lowerMessage = message.toLowerCase().trim();
  
  if (SERVER_MESSAGE_TRANSLATIONS[lowerMessage]) {
    return SERVER_MESSAGE_TRANSLATIONS[lowerMessage];
  }
  
  for (const [key, translation] of Object.entries(SERVER_MESSAGE_TRANSLATIONS)) {
    if (lowerMessage.includes(key)) {
      return translation;
    }
  }
  
  return message;
}

const MESSAGE_PATTERNS = [
  {
    pattern: /City with id '?(\d+)'? not found/i,
    replacement: (match, id) => `Город с ID ${id} не найден`,
  },
  {
    pattern: /City with ID '?(\d+)'? not found/i,
    replacement: (match, id) => `Город с ID ${id} не найден`,
  },
  {
    pattern: /No city found with id '?(\d+)'?/i,
    replacement: (match, id) => `Город с ID ${id} не найден`,
  },
  {
    pattern: /City service is unavailable:?\s*(.*)/i,
    replacement: (match, details) => `Сервис городов недоступен${details ? ': ' + details : ''}`,
  },
  {
    pattern: /Error communicating with city-service:?\s*(.*)/i,
    replacement: (match, details) => `Ошибка связи с сервисом городов${details ? ': ' + details : ''}`,
  },
  {
    pattern: /Internal server error:?\s*(.*)/i,
    replacement: (match, details) => `Внутренняя ошибка сервера${details ? ': ' + details : ''}`,
  },
  {
    pattern: /Invalid request body:?\s*(.*)/i,
    replacement: (match, details) => `Неверный формат запроса${details ? ': ' + details : ''}`,
  },
  {
    pattern: /Failed to convert value.*For input string:?\s*"?([^"]*)"?/i,
    replacement: (match, value) => `Некорректное значение: "${value}". Число слишком большое`,
  },
  {
    pattern: /Method parameter '(\w+)':?\s*(.+)/i,
    replacement: (match, param, details) => `Ошибка параметра '${param}': ${translateFieldMessage(details)}`,
  },
  {
    pattern: /Governor age must be between (\d+) and (\d+)/i,
    replacement: (match, min, max) => `Возраст губернатора должен быть от ${min} до ${max}`,
  },
  {
    pattern: /must be greater than or equal to (\d+)/i,
    replacement: (match, num) => `должно быть больше или равно ${num}`,
  },
  {
    pattern: /must be less than or equal to (\d+)/i,
    replacement: (match, num) => `должно быть меньше или равно ${num}`,
  },
  {
    pattern: /must be greater than (\d+)/i,
    replacement: (match, num) => `должно быть больше ${num}`,
  },
  {
    pattern: /must be less than (\d+)/i,
    replacement: (match, num) => `должно быть меньше ${num}`,
  },
  {
    pattern: /size must be between (\d+) and (\d+)/i,
    replacement: (match, min, max) => `размер должен быть от ${min} до ${max}`,
  },
  {
    pattern: /length must be between (\d+) and (\d+)/i,
    replacement: (match, min, max) => `длина должна быть от ${min} до ${max}`,
  },
  {
    pattern: /Invalid value '([^']+)' for enum/i,
    replacement: (match, value) => `Недопустимое значение '${value}'`,
  },
  {
    pattern: /No cities with governor age (\d+) found/i,
    replacement: (match, age) => `Города с возрастом губернатора ${age} не найдены`,
  },
  {
    pattern: /Deleted (\d+) cities with governor age (\d+)/i,
    replacement: (match, count, age) => `Удалено ${count} городов с возрастом губернатора ${age}`,
  },
  {
    pattern: /Cities with governor age '(\d+)' were deleted\. Count: (\d+)/i,
    replacement: (match, age, count) => `Города с возрастом губернатора ${age} удалены. Количество: ${count}`,
  },
  {
    pattern: /HTTP error (\d+)/i,
    replacement: (match, code) => HTTP_STATUS_MESSAGES[parseInt(code)] || `HTTP ошибка ${code}`,
  },
  {
    pattern: /JSON parse error/i,
    replacement: () => 'Ошибка обработки ответа сервера',
  },
  {
    pattern: /Unexpected token/i,
    replacement: () => 'Ошибка обработки ответа сервера',
  },
  {
    pattern: /Connection refused/i,
    replacement: () => 'Соединение отклонено. Сервер недоступен',
  },
  {
    pattern: /ECONNREFUSED|ENOTFOUND|ETIMEDOUT/i,
    replacement: () => 'Ошибка сетевого подключения. Проверьте соединение с сервером',
  },
  {
    pattern: /^(\w+(?:\.\w+)?)\s*:\s*(.+)$/i,
    replacement: (match, field, message) => {
      const fieldLower = field.toLowerCase();
      const translatedField = FIELD_TRANSLATIONS[fieldLower] || field;
      const translatedMsg = translateFieldMessage(message);
      return `${translatedField}: ${translatedMsg}`;
    },
  },
];

export function translateMessage(message) {
  if (!message || typeof message !== 'string') {
    return 'Неизвестная ошибка';
  }
  
  const lowerMessage = message.toLowerCase().trim();
  
  if (SERVER_MESSAGE_TRANSLATIONS[lowerMessage]) {
    return SERVER_MESSAGE_TRANSLATIONS[lowerMessage];
  }
  
  for (const { pattern, replacement } of MESSAGE_PATTERNS) {
    const match = message.match(pattern);
    if (match) {
      return replacement(...match);
    }
  }
  
  for (const [key, translation] of Object.entries(SERVER_MESSAGE_TRANSLATIONS)) {
    if (lowerMessage.includes(key)) {
      return translation;
    }
  }
  
  return message;
}

export function getHttpErrorMessage(status) {
  return HTTP_STATUS_MESSAGES[status] || `HTTP ошибка ${status}`;
}

export function translateError(errorData, status) {
  if (errorData?.message) {
    return translateMessage(errorData.message);
  }
  
  if (errorData?.error) {
    return translateMessage(errorData.error);
  }
  
  if (errorData?.errors && Array.isArray(errorData.errors)) {
    const translatedErrors = errorData.errors.map(err => {
      if (typeof err === 'string') {
        return translateMessage(err);
      }
      if (err.message) {
        return translateMessage(err.message);
      }
      if (err.defaultMessage) {
        const fieldName = err.field || '';
        const translatedField = SERVER_MESSAGE_TRANSLATIONS[fieldName.toLowerCase()] || fieldName;
        const translatedMsg = translateMessage(err.defaultMessage);
        return translatedField ? `${translatedField}: ${translatedMsg}` : translatedMsg;
      }
      return translateMessage(JSON.stringify(err));
    });
    return translatedErrors.join('; ');
  }
  
  if (errorData?.violations && Array.isArray(errorData.violations)) {
    const translatedViolations = errorData.violations.map(v => {
      const fieldName = v.field || v.propertyPath || '';
      const translatedField = SERVER_MESSAGE_TRANSLATIONS[fieldName.toLowerCase()] || fieldName;
      const translatedMsg = translateMessage(v.message);
      return translatedField ? `${translatedField}: ${translatedMsg}` : translatedMsg;
    });
    return translatedViolations.join('; ');
  }
  
  return getHttpErrorMessage(status);
}

export default {
  translateMessage,
  translateError,
  getHttpErrorMessage,
};
