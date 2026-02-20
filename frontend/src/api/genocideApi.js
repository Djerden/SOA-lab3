import config from '../config';
import { translateError, translateMessage } from '../utils/errorTranslation';

const BASE_URL = config.genocideServiceUrl;

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

export const killPopulation = async (id) => {
  const response = await fetch(`${BASE_URL}/genocide/kill/${id}`, {
    method: 'POST',
  });
  return handleResponse(response);
};

export const moveToPoorest = async (id) => {
  const response = await fetch(`${BASE_URL}/genocide/move-to-poorest/${id}`, {
    method: 'POST',
  });
  return handleResponse(response);
};
