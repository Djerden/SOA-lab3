import { useState } from 'react';
import { killPopulation, moveToPoorest } from '../api/genocideApi';
import { 
  deleteCitiesByGovernorAge, 
  getCityByMinCoordinates, 
  countCitiesByClimate,
  CLIMATES,
  CLIMATE_LABELS,
  STANDARD_OF_LIVING_LABELS
} from '../api/cityApi';
import { translateMessage } from '../utils/errorTranslation';

export default function SpecialOperations({ onDataChange }) {
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const [killId, setKillId] = useState('');
  const [moveId, setMoveId] = useState('');

  const [governorAge, setGovernorAge] = useState('');
  const [climateToCount, setClimateToCount] = useState('STEPPE');

  const MAX_ID = 2147483647;
  const MAX_AGE = 200;

  const handleIdInput = (value, setter) => {
    const filtered = value.replace(/\D/g, '');
    if (filtered.length > 10) return;
    if (filtered && parseInt(filtered) > MAX_ID) return;
    setter(filtered);
  };

  const handleAgeInput = (value) => {
    const filtered = value.replace(/\D/g, '');
    if (filtered.length > 3) return;
    if (filtered && parseInt(filtered) > MAX_AGE) return;
    setGovernorAge(filtered);
  };

  const handleOperation = async (operation, params = {}) => {
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      let response;
      switch (operation) {
        case 'kill':
          response = await killPopulation(params.id);
          setResult({ type: 'success', message: response.message || 'Население уничтожено' });
          onDataChange?.();
          break;
        case 'moveToPoorest':
          response = await moveToPoorest(params.id);
          setResult({ type: 'success', message: response.message || 'Население переселено' });
          onDataChange?.();
          break;
        case 'deleteByGovernor':
          response = await deleteCitiesByGovernorAge(params.age);
          setResult({ type: 'success', message: response.message || `Удалены города с губернаторами возраста ${params.age}` });
          onDataChange?.();
          break;
        case 'minCoordinates':
          response = await getCityByMinCoordinates();
          setResult({ type: 'city', data: response });
          break;
        case 'countByClimate':
          response = await countCitiesByClimate(params.climate);
          setResult({ 
            type: 'count', 
            message: `Городов с климатом "${CLIMATE_LABELS[params.climate]}": ${response.count}` 
          });
          break;
      }
    } catch (err) {
      setError(translateMessage(err.message) || 'Ошибка выполнения операции');
    } finally {
      setLoading(false);
    }
  };

  const formatCity = (city) => (
    <div className="bg-gray-50 p-4 rounded mt-2">
      <h4 className="font-semibold text-lg">{city.name}</h4>
      <div className="grid grid-cols-2 gap-2 mt-2 text-sm">
        <div><span className="text-gray-500">ID:</span> {city.id}</div>
        <div><span className="text-gray-500">Координаты:</span> ({city.coordinates.x}, {city.coordinates.y})</div>
        <div><span className="text-gray-500">Площадь:</span> {city.area.toLocaleString()}</div>
        <div><span className="text-gray-500">Население:</span> {city.population.toLocaleString()}</div>
        <div><span className="text-gray-500">Столица:</span> {city.capital ? 'Да' : 'Нет'}</div>
        <div><span className="text-gray-500">Климат:</span> {city.climate ? CLIMATE_LABELS[city.climate] : '—'}</div>
        <div><span className="text-gray-500">Уровень жизни:</span> {STANDARD_OF_LIVING_LABELS[city.standardOfLiving]}</div>
        <div><span className="text-gray-500">Возраст губернатора:</span> {city.governor.age}</div>
      </div>
    </div>
  );

  return (
    <div className="space-y-6">
      {/* Результат/Ошибка */}
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}
      {result && (
        <div className={`px-4 py-3 rounded ${
          result.type === 'success' ? 'bg-green-100 border border-green-400 text-green-700' : 'bg-blue-100 border border-blue-400 text-blue-700'
        }`}>
          {result.message && <p>{result.message}</p>}
          {result.type === 'city' && formatCity(result.data)}
        </div>
      )}

      {/* Genocide Service */}
      <div className="bg-white p-4 rounded-lg shadow">
        <h3 className="text-lg font-bold text-red-600 mb-4">Операции Genocide Service</h3>
        
        <div className="space-y-4">
          {/* Kill Population */}
          <div className="flex items-end gap-3">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Уничтожить население города
              </label>
              <input
                type="text"
                inputMode="numeric"
                value={killId}
                onChange={(e) => handleIdInput(e.target.value, setKillId)}
                placeholder="ID города"
                maxLength={10}
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <button
              onClick={() => handleOperation('kill', { id: killId })}
              disabled={loading || !killId}
              className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 disabled:opacity-50"
            >
              Уничтожить
            </button>
          </div>

          {/* Move to Poorest */}
          <div className="flex items-end gap-3">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Переселить в самый бедный город
              </label>
              <input
                type="text"
                inputMode="numeric"
                value={moveId}
                onChange={(e) => handleIdInput(e.target.value, setMoveId)}
                placeholder="ID города-источника"
                maxLength={10}
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <button
              onClick={() => handleOperation('moveToPoorest', { id: moveId })}
              disabled={loading || !moveId}
              className="px-4 py-2 bg-orange-600 text-white rounded hover:bg-orange-700 disabled:opacity-50"
            >
              Переселить
            </button>
          </div>
        </div>
      </div>

      {/* City Service Special Operations */}
      <div className="bg-white p-4 rounded-lg shadow">
        <h3 className="text-lg font-bold text-blue-600 mb-4">Специальные операции City Service</h3>
        
        <div className="space-y-4">
          {/* Delete by Governor Age */}
          <div className="flex items-end gap-3">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Удалить города по возрасту губернатора
              </label>
              <input
                type="text"
                inputMode="numeric"
                value={governorAge}
                onChange={(e) => handleAgeInput(e.target.value)}
                placeholder="Возраст (1-200)"
                maxLength={3}
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <button
              onClick={() => handleOperation('deleteByGovernor', { age: governorAge })}
              disabled={loading || !governorAge}
              className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 disabled:opacity-50"
            >
              Удалить
            </button>
          </div>

          {/* Get City with Min Coordinates */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Город с минимальными координатами
            </label>
            <button
              onClick={() => handleOperation('minCoordinates')}
              disabled={loading}
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
            >
              Найти
            </button>
          </div>

          {/* Count by Climate */}
          <div className="flex items-end gap-3">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Подсчитать города по климату
              </label>
              <select
                value={climateToCount}
                onChange={(e) => setClimateToCount(e.target.value)}
                className="w-full border rounded px-3 py-2"
              >
                {CLIMATES.map((c) => (
                  <option key={c} value={c}>{CLIMATE_LABELS[c]}</option>
                ))}
              </select>
            </div>
            <button
              onClick={() => handleOperation('countByClimate', { climate: climateToCount })}
              disabled={loading}
              className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50"
            >
              Подсчитать
            </button>
          </div>
        </div>
      </div>

      {loading && (
        <div className="text-center py-4 text-gray-500">Выполнение...</div>
      )}
    </div>
  );
}
