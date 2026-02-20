import config from '../config';
import { translateError, translateMessage } from '../utils/errorTranslation';

const BASE_URL = config.cityServiceUrl;

const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    const translatedMessage = translateError(errorData, response.status);
    const error = new Error(translatedMessage);
    error.status = response.status;
    error.details = errorData;
    throw error;
  }
  
  if (response.status === 204) {
    return null;
  }
  
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

export const getCity = async (id) => {
  const response = await fetch(`${BASE_URL}/cities/${id}`);
  return handleResponse(response);
};

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

export const deleteCity = async (id) => {
  const response = await fetch(`${BASE_URL}/cities/${id}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
};

export const deleteCitiesByGovernorAge = async (age) => {
  const response = await fetch(`${BASE_URL}/cities/by-governor?age=${age}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
};

export const getCityByMinCoordinates = async () => {
  const response = await fetch(`${BASE_URL}/cities/by-min-coordinates`);
  return handleResponse(response);
};

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

export const CLIMATES = ['TROPICAL_SAVANNA', 'STEPPE', 'SUBARCTIC', 'POLAR_ICECAP'];
export const STANDARDS_OF_LIVING = ['HIGH', 'VERY_LOW', 'ULTRA_LOW'];

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
