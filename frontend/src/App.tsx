import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { EmployeeListPage } from './features/EmployeeListPage/EmployeeListPage';
import CityList from './components/CityList';
import { Layout } from './layout/Layout';
import { EmployeeFullPage } from './features/EmployeeFullPage/EmployeeFullPage';
import { EmployeeRedirect } from './features/EmployeeFullPage/EmployeeRedirect';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<EmployeeListPage />} />
          <Route path="/city" element={<CityList />} />
          <Route path="/employees" element={<EmployeeListPage />} />

          {/* ⚠️ ВАЖНО: этот должен быть ДО /employees/:id/full */}
          <Route path="/employees/:id" element={<EmployeeRedirect />} />

          <Route path="/employees/:id/full" element={<EmployeeFullPage />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
