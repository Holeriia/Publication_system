import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

type Reference = {
  id: string;
  name: string;
};

type EmployeeFullResponse = {
  id: string;
  firstName: string;
  lastName: string;
  patronymic?: string | null;

  degreeId?: string | null;
  degree?: Reference | null;

  titleId?: string | null;
  title?: Reference | null;

  postId?: string | null;
  post?: Reference | null;

  universityStartDate?: string | null;
  industryStartDate?: string | null;
  experienceComment?: string | null;
  diplomaEducation?: string | null;
};

export const EmployeeDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [employee, setEmployee] = useState<EmployeeFullResponse | null>(null);
  const [loadingEmployee, setLoadingEmployee] = useState(true);
  const [employeeError, setEmployeeError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;

    setLoadingEmployee(true);
    fetch(`/api/employees/${id}/full`)
      .then(res => {
        if (!res.ok) throw new Error(`Ошибка загрузки сотрудника: ${res.status}`);
        return res.json();
      })
      .then((data: EmployeeFullResponse) => {
        setEmployee(data);
        setEmployeeError(null);
      })
      .catch(err => setEmployeeError(err.message))
      .finally(() => setLoadingEmployee(false));
  }, [id]);

  if (loadingEmployee) return <div>Загрузка информации о сотруднике...</div>;
  if (employeeError) return <div style={{ color: 'red' }}>Ошибка загрузки сотрудника: {employeeError}</div>;
  if (!employee) return <div>Сотрудник не найден</div>;

  return (
    <div style={{ padding: '1rem' }}>
      <button onClick={() => navigate(-1)} style={{ marginBottom: '1rem' }}>
        ← Назад
      </button>
      <h1>{employee.lastName} {employee.firstName} {employee.patronymic || ''}</h1>
      <ul>
        <li><b>Ученая степень:</b> {employee.degree?.name || 'пока отсутствует'}</li>
        <li><b>Ученое звание:</b> {employee.title?.name || 'пока отсутствует'}</li>
        <li><b>Должность:</b> {employee.post?.name || 'не указано'}</li>
        <li><b>Начало работы в университете:</b> {employee.universityStartDate || 'давным давно'}</li>
        <li><b>Начало работы в отрасли:</b> {employee.industryStartDate || 'давныыым давно'}</li>
        <li><b>Комментарий по опыту:</b> {employee.experienceComment || 'нет'}</li>
        <li><b>Образование (диплом):</b> {employee.diplomaEducation || 'не указано'}</li>
      </ul>
    </div>
  );
};
