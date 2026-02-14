import { useState } from 'react';
import CityList from './components/CityList';
import CityForm from './components/CityForm';
import SpecialOperations from './components/SpecialOperations';

function App() {
  const [activeTab, setActiveTab] = useState('cities');
  const [showForm, setShowForm] = useState(false);
  const [editingCity, setEditingCity] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleEdit = (city) => {
    setEditingCity(city);
    setShowForm(true);
  };

  const handleCreate = () => {
    setEditingCity(null);
    setShowForm(true);
  };

  const handleSave = () => {
    setShowForm(false);
    setEditingCity(null);
    setRefreshTrigger(r => r + 1);
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingCity(null);
  };

  const handleDataChange = () => {
    setRefreshTrigger(r => r + 1);
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <h1 className="text-3xl font-bold text-gray-900">
            City Management System
          </h1>
        </div>
      </header>

      {/* Navigation */}
      <nav className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex space-x-8">
            <button
              onClick={() => setActiveTab('cities')}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${
                activeTab === 'cities'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Города
            </button>
            <button
              onClick={() => setActiveTab('operations')}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${
                activeTab === 'operations'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Специальные операции
            </button>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-6">
        {activeTab === 'cities' && (
          <div>
            <div className="mb-4">
              <button
                onClick={handleCreate}
                className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
              >
                + Создать город
              </button>
            </div>
            <CityList 
              onEdit={handleEdit} 
              refreshTrigger={refreshTrigger}
            />
          </div>
        )}

        {activeTab === 'operations' && (
          <SpecialOperations onDataChange={handleDataChange} />
        )}
      </main>

      {/* Form Modal */}
      {showForm && (
        <CityForm
          city={editingCity}
          onSave={handleSave}
          onCancel={handleCancel}
        />
      )}
    </div>
  );
}

export default App;
