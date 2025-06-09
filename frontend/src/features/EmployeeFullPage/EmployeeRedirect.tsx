import { useParams, Navigate } from 'react-router-dom';

export const EmployeeRedirect: React.FC = () => {
  const { id } = useParams<{ id: string }>();

  if (!id) {
    return <div>Некорректный ID</div>;
  }

  return <Navigate to={`/employees/${id}/full`} replace />;
};
