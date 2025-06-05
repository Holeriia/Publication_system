// src/layout/Layout.tsx
import type { ReactNode } from 'react';
import { Sidebar } from './Sidebar';
import './Layout.css';

interface LayoutProps {
  children: ReactNode;
}

export const Layout = ({ children }: LayoutProps) => (
  <div className="layout">
    <Sidebar />
    <main className="content">
      <header className="page-header">
        <h1>Главная страница</h1>
      </header>
      {children}
    </main>
  </div>
);
