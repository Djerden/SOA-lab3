import config from '../config';
import { translateError, translateMessage } from '../utils/errorTranslation';

const BASE_URL = config.genocideServiceUrl;

// Общий обработчик ответов
const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    const translatedMessage = translateError(errorData, response.status);
    const error = new Error(translatedMessage);
    error.status = response.status;
    error.details = errorData;
    throw error;
  }
  
  // Для 204 No Content возвращаем null
  if (response.status === 204) {
    return null;
  }
  
  // Проверяем, есть ли тело ответа
  const text = await response.text();
  if (!text) {
    return null;
  }
  
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
};

// Уничтожить население города
export const killPopulation = async (id) => {
  const response = await fetch(`${BASE_URL}/genocide/kill/${id}`, {
    method: 'POST',
  });
  return handleResponse(response);
};

// Переселить население в город с самым низким уровнем жизни
export const moveToPoorest = async (id) => {
  const response = await fetch(`${BASE_URL}/genocide/move-to-poorest/${id}`, {
    method: 'POST',
  });
  return handleResponse(response);
};
