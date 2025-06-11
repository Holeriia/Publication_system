// src/pages/EmployeeFullPage.tsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import type { EmployeeFullResponse } from '../../types';
import { EmployeeDetails } from '../../components/EmployeeDetails';
import { OtherAchievementsList } from '../../components/OtherAchievementsList';

export const EmployeeFullPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [fullData, setFullData] = useState<EmployeeFullResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;

    setLoading(true);
    fetch(`/api/employees/${id}/full`)
      .then(res => {
        if (!res.ok) throw new Error(`Ошибка загрузки данных: ${res.status}`);
        return res.json();
      })
      .then((data: EmployeeFullResponse) => {
        setFullData(data);
        setError(null);
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div>Загрузка информации о сотруднике и достижениях...</div>;
  if (error) return <div style={{ color: 'red' }}>Ошибка загрузки данных: {error}</div>;
  if (!fullData) return <div>Данные сотрудника не найдены</div>;

  const { employee, otherAchievements } = fullData;

  // Формируем полное имя текущего сотрудника для выделения
  const currentEmployeeFullName = `${employee.lastName} ${employee.firstName} ${employee.patronymic || ''}`.trim();

  return (
    <div style={{ padding: '1rem' }}>
      <button onClick={() => navigate(-1)} style={{ marginBottom: '1rem' }}>
        ← К списку сотрудников
      </button>

      <EmployeeDetails employee={employee} />

      <hr style={{ margin: '2rem 0' }} />

      {/* Передаем currentEmployeeFullName в компонент списка достижений */}
      <OtherAchievementsList
        achievements={otherAchievements}
        currentEmployeeFullName={currentEmployeeFullName}
      />
    </div>
  );
};