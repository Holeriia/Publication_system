// src/components/OtherAchievementsList.tsx
import React from 'react';
import type { OtherAchievement } from '../types';

interface OtherAchievementsListProps {
  achievements: OtherAchievement[];
  currentEmployeeFullName: string; // Добавляем новый пропс
}

export const OtherAchievementsList: React.FC<OtherAchievementsListProps> = ({ achievements, currentEmployeeFullName }) => {
  if (achievements.length === 0) {
    return (
      <div>
        <h2>Другие достижения</h2>
        <p>Пока нет других достижений.</p>
      </div>
    );
  }

  return (
    <div>
      <h2>Другие достижения</h2>
      <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '1rem' }}>
        <thead>
          <tr style={{ backgroundColor: '#f2f2f2' }}>
            <th style={tableHeaderStyle}>Название</th>
            <th style={tableHeaderStyle}>Описание</th>
            <th style={tableHeaderStyle}>Комментарий</th>
            <th style={tableHeaderStyle}>Дата</th>
            <th style={tableHeaderStyle}>Соавторы</th>
          </tr>
        </thead>
        <tbody>
          {achievements.map(achievement => (
            <tr key={achievement.achievementId} style={tableRowStyle}>
              <td style={tableCellStyle}>{achievement.otherName}</td>
              <td style={tableCellStyle}>{achievement.otherDescription || '—'}</td>
              <td style={tableCellStyle}>{achievement.comment || '—'}</td>
              <td style={tableCellStyle}>{achievement.date || '—'}</td>
              <td style={tableCellStyle}>
                {achievement.coAuthors.length > 0
                  ? achievement.coAuthors.map((author, index) => (
                      <span key={index}>
                        {/* Выделяем жирным, если автор совпадает с текущим сотрудником */}
                        <b style={{ fontWeight: author.trim() === currentEmployeeFullName.trim() ? 'bold' : 'normal' }}>
                          {author}
                        </b>
                        {index < achievement.coAuthors.length - 1 && ', '}
                      </span>
                    ))
                  : '—'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

// Простые стили для таблицы, чтобы она выглядела прилично
const tableHeaderStyle: React.CSSProperties = {
  border: '1px solid #ddd',
  padding: '8px',
  textAlign: 'left',
};

const tableCellStyle: React.CSSProperties = {
  border: '1px solid #ddd',
  padding: '8px',
  verticalAlign: 'top',
};

const tableRowStyle: React.CSSProperties = {
  // Вы можете добавить сюда стили для строк, например, чередование цветов
   backgroundColor: '#ffffff', // Белый фон по умолчанию
   //'&:nth-child(even)': { backgroundColor: '#f9f9f9' } // Для чередования
};
