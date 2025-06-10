// src/components/EmployeeDetails.tsx
import React from 'react';
import type { EmployeeInfo } from '../types'; // Импортируем новый тип

interface EmployeeDetailsProps {
  employee: EmployeeInfo;
}

export const EmployeeDetails: React.FC<EmployeeDetailsProps> = ({ employee }) => {
  return (
    <div>
      <h2>Основная информация</h2>
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