import React, { useState, useEffect } from 'react';
import { EmployeeLink } from '../../components/EmployeeLink';
import './MainPage.css';

interface Employee {
  id: string;
  fullName: string;
}

export const MainPage: React.FC = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);

useEffect(() => {
  fetch('/api/employees', {
    headers: { 'Accept': 'application/json' }
  })
    .then(async res => {
      if (!res.ok) {
        throw new Error(`Ошибка HTTP: ${res.status}`);
      }
      // заменяем две строки на одну
      const json = await res.json() as Employee[];

      // сортировка по фамилии (первое слово)
      const sorted = json.sort((a, b) => {
        const lastNameA = a.fullName.split(' ')[0].toLowerCase();
        const lastNameB = b.fullName.split(' ')[0].toLowerCase();
        return lastNameA.localeCompare(lastNameB);
      });

      setEmployees(sorted);
    })
    .catch(err => {
      console.error('Ошибка при загрузке сотрудников:', err);
    })
    .finally(() => setLoading(false));
}, []);

  return (
    <div className="main-page">
      {loading ? (
        <p>Загрузка сотрудников...</p>
      ) : (
        <ul className="employee-list">
          {employees.map(emp => (
            <li key={emp.id}>
              <EmployeeLink id={emp.id} fullName={emp.fullName} />
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};