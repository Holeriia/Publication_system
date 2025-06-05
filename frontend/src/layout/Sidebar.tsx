import { NavLink } from 'react-router-dom';
import './Sidebar.css';

export const Sidebar = () => {
  return (
    <nav className="sidebar">
      <h2>Меню</h2>
      <ul>
        <li>
          <NavLink to="/" end className={({isActive}) => isActive ? 'active' : undefined}>
            На главную
          </NavLink>
        </li>
        <li><NavLink to="/create-employee">Создать сотрудника</NavLink></li>
        <li>
          Создать достижение
          <ul>
            <li><NavLink to="/create-achievement/book">Публикация</NavLink></li>
            <li><NavLink to="/create-achievement/patent">Методическая деятельность</NavLink></li>
            <li><NavLink to="/create-achievement/publication">Патент или гос. регистрация программы</NavLink></li>
            <li><NavLink to="/create-achievement/other">Другое</NavLink></li>
          </ul>
        </li>
        <li>
          <NavLink to="/edit-tables">Редактировать таблицы</NavLink>
        </li>
      </ul>
    </nav>
  );
};
