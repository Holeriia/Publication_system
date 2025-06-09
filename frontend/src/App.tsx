import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { MainPage } from './features/MainPage/MainPage';
import CityList from './components/CityList';
import { Layout } from './layout/Layout';
import { EmployeeDetailsPage } from './features/EmployeeFullPage/EmployeeDetailsPage';
import { EmployeeRedirect } from './features/EmployeeFullPage/EmployeeRedirect';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/city" element={<CityList />} />
          <Route path="/employees" element={<MainPage />} />

          {/* ⚠️ ВАЖНО: этот должен быть ДО /employees/:id/full */}
          <Route path="/employees/:id" element={<EmployeeRedirect />} />

          <Route path="/employees/:id/full" element={<EmployeeDetailsPage />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
