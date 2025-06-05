import { Link } from 'react-router-dom';

interface EmployeeLinkProps {
  id: string;
  fullName: string;
}

export const EmployeeLink: React.FC<EmployeeLinkProps> = ({ id, fullName }) => {
  return <Link to={`/employees/${id}`}>{fullName}</Link>;
};
