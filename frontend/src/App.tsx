import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { MainPage } from './features/MainPage/MainPage';
import CityList from './components/CityList';
import { Layout } from './layout/Layout';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/city" element={<CityList />} />
          {/* Другие маршруты */}
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
