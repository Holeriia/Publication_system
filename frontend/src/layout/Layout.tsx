// src/layout/Layout.tsx
import type { ReactNode } from 'react';
import { Sidebar } from './Sidebar';
import './Layout.css';
import { useLocation } from 'react-router-dom'; // ИМПОРТИРУЕМ useLocation

interface LayoutProps {
  children: ReactNode;
}

export const Layout = ({ children }: LayoutProps) => {
  const location = useLocation(); // Используем хук useLocation

  // Функция для определения заголовка на основе пути
  const getPageTitle = (pathname: string): string => {
    if (pathname === '/' || pathname === '/employees') {
      return 'Список сотрудников';
    }
    if (pathname.startsWith('/employees/') && pathname.endsWith('/full')) {
      return 'Профиль сотрудника'; // Или "Информация о сотруднике"
    }
    // Можно добавить другие маршруты по мере необходимости
    // if (pathname === '/about') return 'О нас';
    return 'Неизвестная страница'; // Заголовок по умолчанию
  };

  const pageTitle = getPageTitle(location.pathname); // Получаем заголовок для текущего пути

  return (
    <div className="layout">
      <Sidebar />
      <main className="content">
        <header className="page-header">
          <h1>{pageTitle}</h1> {/* ДИНАМИЧЕСКИЙ ЗАГОЛОВОК */}
        </header>
        {children}
      </main>
    </div>
  );
};