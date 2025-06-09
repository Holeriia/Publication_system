
import { Link } from 'react-router-dom';

interface EmployeeLinkProps {
  id: string;
  fullName: string;
}

export const EmployeeLink: React.FC<EmployeeLinkProps> = ({ id, fullName }) => {
  const url = `/employees/${id}/full`;
  console.log("Link to:", url);
  return <Link to={url}>{fullName}</Link>;
};

