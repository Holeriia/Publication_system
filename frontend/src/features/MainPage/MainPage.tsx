import React, { useState, useEffect } from 'react';
import { EmployeeLink } from '../../components/EmployeeLink';
import './MainPage.css';

interface Employee {
  id: string; // UUID должен быть строкой
  fullName: string;
}

export const MainPage: React.FC = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('/api/employees/employees') // ← поправленный URL
      .then(res => res.json())
      .then(data => {
        // Преобразуем ФИО (если приходит имя и фамилия отдельно — склеим)
        const mapped = data.map((e: any) => ({
          id: e.id,
          fullName: `${e.lastName} ${e.firstName} ${e.patronymic ?? ''}`.trim()
        }));
        setEmployees(mapped);
        setLoading(false);
      })
      .catch(err => {
        console.error('Ошибка при загрузке сотрудников:', err);
        setLoading(false);
      });
  }, []);

  return (
    <div className="main-page">
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
