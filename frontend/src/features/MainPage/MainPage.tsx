// MainPage.tsx
import React, { useState, useEffect } from 'react';
import { EmployeeLink } from '../../components/EmployeeLink';
import './MainPage.css';

interface Employee {
  id: string; // UUID должен быть строкой
  fullName: string;
}

export const MainPage: React.FC = () => {
  console.log('MainPage рендерится (до JSX)'); // <-- Эта строка ОК
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    console.log('useEffect в MainPage запущен'); // <-- Эта строка ОК
    fetch('/api/employees', {
      headers: { 'Accept': 'application/json' }
    })
      .then(async res => {
        console.log('Ответ от сервера получен:', res.status); // <-- Эта строка ОК
        if (!res.ok) {
          throw new Error(`Ошибка HTTP: ${res.status}`);
        }
        const text = await res.text();
        try {
          const json = JSON.parse(text);
          console.log('✅ Получены сотрудники:', json); // <-- Эта строка ОК
          const mapped = json.map((e: any) => ({
            id: e.id,
            fullName: `${e.lastName} ${e.firstName} ${e.patronymic ?? ''}`.trim()
          }));
          setEmployees(mapped);
        } catch (e) {
          console.error('❌ Не удалось распарсить JSON:', text); // <-- Эта строка ОК
          throw e;
        }
      })
      .catch(err => {
        console.error('🔥 Ошибка при загрузке сотрудников:', err); // <-- Эта строка ОК
      })
      .finally(() => {
        console.log('Загрузка сотрудников завершена'); // <-- Эта строка ОК
        setLoading(false);
      });
  }, []); // Зависимости пустые, эффект должен запускаться только один раз при монтировании

  return (
    <div className="main-page">
      {/* console.log('MainPage рендерится JSX') ТУТ БЫЛА ОШИБКА, УДАЛЯЕМ */}
      {loading ? (
        <p>Загрузка сотрудников...</p>
      ) : (
        <>
          <ul className="employee-list">
            {employees.map((emp: Employee) => (
              <li key={emp.id}>
                <EmployeeLink id={emp.id} fullName={emp.fullName} />
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
};