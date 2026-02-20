import { useState, useEffect, useCallback } from 'react';
import { filterCities, deleteCity, CLIMATE_LABELS, STANDARD_OF_LIVING_LABELS } from '../api/cityApi';
import { translateMessage } from '../utils/errorTranslation';

const SORT_FIELDS = [
  { value: 'name', label: 'Название' },
  { value: 'population', label: 'Население' },
  { value: 'area', label: 'Площадь' },
  { value: 'standardOfLiving', label: 'Уровень жизни' },
];

const FILTER_FIELDS = [
  { value: 'name', label: 'Название' },
  { value: 'population', label: 'Население' },
  { value: 'area', label: 'Площадь' },
  { value: 'climate', label: 'Климат' },
  { value: 'capital', label: 'Столица' },
  { value: 'standardOfLiving', label: 'Уровень жизни' },
];

const OPERATORS = [
  { value: 'eq', label: '=' },
  { value: 'ne', label: '≠' },
  { value: 'gt', label: '>' },
  { value: 'gte', label: '≥' },
  { value: 'lt', label: '<' },
  { value: 'lte', label: '≤' },
];

export default function CityList({ onEdit, refreshTrigger }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(10);
  
  const [sortRules, setSortRules] = useState([]);
  
  const [filterRules, setFilterRules] = useState([]);

  const loadCities = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const validFilters = filterRules.filter(f => f.value !== null && f.value !== undefined && f.value.trim() !== '');
      
      const result = await filterCities({
        page,
        size,
        sort: sortRules,
        filters: validFilters,
      });
      setData(result);
    } catch (err) {
      setError(translateMessage(err.message) || 'Ошибка загрузки данных');
    } finally {
      setLoading(false);
    }
  }, [page, size, sortRules, filterRules]);

  useEffect(() => {
    loadCities();
  }, [loadCities, refreshTrigger]);

  const handleDelete = async (id) => {
    if (!confirm(`Удалить город с ID ${id}?`)) return;
    try {
      await deleteCity(id);
      loadCities();
    } catch (err) {
      setError(translateMessage(err.message) || 'Ошибка удаления');
    }
  };

  const addSortRule = () => {
    setSortRules([...sortRules, { field: 'name', direction: 'asc' }]);
  };

  const updateSortRule = (index, key, value) => {
    const newRules = [...sortRules];
    newRules[index] = { ...newRules[index], [key]: value };
    setSortRules(newRules);
  };

  const removeSortRule = (index) => {
    setSortRules(sortRules.filter((_, i) => i !== index));
  };

  const addFilterRule = () => {
    setFilterRules([...filterRules, { field: 'name', operator: 'eq', value: '' }]);
  };

  const updateFilterRule = (index, key, value) => {
    const newRules = [...filterRules];
    newRules[index] = { ...newRules[index], [key]: value };
    setFilterRules(newRules);
  };

  const removeFilterRule = (index) => {
    setFilterRules(filterRules.filter((_, i) => i !== index));
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('ru-RU', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="space-y-4">
      {/* Панель фильтров */}
      <div className="bg-white p-4 rounded-lg shadow">
        <h3 className="text-lg font-semibold mb-3">Фильтры</h3>
        <div className="space-y-2">
          {filterRules.map((rule, index) => (
            <div key={index} className="flex gap-2 items-center">
              <select
                value={rule.field}
                onChange={(e) => updateFilterRule(index, 'field', e.target.value)}
                className="border rounded px-2 py-1"
              >
                {FILTER_FIELDS.map((f) => (
                  <option key={f.value} value={f.value}>{f.label}</option>
                ))}
              </select>
              <select
                value={rule.operator}
                onChange={(e) => updateFilterRule(index, 'operator', e.target.value)}
                className="border rounded px-2 py-1"
              >
                {OPERATORS.map((op) => (
                  <option key={op.value} value={op.value}>{op.label}</option>
                ))}
              </select>
              <input
                type="text"
                value={rule.value}
                onChange={(e) => updateFilterRule(index, 'value', e.target.value)}
                className="border rounded px-2 py-1 flex-1"
                placeholder="Значение"
              />
              <button
                onClick={() => removeFilterRule(index)}
                className="text-red-500 hover:text-red-700"
              >
                ✕
              </button>
            </div>
          ))}
          <button
            onClick={addFilterRule}
            className="text-blue-600 hover:text-blue-800 text-sm"
          >
            + Добавить фильтр
          </button>
        </div>
      </div>

      {/* Панель сортировки */}
      <div className="bg-white p-4 rounded-lg shadow">
        <h3 className="text-lg font-semibold mb-3">Сортировка</h3>
        <div className="space-y-2">
          {sortRules.map((rule, index) => (
            <div key={index} className="flex gap-2 items-center">
              <select
                value={rule.field}
                onChange={(e) => updateSortRule(index, 'field', e.target.value)}
                className="border rounded px-2 py-1"
              >
                {SORT_FIELDS.map((f) => (
                  <option key={f.value} value={f.value}>{f.label}</option>
                ))}
              </select>
              <select
                value={rule.direction}
                onChange={(e) => updateSortRule(index, 'direction', e.target.value)}
                className="border rounded px-2 py-1"
              >
                <option value="asc">По возрастанию</option>
                <option value="desc">По убыванию</option>
              </select>
              <button
                onClick={() => removeSortRule(index)}
                className="text-red-500 hover:text-red-700"
              >
                ✕
              </button>
            </div>
          ))}
          <button
            onClick={addSortRule}
            className="text-blue-600 hover:text-blue-800 text-sm"
          >
            + Добавить сортировку
          </button>
        </div>
      </div>

      {/* Кнопка применения */}
      <button
        onClick={() => { setPage(1); loadCities(); }}
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        Применить
      </button>

      {/* Ошибка */}
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* Загрузка */}
      {loading && (
        <div className="text-center py-4">Загрузка...</div>
      )}

      {/* Таблица */}
      {data && !loading && (
        <>
          <div className="bg-white rounded-lg shadow overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Название</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Координаты</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Площадь</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Население</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Высота (м)</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Столица</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Климат</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Уровень жизни</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Возраст губернатора</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Создан</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Действия</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {data.items.map((city) => (
                  <tr key={city.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 whitespace-nowrap">{city.id}</td>
                    <td className="px-4 py-3 whitespace-nowrap font-medium">{city.name}</td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      x: {city.coordinates.x}, y: {city.coordinates.y}
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">{city.area.toLocaleString()}</td>
                    <td className="px-4 py-3 whitespace-nowrap">{city.population.toLocaleString()}</td>
                    <td className="px-4 py-3 whitespace-nowrap">{city.metersAboveSeaLevel ?? '—'}</td>
                    <td className="px-4 py-3 whitespace-nowrap">{city.capital ? 'Да' : 'Нет'}</td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      {city.climate ? CLIMATE_LABELS[city.climate] || city.climate : '—'}
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      {STANDARD_OF_LIVING_LABELS[city.standardOfLiving] || city.standardOfLiving}
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">{city.governor.age}</td>
                    <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                      {formatDate(city.creationDate)}
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap space-x-2">
                      <button
                        onClick={() => onEdit(city)}
                        className="text-blue-600 hover:text-blue-800"
                      >
                        Изменить
                      </button>
                      <button
                        onClick={() => handleDelete(city.id)}
                        className="text-red-600 hover:text-red-800"
                      >
                        Удалить
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Пагинация */}
          <div className="flex items-center justify-between bg-white px-4 py-3 rounded-lg shadow">
            <div className="text-sm text-gray-700">
              Показано {data.items.length} из {data.totalItems} записей
              {data.elapsedTime && ` (${data.elapsedTime.toFixed(1)} мс)`}
            </div>
            <div className="flex items-center gap-4">
              <select
                value={size}
                onChange={(e) => { setSize(Number(e.target.value)); setPage(1); }}
                className="border rounded px-2 py-1"
              >
                <option value={5}>5</option>
                <option value={10}>10</option>
                <option value={25}>25</option>
                <option value={50}>50</option>
              </select>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage(p => Math.max(1, p - 1))}
                  disabled={!data.hasPrevious}
                  className="px-3 py-1 border rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  ←
                </button>
                <span className="px-3 py-1">
                  Страница {data.page} из {data.totalPages}
                </span>
                <button
                  onClick={() => setPage(p => p + 1)}
                  disabled={!data.hasNext}
                  className="px-3 py-1 border rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  →
                </button>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
