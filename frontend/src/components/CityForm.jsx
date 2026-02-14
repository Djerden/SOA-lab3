import { useState, useEffect } from 'react';
import { createCity, updateCity, CLIMATES, STANDARDS_OF_LIVING, CLIMATE_LABELS, STANDARD_OF_LIVING_LABELS } from '../api/cityApi';
import { translateMessage } from '../utils/errorTranslation';

export default function CityForm({ city, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    name: '',
    coordinates: { x: 0, y: 0 },
    area: 1,
    population: 1,
    metersAboveSeaLevel: '',
    capital: false,
    climate: '',
    standardOfLiving: 'HIGH',
    governor: { age: 1 },
  });
  
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [validationErrors, setValidationErrors] = useState({});

  const validateForm = () => {
    const errors = {};

    if (!formData.name || formData.name.trim() === '') {
      errors.name = 'Название обязательно';
    }

    const coordX = parseFloat(formData.coordinates.x);
    if (formData.coordinates.x === '' || isNaN(coordX)) {
      errors['coordinates.x'] = 'Координата X обязательна';
    } else if (coordX > 329) {
      errors['coordinates.x'] = 'Максимум 329';
    }

    const coordY = parseInt(formData.coordinates.y);
    if (formData.coordinates.y === '' || isNaN(coordY)) {
      errors['coordinates.y'] = 'Координата Y обязательна';
    } else if (coordY < -663) {
      errors['coordinates.y'] = 'Минимум -663';
    }

    const area = parseFloat(formData.area);
    if (formData.area === '' || isNaN(area)) {
      errors.area = 'Площадь обязательна';
    } else if (area < 1) {
      errors.area = 'Площадь должна быть минимум 1';
    }

    const population = parseInt(formData.population);
    if (formData.population === '' || isNaN(population)) {
      errors.population = 'Население обязательно';
    } else if (population < 1) {
      errors.population = 'Население должно быть минимум 1';
    }

    if (!formData.standardOfLiving) {
      errors.standardOfLiving = 'Уровень жизни обязателен';
    }

    const age = parseInt(formData.governor.age);
    if (formData.governor.age === '' || isNaN(age)) {
      errors['governor.age'] = 'Возраст обязателен';
    } else if (age < 1) {
      errors['governor.age'] = 'Возраст должен быть минимум 1';
    } else if (age > 200) {
      errors['governor.age'] = 'Возраст не может превышать 200';
    }

    return errors;
  };

  const getFieldClass = (fieldName) => {
    const baseClass = 'w-full border rounded px-3 py-2 focus:ring-2';
    if (validationErrors[fieldName]) {
      return `${baseClass} border-red-500 focus:ring-red-500 focus:border-red-500`;
    }
    return `${baseClass} focus:ring-blue-500 focus:border-blue-500`;
  };

  useEffect(() => {
    if (city) {
      setFormData({
        name: city.name || '',
        coordinates: city.coordinates || { x: 0, y: 0 },
        area: city.area || 1,
        population: city.population || 1,
        metersAboveSeaLevel: city.metersAboveSeaLevel ?? '',
        capital: city.capital || false,
        climate: city.climate || '',
        standardOfLiving: city.standardOfLiving || 'HIGH',
        governor: city.governor || { age: 1 },
      });
    }
  }, [city]);

  // Ограничение количества символов в числовых полях
  const maxLengths = {
    'coordinates.x': 10,
    'coordinates.y': 10,
    'area': 15,
    'population': 15,
    'metersAboveSeaLevel': 12,
    'governor.age': 3,
    'name': 100,
  };

  // Поля, которые являются числовыми (для фильтрации ввода)
  const numericFields = ['coordinates.x', 'coordinates.y', 'area', 'population', 'metersAboveSeaLevel', 'governor.age'];
  const decimalFields = ['coordinates.x', 'area', 'metersAboveSeaLevel'];
  const integerFields = ['coordinates.y', 'population', 'governor.age'];

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    
    // Для числовых полей фильтруем недопустимые символы
    if (numericFields.includes(name)) {
      // Разрешаем: цифры, точку (для decimal), минус в начале
      let filteredValue = value;
      if (decimalFields.includes(name)) {
        // Разрешаем цифры, одну точку, минус в начале
        filteredValue = value.replace(/[^0-9.-]/g, '');
        // Оставляем только первый минус и только в начале
        if (filteredValue.indexOf('-') > 0) {
          filteredValue = filteredValue.replace(/-/g, '');
        }
        // Оставляем только первую точку
        const parts = filteredValue.split('.');
        if (parts.length > 2) {
          filteredValue = parts[0] + '.' + parts.slice(1).join('');
        }
      } else if (integerFields.includes(name)) {
        // Разрешаем цифры и минус в начале
        filteredValue = value.replace(/[^0-9-]/g, '');
        if (filteredValue.indexOf('-') > 0) {
          filteredValue = filteredValue.replace(/-/g, '');
        }
      }
      
      // Проверка на максимальную длину
      const maxLen = maxLengths[name];
      if (maxLen && filteredValue.length > maxLen) {
        return;
      }
      
      // Очищаем ошибку валидации для этого поля
      if (validationErrors[name]) {
        setValidationErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors[name];
          return newErrors;
        });
      }
      
      if (name.startsWith('coordinates.')) {
        const key = name.split('.')[1];
        setFormData(prev => ({
          ...prev,
          coordinates: {
            ...prev.coordinates,
            [key]: filteredValue,
          },
        }));
      } else if (name === 'governor.age') {
        setFormData(prev => ({
          ...prev,
          governor: { age: filteredValue },
        }));
      } else {
        setFormData(prev => ({ ...prev, [name]: filteredValue }));
      }
      return;
    }
    
    // Проверка на максимальную длину для других полей
    const maxLen = maxLengths[name];
    if (maxLen && value.length > maxLen) {
      return;
    }
    
    // Очищаем ошибку валидации для этого поля
    if (validationErrors[name]) {
      setValidationErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
    
    if (type === 'checkbox') {
      setFormData(prev => ({ ...prev, [name]: checked }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    // Валидация формы
    const errors = validateForm();
    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);
      return;
    }

    setLoading(true);

    try {
      const payload = {
        name: formData.name,
        coordinates: {
          x: parseFloat(formData.coordinates.x) || 0,
          y: parseInt(formData.coordinates.y) || 0,
        },
        area: parseFloat(formData.area) || 1,
        population: parseInt(formData.population) || 1,
        metersAboveSeaLevel: formData.metersAboveSeaLevel === '' 
          ? null 
          : parseFloat(formData.metersAboveSeaLevel),
        capital: formData.capital,
        climate: formData.climate || null,
        standardOfLiving: formData.standardOfLiving,
        governor: {
          age: parseInt(formData.governor.age) || 1,
        },
      };

      if (city?.id) {
        await updateCity(city.id, payload);
      } else {
        await createCity(payload);
      }
      onSave();
    } catch (err) {
      // Переводим сообщение об ошибке
      const errorMessage = err.details?.message 
        ? translateMessage(err.details.message) 
        : translateMessage(err.message) || 'Ошибка сохранения';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-lg max-h-[90vh] overflow-y-auto">
        <h2 className="text-xl font-bold mb-4">
          {city?.id ? 'Редактирование города' : 'Создание города'}
        </h2>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4" noValidate>
          {/* Название */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Название *
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              maxLength={100}
              className={getFieldClass('name')}
            />
            {validationErrors.name && (
              <p className="text-red-500 text-sm mt-1">{validationErrors.name}</p>
            )}
          </div>

          {/* Координаты */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Координата X (макс. 329) *
              </label>
              <input
                type="text"
                inputMode="decimal"
                name="coordinates.x"
                value={formData.coordinates.x}
                onChange={handleChange}
                maxLength={10}
                className={getFieldClass('coordinates.x')}
              />
              {validationErrors['coordinates.x'] && (
                <p className="text-red-500 text-sm mt-1">{validationErrors['coordinates.x']}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Координата Y (мин. -663) *
              </label>
              <input
                type="text"
                inputMode="numeric"
                name="coordinates.y"
                value={formData.coordinates.y}
                onChange={handleChange}
                maxLength={10}
                className={getFieldClass('coordinates.y')}
              />
              {validationErrors['coordinates.y'] && (
                <p className="text-red-500 text-sm mt-1">{validationErrors['coordinates.y']}</p>
              )}
            </div>
          </div>

          {/* Площадь и население */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Площадь (мин. 1) *
              </label>
              <input
                type="text"
                inputMode="decimal"
                name="area"
                value={formData.area}
                onChange={handleChange}
                maxLength={15}
                className={getFieldClass('area')}
              />
              {validationErrors.area && (
                <p className="text-red-500 text-sm mt-1">{validationErrors.area}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Население (мин. 1) *
              </label>
              <input
                type="text"
                inputMode="numeric"
                name="population"
                value={formData.population}
                onChange={handleChange}
                maxLength={15}
                className={getFieldClass('population')}
              />
              {validationErrors.population && (
                <p className="text-red-500 text-sm mt-1">{validationErrors.population}</p>
              )}
            </div>
          </div>

          {/* Высота над уровнем моря */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Высота над уровнем моря (м)
            </label>
            <input
              type="text"
              inputMode="decimal"
              name="metersAboveSeaLevel"
              value={formData.metersAboveSeaLevel}
              onChange={handleChange}
              maxLength={12}
              className="w-full border rounded px-3 py-2 focus:ring-2 focus:ring-blue-500"
              placeholder="Опционально"
            />
          </div>

          {/* Столица */}
          <div className="flex items-center">
            <input
              type="checkbox"
              name="capital"
              id="capital"
              checked={formData.capital}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <label htmlFor="capital" className="ml-2 text-sm font-medium text-gray-700">
              Столица
            </label>
          </div>

          {/* Климат */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Климат
            </label>
            <select
              name="climate"
              value={formData.climate}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2 focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Не указан</option>
              {CLIMATES.map((c) => (
                <option key={c} value={c}>{CLIMATE_LABELS[c]}</option>
              ))}
            </select>
          </div>

          {/* Уровень жизни */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Уровень жизни *
            </label>
            <select
              name="standardOfLiving"
              value={formData.standardOfLiving}
              onChange={handleChange}
              className={getFieldClass('standardOfLiving')}
            >
              {STANDARDS_OF_LIVING.map((s) => (
                <option key={s} value={s}>{STANDARD_OF_LIVING_LABELS[s]}</option>
              ))}
            </select>
            {validationErrors.standardOfLiving && (
              <p className="text-red-500 text-sm mt-1">{validationErrors.standardOfLiving}</p>
            )}
          </div>

          {/* Возраст губернатора */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Возраст губернатора (1 — 200) *
            </label>
            <input
              type="text"
              inputMode="numeric"
              name="governor.age"
              value={formData.governor.age}
              onChange={handleChange}
              maxLength={3}
              className={getFieldClass('governor.age')}
            />
            {validationErrors['governor.age'] && (
              <p className="text-red-500 text-sm mt-1">{validationErrors['governor.age']}</p>
            )}
          </div>

          {/* Кнопки */}
          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onCancel}
              className="px-4 py-2 border rounded text-gray-700 hover:bg-gray-50"
            >
              Отмена
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
            >
              {loading ? 'Сохранение...' : 'Сохранить'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
