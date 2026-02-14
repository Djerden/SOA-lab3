import config from '../config';
import { translateError, translateMessage } from '../utils/errorTranslation';

const BASE_URL = config.cityServiceUrl;

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

// Получить города с фильтрацией, сортировкой и пагинацией
export const filterCities = async (filterRequest = {}) => {
  const response = await fetch(`${BASE_URL}/cities/filter`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      page: filterRequest.page || 1,
      size: filterRequest.size || 10,
      sort: filterRequest.sort || [],
      filters: filterRequest.filters || [],
    }),
  });
  return handleResponse(response);
};

// Создать новый город
export const createCity = async (cityData) => {
  const response = await fetch(`${BASE_URL}/cities`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(cityData),
  });
  return handleResponse(response);
};

// Получить город по ID
export const getCity = async (id) => {
  const response = await fetch(`${BASE_URL}/cities/${id}`);
  return handleResponse(response);
};

// Обновить город
export const updateCity = async (id, cityData) => {
  const response = await fetch(`${BASE_URL}/cities/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(cityData),
  });
  return handleResponse(response);
};

// Удалить город по ID
export const deleteCity = async (id) => {
  const response = await fetch(`${BASE_URL}/cities/${id}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
};

// Удалить города по возрасту губернатора
export const deleteCitiesByGovernorAge = async (age) => {
  const response = await fetch(`${BASE_URL}/cities/by-governor?age=${age}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
};

// Получить город с минимальными координатами
export const getCityByMinCoordinates = async () => {
  const response = await fetch(`${BASE_URL}/cities/by-min-coordinates`);
  return handleResponse(response);
};

// Получить количество городов по климату
export const countCitiesByClimate = async (climate) => {
  const response = await fetch(`${BASE_URL}/cities/count-by-climate`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ climate }),
  });
  return handleResponse(response);
};

// Константы для enum значений
export const CLIMATES = ['TROPICAL_SAVANNA', 'STEPPE', 'SUBARCTIC', 'POLAR_ICECAP'];
export const STANDARDS_OF_LIVING = ['HIGH', 'VERY_LOW', 'ULTRA_LOW'];

// Перевод enum значений на русский
export const CLIMATE_LABELS = {
  TROPICAL_SAVANNA: 'Тропическая саванна',
  STEPPE: 'Степь',
  SUBARCTIC: 'Субарктический',
  POLAR_ICECAP: 'Полярный ледник',
};

export const STANDARD_OF_LIVING_LABELS = {
  HIGH: 'Высокий',
  VERY_LOW: 'Очень низкий',
  ULTRA_LOW: 'Ультра низкий',
};
